package com.penny.planner.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.penny.planner.data.repositories.interfaces.FriendsDirectoryRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.UserModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FriendsDirectoryRepositoryImpl @Inject constructor(): FriendsDirectoryRepository {

    private val userDirectory = FirebaseDatabase.getInstance().getReference(Utils.USERS)
    private val auth = FirebaseAuth.getInstance()

    override suspend fun findUser(email: String): Result<UserModel> {
        if (email == auth.currentUser?.email)
            return Result.failure(Exception(Utils.SAME_EMAIL_ERROR))
        return suspendCoroutine { continuation ->
            try {
                userDirectory.child(Utils.formatEmailForFirebase(email)).child(Utils.USER_INFO).addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val model = snapshot.getValue(UserModel::class.java) as UserModel
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

}