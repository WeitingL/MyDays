package com.weiting.mydays.di

import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.weiting.mydays.data.auth.AuthRepository
import com.weiting.mydays.data.auth.AuthRepositoryImpl
import com.weiting.mydays.data.habit.HabitRemoteDataSource
import com.weiting.mydays.data.habit.HabitRepository
import com.weiting.mydays.data.habit.HabitRepositoryImpl
import com.weiting.mydays.data.habit.HabitSyncManager
import com.weiting.mydays.data.habit.MyDaysDatabase
import com.weiting.mydays.notification.HabitReminderScheduler
import com.weiting.mydays.notification.NotificationHelper
import com.weiting.mydays.ui.auth.AuthViewModel
import com.weiting.mydays.ui.habit.HabitViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MyDaysDatabase::class.java,
            "mydays.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }
    single { get<MyDaysDatabase>().habitDao() }
    single { get<MyDaysDatabase>().checkInDao() }
    single { get<MyDaysDatabase>().syncMetaDao() }
    single { FirebaseFirestore.getInstance() }
    single { HabitRemoteDataSource(get()) }
    // app 層級長生命週期 scope：供同步 fire-and-forget 觸發
    single { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    single { NotificationHelper(androidContext()) }
    single { HabitReminderScheduler(androidContext()) }
    single { HabitSyncManager(get(), get(), get(), get(), get(), get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl() }
    single<HabitRepository> { HabitRepositoryImpl(get(), get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { HabitViewModel(get()) }
}
