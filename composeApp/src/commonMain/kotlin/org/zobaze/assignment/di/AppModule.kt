package org.zobaze.assignment.di

import org.koin.dsl.module

val appModule = module {
    includes(
        repositoryModule,
        viewModelModule
    )
}
