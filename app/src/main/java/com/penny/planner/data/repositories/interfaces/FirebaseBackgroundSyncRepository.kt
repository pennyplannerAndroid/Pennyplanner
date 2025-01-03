package com.penny.planner.data.repositories.interfaces

import com.penny.planner.data.db.subcategory.SubCategoryEntity

interface FirebaseBackgroundSyncRepository {
    fun getAllPersonalExpenses()
    fun getAllJoinedGroupsFromFirebaseAtLogin()
    fun init()
    fun addGroupForFirebaseListener(groupId: String)
    suspend fun newSubCategoryAdded(entity: SubCategoryEntity)
}