package com.penny.planner.data.repositories.implementations

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    val db = FirebaseFirestore.getInstance()
    @Inject
    lateinit var applicationScope: CoroutineScope
    private val auth = FirebaseAuth.getInstance()

    private val expenseCollectionRef = db.collection(Utils.USER_EXPENSES)
        .document(FirebaseAuth.getInstance().currentUser!!.uid)
        .collection(Utils.EXPENSES)

    private val groupExpenseCollectionRef = db.collection(Utils.GROUP_EXPENSES)

    override suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>> =
        expenseDao.getAllExpenses()

    override suspend fun getAllExpenses(groupId: String): LiveData<List<GroupDisplayModel>> {
       return expenseDao.getExpenseListForDisplay(groupId)
    }

    override suspend fun addExpense(entity: ExpenseEntity) {
        entity.expensorId = auth.currentUser?.uid ?: ""
        if (entity.groupId.isNotEmpty())
            addExpenseForGroup(entity)
        else
            addExpenseForSelf(entity)
    }

    private suspend fun addExpenseForSelf(entity: ExpenseEntity) {
        entity.id = expenseCollectionRef.document().id
        addToExpenseDb(entity)
        expenseCollectionRef
            .document(entity.id)
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                applicationScope.launch(Dispatchers.IO) {
                    entity.uploadedOnServer = true
                    expenseDao.update(entity)
                }
            }
    }

    private suspend fun addExpenseForGroup(entity: ExpenseEntity) {
        entity.id = groupExpenseCollectionRef
            .document(entity.groupId)
            .collection(Utils.EXPENSES)
            .document().id
        addToExpenseDb(entity)
        groupExpenseCollectionRef
            .document(entity.groupId)
            .collection(Utils.EXPENSES)
            .document(entity.id)
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                entity.uploadedOnServer = true
                applicationScope.launch(Dispatchers.IO) {
                    expenseDao.update(entity)
                }
            }
    }

    private suspend fun addToExpenseDb(entity: ExpenseEntity) {
        expenseDao.insert(entity)
    }

}