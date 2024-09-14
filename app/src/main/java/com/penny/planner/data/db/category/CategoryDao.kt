package com.penny.planner.data.db.category

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.penny.planner.helpers.Utils

@Dao
interface CategoryDao {

    @Query("Select * From ${Utils.CATEGORY_TABLE}")
    fun getAllCategories() : LiveData<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CategoryEntity)

}