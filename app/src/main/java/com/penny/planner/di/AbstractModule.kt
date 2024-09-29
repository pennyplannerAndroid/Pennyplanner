package com.penny.planner.di

import com.penny.planner.data.repositories.implementations.DataStoreEmojiRepositoryImpl
import com.penny.planner.data.repositories.implementations.CategoryAndEmojiRepositoryImpl
import com.penny.planner.data.repositories.implementations.DataStoreBudgetRepositoryImpl
import com.penny.planner.data.repositories.interfaces.GroupRepository
import com.penny.planner.data.repositories.implementations.GroupRepositoryImpl
import com.penny.planner.data.repositories.interfaces.OnboardingRepository
import com.penny.planner.data.repositories.implementations.OnboardingRepositoryImpl
import com.penny.planner.data.repositories.interfaces.ExpenseRepository
import com.penny.planner.data.repositories.implementations.ExpenseRepositoryImpl
import com.penny.planner.data.repositories.interfaces.UserRepository
import com.penny.planner.data.repositories.implementations.UserRepositoryImpl
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.DataStoreBudgetRepository
import com.penny.planner.data.repositories.interfaces.DataStoreEmojiRepository
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
    abstract fun bindUserAndExpenseRepository(repository: ExpenseRepositoryImpl): ExpenseRepository

    @Singleton
    @Binds
    abstract fun bindOnBoardingRepository(repository: OnboardingRepositoryImpl): OnboardingRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindGroupRepository(repository: GroupRepositoryImpl): GroupRepository

    @Singleton
    @Binds
    abstract fun bindCategoryAndEmojiRepository(repository: CategoryAndEmojiRepositoryImpl): CategoryAndEmojiRepository

    @Singleton
    @Binds
    abstract fun bindEmojiDataStoreRepository(repository: DataStoreEmojiRepositoryImpl): DataStoreEmojiRepository

    @Singleton
    @Binds
    abstract fun bindBudgetDataStoreRepository(repository: DataStoreBudgetRepositoryImpl): DataStoreBudgetRepository
}