package com.penny.planner.data.repositories.interfaces

interface FirebaseBackgroundSyncRepository {
    fun getAllPersonalExpenses()
    fun getAllGroupsFromFirebase()
    fun getAllPendingGroups()
    fun updateGroupsTransactions()
    fun addGroupForFirebaseListener(groupId: String)
}