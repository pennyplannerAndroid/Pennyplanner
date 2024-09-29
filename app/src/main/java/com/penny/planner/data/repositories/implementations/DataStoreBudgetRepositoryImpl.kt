package com.penny.planner.data.repositories.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.penny.planner.data.repositories.interfaces.DataStoreBudgetRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreBudgetRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): DataStoreBudgetRepository {
    private val budget = stringPreferencesKey(Utils.BUDGET)

    override suspend fun updateBudget(amount: String) {
        dataStore.edit { budgetValue ->
            budgetValue[budget] = amount
        }
    }

    override suspend fun getBudget(): String? {
        return dataStore.data.map { preferences ->
            preferences[budget]
        }.first()
    }
}