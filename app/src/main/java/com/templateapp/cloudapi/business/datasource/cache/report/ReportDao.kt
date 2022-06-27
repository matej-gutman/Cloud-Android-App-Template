package com.templateapp.cloudapi.business.datasource.cache.report

import androidx.room.*
import com.templateapp.cloudapi.business.datasource.cache.task.TaskEntity
import com.templateapp.cloudapi.business.datasource.network.responseObjects.Report
import com.templateapp.cloudapi.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: ReportEntity): Long


    @Query("""
        SELECT * FROM report 
        LIMIT (:page * :pageSize)
        """)
    suspend fun getAllReports(
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<ReportEntity>


    @Query("DELETE FROM report WHERE _id = :id")
    suspend fun deleteReport(id: String)
}











