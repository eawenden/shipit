package net.dpgmedia.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

@Serializable
data class Leaderboard(val userId: String, val userName: String?, val score: Long)

object Leaderboards : Table() {
    val userId = varchar("userId", 128)
    val userName = varchar("userName", 128).nullable()
    val score = long("score").default(0)

    override val primaryKey = PrimaryKey(userId)
}
