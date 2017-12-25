package com.sxb.karch

import android.arch.lifecycle.Observer
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sxb.karch.db.User
import kotlinx.android.synthetic.main.activity_simplelist.*

class ListPageWithDbActivity : AppCompatActivity() {

    private val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplelist)
        setSupportActionBar(toolbar)

        var count = 0
        fab.setOnClickListener { _ ->
            Thread(Runnable {
                AppDatabase.getInstance(this@ListPageWithDbActivity).userDao()
                        .insertUser(User(++count, "", "Hello world! + $count"))
            }).start()
        }

        recyclerView.adapter = adapter
        LivePagedListBuilder<Int, User>(AppDatabase.getInstance(this).userDao().loadAllUser2(), 7)
                .build().observe(this, Observer {
            adapter.setList(it)
        })
    }

    private inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            v.setPadding(20, 120, 20, 120)
        }

        fun bindData(user:  User?) {
            (itemView as TextView).text = user?.name
        }
    }

    private inner class MyDiffCallback : DiffCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }

    private inner class MyAdapter : PagedListAdapter<User, MyHolder>(MyDiffCallback()) {
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bindData(getItem(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder
                = MyHolder(TextView(this@ListPageWithDbActivity))
    }
}
