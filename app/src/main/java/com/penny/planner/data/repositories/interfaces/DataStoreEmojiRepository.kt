package com.penny.planner.data.repositories.interfaces

interface DataStoreEmojiRepository {
    suspend fun readEmojiFileId(): String?
    suspend fun saveEmojiFileId(fileId: String)
    suspend fun readEmojiJson(): String?
    suspend fun saveEmojiJson(json: String)
}