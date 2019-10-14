package com.cloudsourceit.alarmmanagerdemo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cloudsourceit.alarmmanagerdemo.R
import com.cloudsourceit.alarmmanagerdemo.model.Alarm
import com.cloudsourceit.alarmmanagerdemo.utils.getAllAlarms
import com.cloudsourceit.alarmmanagerdemo.utils.toClockHour
import kotlinx.android.synthetic.main.item_alarm.view.*

class AlarmAdapter(private val items: MutableList<Alarm>, private val onDeleteClick: ((Long) -> Unit)? = null): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder{
            return AlarmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false))
    }

    override fun onBindViewHolder(viewHolder: AlarmViewHolder, position: Int) {
        val alarm = items[viewHolder.adapterPosition]
        alarm.apply {
            viewHolder.itemView.tv_hour.text = time.toClockHour()
            viewHolder.itemView.sw_enabled.isChecked = enabled
            viewHolder.itemView.bt_delete.setOnClickListener {
                onDeleteClick?.invoke(alarm.id)
                refreshData()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getItem(position: Int): Alarm?{
        return if(items.size > position) items[position] else null
    }

    fun refreshData(){
        this.items.clear()
        this.items.addAll(getAllAlarms())
        notifyDataSetChanged()
    }

    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}