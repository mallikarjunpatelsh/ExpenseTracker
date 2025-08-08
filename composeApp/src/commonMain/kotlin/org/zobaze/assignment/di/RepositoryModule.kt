package org.zobaze.assignment.di

import org.koin.dsl.module
import org.zobaze.assignment.data.repository.ExpenseRepository
import org.zobaze.assignment.data.repository.InMemoryExpenseRepository

val repositoryModule = module {
    single<ExpenseRepository> { 
        InMemoryExpenseRepository()
    }
}
