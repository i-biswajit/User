package com.cab.user.taxi.views.voip

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sinch.android.rtc.*

import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallClient
import com.sinch.android.rtc.calling.CallClientListener
import com.sinch.android.rtc.calling.CallListener
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.network.AppController
import com.cab.user.common.pushnotification.Config
import com.cab.user.common.pushnotification.NotificationUtils
import com.cab.user.common.utils.CommonKeys
import java.text.SimpleDateFormat
import java.util.*

import javax.inject.Inject


class CabmeSinchService : Service(), PushTokenRegistrationCallback {
    lateinit var mContext: Context
    internal var mPushTokenIsRegistered: Boolean? = true


    @Inject
    lateinit var sessionManager: SessionManager

    private var mBinder = MyBinder()
    override fun onBind(intent: Intent?): IBinder? {
        mBinder = MyBinder()

        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        AppController.appComponent.inject(this)
        mContext = applicationContext
        createSinchClient(sessionManager.userId!!, sessionManager.sinchKey!!, sessionManager.sinchSecret!!, mContext)
    }

    private fun createSinchClient(userCallId: String, sinchKey: String, sinchSecret: String, context: Context) {
        sinchClient = Sinch.getSinchClientBuilder()
                .context(context)
                .userId(userCallId)
                .applicationKey(sinchKey)
                .applicationSecret(sinchSecret)
                .environmentHost("clientapi.sinch.com")
                .build()

        sinchClient!!.setSupportPushNotifications(true)
        sinchClient!!.setSupportManagedPush(true)
        sinchClient!!.setSupportActiveConnectionInBackground(true)
        sinchClient!!.setPushNotificationDisplayName("you missed a call from")
        sinchClient!!.setSupportCalling(true)
        sinchClient!!.startListeningOnActiveConnection()
        //sinchClient.checkManifest();

        sinchClient!!.callClient.addCallClientListener(SinchCallClientListener())
        sinchClient!!.setSupportPushNotifications(true)
        sinchClient!!.setSupportManagedPush(true)
        sinchClient!!.start()
        if (!(mPushTokenIsRegistered)!!) {
            getManagedPush(userCallId)!!.registerPushToken(this)
        }
    }

    private fun getManagedPush(username: String): ManagedPush? {
        // create client, but you don't need to start it
        //initClient(username);
        // retrieve ManagedPush
        createSinchClient(sessionManager.userId!!, sessionManager.sinchKey!!, sessionManager.sinchSecret!!, mContext)
        return Beta.createManagedPush(sinchClient)
    }

    override fun tokenRegistered() {
        mPushTokenIsRegistered = true
    }

    override fun tokenRegistrationFailed(sinchError: SinchError) {
        mPushTokenIsRegistered = false
    }

    private inner class SinchCallClientListener : CallClientListener {
        override fun onIncomingCall(callClient: CallClient, incomingCall: Call) {

            call = incomingCall
            call?.addCallListener(object : CallListener {
                override fun onCallEstablished(p0: Call?) {
                    println("onCallEstablished")
                }

                override fun onCallProgressing(p0: Call?) {
                    println("onCallProgressing")
                }

                override fun onShouldSendPushNotification(p0: Call?, p1: MutableList<PushPair>?) {
                    println("onShouldSendPushNotification")
                }

                override fun onCallEnded(p0: Call?) {
                    println("onCallEnded")
                    NotificationUtils.clearNotifications(mContext)
                }

            })
            try{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && NotificationUtils.isAppIsInBackground(mContext)) {
                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    val notificationIntent = Intent(mContext, CallProcessingActivity::class.java)
                    notificationIntent.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CallProcessingActivity.CallActivityType.Ringing)
                    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //notificationIntent.putExtra(CommonKeys.FIREBASE_CHAT_FROM_PUSH, json.toString())
                    val notificationUtils = NotificationUtils(mContext)
                    notificationUtils.playNotificationSound()
                    val message = CommonKeys.keyCaller
                    val title = mContext.getString(R.string.app_name)
                    notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null)
                    val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                    pushNotification.putExtra("message", "message")
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(pushNotification)
                }else{
                    val callScreen = Intent(this@CabmeSinchService, CallProcessingActivity::class.java)
                    callScreen.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CallProcessingActivity.CallActivityType.Ringing)
                    callScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(callScreen)
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    inner class MyBinder : Binder() {
        val sinchClient: CabmeSinchService
            get() = this@CabmeSinchService
    }

    companion object {
        var sinchClient: SinchClient? = null
        lateinit var call: Call
    }
}