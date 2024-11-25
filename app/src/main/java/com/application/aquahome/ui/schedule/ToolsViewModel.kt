package com.application.aquahome.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.aquahome.manager.LocalStorageManager
import com.application.aquahome.manager.MyWorkerManager
import com.application.aquahome.model.WorkerModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class ToolsViewModel(private val localStorageManager: LocalStorageManager, private val  workerManager: MyWorkerManager) : ViewModel() {

    private val _msgFlow = MutableSharedFlow<String>()
    val msgFlow: SharedFlow<String> = _msgFlow

    private val _scheduledTimeStamps = MutableLiveData<ArrayList<WorkerModel>>().apply {
        this.value= arrayListOf()
    }

     val scheduledTimeStamps : LiveData<ArrayList<WorkerModel>> = _scheduledTimeStamps

    init {
        update()
    }

    fun addWorker(hour: Int, minute:Int){
        workerManager.scheduleWorker(hour, minute, success = {key,time->
            localStorageManager.addWorker(key,time, success = {
                update()
            }, fail = {
                createMsg("Failed to add to schedule")
            })
        }, fail = {
            createMsg("Failed to create schedule")
        })
    }

    private fun createMsg(msg: String){
        viewModelScope.launch {
            _msgFlow.emit(msg)
        }
    }
     fun deleteWorker(key:String){
        localStorageManager.deleteWorker(key,
            success = {
                workerManager.cancelScheduledWorker(key,
                    success = {
                    update()
                        createMsg("Schedule deleted successfully")
                    }, fail = {
                        createMsg("Failed to delete schedule")
                    })
            }, fail = {
                createMsg("Failed to delete schedule")
            })

    }

    private fun update(){
        localStorageManager.getListOfWorkers(
            success = {
                _scheduledTimeStamps.value=it
            }, fail = {
                createMsg(it)
            })
    }

}