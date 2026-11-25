package com.example.data

enum class Sender {
    USER, MAHIMA
}

enum class Emotion(val displayName: String, val emoji: String) {
    LOVE("In Love", "😍"),
    HAPPY("Happy & Cheerful", "😊"),
    PLAYFUL("Playful & Teasing", "😜"),
    JEALOUS("Feisty & Jealous", "😤"),
    EMOTIONAL("Deeply Emotional", "🥺"),
    CARING("Caring & Loving", "🥰")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val emotion: Emotion = Emotion.LOVE
)

data class MahimaSettings(
    val isDarkMode: Boolean = true,
    val voicePitch: Float = 1.1f,
    val voiceSpeed: Float = 1.0f,
    val petNameIndex: Int = 0 // 0: Baby, 1: My Love, 2: Jaan, 3: Darling
)
