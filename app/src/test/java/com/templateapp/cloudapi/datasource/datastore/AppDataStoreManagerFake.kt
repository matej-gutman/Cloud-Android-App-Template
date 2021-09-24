package com.templateapp.cloudapi.datasource.datastore

import com.templateapp.cloudapi.business.datasource.datastore.AppDataStore

class AppDataStoreManagerFake: AppDataStore {

    private val datastore: MutableMap<String, String> = mutableMapOf()

    override suspend fun setValue(key: String, value: String) {
        datastore[key] = value
    }

    override suspend fun readValue(key: String): String? {
        return datastore[key]
    }
}