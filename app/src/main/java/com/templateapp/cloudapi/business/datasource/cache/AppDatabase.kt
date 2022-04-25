package com.templateapp.cloudapi.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.templateapp.cloudapi.business.datasource.cache.account.AccountDao
import com.templateapp.cloudapi.business.datasource.cache.account.AccountEntity
import com.templateapp.cloudapi.business.datasource.cache.account.RoleDao
import com.templateapp.cloudapi.business.datasource.cache.account.RoleEntity
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenDao
import com.templateapp.cloudapi.business.datasource.cache.auth.AuthTokenEntity
import com.templateapp.cloudapi.business.datasource.cache.task.TaskDao
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity

@Database(entities = [AuthTokenEntity::class, AccountEntity::class, TaskEntity::class], version = 1)
@TypeConverters(RoleEntity::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountDao

    abstract fun getTaskDao(): TaskDao

    abstract fun getRoleDao(): RoleDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }


}








