package com.penny.planner.models

data class MonthlyBudgetInfoModel(
    var monthlyBudget: String = "",
    var safeToSpendLimit: Int = 80
)