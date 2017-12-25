package com.sxb.karch

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.arch.paging.*
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_simplelist.*

/**
 *
 */
class ListWithPageActivity : AppCompatActivity() {

    private var texts = mutableListOf<String>()
    private val data = MutableLiveData<MutableList<String>>()
    private val handler = Handler(Handler.Callback {
        texts.add("hello world! + ${texts.size}")

        data.postValue(texts)
        true
    })
    private val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplelist)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            //            handler.sendEmptyMessageDelayed(0, 1000)
            handler.sendEmptyMessage(0)
        }

        recyclerView.adapter = adapter

        val factory = DataSource.Factory<Int, String> {
            /*object : PositionalDataSource<String>() {
                private fun loadRangeInternal(position:Int, loadSize:Int):List<String>{
                    val end = min(position + loadSize, texts.size)
                    return texts.subList(position, end ).toList()
                }
                override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<String>) {
                    val totalCount = texts.size
                    val position  = computeInitialLoadPosition(params, totalCount)
                    val loadSize  = computeInitialLoadSize(params, position, totalCount)
                    callback.onResult(loadRangeInternal(position, loadSize), position, totalCount)

                }

                override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<String>) {
                    val sub = loadRangeInternal(params.startPosition, params.loadSize)
                    callback.onResult(sub)
                }

            }*/
            /*object : ItemKeyedDataSource<Int, String>() {
                override fun getKey(item: String): Int {
                    return texts.indexOf(item) + 1
                }

                override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<String>) {
                    callback.onResult(loadRangeInternal(params.key, params.requestedLoadSize))
                }

                override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<String>) {

                }

                override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<String>) {
                    val totalCount = texts.size
                    val position = min(totalCount, params.requestedInitialKey ?: 0)
                    val loadSize = params.requestedLoadSize
                    callback.onResult(loadRangeInternal(position, loadSize), position, totalCount)
                }

                private fun loadRangeInternal(position: Int, loadSize: Int): List<String> {
                    val end = min(position + loadSize, texts.size)
                    return texts.subList(position, end).toList()
                }
            }*/
            object : TiledDataSource<String>() {
                @MainThread
                override fun countItems(): Int {
                    return texts.size
                }

                @MainThread
                override fun loadRange(startPosition: Int, count: Int): MutableList<String> {
                    return texts.subList(startPosition, startPosition + count).toMutableList()
                }
            }
        }
        val pagedListConfig = PagedList.Config.Builder()
//                .setInitialLoadSizeHint(6 * 2)
                .setPageSize(6)
//                .setPrefetchDistance(6)
                .build()

        Transformations.switchMap(data, { LivePagedListBuilder(factory, pagedListConfig).build() }).observe(this, Observer {
            adapter.setList(it)
        })

        /*val pagedata = LivePagedListBuilder(factory, pagedListConfig).build()
        .observe(this, Observer {
            adapter.setList(it)
        })*/
    }

    private inner class MyHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            v.setPadding(20, 120, 20, 120)
        }

        fun bindData(text: String?) {
            (itemView as TextView).text = text
        }
    }

    private inner class MyDiffCallback : DiffCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
    }

    private inner class MyAdapter : PagedListAdapter<String, MyHolder>(MyDiffCallback()) {
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.bindData(getItem(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder
                = MyHolder(TextView(this@ListWithPageActivity))
    }

}