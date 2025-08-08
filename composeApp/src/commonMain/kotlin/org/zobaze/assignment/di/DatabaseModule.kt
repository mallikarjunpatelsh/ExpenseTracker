package org.zobaze.assignment.di

import org.koin.dsl.module

// Common database module - platform-specific implementations will be provided
val databaseModule = module {
    // Database and DAO will be provided by platform-specific modules
}
