package com.penny.planner.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.penny.planner.data.db.friends.UsersDao
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendsDirectoryRepositoryImpl @Inject constructor(
    private val usersDao: UsersDao
): FriendsDirectoryRepository {

    @Inject lateinit var applicationScope: CoroutineScope
    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val auth = FirebaseAuth.getInstance()

    override suspend fun findUserFromServer(email: String): Result<UsersEntity> {
        if (email == auth.currentUser?.email || usersDao.doesFriendExists(email))
            return Result.failure(Exception(Utils.SAME_EMAIL_ERROR))
        return suspendCoroutine { continuation ->
            try {
                userDirectory.child(Utils.formatEmailForFirebase(email)).child(Utils.USER_INFO).addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(UsersEntity::class.java) as UsersEntity
                            continuation.resume(Result.success(model))
                        } else
                            continuation.resume(Result.failure(Exception(Utils.USER_NOT_FOUND)))
                    }
                    override fun onCancelled(error: DatabaseError) {
                        continuation.resume(Result.failure(Exception(error.message)))
                    }
                })
            } catch (error: Exception) {
                continuation.resume(Result.failure(error))
            }
        }
    }

    override suspend fun addFriend(entity: UsersEntity) {
        usersDao.insertList(entity)
    }

    override suspend fun addFriend(list: List<UsersEntity>) {
        usersDao.insertList(list)
    }

    override suspend fun updateFriend(entity: UsersEntity) {
        usersDao.update(entity)
    }

    override suspend fun getFriends(list: List<String>) =
        usersDao.getUsersByEmailList(list.filter { it != auth.currentUser?.email })

    override suspend fun doesFriendExists(email: String) = usersDao.doesFriendExists(email)

    override suspend fun getAllFriends(): List<UsersEntity> {
        return usersDao.getAllFriends()
    }

    override suspend fun findFriend(email: String): UsersEntity {
        return usersDao.findFriend(email)
    }
}