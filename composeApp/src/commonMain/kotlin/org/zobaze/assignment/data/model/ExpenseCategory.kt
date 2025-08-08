package org.zobaze.assignment.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExpenseCategory(val displayName: String, val emoji: String) {
    STAFF("Staff", "👥"),
    TRAVEL("Travel", "✈️"),
    FOOD("Food", "🍽️"),
    UTILITY("Utility", "⚡")
}
