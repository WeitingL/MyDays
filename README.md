# MyDays

A habit-tracking Android app (daily habits, journaling, check-ins) built with a **soft "liquid glass" visual style** — and the primary testbed for an ongoing research project on **multi-agent, orchestrator-driven AI development workflows**.

This repo is genuinely early-stage as a *product* (see [Status](#status)). What it's further along on is the *process* it's being built with: most of the code here was produced by a self-designed team of AI sub-agents, coordinated by an orchestrator, with me acting as the reviewer/decision-maker rather than writing most of the code by hand.

## Screenshots

| Home | Habit check-in | Home widget |
|---|---|---|
| ![home](docs/screenshots/tc1_launch.png) | ![habit](docs/screenshots/tc5_habit_screen.png) | ![widget](docs/screenshots/tc2_home_widget.png) |

## Tech stack

| Layer | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Clean Architecture (early) |
| DI | Koin |
| Local storage | Room |
| Remote sync | Firebase Firestore (offline-first) |
| Auth | Firebase Authentication (Google Sign-In) |
| Background work | WorkManager / AlarmManager (habit reminders) |
| Extras | Home screen widget (Glance), notifications |

## What I'm actually researching here

The app is the output; the interesting part to me is the workflow that produced it. I designed a small "crew" of AI sub-agents, each with a narrow role, coordinated by an orchestrator that decides on its own when to invoke which agent.

### The crew

- **Orchestrator ("First Officer")** — takes a task in plain language, plans the work, decides which agents to call and in what order, auto-retries failing tests/builds within limits, and only escalates to me for decisions that are risky, architectural, or destructive (e.g. touching a public API, deleting >100 lines, a schema change).
- **PMM Agent** (PM + Tech Lead) — turns a request into a spec and acceptance criteria, chooses the technical approach, flags risk.
- **Engineer Agent** — implements against the spec, writes tests, fixes its own failures.
- **QA Agent** — tests against the acceptance criteria, reports bugs with repro steps, checks against project conventions.
- **UI Designer Agent** — builds screens as previewable Compose components (`@Preview`) *before* they're wired up, so I can see and approve the look/feel before any ViewModel or data-layer code is touched. Doesn't touch screens, ViewModels, or the data layer at all.

### How I actually hand it a task

There's no ticket format. I just say what I want in plain language ("add per-habit reminder notifications", "the streak counter doesn't reset properly"). The Orchestrator doesn't ask "should I start analyzing?" — it dispatches PMM immediately, then comes back with a short, concrete recommendation instead of a wall of text, e.g.:

> Two ways to do this: (A) reuse the existing Theme system, ~2h, or (B) a new CompositionLocal-based approach, ~4h, more decoupled. I'd go with A — smaller, lower risk. Sound good?

I answer in plain language and it proceeds. Progress gets reported as it happens — a failing test, an unexpected bug, a finished stage — rather than silently retrying for a while and handing me one long report at the end.

### How the Orchestrator decides what to do — and when to ask me

There's no fixed PM→Engineer→QA pipeline; the Orchestrator looks at the state of the task and decides which agent to call next, including re-dispatching the same one to fix its own output. It follows a few standing rules so it doesn't have to ask about everything:

- **Tech choices** default to whatever the project already uses, then official Jetpack libraries, then the Kotlin stdlib — only a genuinely new third-party dependency gets escalated to me.
- **Implementation approach** defaults to the most conservative option (smallest diff, closest to existing patterns); when there's a real fork in the road, it lays out the options and asks.
- **Auto-handled without asking**: lint/formatting issues, import ordering, a failing test retried a couple of times with an explained fix.
- **Always escalated to me**: an estimated scope over ~4 hours, more than ~10 files touched, anything touching a DB schema, a public API, or auth/security code — and it stops and asks rather than continuing to retry once a fix attempt fails twice in a row.

To make this machine-checkable rather than vibes-based, every sub-agent reply follows a fixed JSON schema (`agent`, `status`, `summary`, `next_suggested`, plus role-specific fields like `findings` or `decision_needed` for PMM) — so the Orchestrator can decide its next dispatch by parsing a field, not by re-reading prose. Every task also leaves a paper trail under `.claude/outputs/<task>/`: `spec.md`, `ac.md`, `implementation.md`, `test-report.md`, and an `orchestrator-log.md` with a timeline of what it decided and why. `CONVENTIONS.md` accumulates decisions we don't want to re-litigate, each with a dated "why."

### Working across two folders — and two CLI sessions — at once

The repo is checked out twice on disk — `myday-work1` and `myday-work2` — both pointed at this same GitHub repo. In practice, that means running two separate CLI sessions side by side, one rooted at each checkout: a task can be running in `myday-work1` while I scope or start a second, unrelated task in `myday-work2`, without either session's file edits colliding in a shared working tree. (There's also a lighter single-session mechanism for this — a `current-repo.txt` file one Orchestrator instance can be told to repoint at the other checkout — but two independent CLI sessions running in parallel is the setup I've actually been testing.)

Within a single task, the Orchestrator can also fan out to more than one sub-agent at once rather than strictly one-at-a-time — the open question isn't *whether* to parallelize, it's figuring out a good dispatch strategy: what's actually safe/useful to run in parallel vs. what needs to stay sequential, and how agents hand off or merge work without stepping on each other.

### Agent → model mapping

The Orchestrator, PMM, Engineer, and QA are dispatched on demand as subagents, with the model chosen per dispatch — currently Sonnet, since these are bounded, well-specified tasks with a clear rubric (a spec to follow, acceptance criteria to test against) rather than open-ended judgment calls. The UI Designer is set up as a standing subagent pinned to Opus instead, since visual/design judgment benefits more from a stronger model than mechanical implementation does, and it's only invoked when a task changes something the user actually sees. Part of what I'm evaluating is whether that split is the right one, or whether it should be more dynamic (e.g. escalating Engineer to a stronger model when a fix has failed twice).

### Specific questions I'm trying to answer with this project

1. **Cross-project parallel work** — running two CLI sessions across `myday-work1`/`myday-work2`, can I productively keep two unrelated tasks moving at once without them interfering with each other?
2. **Orchestrator vs. fixed pipeline** — is it better for a main agent to *decide* which sub-agent to call next and why, versus a hard-coded PM→Engineer→QA sequence?
3. **Best dispatch strategy for multiple sub-agents** — when is parallel dispatch actually safe/useful versus needing to stay sequential, and how should agents hand off work cleanly?
4. **A dedicated UI/UX sub-agent** — does separating "what it should look like" (previewable, no wiring) from "how it's implemented" produce a better development flow for product-shaped work — and how do I get it to reason about realistic user flows, not just "technically works"?
5. **Spec-driven flow** — which parts of a spec-first process (written AC, structured handoff docs between agents) actually pay off versus add ceremony?
6. **Model selection per role** — does pinning a specific sub-agent (like UI Designer) to a stronger model actually pay off, versus routing every dispatch through one default model?

## Status

Early. The app currently has: Google sign-in, habit CRUD, daily check-in with streaks, offline-first Room↔Firestore sync, per-habit reminder notifications, and a home screen widget. Navigation and some screens are still being reshaped as the underlying agent workflow itself evolves — this is a live research project, not a finished product, and I'm not trying to present it as one.

### Open problems

- **Finding a good dispatch strategy** — parallel dispatch across sub-agents (and across the two CLI sessions above) is possible; I haven't yet nailed down *how* to dispatch well — what to run in parallel, what to keep sequential, how to hand off cleanly.
- **Weak user-flow reasoning** — agents reliably produce something that *runs*, but the resulting user flow is sometimes one no real user would actually follow. Getting an agent to reason about "what would a user naturally do here" is still unsolved.
- **QA's UI-testing approach needs more scaffolding** — the QA agent's current concept of "testing the UI" needs clearer guidance and tooling; right now it's underspecified.
- **Token cost is high** — this workflow burns noticeably more tokens than a simpler single-agent loop. I think the output quality is better than that simpler loop, but I'm still weighing whether the tradeoff holds up at scale.

## Running it

```bash
./gradlew :app:assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Requires your own `google-services.json` (Firebase project) dropped into `app/`.
