package com.ctrlroom.myapplication.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ctrlroom.myapplication.MainActivity
import com.ctrlroom.myapplication.R
import com.ctrlroom.myapplication.utils.Util
import com.ctrlroom.myapplication.events.CounterEvents
import com.ctrlroom.myapplication.events.MessageEvent
import com.ctrlroom.myapplication.events.NetworkEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class TimerService: Service() {

    private var timer: Timer? = null
    private var countDownTimer: CountDownTimer? = null
    var millis: Long = 0

    override fun onCreate() {
        super.onCreate()
        Log.d("service_timer", "service created")
        startForeground()
        postToast()
        EventBus.getDefault().register(this)
    }

    private fun postToast() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val jobj = JSONObject()
                    jobj.put("battery_percentage", Util.getBatteryLevel(applicationContext).toString())
                    Util.writeToJson(applicationContext, jobj.toString())
                    Toast.makeText(applicationContext, Util.getBatteryLevel(applicationContext).toString()+"%", Toast.LENGTH_SHORT).show()
                })
            }
        }, 0, 10000)
    }

    private fun startForeground() {
        startForeground(1, setForegroundNotification(""))
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service_timer", "service destroyed")
        timer!!.cancel()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true)
    fun onCounterEvents(event: CounterEvents?) {
        if (event!!.getEventType() == "start") {
            startCount(100000)
        } else if (event.getEventType() == "pause") {
            pauseCounter()
        } else if (event.getEventType() == "resume") {
            startCount(millis)
        } else if (event.getEventType() == "cancel") {
            cancelCounter()
        }
    }

    @Subscribe
    fun onNetworkEvent(event: NetworkEvent) {
        if (event.getStatus() == "connected") {
            Toast.makeText(applicationContext, "connected", Toast.LENGTH_SHORT).show()
        } else if (event.getStatus() == "disconnected") {
            Toast.makeText(applicationContext, "disconnected", Toast.LENGTH_SHORT).show()        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setForegroundNotification(text: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "ch1",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }

        return NotificationCompat.Builder(this,"my_channel_01")
        .setContentTitle("Time remaining")
            .setContentText(text)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setChannelId("ch1")
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = setForegroundNotification(text)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, notification)
    }

    private fun startCount(timemillis: Long) {
        countDownTimer = object : CountDownTimer(timemillis, 1000) {
            override fun onTick(p0: Long) {
                millis = p0
                val cdown = String.format(
                    "%02d:%02d:%02d",
                    (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(
                            millis
                        )
                    )),
                    (TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                    (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                    ))
                )

                updateNotification(cdown)
                EventBus.getDefault().postSticky(MessageEvent(cdown))
            }

            override fun onFinish() {
                EventBus.getDefault().post(MessageEvent("Done"))
            }
        }
        (countDownTimer as CountDownTimer).start()
    }

    private fun pauseCounter() {
        countDownTimer!!.cancel()
    }

    private fun cancelCounter() {
        countDownTimer!!.cancel()
        millis = 0
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}