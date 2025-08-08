package org.zobaze.assignment.di

import org.koin.dsl.module
import org.zobaze.assignment.data.database.ExpenseDatabase
import org.zobaze.assignment.data.database.getDatabaseBuilder

val iosDatabaseModule = module {
    single<ExpenseDatabase> {
        getDatabaseBuilder()
            .fallbackToDestructiveMigration(true)
            .build()
    }
    
    single { get<ExpenseDatabase>().expenseDao() }
}
