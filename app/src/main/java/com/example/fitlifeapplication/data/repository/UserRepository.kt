package com.example.fitlifeapplication.data.repository

import com.example.fitlifeapplication.data.db.UserDao
import com.example.fitlifeapplication.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    fun getUserById(userId: Long): Flow<User?> = userDao.getUserById(userId)

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }
}
