package com.templateapp.cloudapi.business.datasource.cache.account

import androidx.room.*
import com.templateapp.cloudapi.business.domain.models.Account
import com.templateapp.cloudapi.business.domain.models.Company
import com.templateapp.cloudapi.business.domain.models.Role

@Entity(
    tableName = "account_properties",
)
data class AccountEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "firstName")
    val firstName: String,

    @ColumnInfo(name = "lastName")
    val lastName: String,

    @ColumnInfo(name = "age")
    val age: Int,

    @ColumnInfo(name = "createdAt")
    val createdAt: String,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: String,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean,

    @ColumnInfo(name = "userCreatedSequence")
    val userCreatedSequence: Int,

    @ColumnInfo(name = "__v")
    val __v: Int,

    @ColumnInfo(name = "role")
    @TypeConverters(RoleConverter::class)
    val role: Role,


    @ColumnInfo(name = "company")
    @TypeConverters(CompanyConverter::class)
    val company: Company?

    )

fun AccountEntity.toAccount(): Account {
    return Account(
        _id = _id,
        email = email,
        username = username,
        firstName = firstName,
        lastName = lastName,
        company = company,
        age = age,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userCreatedSequence = userCreatedSequence,
        __v = __v,
        role = role,
        enabled = enabled,
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        _id = _id,
        email = email,
        username = username,
        firstName = firstName,
        lastName = lastName,
        company = company,
        age = age,
        createdAt = createdAt,
        updatedAt = updatedAt,
        userCreatedSequence = userCreatedSequence,
        __v = __v,
        role = role,
        enabled = enabled
    )
}