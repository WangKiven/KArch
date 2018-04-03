package com.sxb.karch

import android.arch.lifecycle.Observer
import android.arch.paging.*
import android.arch.paging.DataSource.Factory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
//import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sxb.karch.db.User
import kotlinx.android.synthetic.main.activity_simplelist.*

/**
 * 优点：无限数据
 * 缺点：数据更新时，界面紊乱
 */
class ListWithPageActivity : AppCompatActivity() {

    private var start = "Hello "
    private val factory = object :Factory<Int, User>(){
        override fun create(): DataSource<Int, User> {
            return object : ItemKeyedDataSource<Int, User>() {
                override fun getKey(item: User): Int {
                    return item.id
                }

                override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<User>) {
                    callback.onResult(loadRangeInternal(params.key, params.requestedLoadSize))
                }

                override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<User>) {
                    callback.onResult(loadRangeInternal(params.key - params.requestedLoadSize - 1, params.requestedLoadSize))
                }

                override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<User>) {
                    callback.onResult(loadRangeInternal(params.requestedInitialKey ?: 0 + 1, params.requestedLoadSize))
                }

                private fun loadRangeInternal(position: Int, loadSize: Int): List<User> {
                    val end = position + loadSize
                    return (position..end).map { User(it, "", "$start $it") }
                }
            }
        }

    }
    private val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplelist)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            start = "Hi "

            LivePagedListBuilder(factory, 6).build().observe(this, Observer {
//                adapter.setList(it)
                adapter.submitList(it)
            })
        }

        recyclerView.adapter = adapter

        val pagedListConfig = PagedList.Config.Builder()
//                .setInitialLoadSizeHint(6 * 2)
                .setPageSize(6)
//                .setPrefetchDistance(6)
                .build()

        LivePagedListBuilder(factory, pagedListConfig).build().observe(this, Observer {
//            adapter.setList(it)
            adapter.submitList(it)
        })
    }

    private inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            v.setPadding(20, 120, 20, 120)
        }

        fun bindData(text: User?) {
            (itemView as TextView).text = text?.name
        }
    }

    /*private inner class MyDiffCallback : DiffCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }*/
    private inner class MyDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }

    private inner class MyAdapter : PagedListAdapter<User, MyHolder>(MyDiffCallback()) {
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bindData(getItem(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder
                = MyHolder(TextView(this@ListWithPageActivity))
    }

}