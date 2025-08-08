package org.zobaze.assignment.di

import org.koin.dsl.module
import org.zobaze.assignment.presentation.entry.ExpenseEntryViewModel
import org.zobaze.assignment.presentation.list.ExpenseListViewModel
import org.zobaze.assignment.presentation.report.ExpenseReportViewModel

val viewModelModule = module {
    factory { ExpenseEntryViewModel(get()) }
    factory { ExpenseListViewModel(get()) }
    factory { ExpenseReportViewModel(get()) }
}
