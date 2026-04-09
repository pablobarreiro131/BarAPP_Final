package org.pabarreiro.barapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform