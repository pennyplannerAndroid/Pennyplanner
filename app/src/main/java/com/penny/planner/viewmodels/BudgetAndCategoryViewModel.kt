package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.BudgetRepository
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.data.repositories.interfaces.MonthlyExpenseRepository
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.CategoryExpenseModel
import com.penny.planner.models.MonthlyBudgetInfoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class BudgetAndCategoryViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
    private val monthlyExpenseRepository: MonthlyExpenseRepository,
    private val groupRepository: GroupRepository,
    private val monthlyBudgetRepository: MonthlyBudgetRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private lateinit var entityId: String
    private var month: Int = 1
    private var year: Int = 2025
    private var isPersonal = true
    private var monthlyBudget: MonthlyBudgetInfoModel = MonthlyBudgetInfoModel()
    private var monthlyExpense: Int = 0
    private lateinit var groupedExpenses: Map<String, List<ExpenseEntity>>
    private var categoryDetails: MutableList<CategoryExpenseModel> = mutableListOf()
    private var profileUrl: String = ""
    private var name: String = "Personal Expenses"
    private var expenseCount = 0

    suspend fun build(entityId: String) {
        this.entityId = entityId
        this.isPersonal = userRepository.getSelfId() == entityId
        year = Calendar.getInstance().get(Calendar.YEAR)
        month = Calendar.getInstance().get(Calendar.MONTH)
        processBudgetData(month, year)
    }

    private suspend fun processBudgetData(month: Int, year: Int) {
        if (isPersonal) {
            monthlyBudget = monthlyBudgetRepository.getMonthlyBudget() ?: MonthlyBudgetInfoModel()
            profileUrl = userRepository.getImagePath()
        } else {
            val group = groupRepository.getGroupById(entityId)
            profileUrl = group.localImagePath.ifEmpty { group.profileImage }
            name = group.name
            monthlyBudget = MonthlyBudgetInfoModel(
                monthlyBudget = group.monthlyBudget,
                safeToSpendLimit = group.safeToSpendLimit
            )
        }
        monthlyExpense = monthlyExpenseRepository
            .getMonthlyExpenseEntity(entityId, Utils.getCurrentMonthYear(month = month, year = year))?.expense?.toInt() ?: 0
        val (start, end) = Utils.getStartAndEndOfMonth(month = month, year = year)
        getAllExpenseForBudgeting(start, end)
    }

    private suspend fun getAllExpenseForBudgeting(start: Long, end: Long) {
        val expenses = expenseRepository.getAllExpensesExceptMessage(if (isPersonal) "" else entityId, start, end)
        expenseCount = expenses.size
        groupedExpenses = expenses.groupBy { it.category }
        val budgetForCategories = budgetRepository.getAllBudgetsForEntity(entityId = entityId)
        for (budgetItem in budgetForCategories) {
            val totalExpense = groupedExpenses[budgetItem.category]?.sumOf { it.price } ?: 0.0
            val totalTransactions = groupedExpenses[budgetItem.category]?.size ?: 0
            categoryDetails.add(
                CategoryExpenseModel(
                    id = budgetItem.id,
                    category = budgetItem.category,
                    spendLimit = budgetItem.spendLimit,
                    entityId = budgetItem.entityId,
                    alertAdded = budgetItem.alertAdded,
                    alertLimit = budgetItem.alertLimit,
                    icon = budgetItem.icon,
                    expenses = totalExpense,
                    totalTransactions = totalTransactions
                )
            )
        }
        categoryDetails.sortByDescending { it.expenses }
    }

    fun getAllCategoryDetails() = categoryDetails.toList()

    fun getGroupBudgetDetails() = CategoryExpenseModel(
        id = 1,
        category = name,
        spendLimit = monthlyBudget.monthlyBudget,
        entityId = "",
        alertAdded = true,
        alertLimit = 80,
        icon = "",
        expenses = monthlyExpense.toDouble(),
        totalTransactions = expenseCount
    )
}