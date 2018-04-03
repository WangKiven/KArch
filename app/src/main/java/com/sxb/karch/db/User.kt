package com.sxb.karch.db

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*

/**
 * Created by wangk on 2017/12/20.
 */
@Entity(tableName = "User")
class User(){
    @PrimaryKey
    @ColumnInfo(name = "userId")
    var id: Int = 0
    @ColumnInfo(name = "account")
    var account: String = ""
    var name: String = ""
}


@Dao
interface UserDao {
    @Query("SELECT * FROM User where userId = :userId")
    fun loadUser(userId: Int): LiveData<User>

    @Query("SELECT * FROM User")
    fun loadAllUser(): LiveData<List<User>>

    @Query("SELECT * FROM User")
    fun loadAllUser2(): DataSource.Factory<Int, User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM User")
    fun deleteAllUser()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: User)
}