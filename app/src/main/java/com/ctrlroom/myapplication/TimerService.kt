package com.ctrlroom.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit

class TimerService: Service() {

    private var countDownTimer: CountDownTimer? = null
    var millis: Long = 0

    override fun onCreate() {
        super.onCreate()
        startForeground()
        EventBus.getDefault().register(this)
    }

    private fun startForeground() {
        startForeground(1, getMyActivityNotification(""))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_NOT_STICKY
    }

    @Subscribe(sticky = true)
    fun onBtn(event: CounterEvents?) {
        if (event!!.getEventType() == "start") {
            startCount(900000)
        } else if (event.getEventType() == "pause") {
            pauseCounter()
        } else if (event.getEventType() == "resume") {
            startCount(millis)
        } else if (event.getEventType() == "cancel") {
            cancelCounter()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getMyActivityNotification(text: String): Notification {
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
        val notification = getMyActivityNotification(text)
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