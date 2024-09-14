package com.penny.planner.di

import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.implementations.GroupRepositoryImpl
import com.penny.planner.data.repositories.interfaces.OnboardingRepository
import com.penny.planner.data.repositories.implementations.OnboardingRepositoryImpl
import com.penny.planner.data.repositories.interfaces.ExpenseAndCategoryRepository
import com.penny.planner.data.repositories.implementations.ExpenseAndCategoryRepositoryImpl
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.data.repositories.implementations.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AbstractModule {

    @Singleton
    @Binds
    abstract fun bindUserAndExpenseRepository(repository: ExpenseAndCategoryRepositoryImpl): ExpenseAndCategoryRepository

    @Singleton
    @Binds
    abstract fun bindOnBoardingRepository(repository: OnboardingRepositoryImpl): OnboardingRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindGroupRepository(repository: GroupRepositoryImpl): GroupRepository

}