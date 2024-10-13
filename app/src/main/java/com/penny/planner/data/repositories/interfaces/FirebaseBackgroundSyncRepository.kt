package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.subcategory.SubCategoryEntity

interface FirebaseBackgroundSyncRepository {
    fun getAllPersonalExpenses()
    fun getAllGroupsFromFirebase()
    fun getAllPendingGroups()
    fun updateGroupsTransactions()
    fun addGroupForFirebaseListener(groupId: String)
    suspend fun newSubCategoryAdded(entity: SubCategoryEntity)
}