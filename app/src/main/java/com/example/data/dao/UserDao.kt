package com.example.data.dao

import androidx.room.*
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "local_user"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfileOnce(id: String = "local_user"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profiles WHERE id = :id")
    suspend fun deleteProfile(id: String)
}
