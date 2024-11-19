package com.studios1299.playwall.app.navigation

/**
 * Some people use UPPERCASE, some lowercase, I haven't came to a definite conclusion about case
 * when it comes to navigation routes
 */
sealed class Graphs {
    object Main {
        const val root = "main"
        object Screens {
            const val feed = "feed"
            const val detail = "detail"
        }
    }
}
