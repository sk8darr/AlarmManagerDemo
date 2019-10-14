package com.cloudsourceit.alarmmanagerdemo.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudsourceit.alarmmanagerdemo.R
import com.cloudsourceit.alarmmanagerdemo.ui.adapter.AlarmAdapter
import com.cloudsourceit.alarmmanagerdemo.utils.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ItemClickSupport.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = AlarmAdapter(getAllAlarms()) { id: Long -> deleteAlarmAndInstances(id)}
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
        }

        ItemClickSupport.addTo(rv).setOnItemClickListener(this)
        fb_add.setOnClickListener { startActivity<AlarmConfigActivity>() }
    }

    override fun onResume() {
        super.onResume()
        rv?.adapter?.run{
            (this as AlarmAdapter).refreshData()
        }
    }

    override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
        (recyclerView.adapter as AlarmAdapter).getItem(position)?.run {
            Bundle().apply {
                putLong(KEY_ALARM_ID, this@run.id)
                startActivity<AlarmConfigActivity>(extras = this)
            }
        }
    }
}
