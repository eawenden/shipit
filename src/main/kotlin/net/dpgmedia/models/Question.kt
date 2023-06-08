package net.dpgmedia.models

import kotlinx.serialization.Serializable

@Serializable
data class Answer(val id: String, val label: String, val correct: Boolean)
@Serializable
data class Question(val question: String, val answers: List<Answer>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question

        return question == other.question
    }

    override fun hashCode(): Int {
        return question.hashCode()
    }
}