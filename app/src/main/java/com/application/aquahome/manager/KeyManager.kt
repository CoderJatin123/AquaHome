package com.application.aquahome.manager

import android.app.NotificationManager

object KeyManager {
    //Shared preferences keys
    const val BOARDING_KEY="KEY_FIRST_TIME"
    const val AUTH_KEY="AUTH_KEY"
    const val SAVE_USER_KEY="SAVE_USER_KEY"

    //Notification Keys
    const val NOTIFICATION_CHANNEL_ID="121"
    const val CHANNEL_NAME="CHANNEL_1"
    const val CHANNEL_DESCRIPTION="THIS IS DESCRIPTION"
    const val CHANNEL_IMPORTANCE= NotificationManager.IMPORTANCE_DEFAULT

}