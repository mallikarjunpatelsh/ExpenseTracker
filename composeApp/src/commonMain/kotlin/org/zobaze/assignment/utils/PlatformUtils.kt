package org.zobaze.assignment.utils

import kotlinx.datetime.Clock

expect fun formatCurrency(amount: Double): String

expect fun getCurrentTimeMillis(): Long

// Multiplatform-compatible currency formatting
fun formatAmount(amount: Double): String {
    return formatCurrency(amount)
}
