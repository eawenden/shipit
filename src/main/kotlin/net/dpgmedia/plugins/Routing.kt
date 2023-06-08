package net.dpgmedia.plugins

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import net.dpgmedia.services.RetrieveFreemiumArticle

fun Application.configureRouting() {
    routing {
        get("/question/{article-short-id}") {
            call.application.environment.log.info("Article ID: " + call.parameters["article-short-id"] + " Brand: " + call.parameters["brand"])
            call.application.environment.log.info("Article Content: " + RetrieveFreemiumArticle.getArticleText(call.parameters["article-short-id"], call.request.queryParameters["brand"]))
            call.respondText("""{
                      "question": "Hoe lang blijft de online examentrainer ExamenKracht toegankelijk voor herkansers?",
                      "answers": [
                        {
                          "id": "A",
                          "label": "Tot het einde van het schooljaar.",
                          "correct": false
                        },
                        {
                          "id": "B",
                          "label": "Gedurende de komende weken.",
                          "correct": false
                        },
                        {
                          "id": "C",
                          "label": "Tot de herexamens voorbij zijn.",
                          "correct": true
                        },
                        {
                          "id": "D",
                          "label": "Voor onbepaalde tijd.",
                          "correct": false
                        }
                      ]
                    }""", ContentType.Application.Json)
        }
    }
}
