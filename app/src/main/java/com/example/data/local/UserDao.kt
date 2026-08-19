package com.example.data.local

import androidx.room.*
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts ORDER BY createdAtMillis DESC")
    fun getAllUsersFlow(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE role = :role ORDER BY fullName ASC")
    fun getUsersByRoleFlow(role: UserRole): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE (userId = :idOrEmail OR email = :idOrEmail) AND password = :password LIMIT 1")
    suspend fun authenticate(idOrEmail: String, password: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE (userId = :idOrEmail OR email = :idOrEmail) AND password = :password AND role = :role LIMIT 1")
    suspend fun authenticateWithRole(idOrEmail: String, password: String, role: UserRole): UserAccount?

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun getUserCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Query("DELETE FROM user_accounts WHERE userId = :userId")
    suspend fun deleteUserById(userId: String)

    @Query("DELETE FROM user_accounts")
    suspend fun clearAllUsers()
}
