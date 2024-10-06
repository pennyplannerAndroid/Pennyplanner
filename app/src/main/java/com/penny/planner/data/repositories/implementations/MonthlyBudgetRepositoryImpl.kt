package com.penny.planner.data.repositories.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MonthlyBudgetRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): MonthlyBudgetRepository {

    private val budget = stringPreferencesKey(Utils.BUDGET)
    private val directoryReference = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override suspend fun updateMonthlyBudget(amount: String): Result<Boolean> {
        try {
            directoryReference
                .child(Utils.formatEmailForFirebase(FirebaseAuth.getInstance().currentUser!!.email!!))
                .child(Utils.BUDGET_INFO)
                .child(Utils.MONTHLY_BUDGET)
                .setValue(amount)
                .await()
            updateLocalWithMonthlyBudget(amount)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updateLocalWithMonthlyBudget(amount: String) {
        dataStore.edit { budgetValue ->
            budgetValue[budget] = amount
        }
    }

    override suspend fun getMonthlyBudget(): String? {
        return dataStore.data.map { preferences ->
            preferences[budget]
        }.first()
    }
}