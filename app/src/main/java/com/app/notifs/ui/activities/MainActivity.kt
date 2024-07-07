package com.app.notifs.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.app.notifs.ui.theme.NotifsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotifsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        text = intent.extras?.let { bundleToString(it) } ?: "Empty Intent Data"
                    )
                }
            }
        }
    }

    private fun bundleToString(bundle: Bundle): String {
        val stringBuilder = StringBuilder()
        for (key in bundle.keySet()) {
            val value = bundle.getString(key)
            stringBuilder.append(key).append(": ").append(value).append("\n")
        }
        return stringBuilder.toString()
    }
}