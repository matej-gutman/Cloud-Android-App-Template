package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity
import com.templateapp.cloudapi.business.domain.util.Constants
import dagger.Provides

@Dao
interface RoleDao {
    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(role: RoleEntity): Long*/


   /* @Query("""
        SELECT * FROM role_properties
        """)
    suspend fun getAllRoles(
    ): List<RoleEntity>*/

}


















