package com.application.aquahome.manager

import android.content.Context
import android.util.Log
import com.application.aquahome.model.WorkerModel
import io.realm.Realm
import io.realm.RealmConfiguration

class LocalStorageManager(val cxt: Context) {

    private var realDb : Realm
    init {

        Realm.init(cxt)
        val config = RealmConfiguration.Builder()
        .allowQueriesOnUiThread(true)
        .allowWritesOnUiThread(true)
        .build()

        Realm.setDefaultConfiguration(config)
        realDb=Realm.getDefaultInstance()
    }

    fun addWorker(key : String,time : String, success:()->Unit, fail:()->Unit) {
        var result = false
        try {
            realDb.executeTransaction{ realm ->
                val num = realm.where(WorkerModel::class.java).max("id")
                val nextId = if ((num == null)) 1 else num.toInt() + 1

                val worker: WorkerModel = realm.createObject(WorkerModel::class.java, nextId)
                worker.key = key
                worker.time=time
                realm.insertOrUpdate(worker)
                result=true

            }
        } catch (e: Exception) {
            result=false
            Log.d("TAG", "addWorker:${e.message}")
        } finally {
            if(result)
                success()
            else
                fail()
        }
    }

    fun getListOfWorkers( success:(ArrayList<WorkerModel>)->Unit, fail:(String)->Unit){
        var result = false
        val myList  = ArrayList<WorkerModel>()
        var error =""
        try {
            realDb.executeTransaction{ realm ->
                val list = realm.where(WorkerModel::class.java).findAll()
                if(list!=null){
                    for(x in list){
                        if(x!=null){
                            myList.add(x)
                        }
                    }
                }
                result=true
                Log.d("TAG", "getListOfWorkers: s")

            }
        } catch (e: Exception) {
            result=false
            error=e.message.toString()
            Log.d("TAG", "getListOfWorkers: ${e.message}")
            e.localizedMessage?.let { fail(it) }
        } finally {
            if(result)
                success(myList)
            else
                fail(error)
//            realDb.close()
        }

    }

    fun deleteWorker(key: String,success:()->Unit, fail:(String)->Unit){
        var operationResult = false
        var error =""
        try {
            realDb.executeTransaction{ realm ->
                val result = realm.where(WorkerModel::class.java).equalTo("key",key).findAll()
                result.deleteAllFromRealm()
                operationResult=true

            }
        } catch (e: Exception) {
            operationResult=false
            e.localizedMessage?.let {
                error=it
            }
        } finally {
            if(operationResult)
                success()
            else
                fail(error)
        }
    }
}