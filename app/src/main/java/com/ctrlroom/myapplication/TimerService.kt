package com.ctrlroom.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
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
        setNotif()
        EventBus.getDefault().register(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
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

    private fun setNotif() {
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
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }
        val notification = NotificationCompat.Builder(this, "ch1")
            .setContentTitle("Timer")
            .setContentText("Timer service is running")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
        startForeground(1, notification)
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