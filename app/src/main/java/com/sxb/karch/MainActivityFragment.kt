package com.sxb.karch

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sxb.karch.db.User
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A placeholder fragment containing a simple view.
 */
class MainActivityFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 监听生命周期，通过注解实现
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun start() {
                Log.i("ULog_default", "start")
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stop() {
                Log.i("ULog_default", "stop")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private lateinit var user: User

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Thread(Runnable { AppDatabase.getInstance(context).userDao().deleteAllUser() }).start()

        // TODO: 2017/12/21 ------------------------简单功能-----------------------------
        // 点击按钮，保存数据
        btn_save.setOnClickListener {
            val text = et_name.editText?.text.toString()
            if (!TextUtils.isEmpty(text)) {
                Thread(Runnable { AppDatabase.getInstance(context).userDao().insertUser(user.copy(name = text)) }).start()
            }
        }

        // 观察数据，改变界面
        val id = 13
        AppDatabase.getInstance(context).userDao().loadUser(id).observe(this, Observer {
            user = it ?: User(id, "", "")
            et_name.hint = user.name
        })

        // 观察数据库变换，打印所有数据
        AppDatabase.getInstance(context).userDao().loadAllUser().observe(this, Observer {
            if (it != null)
                for (user in it) {
                    Log.i("ULog_default", "${user.id}:${user.name}")
                }
        })







        // TODO: 2017/12/21 ----------------------- 其他功能 ------------------------------
        btn_samplelist.setOnClickListener {
            startActivity(Intent(activity, SimplelistActivity::class.java))
        }
        btn_listwithpage.setOnClickListener{
            startActivity(Intent(activity, ListWithPageActivity::class.java))
        }
        btn_listpagewithdb.setOnClickListener{
            startActivity(Intent(activity, ListPageWithDbActivity::class.java))
        }
        btn_list.setOnClickListener {
            startActivity(Intent(activity, ListActivity::class.java))
        }
    }
}
