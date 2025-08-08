package org.zobaze.assignment

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform