package com.penny.planner.data.repositories.implementations

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.repositories.interfaces.GroupBackgroundSyncRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupBackgroundSyncRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao
): GroupBackgroundSyncRepository {

    val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val auth = FirebaseAuth.getInstance()
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)

    override fun getAllGroupsFromFirebase() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.JOINED)
            }
        }
    }

    override fun getAllPendingGroups() {
        if (auth.currentUser != null) {
            scope.launch {
                fetchAllGroupsFromFirebase(Utils.PENDING)
            }
        }
    }

    override fun updateGroupsTransactions() {

    }

    private fun fetchAllGroupsFromFirebase(source: String) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(source).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val pendingGroups = snapshot.value as Map<*, *>
                        if (pendingGroups.isNotEmpty()) {
                            for (groupId in pendingGroups.keys) {
                                updateLocalWithPendingGroups(groupId.toString(), source == Utils.PENDING)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private fun updateLocalWithPendingGroups(groupId: String, needUpdatePendingList: Boolean) {
        FirebaseDatabase.getInstance()
            .getReference(Utils.GROUPS)
            .child(groupId).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val entity = snapshot.getValue(GroupEntity::class.java)
                        if (entity != null) {
                            scope.launch {
                                groupDao.addGroup(entity)
                                if (needUpdatePendingList)
                                    updatePendingNode(entity)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.d("GroupRepository:: ", error.message)
                }
            })
    }

    private fun updatePendingNode(entity: GroupEntity) {
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.JOINED).child(entity.groupId).setValue(Utils.NON_ADMIN_VALUE)
        userDirectory
            .child(Utils.formatEmailForFirebase(auth.currentUser!!.email!!))
            .child(Utils.GROUP_INFO)
            .child(Utils.PENDING).child(entity.groupId).removeValue()
    }

}