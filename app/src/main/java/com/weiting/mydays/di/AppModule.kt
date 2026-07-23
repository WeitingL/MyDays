package com.weiting.mydays.di

import androidx.room.Room
import com.weiting.mydays.data.auth.AuthRepository
import com.weiting.mydays.data.auth.AuthRepositoryImpl
import com.weiting.mydays.data.habit.HabitRepository
import com.weiting.mydays.data.habit.HabitRepositoryImpl
import com.weiting.mydays.data.habit.MyDaysDatabase
import com.weiting.mydays.ui.auth.AuthViewModel
import com.weiting.mydays.ui.habit.HabitViewModel
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
    single<AuthRepository> { AuthRepositoryImpl() }
    single<HabitRepository> { HabitRepositoryImpl(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { HabitViewModel(get()) }
}
