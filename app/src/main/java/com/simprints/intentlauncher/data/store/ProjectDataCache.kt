package com.simprints.intentlauncher.data.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectDataCache @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.projectCache: DataStore<Preferences> by preferencesDataStore(name = "projectCache")

    private val keyProjectId = stringPreferencesKey("project_id")
    private val keyUserId = stringPreferencesKey("user_id")
    private val keyModuleId = stringPreferencesKey("module_id")
    private val keyGuid = stringPreferencesKey("guid")

    suspend fun save(projectId: String, userId: String, moduleId: String, guid: String) =
        withContext(Dispatchers.IO) {
            context.projectCache.edit {
                it[keyProjectId] = projectId
                it[keyUserId] = userId
                it[keyModuleId] = moduleId
                it[keyGuid] = guid
            }
        }

    suspend fun getProjectId(): String = getFirstValue(keyProjectId)

    suspend fun getUserId() = getFirstValue(keyUserId)

    suspend fun getModuleId() = getFirstValue(keyModuleId)

    suspend fun getGuid() = getFirstValue(keyGuid)

    private suspend fun getFirstValue(id: Preferences.Key<String>) = context.projectCache.data
        .map { it[id] }
        .flowOn(Dispatchers.IO)
        .firstOrNull()
        .orEmpty()

    suspend fun clear() = withContext(Dispatchers.IO) {
        context.projectCache.edit { it.clear() }
    }

    private val keyCustomAction = stringPreferencesKey("custom_action")
    private val keyCustomExtras = stringPreferencesKey("custom_extras")

    suspend fun saveCustomIntent(action: String, extras: String) = withContext(Dispatchers.IO) {
        context.projectCache.edit {
            it[keyCustomAction] = action
            it[keyCustomExtras] = extras
        }
    }

    suspend fun getCustomAction(): String = getFirstValue(keyCustomAction)

    suspend fun getCustomExtras() = getFirstValue(keyCustomExtras)

    suspend fun clearCustomIntent() = withContext(Dispatchers.IO) {
        context.projectCache.edit { it.clear() }
    }
}
