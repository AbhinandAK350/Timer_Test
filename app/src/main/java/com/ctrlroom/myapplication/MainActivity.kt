package com.ctrlroom.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.ctrlroom.myapplication.events.CounterEvents
import com.ctrlroom.myapplication.events.MessageEvent
import com.ctrlroom.myapplication.events.NetworkEvent
import com.ctrlroom.myapplication.services.TimerService
import com.ctrlroom.myapplication.utils.NetworkConnectionLiveData
import com.ctrlroom.myapplication.utils.Pref
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    var textv: TextView? = null
    private var btn_startStop: Button? =null
    private var btn_pauseResume: Button ? = null
    private var eventBus: EventBus? = null
    private var btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textv = findViewById<TextView>(R.id.text_timer) as TextView
        btn_startStop = findViewById(R.id.btn_start_stop)
        btn_pauseResume = findViewById(R.id.btn_pause_resume)
        btn = findViewById(R.id.btn_next)

        eventBus = EventBus.getDefault()

        btn!!.setOnClickListener {
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        }

        NetworkConnectionLiveData(this@MainActivity)
            .observe(this@MainActivity, Observer { isConnected ->
                if (!isConnected) {
                    EventBus.getDefault().post(NetworkEvent("disconnected"))
                    return@Observer
                } else {
                    EventBus.getDefault().post(NetworkEvent("connected"))
                }
            })

        if (Pref.getString(this@MainActivity, "ssb", "stopped") == "stopped") {
            textv!!.text = "00:00:00"
            btn_startStop!!.visibility = View.VISIBLE
            btn_startStop!!.text = "Start"
            btn_pauseResume!!.visibility = View.GONE
        } else if (Pref.getString(this@MainActivity, "ssb", "stopped") == "running") {
            if (Pref.getString(this@MainActivity, "prb", "paused") == "resumed") {
                btn_pauseResume!!.visibility = View.VISIBLE
                btn_pauseResume!!.text = "Resume"
            } else if (Pref.getString(this@MainActivity, "prb", "paused") == "paused") {
                btn_pauseResume!!.text = "Pause"
                btn_pauseResume!!.visibility = View.VISIBLE
            }
            btn_startStop!!.visibility = View.VISIBLE
            btn_startStop!!.text = "Stop"
            btn_pauseResume!!.visibility = View.VISIBLE
        }

        btn_startStop!!.setOnClickListener {
            if (Pref.getString(this@MainActivity, "ssb", "stopped") == "stopped") {
                startService(Intent(this@MainActivity, TimerService::class.java))
                btn_startStop!!.text = "Stop"
                btn_pauseResume!!.visibility = View.VISIBLE
                EventBus.getDefault().postSticky(CounterEvents("start"))
                Pref.putString(this@MainActivity, "ssb", "running")
            } else if (Pref.getString(this@MainActivity, "ssb", "stopped") == "running") {
                btn_startStop!!.text = "Start"
                textv!!.text = "00:00:00"
                EventBus.getDefault().postSticky(CounterEvents("cancel"))
                Pref.putString(this@MainActivity, "ssb", "stopped")
                btn_pauseResume!!.visibility = View.GONE
                stopService(Intent(this@MainActivity, TimerService::class.java))
            }
        }

        btn_pauseResume!!.setOnClickListener {
            if (Pref.getString(this@MainActivity, "prb", "paused") == "resumed") {
                btn_pauseResume!!.visibility = View.VISIBLE
                btn_pauseResume!!.text = "Pause"
                EventBus.getDefault().postSticky(CounterEvents("resume"))
                Pref.putString(this@MainActivity, "prb", "paused")
            } else if (Pref.getString(this@MainActivity, "prb", "paused") == "paused") {
                btn_pauseResume!!.text = "Resume"
                btn_pauseResume!!.visibility = View.VISIBLE
                EventBus.getDefault().postSticky(CounterEvents("pause"))
                Pref.putString(this@MainActivity, "prb", "resumed")
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent?) {
        if (event!!.getMessage() == "Done") {
            textv!!.text = "Done"
            Pref.putString(this@MainActivity, "ssb", "stopped")
            btn_pauseResume!!.visibility = View.GONE
            btn_startStop!!.visibility = View.VISIBLE
            btn_startStop!!.text = "Start"
            stopService(Intent(this@MainActivity, TimerService::class.java))
        } else {
            textv!!.text = event.getMessage()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

}