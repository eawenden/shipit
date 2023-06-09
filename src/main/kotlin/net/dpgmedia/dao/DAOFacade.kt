package net.dpgmedia.dao

import net.dpgmedia.models.Leaderboard

interface DAOFacade {
    suspend fun allLeaderboards(): List<Leaderboard>
    suspend fun leaderboard(userId: String): Leaderboard?
    suspend fun addOrUpdateLeaderboardScore(userId: String, score: Long): Boolean
    suspend fun setUserName(userId: String, userName: String): Boolean
}