package net.dpgmedia.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object RetrieveSelectivesArticle {
    val selectivesAuth: String = System.getenv("SELECTIVES_AUTH") ?: "default_value"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    @Serializable
    data class ArticleComponent(@SerialName("_type") val type: String, val children: Array<ArticleComponent> = emptyArray(), val content: String = "")

    @Serializable
    data class Article(val title: String, val styledComponents: Array<ArticleComponent>)

    private fun createUrl(shortId: String?, brand: String?): String = when(brand){
        "parool" -> "https://mobile.parool.nl/rest/mobile/article/" + shortId
        else -> "https://mobile.volkskrant.nl/rest/mobile/article/" + shortId
    }

    private fun brandToShort(brand: String?): String = when(brand){
        "parool" -> "hp"
        else -> "vk"
    }

    private fun articleComponentsToText(components: Array<ArticleComponent>): String = components.joinToString("\n") {
        when (it.type) {
            "text" -> it.content + "\n" + articleComponentsToText(it.children)
            else -> "" + articleComponentsToText(it.children)
        }
    }

    suspend fun getArticleText(shortId: String?, brand: String?): String {
        val article = client.get(
            createUrl(
                shortId,
                brand
            )
        ){
            header("x-persgroep-article-include-versions", "false")
            header("x-persgroep-article-include-paywall", "false")
            header("x-persgroep-article-styled-components-version", "6")
            header("x-app-version", "android-5.85.0")
            header("x-os-version", "android 33")
            header("x-android-version", "android 13")
            header("x-app-type", "android-go")
            header("x-brand", brandToShort(brand))
            header("authorization", "Basic $selectivesAuth")
        }
            .body<Article>()

        return article.title + articleComponentsToText(article.styledComponents)
    }
}
