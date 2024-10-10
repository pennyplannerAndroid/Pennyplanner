package com.penny.planner.data.repositories.interfaces

interface GroupBackgroundSyncRepository {
    fun getAllGroupsFromFirebase()
    fun getAllPendingGroups()
    fun updateGroupsTransactions()
}