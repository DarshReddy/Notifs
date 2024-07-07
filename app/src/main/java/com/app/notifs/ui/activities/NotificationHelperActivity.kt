package com.app.notifs.ui.activities

import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.app.notifs.R
import com.app.notifs.helpers.NotificationHelper
import com.app.notifs.helpers.NotificationHelper.Companion.ACTION_ACCEPT_CALL
import com.app.notifs.helpers.NotificationHelper.Companion.ACTION_FULL_SCREEN
import com.app.notifs.helpers.NotificationHelper.Companion.ACTION_REJECT_CALL
import com.app.notifs.helpers.NotificationHelper.Companion.BODY
import com.app.notifs.helpers.NotificationHelper.Companion.TITLE
import com.app.notifs.helpers.turnScreenOnAndKeyguardOff
import com.app.notifs.ui.theme.NotifsTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationHelperActivity : ComponentActivity() {

    companion object {
        const val RINGER_MILLIS = 30000L
    }

    private val ringtoneManger by lazy {
        RingtoneManager.getRingtone(
            this,
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL)
        )?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                isLooping = true
        }
    }

    private var audioManager: AudioManager? = null

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val audioFocusRequest: AudioFocusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS -> ringtoneManger?.stop()
                    AudioManager.AUDIOFOCUS_GAIN -> ringtoneManger?.stop()
                }
            }
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (intent?.action) {
            ACTION_ACCEPT_CALL -> {
                Toast.makeText(this, "Call accepted", Toast.LENGTH_SHORT).show()
                NotificationHelper(this).cancelNotification()
                navigateToMainActivity()
            }

            ACTION_REJECT_CALL -> {
                Toast.makeText(this, "Call rejected", Toast.LENGTH_SHORT).show()
                NotificationHelper(this).cancelNotification()
                finish()
            }

            ACTION_FULL_SCREEN -> {
                turnScreenOnAndKeyguardOff()
                audioManager = ContextCompat.getSystemService(this, AudioManager::class.java)
                lifecycleScope.launch {
                    val result = audioManager?.requestAudioFocus(audioFocusRequest)
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        ringtoneManger?.play()
                    }
                    delay(RINGER_MILLIS)
                    ringtoneManger?.stop()
                    audioManager?.abandonAudioFocusRequest(audioFocusRequest)
                    finish()
                }

                setContent {
                    NotifsTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .background(Color.Black),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(innerPadding),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = intent.extras?.getString(TITLE)
                                            ?: stringResource(R.string.no_title),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = intent.extras?.getString(BODY)
                                            ?: stringResource(R.string.no_body),
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Button(
                                        onClick = { navigateToMainActivity() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(text = stringResource(R.string.accept))
                                    }
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Button(
                                        modifier = Modifier,
                                        onClick = { finish() },
                                        border = BorderStroke(2.dp, Color.White),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(text = stringResource(R.string.decline))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            intent.extras?.let { putExtras(it) }
        })
        finish()
    }

    override fun onStop() {
        ringtoneManger?.stop()
        audioManager?.abandonAudioFocusRequest(audioFocusRequest)
        super.onStop()
    }
}