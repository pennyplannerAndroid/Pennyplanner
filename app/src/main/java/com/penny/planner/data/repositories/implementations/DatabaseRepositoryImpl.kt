package com.penny.planner.data.repositories.implementations

import android.content.Context
import com.penny.planner.data.repositories.interfaces.DatabaseRepository
import com.penny.planner.helpers.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): DatabaseRepository {

    override fun deleteDb() {
        context.deleteDatabase(Utils.PENNY_DATABASE)
    }
}