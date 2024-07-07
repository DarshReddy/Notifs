package com.app.notifs.services

import android.content.Context
import android.util.Log
import com.app.notifs.constants.AppConstants.FCM_TOKEN
import com.app.notifs.constants.AppConstants.SHARED_PREFS
import com.app.notifs.helpers.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.data.isNotEmpty()) {
            NotificationHelper(this).showNotification(message.data)
        }
    }

    override fun onNewToken(token: String) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(FCM_TOKEN, token).apply()
        Log.d(this.javaClass.simpleName, "onNewToken: $token")
    }
}