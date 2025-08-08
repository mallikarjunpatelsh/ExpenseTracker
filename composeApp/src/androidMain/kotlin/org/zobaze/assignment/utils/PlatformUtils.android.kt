package org.zobaze.assignment.utils

actual fun formatCurrency(amount: Double): String {
    return String.format("%.2f", amount)
}

actual fun getCurrentTimeMillis(): Long {
    return System.currentTimeMillis()
}
