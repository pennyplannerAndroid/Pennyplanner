package com.penny.planner.data.db.friends

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UsersEntity::class], version = 1)
abstract class UsersDatabase: RoomDatabase() {
    abstract fun getFriendDao(): UsersDao
}