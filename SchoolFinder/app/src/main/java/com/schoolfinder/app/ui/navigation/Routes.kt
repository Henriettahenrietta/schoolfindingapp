package com.schoolfinder.app.ui.navigation

object Routes {
    const val HOME = "home"
    const val COMPARE = "compare"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
    const val MANAGE_SCHOOLS = "manage_schools"
    const val DETAILS = "details"

    fun details(id: Int) = "$DETAILS/$id"
}
