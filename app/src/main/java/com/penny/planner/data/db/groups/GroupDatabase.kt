package com.penny.planner.data.db.groups

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.penny.planner.helpers.ArrayListConverter

@TypeConverters(ArrayListConverter::class)
@Database(entities = [GroupEntity::class], version = 1)
abstract class GroupDatabase: RoomDatabase() {
    abstract fun getGroupDao(): GroupDao
}