package com.application.aquahome.manager

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.application.aquahome.model.BTSensor
import com.application.aquahome.model.HCBluetooth
import com.application.aquahome.model.WaterLevel
import com.application.aquahome.model.WorkerModel
import com.application.aquahome.util.BluetoothDeviceConverter
import com.application.aquahome.util.BluetoothDeviceSerializer
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

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
            }
        } catch (e: Exception) {
            result=false
            error=e.message.toString()
            e.localizedMessage?.let { fail(it) }
        } finally {
            if(result)
                success(myList)
            else
                fail(error)
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

    fun addSensor(device: BluetoothDevice, success: () -> Unit, fail: () -> Unit) {
        var result = false
        try {
            realDb.executeTransaction { realm ->
                val obj = BTSensor()
                val hcb=HCBluetooth(device)
                val json = Json {
                    serializersModule = SerializersModule {
                        contextual(BluetoothDevice::class, BluetoothDeviceSerializer)
                    }
                }
                val hcbster=json.encodeToString(HCBluetooth.serializer(),hcb)
                obj.sensor= hcbster
                realm.where(BTSensor::class.java).findAll().deleteAllFromRealm()
                realm.insertOrUpdate(obj)
                result = true
            }
        } catch (e: Exception) {
            result = false
            e.localizedMessage?.let { fail() }
        } finally {
            if (result)
                success()
            else
                fail()
        }
    }

    fun getSensor(device: (BluetoothDevice) -> Unit, fail: (String) -> Unit){
        var error =""
        var result = false
        var sensor: BluetoothDevice? = null
        try {
            realDb.executeTransaction { realm ->
                val s = realm.where(BTSensor::class.java).findAll().first()

                val json = Json {
                    serializersModule = SerializersModule {
                        contextual(BluetoothDevice::class, BluetoothDeviceSerializer)
                    }
                }

                sensor = json.decodeFromString<HCBluetooth>(s!!.sensor).sensor
                result = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result = false
            e.localizedMessage?.let {
                error=it
            }
        } finally {
            if (result)
                sensor?.let { device(it) }
            else
                fail(error)
        }
    }

    fun updateWaterLevel(waterLevel: WaterLevel){
        val currentTime = System.currentTimeMillis()
        val fortyEightHoursAgo = currentTime - 48 * 60 * 60 * 1000

        try {
            realDb.executeTransaction { realm ->
                realm.where(WaterLevel::class.java)
                    .lessThan("time", fortyEightHoursAgo)
                    .findAll()
                    .deleteAllFromRealm()
                realm.insertOrUpdate(waterLevel)
            }
        }catch (_: Exception){

        }
    }

    fun getWaterLevels(waterLevels:(List<WaterLevel>)->Unit,fail: () -> Unit){
        try {
            realDb.executeTransaction { realm ->
                val s = realm.where(WaterLevel::class.java).findAll().sort("time").toList()
                waterLevels(s)
            }
        }catch (_: Exception){
            fail()
        }
    }
}