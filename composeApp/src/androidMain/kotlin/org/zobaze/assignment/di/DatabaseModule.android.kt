package org.zobaze.assignment.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.zobaze.assignment.data.database.ExpenseDatabase
import org.zobaze.assignment.data.database.getDatabaseBuilder

val androidDatabaseModule = module {
    single<ExpenseDatabase> {
        getDatabaseBuilder(androidContext())
            .fallbackToDestructiveMigration(true)
            .build()
    }
    
    single { get<ExpenseDatabase>().expenseDao() }
}
