package com.application.aquahome.model

import android.bluetooth.BluetoothDevice
import io.realm.RealmObject
open class BTSensor() : RealmObject(){
    lateinit var sensor: String
}

