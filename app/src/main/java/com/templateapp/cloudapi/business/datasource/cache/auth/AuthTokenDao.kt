package com.templateapp.cloudapi.business.datasource.cache.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthTokenEntity): Long

    @Query("DELETE FROM auth_token")
    suspend fun clearTokens()

    @Query("SELECT * FROM auth_token WHERE account_id = :id")
    suspend fun searchById(id: String): AuthTokenEntity?

}


















