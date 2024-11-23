package com.application.aquahome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.aquahome.R
import com.application.aquahome.model.WorkerModel

class ScheduledTimeStampAdapter(val cxt: Context, val deleteWorker:(String)->Unit) :
    RecyclerView.Adapter<ScheduledTimeStampAdapter.TimeViewHolder>() {

    private val timeList = ArrayList<WorkerModel>()

    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.time)
        val moreImageView: ImageButton = itemView.findViewById(R.id.moreBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scheduledtimestamp, parent, false) // Replace with your layout
        return TimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val w = timeList[position]
        holder.timeTextView.text = w.time
        holder.moreImageView.setOnClickListener {
            showPopup(holder.moreImageView, w)
        }
    }

    override fun getItemCount(): Int = timeList.size
    fun update(list: ArrayList<WorkerModel>){
        this.timeList.clear()
        this.timeList.addAll(list)
        notifyDataSetChanged()
    }

    fun showPopup(view: View, w: WorkerModel){
        val popupMenu = PopupMenu(cxt,view)
        popupMenu.menuInflater.inflate(R.menu.shedule_time_item_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete->{
                   deleteWorker(w.key)
                }

                else -> {}
            }
            true
        }

        popupMenu.show()

    }
}