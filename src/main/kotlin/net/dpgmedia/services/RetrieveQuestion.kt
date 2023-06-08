package net.dpgmedia.services

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.util.logging.*
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
    @OptIn(BetaOpenAI::class)
    suspend fun getQuestion(articleText: String): Question {
        val request = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = "Create a multiple choice question in dutch for the following article and give letter of the correct answer:\n $articleText",
                )
            )
        )

        val response = openai.chatCompletion(request).choices.first().message?.content?.split("\n")

        logger.info("ChatGPT Response: $response")

//        val response = arrayOf(
//            "Wat biedt Stichting VO-content gratis aan herkansers aan om hen te helpen zich voor te bereiden op het eindexamen?",
//            "A) Een examentrainer",
//            "B) Een gratis examen",
//            "C) Een herkansingsoptie voor een niet-kernvak",
//            "D) Een extra tijdvak voor herkansingen",
//            "Antwoord: A) Een examentrainer"
//        )
        val correctAnswer = response?.last()?.split(": ")?.get(1)?.first().toString()
        val answers = response?.drop(1)?.filter { it.length > 1 }?.dropLast(1)?.map {
            val parts = it.split(". ", ") ")

            logger.info("Answer parts: $parts")

            Answer(
                id = parts.first(),
                label = parts.get(1) ?: "",
                correct = parts.first() == correctAnswer
            )
        }

        return Question(question = response?.first() ?: "", answers = answers.orEmpty())
    }
}
