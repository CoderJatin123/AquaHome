package com.application.aquahome.model

import io.realm.RealmObject

open class WaterLevel(): RealmObject(){
    var value : Int =0
    var time : Long = 0
}
