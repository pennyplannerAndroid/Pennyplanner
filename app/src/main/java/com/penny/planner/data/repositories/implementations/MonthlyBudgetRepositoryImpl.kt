package com.penny.planner.data.repositories.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.penny.planner.data.repositories.interfaces.MonthlyBudgetRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.MonthlyBudgetInfoModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MonthlyBudgetRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): MonthlyBudgetRepository {

    private val budget = stringPreferencesKey(Utils.BUDGET)
    private val directoryReference = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override suspend fun updateMonthlyBudget(monthlyBudgetInfo: MonthlyBudgetInfoModel): Result<Boolean> {
        try {
            directoryReference
                .child(Utils.formatEmailForFirebase(FirebaseAuth.getInstance().currentUser!!.email!!))
                .child(Utils.BUDGET_INFO)
                .setValue(monthlyBudgetInfo.toFireBaseEntity())
                .await()
            updateLocalWithMonthlyBudget(monthlyBudgetInfo)
            return Result.success(true)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updateLocalWithMonthlyBudget(monthlyBudgetInfo: MonthlyBudgetInfoModel) {
        dataStore.edit { budgetValue ->
            budgetValue[budget] = Gson().toJson(monthlyBudgetInfo)
        }
    }

    override suspend fun getMonthlyBudget(): MonthlyBudgetInfoModel? {
        val budgetInfo = dataStore.data.map { preferences ->
            preferences[budget]
        }.first()
        if (budgetInfo == null) return null
        return Gson().fromJson(budgetInfo, MonthlyBudgetInfoModel::class.java)
    }
}