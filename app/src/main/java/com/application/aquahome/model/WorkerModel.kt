package com.application.aquahome.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WorkerModel() : RealmObject() {
    @PrimaryKey
    var  id: Int = 0
    var key: String = ""
    var time: String= ""
}