package net.dpgmedia.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import net.dpgmedia.services.RetrieveFreemiumArticle
import net.dpgmedia.services.RetrieveQuestion

fun Application.configureRouting() {
    routing {
        get("/question/{article-short-id}") {
            val articleId = call.parameters["article-short-id"]
            val brand = call.parameters["brand"]
            val articleText = RetrieveFreemiumArticle.getArticleText(articleId, brand)
            val question = RetrieveQuestion.getQuestion(articleText)

            call.application.environment.log.info("Article ID: $articleId Brand: $brand")
            call.application.environment.log.info("Article Content: $articleText")
            call.application.environment.log.info("Question: $question")
            call.respond(question)
        }
    }
}
