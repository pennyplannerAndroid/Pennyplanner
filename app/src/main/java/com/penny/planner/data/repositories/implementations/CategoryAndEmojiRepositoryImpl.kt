package com.penny.planner.data.repositories.implementations

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.penny.planner.data.db.category.CategoryDao
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.subcategory.SubCategoryDao
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.network.RetrofitInstance
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.DataStoreRepository
import com.penny.planner.helpers.Utils
import com.penny.planner.models.EmojiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryAndEmojiRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val subCategoryDao: SubCategoryDao,
    private val dataStoreRepository: DataStoreRepository
): CategoryAndEmojiRepository {

    private val generalDataRef = FirebaseDatabase.getInstance().getReference(Utils.GENERAL_DATA)
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private var emojiMap = mutableMapOf<String, String>()
    private var categoryMap = mutableMapOf<String, List<String>>()

    override fun checkServerAndUpdateCategory() {
        if (FirebaseAuth.getInstance().currentUser != null)
            updateRecommendedCategory()
    }

    private fun updateRecommendedCategory() {
        scope.launch {
            try {
                updateModel(dataStoreRepository.readEmojiJson())
                val emojiFIleID = generalDataRef.child(Utils.EMOJI_FILE_ID).get().await()
                if (emojiFIleID.value.toString() != dataStoreRepository.readEmojiFileId()) {
                    fetchEmojiJson(emojiFIleID.value.toString())
                }
            } catch (e: Exception){
                Log.d("CategoryAndEmojiRepository:: ", e.toString())
            }
        }
    }

     private suspend fun fetchEmojiJson(fileId: String) {
         val response = RetrofitInstance.retrofitApi.getEmojiJsonFromDrive(fileId)
         if (response.isSuccessful) {
             val json = response.body().toString()
             dataStoreRepository.saveEmojiFileId(fileId)
             dataStoreRepository.saveEmojiJson(json)
             updateModel(json)
         }
    }

    private fun updateModel(json: String?) {
        if (json != null) {
            val gson = Gson()
            val emojiData = gson.fromJson(json, EmojiModel::class.java)
            categoryMap = emojiData.recommendedCategories as MutableMap<String, List<String>>
            emojiMap = emojiData.emoji as MutableMap<String, String>
        }
    }

    override fun getAllEmoji(): List<String> {
        return emojiMap.values.toList()
    }

    override fun getAllRecommendedCategories(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for(category in categoryMap.keys) {
            map[category] = emojiMap[category].toString()
        }
        return map
    }

    override fun getRecommendedSubCategory(category: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val subCategories = categoryMap[category]
        if (!subCategories.isNullOrEmpty()) {
            for(subCategory in subCategories) {
                map[subCategory] = emojiMap[subCategory].toString()
            }
        }
        return map
    }

    override suspend fun getAllSavedCategories(): LiveData<List<CategoryEntity>> = categoryDao.getAllCategories()

    override suspend fun getAllSavedSubCategories(categoryName: String): List<SubCategoryEntity> = subCategoryDao.getAllSubCategories(categoryName)

    override suspend fun addCategory(entity: CategoryEntity) {
        categoryDao.insert(entity)
    }

    override suspend fun addSubCategory(entity: SubCategoryEntity) {
        subCategoryDao.addSubCategory(entity)
    }

}