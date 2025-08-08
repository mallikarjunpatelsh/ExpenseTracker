package org.zobaze.assignment.utils

import platform.Foundation.NSString
import platform.Foundation.stringWithFormat
import kotlinx.datetime.Clock

actual fun formatCurrency(amount: Double): String {
    return NSString.stringWithFormat("%.2f", amount)
}

actual fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}
