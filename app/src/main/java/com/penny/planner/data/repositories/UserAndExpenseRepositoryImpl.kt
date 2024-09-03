package com.penny.planner.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.penny.planner.models.firebase.UserModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserAndExpenseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : UserAndExpenseRepository {

    override suspend fun newGroup(email: String): Result<Boolean> {
//        val id = auth.currentUser?.uid ?: "1"
//        val emai1 = auth.currentUser?.email ?: ""
//        FirebaseDatabase.getInstance()
//                .getReference("UserDirectory").child(id).setValue(UserModel(emai1, id, getUserName())).await()
        return Result.success(true)
//        return suspendCoroutine { continuation ->
//            FirebaseDatabase.getInstance()
//                .getReference("UserDirectory")
//                .child(email).addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        continuation.resume(Result.success(true))
//                    }
//                    override fun onCancelled(error: DatabaseError) {
//                        continuation.resume(Result.failure(error.toException()))
//                    }
//                })
//        }
    }

    override fun getUserName(): String {
        return auth.currentUser?.displayName ?: "User"
    }
}