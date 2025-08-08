package org.zobaze.assignment.data.database

import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(): RoomDatabase.Builder<ExpenseDatabase> {
    // For iOS, we'll use an in-memory database to avoid KSP compilation issues
    return Room.inMemoryDatabaseBuilder<ExpenseDatabase>()
}
