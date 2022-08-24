package com.cab.user.taxi.views.voip

import android.annotation.TargetApi
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.sinch.android.rtc.PushPair
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallListener
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.helper.NetworkChangeReceiver
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.pushnotification.Config
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonKeys.KEY_CALLER_ID
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogD
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogE
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.utils.RuntimePermissionDialogFragment
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject


class CallProcessingActivity : CommonActivity(), RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback, ServiceListener {

    private val mSensorManager: SensorManager? = null
    private val mProximity: Sensor? = null

    lateinit var callAudio: AudioManager

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var customDialog: CustomDialog
    @Inject
    lateinit var gson: Gson

    lateinit var dialog: AlertDialog

    private  var callerId: String?=null
    private var isThisActivityIsLive = true

    @BindView(R.id.cl_answer_view)
    lateinit var clCallAnsweredView: ConstraintLayout

    @BindView(R.id.cl_incomming_view)
    lateinit var clCallRingingView: ConstraintLayout

    @BindView(R.id.chronometer_call_timer)
    lateinit var onGoingCallTimerChronometer: Chronometer

    @BindView(R.id.tv_call_connection_status)
    lateinit var callConnectionStatus: TextView

    @BindView(R.id.imgv_loud_speaker)
    lateinit var imgvLoudSpeaker: ImageView

    @BindView(R.id.imgv_mute_voice)
    lateinit var imgvMic: ImageView

    @BindView(R.id.profile_image)
    lateinit var profileImage: CircleImageView

    @BindView(R.id.tv_caller_name)
    lateinit var tvCallerName: TextView

    var mNetworkReceiver: NetworkChangeReceiver?=null
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    lateinit var defaultRingtone: Uri
    private var callRingtone: Ringtone? = null
    private var outGointRingtoneMediaplayer: MediaPlayer? = null
    private var callConnectingSound: MediaPlayer? = null

    /*SensorEventListener proximitrySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                runOnUiThread(() -> {
                    if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY && mSensorManager != null && mProximity !=null) {
                        //near

                            turnOffScreen();

                    } else {
                        //far

                            turnOnScreen();

                    }
                });
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            //mSensorManager.unregisterListener(proximitrySensorEventListener);
        }
    };*/

    private var mPowerManager: PowerManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null


    override fun permissionDenied(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {
        finishThisActivity()
    }


    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        if (jsonResp.isSuccess) {
            commonMethods.hideProgressDialog()
            onSuccessProfile(jsonResp)
        } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.hideProgressDialog()
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }

