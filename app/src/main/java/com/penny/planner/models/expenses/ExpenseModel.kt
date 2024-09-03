package com.penny.planner.models.expenses

fun main() {
    println(ExpenseModel("1", Category(name = "Travel", id = "123"), SubCategory(name = "DailyCommute", id = "12")).toString())
}

data class ExpenseModel(
    val id : String,
    val category: Category,
    val subCategory: SubCategory
)

data class Category (
    val name: String,
    val id: String,
    val list: List<SubCategory> = listOf()
)

data class SubCategory (
    val id: String,
    val name: String
)