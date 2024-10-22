package com.penny.planner.models

data class MonthlyBudgetInfoModel (
    var monthlyBudget: Double = 0.0,
    var safeToSpendLimit: Int = 80
)