package com.sxb.karch

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sxb.karch.db.User
import kotlinx.android.synthetic.main.activity_simplelist.*

/**
 * 相关文档：https://developer.android.google.cn/reference/android/support/v7/recyclerview/extensions/ListAdapter.html
 * 优点：ListAdapter会对比新旧数据，检测新增、删除数据并动画实现
 * 缺点：需要外部持有数据，造成多处持有相同数据
 */
class ListActivity : AppCompatActivity() {

    private val users = mutableListOf<User>()
    private val data = MutableLiveData<List<User>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simplelist)
        setSupportActionBar(toolbar)

        val adapter = MyAdapter()
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            users.add(User(users.size, "", "Hello ${users.size}"))
            if (users.size > 5) {
                // todo 重新生成User，adapter才能识别数据改变。否则使用adapter.notifyDataSetChanged()刷新数据
                users[3] = users[3].copy(name = "kiven")
            }

            if (users.size > 15) {
                users.removeAt(4)
            }

            data.value = users.toList()
//            adapter.notifyDataSetChanged()
        }

        data.value = users
        data.observe(this, Observer { adapter.setList(it) })
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

    private inner class MyAdapter : ListAdapter<User, MyHolder>(MyDiffCallback()) {
        override fun onBindViewHolder(holder: MyHolder?, position: Int) {
            holder?.bindData(getItem(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder
                = MyHolder(TextView(this@ListActivity))

    }
}
