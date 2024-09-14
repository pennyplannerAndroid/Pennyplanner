package com.penny.planner.models

data class HomeNavigationItem<T: Any>(
    val name: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val position: T
)
