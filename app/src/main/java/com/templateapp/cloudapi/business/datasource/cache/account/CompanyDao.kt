package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity
import com.templateapp.cloudapi.business.domain.util.Constants
import dagger.Provides

@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAndReplace(company: CompanyEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnore(company: CompanyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(company: CompanyEntity): Long

    @Query("""
        SELECT * FROM company_properties
        """)
    suspend fun getAllCompanies(): List<CompanyEntity>

}


















