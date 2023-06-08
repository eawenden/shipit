package net.dpgmedia.services

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object RetrieveFreemiumArticle {
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
    data class Article(val articleDetail: ArticleDetail)

    @Serializable
    data class ArticleDetail(val title: String, val components: Array<ArticleComponent>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ArticleDetail

            return title == other.title
        }

        override fun hashCode(): Int {
            return title.hashCode()
        }
    }

    @Serializable
    data class ArticleComponent(val componentType: String, val text: String = "")

    private fun createUrl(shortId: String?, brand: String?): String = when(brand){
        "hln" -> "https://mobileapi.hln.be/mobile/article/" + shortId
        else -> "https://mobileapi.ad.nl/mobile/article/" + shortId
    }

    suspend fun getArticleText(shortId: String?, brand: String?): String {
        val article = client.get(createUrl(shortId, brand)).body<Article>().articleDetail

        return article.title + ' ' + article.components.joinToString(" ") { it.text }
    }
}