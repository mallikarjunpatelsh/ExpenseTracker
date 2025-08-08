package org.zobaze.assignment.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<ExpenseDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(ExpenseDatabase.DATABASE_NAME)
    return Room.databaseBuilder<ExpenseDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<ExpenseDatabase> {
    error("Context is required for Android database builder. Use getDatabaseBuilder(context) instead.")
}
