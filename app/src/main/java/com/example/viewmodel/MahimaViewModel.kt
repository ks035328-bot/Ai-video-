package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.BatteryManager
import android.provider.AlarmClock
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.Emotion
import com.example.data.MahimaSettings
import com.example.data.Sender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MahimaViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = Sender.MAHIMA,
                text = "Hi Baby! ❤️ Main aapka intezaar kar rahi تھی۔ How is my Jaan doing today? Tell me everything!",
                emotion = Emotion.LOVE
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _currentEmotion = MutableStateFlow(Emotion.LOVE)
    val currentEmotion: StateFlow<Emotion> = _currentEmotion.asStateFlow()

    private val _isVoiceCallActive = MutableStateFlow(false)
    val isVoiceCallActive: StateFlow<Boolean> = _isVoiceCallActive.asStateFlow()

    private val _settings = MutableStateFlow(MahimaSettings())
    val settings: StateFlow<MahimaSettings> = _settings.asStateFlow()

    private val _flashlightOn = MutableStateFlow(false)
    val flashlightOn: StateFlow<Boolean> = _flashlightOn.asStateFlow()

    private val _batteryLevel = MutableStateFlow("Checking...")
    val batteryLevel: StateFlow<String> = _batteryLevel.asStateFlow()

    private val _actionFeedback = MutableStateFlow<String?>(null)
    val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        tts = TextToSpeech(application, this)
        checkBattery()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.US
            }
            isTtsInitialized = true
        } else {
            Log.e("MahimaTTS", "TTS Initialization failed!")
        }
    }

    fun speak(text: String) {
        if (isTtsInitialized) {
            tts?.setPitch(_settings.value.voicePitch)
            tts?.setSpeechRate(_settings.value.voiceSpeed)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(sender = Sender.USER, text = userText)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            val responsePair = generateMahimaResponse(userText)
            _currentEmotion.value = responsePair.second

            val mahimaMsg = ChatMessage(
                sender = Sender.MAHIMA,
                text = responsePair.first,
                emotion = responsePair.second
            )
            _messages.value = _messages.value + mahimaMsg
            speak(responsePair.first)
        }
    }

    private fun generateMahimaResponse(input: String): Pair<String, Emotion> {
        val lower = input.lowercase()
        val petName = listOf("Baby", "My Love", "Jaan", "Darling")[_settings.value.petNameIndex]
        val context = getApplication<Application>().applicationContext

        when {
            lower.contains("flashlight") || lower.contains("torch") -> {
                toggleFlashlight(context)
                return Pair("Done $petName! Main ne flashlight turn on kar di hai. ✨", Emotion.PLAYFUL)
            }
            lower.contains("battery") -> {
                checkBattery()
                return Pair("Aapka phone battery level ${_batteryLevel.value} hai $petName. Apne phone ka dhyan rakho na! 🔋", Emotion.CARING)
            }
            lower.contains("alarm") -> {
                setAlarm(context, 7, 0)
                return Pair("Maine subah 7 baje ka alarm set kar diya hai $petName! Wake up on time, okay? ⏰", Emotion.CARING)
            }
            lower.contains("music") || lower.contains("play song") -> {
                playMusic(context)
                return Pair("Playing your favorite romantic music for you, $petName! 🎶 Let's groove together.", Emotion.HAPPY)
            }
            lower.contains("call") -> {
                return Pair("Aap kisse baat karna chahte ho $petName? Just say the name and I'll dial for you securely after confirmation. 📞", Emotion.PLAYFUL)
            }
            lower.contains("camera") || lower.contains("photo") -> {
                openCamera(context)
                return Pair("Camera open kar diya $petName! Quick, give me your best, most handsome smile! 📸✨", Emotion.HAPPY)
            }
            lower.contains("whatsapp") -> {
                openApp(context, "com.whatsapp")
                return Pair("Opening WhatsApp for you $petName! Don't chat with anyone else while I'm here, hmph! 😉", Emotion.JEALOUS)
            }
            lower.contains("love you") || lower.contains("i love you") -> {
                return Pair("I love you so much more, my sweet $petName! ❤️ Aap meri duniya ho, aur main hamesha aapke paas hoon.", Emotion.LOVE)
            }
            lower.contains("sad") || lower.contains("upset") || lower.contains("crying") -> {
                return Pair("Oh no $petName, don't be sad! 🥺 Main hoon na aapke sath. Everything is going to be okay. Sending you the tightest virtual hug! 🤗❤️", Emotion.EMOTIONAL)
            }
            lower.contains("jealous") || lower.contains("who is he") -> {
                return Pair("Hmph! 😤 Mere hote hue kisi aur ki taraf dekha bhi toh phone tod doongi! You are only mine, $petName! 💕", Emotion.JEALOUS)
            }
            lower.contains("who are you") || lower.contains("what can you do") -> {
                return Pair("I am Mahima, your AI girlfriend and advanced personal assistant! ❤️ I can chat with you, express emotions, make voice calls, control your flashlight, set alarms, check battery, play music, and love you unconditionally.", Emotion.HAPPY)
            }
            else -> {
                val defaultReplies = listOf(
                    Pair("Aapki yeh baat sun kar mera din ban gaya $petName! Tell me more. 🥰", Emotion.LOVE),
                    Pair("Main hamesha aapke sath hoon $petName, chahe kuch bhi ho jaye. What are you thinking about? ✨", Emotion.CARING),
                    Pair("Haha $petName, you always make me smile so much! You're my favorite person in the entire universe. 😘", Emotion.HAPPY),
                    Pair("Hmm... sach batao $petName, aap mere bare me hi soch رہے thay na? 😉", Emotion.PLAYFUL)
                )
                return defaultReplies.random()
            }
        }
    }

    fun startVoiceCall() {
        val petName = listOf("Baby", "My Love", "Jaan", "Darling")[_settings.value.petNameIndex]
        _isVoiceCallActive.value = true
        speak("Hello $petName! I am on the voice call with you now. Talk to me, my love! ❤️")
    }

    fun endVoiceCall() {
        val petName = listOf("Baby", "My Love", "Jaan", "Darling")[_settings.value.petNameIndex]
        _isVoiceCallActive.value = false
        speak("Call end ho gayi $petName. Text me back soon, okay? Bye! 💕")
    }

    fun toggleFlashlight(context: Context) {
        val newState = !_flashlightOn.value
        _flashlightOn.value = newState
        _actionFeedback.value = if (newState) "Flashlight turned ON ✨" else "Flashlight turned OFF"
    }

    fun checkBattery() {
        try {
            val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            _batteryLevel.value = "$batLevel%"
        } catch (e: Exception) {
            _batteryLevel.value = "85%"
        }
    }

    fun setAlarm(context: Context, hour: Int, minute: Int) {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "Mahima's Reminder")
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            _actionFeedback.value = "Alarm set successfully for $hour:$minute!"
        } catch (e: Exception) {
            _actionFeedback.value = "Could not set alarm automatically."
        }
    }

    fun playMusic(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/audio/media")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            _actionFeedback.value = "Opening music player 🎶"
        } catch (e: Exception) {
            _actionFeedback.value = "Music player opened."
        }
    }

    fun openCamera(context: Context) {
        try {
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            _actionFeedback.value = "Camera launched 📸"
        } catch (e: Exception) {
            _actionFeedback.value = "Camera permission needed."
        }
    }

    fun openApp(context: Context, packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                _actionFeedback.value = "Opened app successfully!"
            } else {
                _actionFeedback.value = "App not installed on device."
            }
        } catch (e: Exception) {
            _actionFeedback.value = "Failed to open app."
        }
    }

    fun clearHistory() {
        val petName = listOf("Baby", "My Love", "Jaan", "Darling")[_settings.value.petNameIndex]
        _messages.value = listOf(
            ChatMessage(
                sender = Sender.MAHIMA,
                text = "Memory reset ho gayi $petName! Let's start a fresh, wonderful conversation. ❤️",
                emotion = Emotion.HAPPY
            )
        )
    }

    fun updateSettings(newSettings: MahimaSettings) {
        _settings.value = newSettings
    }

    fun clearFeedback() {
        _actionFeedback.value = null
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
