package com.good4.navigation

import com.good4.user.domain.UserRole

fun UserRole.toHomeRoute(): Route {
    return when (this) {
        UserRole.ADMIN -> Route.AdminHome
        UserRole.BUSINESS -> Route.BusinessHome
        UserRole.STUDENT -> Route.StudentHome
        // Keep the legacy role readable, but send old supporter accounts back to login.
        UserRole.SUPPORTER -> Route.Login
    }
}
