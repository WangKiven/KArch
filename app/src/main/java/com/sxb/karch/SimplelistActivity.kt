package com.sxb.karch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_simplelist.*

/**
 * 最简单的列表使用，仅需导入lifecycle库即可
 */
class SimplelistActivity : AppCompatActivity() {
    private var texts = MutableLiveData<MutableList<String>>()
    private val handler = Handler(Handler.Callback {
        var ts = texts.value
        if (ts == null) {
            ts = mutableListOf()
        }
        ts.add("hello world!")

        texts.value = ts

        true
    })
    private val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplelist)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
//            handler.sendEmptyMessageDelayed(0, 1000)
            handler.sendEmptyMessage(0)
        }

        recyclerView.adapter = adapter

        texts.observe(this, Observer {
            adapter.notifyDataSetChanged()
        })
    }


    private inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bindData(text: String?) {
            (itemView as TextView).text = text
        }
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyHolder>() {
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bindData(texts.value!![position])
        }

        override fun getItemCount(): Int = texts.value?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder
                = MyHolder(TextView(this@SimplelistActivity))
    }
}
