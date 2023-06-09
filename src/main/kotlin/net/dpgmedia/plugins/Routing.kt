package net.dpgmedia.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import net.dpgmedia.dao.dao
import net.dpgmedia.services.RetrieveFreemiumArticle
import net.dpgmedia.services.RetrieveQuestion
import net.dpgmedia.services.RetrieveSelectivesArticle

fun Application.configureRouting() {
    routing {
        get("/question/{article-short-id}") {
            val articleId = call.parameters["article-short-id"]
            val brand = call.parameters["brand"]
            val articleText = when(brand) {
                "ad", "hln" -> RetrieveFreemiumArticle.getArticleText(articleId, brand)
                else -> RetrieveSelectivesArticle.getArticleText(articleId, brand)
            }
            val question = RetrieveQuestion.getQuestion(articleText)

            call.application.environment.log.debug("Article ID: $articleId Brand: $brand")
            call.application.environment.log.debug("Article Content: $articleText")
            call.application.environment.log.debug("Question: $question")
            call.respond(question)
        }

        get("/leaderboard") {
            val leaderboards = ArrayList(dao.allLeaderboards())

            call.respond(leaderboards)
        }

        post("/leaderboard") {
            @Serializable
            data class Input(val userId: String, val points: Long)

            val leaderboard: Input = call.receive<Input>()

            call.application.environment.log.debug("Leaderboard: $leaderboard")

            dao.addOrUpdateLeaderboardScore(leaderboard.userId, leaderboard.points)

            call.respond(HttpStatusCode.OK)
        }

        put("/leaderboard/{userId}/{userName}") {
            val userId = call.parameters["userId"]
            val userName = call.parameters["userName"]

            dao.setUserName(userId!!, userName!!)

            call.respond(HttpStatusCode.OK)
        }
    }
}