    private fun onSuccessProfile(jsonResp: JsonResponse) {
        val profileURL = commonMethods.getJsonValue(jsonResp.strResponse, "profile_image", String::class.java) as String
        val firstName = commonMethods.getJsonValue(jsonResp.strResponse, "first_name", String::class.java) as String
        val lastName = commonMethods.getJsonValue(jsonResp.strResponse, "last_name", String::class.java) as String
        Picasso.get().load(profileURL).into(profileImage)
        tvCallerName.text = firstName + "\t" + lastName
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {

    }

    @IntDef(CallActivityType.Ringing, CallActivityType.CallProcessing)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class CallActivityType {
        companion object {
            const val Ringing = 0
            const val CallProcessing = 1
        }
    }

    @OnClick(R.id.fab_answer)
    fun answerACall() {
        RuntimePermissionDialogFragment.checkPermissionStatus(this, supportFragmentManager, this, arrayOf(RuntimePermissionDialogFragment.RECORD_AUDIO_PERMISSION, RuntimePermissionDialogFragment.MODIFY_AUDIO_PERMISSION), RuntimePermissionDialogFragment.audioCallbackCode, 0)
        stopCallRingtone()
    }


    @OnClick(R.id.ll_loudspeaker)
    fun doLoudSpeakerfunctionality() {
        if (callAudio.isSpeakerphoneOn) {
            callAudio.isSpeakerphoneOn = false
            imgvLoudSpeaker.isEnabled = false
        } else {
            imgvLoudSpeaker.isEnabled = true
            callAudio.isSpeakerphoneOn = true
        }
    }

    @OnClick(R.id.ll_mic)
    fun doVoiceMutefunctionality() {
        if (callAudio.isMicrophoneMute) {
            callAudio.isMicrophoneMute = false
            imgvMic.isEnabled = true
        } else {
            imgvMic.isEnabled = false
            callAudio.isMicrophoneMute = true
        }

    }
    lateinit var context : Context
    var callEndedByDriver : Boolean = false

    override fun permissionGranted(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {
        if (requestCodeForCallbackIdentificationCodeSubDivision == 1) {

            try {
                if (CabmeSinchService.sinchClient != null) {
                    callerId?.let { DebuggableLogD("calling user id", it) }
                    CabmeSinchService.call = CabmeSinchService.sinchClient!!.callClient.callUser(callerId)
                    initCallListener()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            try {
                CabmeSinchService.call.answer()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    fun finishThisActivity() {
        unRegisterProximitySensor()
       // unregisterNetworkChanges()
        finish()
    }

    fun unRegisterProximitySensor() {
        /* if(mSensorManager != null && mProximity !=null){
            mSensorManager.unregisterListener(proximitrySensorEventListener,mProximity);
            mSensorManager = null;
            mProximity = null;
        }*/

    }

    @OnClick(R.id.fab_dismiss, R.id.fab_end_call)
    fun cutTheCall() {
        callEndedByDriver = true
        if (callRingtone != null) {
            callRingtone!!.stop()
        }

        CabmeSinchService.call.hangup()

        finishThisActivity()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling_processing)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        //stopService(new Intent(this,SinchService.class));
        context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }

        callerId = intent.getStringExtra(KEY_CALLER_ID)

        CommonKeys.keyCaller=""
        callAudio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);*/

        dialog = commonMethods.getAlertDialog(this)
        initInternetBroadCastProcess()
        initView()

    }


    private fun initView() {
        val incommingPageType = intent.getIntExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CallActivityType.Ringing)


        if (incommingPageType == CallActivityType.Ringing) {
            DebuggableLogE("remote user id", CabmeSinchService.call.remoteUserId)
            callApiToGetCallerDetail(CabmeSinchService.call.remoteUserId)
            showRingingView()
            playIncommingCallRingtone()
            initCallListener()


        } else {
            callApiToGetCallerDetail(callerId.toString())
            playOutgoingRingtone()
            callToAppropreatePerson()
            showAnsweredView()
        }


    }

    private fun callApiToGetCallerDetail(userID: String) {
        apiService.getCallerDetail(sessionManager.accessToken!!, userID, "1").enqueue(RequestCallback(this))
    }

    private fun callToAppropreatePerson() {
        RuntimePermissionDialogFragment.checkPermissionStatus(this, supportFragmentManager, this, arrayOf(RuntimePermissionDialogFragment.RECORD_AUDIO_PERMISSION, RuntimePermissionDialogFragment.MODIFY_AUDIO_PERMISSION), RuntimePermissionDialogFragment.audioCallbackCode, 1)

    }

    private fun playOutgoingRingtone() {

        callConnectingSound = MediaPlayer.create(this@CallProcessingActivity, R.raw.outgoint_call_connection)
        //outGointRingtoneMediaplayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        callConnectingSound!!.isLooping = true
        callConnectingSound!!.start()

    }

    fun stopCallConnectingSound() {
        if (callConnectingSound != null) {
            callConnectingSound!!.release()
        }
    }

    fun stopCallRingtone() {
        if (callRingtone != null) {
            callRingtone!!.stop()
        }
    }

    private fun initCallListener() {
        CabmeSinchService.call.addCallListener(SinchCallListener())
    }

    private fun playIncommingCallRingtone() {
        defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        callRingtone = RingtoneManager.getRingtone(applicationContext, defaultRingtone)
        callRingtone!!.play()
    }

    fun showRingingView() {
        clCallRingingView.visibility = View.VISIBLE
        clCallAnsweredView.visibility = View.GONE
    }

    fun showAnsweredView() {
        clCallAnsweredView.visibility = View.VISIBLE
        clCallRingingView.visibility = View.GONE
        imgvLoudSpeaker.isEnabled = false
        imgvMic.isEnabled = true
    }


    private inner class SinchCallListener : CallListener {
        override fun onCallEnded(endedCall: Call) {
            if (outGointRingtoneMediaplayer != null) {
                outGointRingtoneMediaplayer!!.release()
            }
            if(endedCall.details.endCause.name.equals("DENIED")&&!callEndedByDriver){
                Toast.makeText(applicationContext,resources.getString(R.string.driver_busy),Toast.LENGTH_SHORT).show()
                //commonMethods.showMessage(context, dialog, resources.getString(R.string.driver_busy))
            }
            stopCallConnectingSound()
            volumeControlStream = AudioManager.USE_DEFAULT_STREAM_TYPE
            callAudio.isSpeakerphoneOn = false
            callAudio.isMicrophoneMute = false
            finishThisActivity()

        }

        override fun onCallEstablished(establishedCall: Call) {

            volumeControlStream = AudioManager.STREAM_VOICE_CALL
            CommonMethods.playVibration()

            if (outGointRingtoneMediaplayer != null) {
                outGointRingtoneMediaplayer!!.release()
            }

            showAnsweredView()
            callConnectionStatus.text = resources.getString(R.string.connected)
            runTimerForOnGoingCall()

        }

        override fun onCallProgressing(progressingCall: Call) {


            outGointRingtoneMediaplayer = MediaPlayer.create(this@CallProcessingActivity, R.raw.outgoing_ringtone)
            //outGointRingtoneMediaplayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            outGointRingtoneMediaplayer!!.isLooping = true
            outGointRingtoneMediaplayer!!.start()
            stopCallConnectingSound()

            callConnectionStatus.text = resources.getString(R.string.ringing)
        }

        override fun onShouldSendPushNotification(call: Call, pushPairs: List<PushPair>) {
            //don't worry about this right now


            /*NotificationResult result = sinchClient.relayRemotePushNotificationPayload(payload);
// handle result, e.g. show a notification for a missed call:
            if (result.isValid() && result.isCall()) {
                CallNotificationResult callResult = result.getCallResult();
                if (callResult.isCallCanceled()) {
                    // user-defined method to show notification
                    createNotification(callResult.getRemoteUserId());
                }
            }*/
        }
    }

    fun runTimerForOnGoingCall() {
        onGoingCallTimerChronometer.visibility = View.VISIBLE
        onGoingCallTimerChronometer.format = " %s"
        onGoingCallTimerChronometer.base = SystemClock.elapsedRealtime()
        onGoingCallTimerChronometer.start()
    }

    override fun onPause() {
        super.onPause()
        //mSensorManager.unregisterListener(proximitrySensorEventListener);
        stopCallRingtone()
        stopCallConnectingSound()
    }

    override fun onStop() {
        isThisActivityIsLive = false
        unRegisterProximitySensor()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
        // finishThisActivity()
        if (callRingtone != null) {
            callRingtone!!.stop()
        }
        try{
            CabmeSinchService.call.hangup()
        }catch (e:Exception){

        }


        finishThisActivity()
    }

    override fun onResume() {
        super.onResume()
        //mSensorManager.registerListener(proximitrySensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    fun turnOnScreen() {
        // turn on screen
        Log.v("ProximityActivity", "ON!")
        mPowerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = mPowerManager!!.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "callTag:myWaketag")
        mWakeLock!!.acquire()
    }

    @TargetApi(21) //Suppress lint error for PROXIMITY_SCREEN_OFF_WAKE_LOCK
    fun turnOffScreen() {
        // turn off screen
        Log.v("ProximityActivity", "OFF!")
        mWakeLock = mPowerManager!!.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "callTag:myWaketag")
        mWakeLock!!.acquire()
    }

    override fun onBackPressed() {
        //super.onBackPressed();
    }



    private fun initInternetBroadCastProcess() {


        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // checking for type intent filter
                if (intent.action == Config.NETWORK_CHANGES && !commonMethods.isOnline(this@CallProcessingActivity)) {

                    finishThisActivity()

                }
            }
        }


        registerNetworkBroadcastReceiver()
    }

    private fun registerNetworkBroadcastReceiver() {
        mNetworkReceiver = NetworkChangeReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver!!,
                IntentFilter(Config.NETWORK_CHANGES))
    }

    protected fun unregisterNetworkChanges() {
        try {

            if(mNetworkReceiver!=null)
            {
                unregisterReceiver(mNetworkReceiver)
            }
            if(mRegistrationBroadcastReceiver!=null)
            {
                unregisterReceiver(mRegistrationBroadcastReceiver)
            }

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }finally {
            mRegistrationBroadcastReceiver=null
            mNetworkReceiver=null
        }

    }

    companion object {
        private val SENSOR_SENSITIVITY = 4
    }
}
