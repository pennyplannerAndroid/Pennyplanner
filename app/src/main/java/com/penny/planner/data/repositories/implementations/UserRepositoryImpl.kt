package com.penny.planner.data.repositories.implementations

import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.helpers.Utils
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(): UserRepository {

    private val auth = FirebaseAuth.getInstance()

    override fun navigationToOnboardingNeeded(): String? {
        if (auth.currentUser == null)
            Utils.TUTORIAL
        else if (!auth.currentUser?.isEmailVerified!!)
            Utils.EMAIL_VERIFICATION
        else if (auth.currentUser?.displayName == null || FirebaseAuth.getInstance().currentUser?.displayName!!.isEmpty()) {
            Utils.UPDATE_PROFILE
        }
        return null
    }

    override fun getUserName(): String {
        return auth.currentUser?.displayName ?: Utils.USER
    }

    override fun getEmail(): String {
        return auth.currentUser?.email ?: Utils.DEFAULT_EMAIL_STRING
    }

    override fun getImagePath(): String {
        return FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
    }

    override fun getSelfId() = FirebaseAuth.getInstance().currentUser!!.uid

}