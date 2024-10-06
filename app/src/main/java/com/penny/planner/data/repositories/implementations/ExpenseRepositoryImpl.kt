package com.penny.planner.data.repositories.implementations

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.helpers.Utils
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

    override suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>> =
        expenseDao.getAllExpenses()

    override suspend fun addExpense(entity: ExpenseEntity) {
        entity.expensorId = auth.currentUser?.uid ?: ""
        expenseDao.insert(entity)
        expenseCollectionRef
            .document(entity.time.toString())
            .set(entity.toFireBaseEntity())
            .addOnSuccessListener {
                applicationScope.launch(Dispatchers.IO) {
                    entity.uploadedOnServer = true
                    expenseDao.update(entity)
                }
            }
    }

}