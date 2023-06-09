package net.dpgmedia.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
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

        get("/test/{article-short-id}") {
            val articleId = call.parameters["article-short-id"]
            val brand = call.parameters["brand"]
            val articleText = RetrieveSelectivesArticle.getArticleText(articleId, brand)

            call.respond(articleText)
        }
    }
}
