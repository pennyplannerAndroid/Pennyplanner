package com.penny.planner.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.EmailVerificationRepository
import com.penny.planner.data.EmailVerificationRepositoryImpl
import com.penny.planner.data.ForgetPasswordRepository
import com.penny.planner.data.ForgetPasswordRepositoryImpl
import com.penny.planner.data.LoginRepository
import com.penny.planner.data.LoginRepositoryImpl
import com.penny.planner.data.SignUpRepository
import com.penny.planner.data.SignUpRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class OnboardingModule {

    @Singleton
    @Provides
    fun bindSignupRepository() : SignUpRepository {
        return SignUpRepositoryImpl(FirebaseAuth.getInstance())
    }

    @Singleton
    @Provides
    fun bindLoginRepository() : LoginRepository {
        return LoginRepositoryImpl(FirebaseAuth.getInstance())
    }

    @Singleton
    @Provides
    fun bindEmailVerificationRepository() : EmailVerificationRepository {
        return EmailVerificationRepositoryImpl(FirebaseAuth.getInstance())
    }

}