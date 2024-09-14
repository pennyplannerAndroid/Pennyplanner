package com.penny.planner.di

import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.repositories.OnboardingRepository
import com.penny.planner.data.repositories.OnboardingRepositoryImpl
import com.penny.planner.data.repositories.UserAndExpenseRepository
import com.penny.planner.data.repositories.UserAndExpenseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractModule {
    @Binds
    abstract fun bindUserAndExpenseRepository(repository: UserAndExpenseRepositoryImpl): UserAndExpenseRepository
}