package org.zobaze.assignment.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.zobaze.assignment.di.appModule

class ExpenseTrackerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@ExpenseTrackerApplication)
            modules(appModule)
        }
    }
}
