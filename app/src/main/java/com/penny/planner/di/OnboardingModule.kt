package com.penny.planner.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.MyApplication
import com.penny.planner.data.repositories.OnboardingRepository
import com.penny.planner.data.repositories.OnboardingRepositoryImpl
import com.penny.planner.data.repositories.UserAndExpenseRepository
import com.penny.planner.data.repositories.UserAndExpenseRepositoryImpl
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
    fun bindLoginRepository() : OnboardingRepository {
        return OnboardingRepositoryImpl(FirebaseAuth.getInstance())
    }

    @Singleton
    @Provides
    fun bindUserAndExpenseRepository() : UserAndExpenseRepository {
        return UserAndExpenseRepositoryImpl(FirebaseAuth.getInstance())
    }

}