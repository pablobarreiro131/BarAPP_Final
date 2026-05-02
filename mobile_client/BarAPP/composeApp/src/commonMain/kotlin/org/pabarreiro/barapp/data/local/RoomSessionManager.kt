package org.pabarreiro.barapp.data.local

import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.pabarreiro.barapp.data.local.dao.BarDao
import org.pabarreiro.barapp.data.local.entity.SessionEntity

class RoomSessionManager(
    private val barDao: BarDao
) : SessionManager {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveSession(session: UserSession) {
        val jsonString = json.encodeToString(session)
        barDao.insertSession(SessionEntity(sessionJson = jsonString))
    }

    override suspend fun loadSession(): UserSession? {
        val entity = barDao.getSession()
        return entity?.let {
            try {
                json.decodeFromString<UserSession>(it.sessionJson)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun deleteSession() {
        barDao.deleteSession()
    }
}
