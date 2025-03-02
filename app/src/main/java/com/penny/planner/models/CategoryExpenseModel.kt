package com.penny.planner.models

data class CategoryExpenseModel(
    var id: Int,
    val category: String,
    var spendLimit: Double,
    val entityId: String,
    var alertAdded: Boolean,
    var alertLimit: Int = 80,
    var icon: String,
    var expenses: Double = 0.0,
    var totalTransactions: Int = 0
)