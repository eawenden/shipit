package net.dpgmedia.services

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.dpgmedia.models.Answer
import net.dpgmedia.models.Question
import kotlin.time.Duration.Companion.seconds

object RetrieveQuestion {
    private val logger = KtorSimpleLogger("net.dpgmedia.services.RetrieveQuestion")
    private val openaiKey: String = System.getenv("OPENAI_KEY") ?: "default_value"
    private val openai = OpenAI(
        token = openaiKey,
        timeout = Timeout(socket = 60.seconds),
    )

    @Serializable
    data class OpenAIOption(val id: String, val label: String)

    @Serializable
    data class OpenAIQuestion(val question: String, val options: Array<OpenAIOption>, val correct_answer: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as OpenAIQuestion

            return question == other.question
        }

        override fun hashCode(): Int {
            return question.hashCode()
        }
    }

    @OptIn(BetaOpenAI::class)
    suspend fun getQuestion(articleText: String): Question {
        val prompt: String = """
            Create a multiple choice question for the article below.
            Do it in Dutch.
            Do not include any explanations, only provide a  RFC8259 compliant JSON response  following this format without deviation.
            {
              "question": "the question",
              "options": [{
                "id": "the letter associated with the answer",
                "label": "the answer"
              }],
              "correct_answer": "the letter associated with the correct answer",
            }
            The article:
            $articleText
        """.trimIndent()

        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt,
                )
            )
        )

        val rawResponse = openai.chatCompletion(request).choices.first().message?.content
        logger.info("ChatGPT Raw Response: $rawResponse")

        val response = rawResponse?.let { Json.decodeFromString<OpenAIQuestion>(it) }
        logger.info("ChatGPT Parsed Response: $response")

        val question = response?.let { Question(
            question = it.question,
            answers = it.options.map { option -> Answer(
                id = option.id,
                label = option.label,
                correct = option.id == it.correct_answer
            )}
        )}

        return question ?: Question(question = "", answers = emptyList())
    }
}
