package com.penny.planner.data.repositories.implementations

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.monthlyexpenses.MonthlyExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.interfaces.MonthlyExpenseRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val monthlyExpenseRepository: MonthlyExpenseRepository
) : ExpenseRepository {

    private val dateFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val db = FirebaseFirestore.getInstance()
    @Inject
    lateinit var applicationScope: CoroutineScope
    private val auth = FirebaseAuth.getInstance()

    private val userCollectionRef = db.collection(Utils.USER_EXPENSES)
    private val groupCollectionRef = db.collection(Utils.GROUP_EXPENSES)

    override suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>> =
        expenseDao.getAllExpenses()

    override suspend fun getExpensesForDisplayAtHomePage(): LiveData<List<ExpenseEntity>> =
        expenseDao.getExpensesForDisplayAtHomePage(auth.currentUser!!.uid)

    override suspend fun getAllExpenses(groupId: String): LiveData<List<GroupDisplayModel>> {
       return expenseDao.getExpenseListForDisplay(groupId)
    }

    override suspend fun insertBulkExpenseFromServer(list: List<ExpenseEntity>) {
        updateMonthlyExpenseTable(list)
        expenseDao.insertList(list)
    }

    override suspend fun getMonthlyExpenseEntity(id: String, month: String) =
        monthlyExpenseRepository.getMonthlyExpenseEntity(entityId = id, month = month)

    override suspend fun addExpense(entity: ExpenseEntity) {
        entity.expensorId = auth.currentUser?.uid ?: ""
        if (entity.groupId.isNotEmpty())
            addExpenseForGroup(entity)
        else
            addExpenseForSelf(entity)
    }

    private suspend fun addExpenseForSelf(entity: ExpenseEntity) {
        val reference = userCollectionRef
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(Utils.EXPENSES)
        entity.id = reference.document().id
        addToExpenseDb(entity)
        reference.document(entity.id)
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                applicationScope.launch(Dispatchers.IO) {
                    entity.uploadedOnServer = true
                    expenseDao.update(entity)
                }
            }
    }

    private suspend fun addExpenseForGroup(entity: ExpenseEntity) {
        val reference = groupCollectionRef
            .document(entity.groupId)
            .collection(Utils.EXPENSES)
        entity.id = reference.document().id
        addToExpenseDb(entity)
        reference.document(entity.id)
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                entity.uploadedOnServer = true
                applicationScope.launch(Dispatchers.IO) {
                    expenseDao.update(entity)
                }
            }
    }

    private suspend fun addToExpenseDb(entity: ExpenseEntity) {
        if (!expenseDao.doesExpenseExists(entity.id))
            updateMonthlyExpenseTable(entity)
        expenseDao.insert(entity)
    }

    private suspend fun updateMonthlyExpenseTable(list: List<ExpenseEntity>) {
        val expensesByMonth = mutableMapOf<String, Double>()
        var id = list[0].groupId
        if (id.isEmpty()) {
            id = auth.currentUser!!.uid
        }
        for (expense in list) {
            if (!expenseDao.doesExpenseExists(expense.id)) {
                val monthYear = dateFormatter.format(expense.time.toDate())
                expensesByMonth[monthYear] =
                    expensesByMonth.getOrDefault(monthYear, 0.0) + expense.price
            }
        }
        for (item in expensesByMonth) {
            updateExpenseItem(id, item.key, item.value)
        }
    }

    private suspend fun updateMonthlyExpenseTable(entity: ExpenseEntity) {
        val monthYear = dateFormatter.format(entity.time.toDate())
        var id = entity.groupId
        if (id.isEmpty()) {
            id = auth.currentUser!!.uid
        }
        updateExpenseItem(id, monthYear, entity.price)
    }

    private suspend fun updateExpenseItem(id: String, monthYear: String, price: Double) {
        val monthlyExpense = getMonthlyExpenseEntity(id, monthYear)
        if (monthlyExpense != null) {
            val updatedTotal = monthlyExpense.expense + price
            monthlyExpenseRepository.updateExpenseEntity(monthlyExpense.copy(expense = updatedTotal))
        } else {
            monthlyExpenseRepository.addMonthlyExpenseEntity(
                MonthlyExpenseEntity(entityID = id, month = monthYear, expense = price)
            )
        }
    }

}