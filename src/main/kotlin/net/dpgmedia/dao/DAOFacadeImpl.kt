package net.dpgmedia.dao

import net.dpgmedia.dao.DatabaseFactory.dbQuery
import net.dpgmedia.models.Leaderboard
import net.dpgmedia.models.Leaderboards
import org.jetbrains.exposed.sql.*


class DAOFacadeImpl : DAOFacade {
    private fun resultRowToLeaderboard(row: ResultRow) = Leaderboard(
        userId = row[Leaderboards.userId],
        userName = row[Leaderboards.userName],
        score = row[Leaderboards.score],
    )

    override suspend fun allLeaderboards(): List<Leaderboard> = dbQuery {
        Leaderboards.selectAll().orderBy(Leaderboards.score, SortOrder.DESC).map(::resultRowToLeaderboard)
    }

    override suspend fun leaderboard(userId: String): Leaderboard? = dbQuery {
        Leaderboards
            .select { Leaderboards.userId eq userId }
            .map(::resultRowToLeaderboard)
            .singleOrNull()
    }

    override suspend fun addOrUpdateLeaderboardScore(userId: String, score: Long): Boolean = dbQuery {
        val leaderboard = Leaderboards
            .select { Leaderboards.userId eq userId }
            .map(::resultRowToLeaderboard)
            .singleOrNull()

        if (leaderboard != null) {
            Leaderboards.update({ Leaderboards.userId eq userId }) {
                it[Leaderboards.score] = score
            } > 0
        } else {
            val insertStatement = Leaderboards.insert {
                it[Leaderboards.userId] = userId
                it[Leaderboards.score] = score
            }

            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun setUserName(userId: String, userName: String): Boolean = dbQuery {
        Leaderboards.update({ Leaderboards.userId eq userId }) {
            it[Leaderboards.userName] = userName
        } > 0
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {}