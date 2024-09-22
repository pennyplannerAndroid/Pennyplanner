package com.penny.planner.data.repositories.implementations

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.penny.planner.data.repositories.interfaces.DataStoreRepository
import com.penny.planner.helpers.Utils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {

    private val emojiFileId = stringPreferencesKey(Utils.EMOJI_FILE_ID)
    private val emojiJson = stringPreferencesKey(Utils.EMOJI_JSON)

    override suspend fun readEmojiFileId(): String? {
        return dataStore.data.map { preferences ->
            preferences[emojiFileId]
        }.first()
    }

    override suspend fun saveEmojiFileId(fileId: String) {
        dataStore.edit { category ->
            category[emojiFileId] = fileId
        }
    }

    override suspend fun readEmojiJson(): String? {
        return dataStore.data.map { preferences ->
            preferences[emojiJson]
        }.first()
    }

    override suspend fun saveEmojiJson(json: String) {
        dataStore.edit { category ->
            category[emojiJson] = json
        }
    }

}