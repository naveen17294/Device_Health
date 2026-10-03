package com.devicehealth.assistant.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LagDao {
    @Insert
    suspend fun insertEvent(event: LagEvent)

    @Query("SELECT * FROM lag_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<LagEvent>>
}
