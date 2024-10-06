package com.penny.planner.models

data class LoginResultModel(
    var isEmailVerified: Boolean,
    var isProfileUpdated: Boolean,
    var isBudgetSet: Boolean
)
