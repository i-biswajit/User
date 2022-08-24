package com.cab.user.common.pushnotification

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage pushnotification
 * @category FirebaseMessagingService
 * @author SMR IT Solutions
 * 
 */


import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sinch.android.rtc.SinchHelpers
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogE
import com.cab.user.taxi.views.voip.CabmeSinchService
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/* ************************************************************
   Firebase Messaging Service to base push notification message to activity
   *************************************************************** */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var sessionManager: SessionManager

    override fun onNewToken(s: String) {
        super.onNewToken(s)


        //CabmeSinchService.getManagedPush(s).registerPushToken(this);
    }

    override fun onCreate() {
        super.onCreate()
        AppController.appComponent.inject(this)
        setLocale()

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        DebuggableLogE(TAG, "From tri: " + remoteMessage.from!!)
        DebuggableLogE(TAG, "From tri: $remoteMessage")
        DebuggableLogE(TAG, "Data Payload: " + remoteMessage.data.toString())

        Log.i(TAG, "onMessageReceived: data=${remoteMessage.data}")
        Log.i(TAG, "onMessageReceived: data=${remoteMessage.notification}")
        Log.i(TAG, "onMessageReceived: data=${remoteMessage.notification?.body}")
        Log.i(TAG, "onMessageReceived: data=${remoteMessage.notification?.title}")

        if (SinchHelpers.isSinchPushPayload(remoteMessage.data)) {

        }

        if (SinchHelpers.isSinchPushPayload(remoteMessage.data)) {

            DebuggableLogE("test push noti", "Sinch message")
            initSinchService()

        } else {

            DebuggableLogE("test push noti", "ours")

            // Check if message contains a data payload.
            if (remoteMessage.data.size > 0) {
                DebuggableLogE(TAG, "Data Payload: " + remoteMessage.data.toString())

                try {
                    val json = JSONObject(remoteMessage.data.toString())
                     commonMethods.handleDataMessage(json,this)
                    if (remoteMessage.notification != null) {
                        DebuggableLogE(TAG, "Notification Body: " + remoteMessage.notification!!.body!!)

                    }

                } catch (e: Exception) {
                    DebuggableLogE(TAG, "Exception: " + e.message)
                }

            }
        }
    }


    /**
     * set language
     */
    private fun setLocale() {
        val lang = sessionManager.language

        if (lang != "") {
            val langC = sessionManager.languageCode
            val locale = Locale(langC)
            Locale.setDefault(locale)
            val config = Configuration()
            config.locale = locale
           resources.updateConfiguration(config, this.resources.displayMetrics)
        } else {
            sessionManager.language = "English"
            sessionManager.languageCode = "en"
        }


    }

   /* private fun handleDataMessage(json: JSONObject,context : Context) {
        DebuggableLogE(TAG, "push json: $json")

        try {
            sessionManager.pushJson = json.toString()

            if (!NotificationUtils.isAppIsInBackground(context)) {
                DebuggableLogE(TAG, "IF: $json")
                // app is in foreground, broadcast the push message
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("message", "message")
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)

                if (json.getJSONObject("custom").has("accept_request")) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                   *//* if (!isSendingRequestisLive) {
                        val rating = Intent(context, SendingRequestActivity::class.java)
                        rating.putExtra("loadData", "loadData")
                        val locationHashMap = HashMap<String, String>()
                        locationHashMap["pickup_latitude"] = "0.0"
                        locationHashMap["pickup_longitude"] = "0.0"
                        locationHashMap["drop_latitude"] = "0.0"
                        locationHashMap["drop_longitude"] = "0.0"
                        rating.putExtra("hashMap", locationHashMap)
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(rating)
                    }*//*
                    if (!isSendingRequestisLive) {
                        val rating = Intent(context, MainActivity::class.java)
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(rating)
                        val trip_id = json.getJSONObject("custom").getJSONObject("accept_request").getString("trip_id")
                        sessionManager.tripId = trip_id
                        sessionManager.tripStatus = "accept_request"
                        sessionManager.isDriverAndRiderAbleToChat = true
                    }


                } else if (json.getJSONObject("custom").has("arrive_now")) {

                    MainActivity.isMainActivity = true
                    *//*if (!MainActivity.isMainActivity) {
                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.driverarrrive));
                        dialogs.putExtra("type", 0);
                        startActivity(dialogs);
                    }*//*

                    val trip_id = json.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "arrive_now"
                    sessionManager.isDriverAndRiderAbleToChat = true

                } else if (json.getJSONObject("custom").has("begin_trip")) {

                    if (!MainActivity.isMainActivity) {


                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message",resources.getString(R.string.yourtripbegin))
                        dialogs.putExtra("type", 0)
                        startActivity(dialogs)


                    }

                    val trip_id = json.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "end_trip"
                    sessionManager.isDriverAndRiderAbleToChat = true

                } else if (json.getJSONObject("custom").has("end_trip")) {
                    val trip_id = json.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "end_trip"
                    sessionManager.clearDriverNameRatingAndProfilePicture()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)
                    *//*startActivity(new Intent(getApplicationContext(), PaymentAmountPage.class));*//*
                    stopSinchService()
                    val rating = Intent(context, DriverRatingActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    rating.putExtra("imgprofile", json.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image"))
                    startActivity(rating)
                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID()
                    sessionManager.clearDriverNameRatingAndProfilePicture()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)
                    sessionManager.isrequest = false
                    sessionManager.isTrip = false
                    if (!MainActivity.isMainActivity) {


                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.yourtripcancelledbydriver))
                        dialogs.putExtra("type", 1)
                        startActivity(dialogs)


                    }
                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    commonMethods.removeLiveTrackingNodesAfterCompletedTrip(context)

                    commonMethods.removeTripNodesAfterCompletedTrip(context)
                    sessionManager.clearTripID()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    *//*Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);*//*
                    sessionManager.isrequest = false
                    sessionManager.isTrip = false
                    //statusDialog("Your trip cancelled by driver",1);
                    if (!MainActivity.isMainActivity) {

                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                        //dialogs.putExtra("driverImage",json.getJSONObject("custom").getJSONObject("trip_payment").getString("driver_thumb_image"));
                        dialogs.putExtra("type", 1)
                        startActivity(dialogs)


                    }
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    initSinchService()
                } else {
                    if (json.getJSONObject("custom").has("custom_message")) {
                        // statusDialog("Your Trip Begins Now...");
                        val notificationUtils = NotificationUtils(context)
                        notificationUtils.playNotificationSound()
                        val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                        val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")

                        notificationUtils.generateNotification(context, message, title)
                    }
                }
                // play notification sound
                // notificationUtils.playNotificationSound();
                // notificationUtils.generateNotification(getApplicationContext(),message);
            } else {
                DebuggableLogE(TAG, "ELSE: $json")

                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra("message", "message")
                if ("" != sessionManager.accessToken) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                }

                if (json.getJSONObject("custom").has("accept_request")) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                  *//*  if (!isSendingRequestisLive) {
                        val rating = Intent(context, SendingRequestActivity::class.java)
                       *//**//* rating.putExtra("loadData", "loadData")
                        val locationHashMap = HashMap<String, String>()
                        locationHashMap["pickup_latitude"] = "0.0"
                        locationHashMap["pickup_longitude"] = "0.0"
                        locationHashMap["drop_latitude"] = "0.0"
                        locationHashMap["drop_longitude"] = "0.0"
                        rating.putExtra("hashMap", locationHashMap)*//**//*
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(rating)
                    }*//*
                    val rating = Intent(context, MainActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                    val trip_id = json.getJSONObject("custom").getJSONObject("accept_request").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "accept_request"

                }*//*else if(json.getJSONObject("custom").has("no_cars")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    Intent rating=new Intent(getApplicationContext(), SendingRequestActivity.class);
                    rating.putExtra("loadData","loadData");
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);

                   *//**//* Intent pushNotifications = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotifications.putExtra("message", "message");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifications);*//**//*
                }*//*
                else if (json.getJSONObject("custom").has("arrive_now")) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                    val rating = Intent(context, MainActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                    if (!MainActivity.isMainActivity) {

                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.driverarrrive))
                        dialogs.putExtra("type", 0)
                        startActivity(dialogs)

                    }

                    val trip_id = json.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "arrive_now"

                } else if (json.getJSONObject("custom").has("begin_trip")) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                    val rating = Intent(context, MainActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                    if (!MainActivity.isMainActivity) {


                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.yourtripbegin))
                        dialogs.putExtra("type", 0)
                        startActivity(dialogs)


                    }

                    val trip_id = json.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "end_trip"

                } else if (json.getJSONObject("custom").has("end_trip")) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)
                    val trip_id = json.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id")
                    sessionManager.tripId = trip_id
                    sessionManager.tripStatus = "end_trip"
                    stopSinchService()
                    *//*startActivity(new Intent(getApplicationContext(), PaymentAmountPage.class));*//*
                    val rating = Intent(context, DriverRatingActivity::class.java)
                    rating.putExtra("imgprofile", json.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image"))
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    val rating = Intent(context, MainActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                    sessionManager.isrequest = false
                    sessionManager.isTrip = false
                    //statusDialog("Your trip cancelled by driver",1);
                    if (!MainActivity.isMainActivity) {

                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.yourtripcancelledbydriver))
                        dialogs.putExtra("type", 1)
                        startActivity(dialogs)
                    }
                } else if (json.getJSONObject("custom").has("trip_payment")) {
                    commonMethods.removeLiveTrackingNodesAfterCompletedTrip(context)
                    commonMethods.removeTripNodesAfterCompletedTrip(context)
                    sessionManager.clearTripID()
                    sessionManager.isDriverAndRiderAbleToChat = false
                    val rating = Intent(context, MainActivity::class.java)
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(rating)
                    sessionManager.isrequest = false
                    sessionManager.isTrip = false
                    //statusDialog("Your trip cancelled by driver",1);
                    if (!MainActivity.isMainActivity) {

                        val dialogs = Intent(context, CommonDialog::class.java)
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        dialogs.putExtra("message", resources.getString(R.string.paymentcompleted))
                        //dialogs.putExtra("driverImage",json.getJSONObject("custom").getJSONObject("trip_payment").getString("driver_thumb_image"));
                        dialogs.putExtra("type", 1)
                        startActivity(dialogs)


                    }
                } else if (json.getJSONObject("custom").has("user_calling")) {
                    initSinchService()
                } else if (json.getJSONObject("custom").has("chat_notification")) {



                    val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    //String timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());

                    val notificationIntent = Intent(context, ActivityChat::class.java)
                    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP




                    val notificationUtils = NotificationUtils(context)
                    notificationUtils.playNotificationSound()
                    val message = json.getJSONObject("custom").getJSONObject("chat_notification").getString("message_data")
                    val title = json.getJSONObject("custom").getJSONObject("chat_notification").getString("user_name")
                    println("ChatNotification : Rider " + message)
                    notificationUtils.showNotificationMessage(title, message, timeStamp, notificationIntent, null)



                } else {
                    if (json.getJSONObject("custom").has("custom_message")) {
                        sessionManager.isrequest = false
                        sessionManager.isTrip = false
                        *//*Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(rating);*//*
                        val notificationUtils = NotificationUtils(context)
                        notificationUtils.playNotificationSound()
                        val message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data")
                        val title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title")

                        notificationUtils.generateNotification(context, message, title)
                    }
                }
                // play notification sound
                // notificationUtils.playNotificationSound();
                // notificationUtils.generateNotification(getApplicationContext(),message);

            }
        } catch (e: JSONException) {
            DebuggableLogE(TAG, "Json Exception: " + e.message)
        } catch (e: Exception) {
            DebuggableLogE(TAG, "Exception: " + e.message)
        }

    }
*/
    private fun initSinchService() {
        if (sessionManager.accessToken != "") {
            startService(Intent(this, CabmeSinchService::class.java))
        }

    }

    private fun stopSinchService() {
        CommonMethods.stopSinchService(this)

    }

    companion object {

        private val TAG = MyFirebaseMessagingService::class.java.simpleName
    }


}
