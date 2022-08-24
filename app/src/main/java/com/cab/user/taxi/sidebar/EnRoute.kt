package com.cab.user.taxi.sidebar

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage Side_Bar
 * @category EnRoute
 * @author SMR IT Solutions
 * 
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.taxi.datamodels.trip.Riders
import com.cab.user.taxi.datamodels.trip.TripDetailsModel
import com.cab.user.common.network.AppController
import com.cab.user.common.pushnotification.Config
import com.cab.user.common.pushnotification.NotificationUtils
import com.cab.user.taxi.sendrequest.CancelYourTripActivity
import com.cab.user.common.utils.CommonKeys.KEY_CALLER_ID
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.views.CommonActivity
import kotlinx.android.synthetic.main.app_activity_add_wallet.*
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/* ************************************************************
  Driver start the trip map and route shown
    *********************************************************** */
class EnRoute : CommonActivity() {
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @BindView(R.id.cancel)
    lateinit var cancel: RelativeLayout
    @BindView(R.id.cancel_txt)
    lateinit var cancel_txt: TextView
    @BindView(R.id.driver_name)
    lateinit var driver_name: TextView
    @BindView(R.id.arrivel_time)
    lateinit var arrivel_time: TextView
    @BindView(R.id.vehicle_name)
    lateinit var vehicle_name: TextView
    @BindView(R.id.starrating)
    lateinit var starrating: TextView
    @BindView(R.id.driver_car_number)
    lateinit var driver_car_number: TextView
    @BindView(R.id.arrival_txt)
    lateinit var arrival_txt: TextView
    @BindView(R.id.pickup_location)
    lateinit var pickup_location: TextView
    @BindView(R.id.profile_image1)
    lateinit var driver_profile_image: ImageView
    @BindView(R.id.common_profile)
    lateinit var commonProfile : View

    var totalDuration = ""

    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null

    @OnClick(R.id.arrow)
    fun onback() {
        onBackPressed()
    }

    @OnClick(R.id.cancel)
    fun cancel() {
        val intent = Intent(this, CancelYourTripActivity::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.contactlayout)
    fun contactlayout() {
        val intent = Intent(this, DriverContactActivity::class.java)
        intent.putExtra("drivername", tripDetailsModel.driverName)
        intent.putExtra("drivernumber", tripDetailsModel.mobileNumber)
        intent.putExtra(KEY_CALLER_ID, tripDetailsModel.driverId.toString())
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_en_route)
        val extras = intent.extras
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)

        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.enrote),common_header)
        /**
         * Get accepted driver details
         */

        if (extras != null) {
            tripDetailsModel = intent.getSerializableExtra("driverDetails") as TripDetailsModel //Obtaining data
            riderdetails= tripDetailsModel.riders.get(0)
            totalDuration = intent.getStringExtra("duration").toString()
        }

        /**
         * Receive push notification
         */
        receivePushNotification()


        /**
         * Show driver details
         */
        insertDriverInfoToSession()
        driver_name.text = tripDetailsModel.driverName


        /*if (Integer.parseInt(tripDetailsModel.getArrivalTime()) > 1) {
            arrivel_time.setText(tripDetailsModel.getArrivaltime() + " " + getResources().getString(R.string.mins));
        } else {
            arrivel_time.setText(tripDetailsModel.getArrivaltime() + " " + getResources().getString(R.string.min));
        }*/
        vehicle_name.text = tripDetailsModel.vehicleName

        if (tripDetailsModel.rating == "" || tripDetailsModel.rating == "0.0") {
            starrating.visibility = View.GONE
        } else {
            starrating.text = tripDetailsModel.rating

        }
        driver_car_number.text = tripDetailsModel.vehicleNumber


     /*   if (tripDetailsModel.arrivalTime > 1) {
            arrival_txt.text = tripDetailsModel.arrivalTime.toString() + " " + resources.getString(R.string.mins)
        } else {
            arrival_txt.text = tripDetailsModel.arrivalTime.toString() + " " + resources.getString(R.string.min)
        }*/

        var etaText: String

        val c = Calendar.getInstance()
        c.add(Calendar.MINUTE, tripDetailsModel.arrivarTime)
        val sdf = SimpleDateFormat("hh:mm a")
        val arTime = sdf.format(c.time)

        println("sessionManager.tripStatus ${sessionManager.tripStatus}")
        if (!TextUtils.isEmpty(totalDuration)) {

            if (sessionManager.tripStatus.equals("begin_trip", ignoreCase = true)|| sessionManager.tripStatus.equals("end_trip", ignoreCase = true)) {
                arrival_txt.text = totalDuration+ " " + resources.getString(R.string.to_reach)

            } else {
                arrival_txt.text = totalDuration+ " " + resources.getString(R.string.to_arrive)
            }

        }else{
            if (sessionManager.tripStatus.equals("begin_trip", ignoreCase = true) || sessionManager.tripStatus.equals("end_trip", ignoreCase = true)) {
                arrival_txt.text =resources.getQuantityString(R.plurals.minutes,1,1)+ resources.getString(R.string.to_reach)

            } else {
                arrival_txt.text ==resources.getQuantityString(R.plurals.minutes,1,1)+ resources.getString(R.string.to_arrive)
            }
        }
        if (sessionManager.tripStatus.equals("begin_trip", ignoreCase = true)|| sessionManager.tripStatus.equals("end_trip", ignoreCase = true)) {
            pickup_location.text = riderdetails.drop
        }else {
            pickup_location.text = riderdetails.pickup
        }

        Picasso.get().load(tripDetailsModel.driverThumbImage)
                .into(driver_profile_image)


        if ((sessionManager.tripStatus != ""
                        //|| !sessionManager.getTripStatus().equals("null")
                        && sessionManager.tripStatus == "begin_trip") || sessionManager.tripStatus == "end_trip") {
            cancel.isClickable = false
            cancel.isEnabled = false
            cancel_txt.setTextColor(ContextCompat.getColor(this,R.color.cancel_text_color))
        } else {
            cancel.isClickable = true
            cancel.isEnabled = true
            cancel_txt.setTextColor(ContextCompat.getColor(this,R.color.app_primary_text))
        }

    }

    fun insertDriverInfoToSession() {
        sessionManager.driverProfilePic = tripDetailsModel.driverThumbImage
        sessionManager.driverRating = tripDetailsModel.rating
        sessionManager.driverName = tripDetailsModel.driverName
        sessionManager.driverId = tripDetailsModel.driverId.toString()
    }

    /**
     * Receive push notification
     */
    fun receivePushNotification() {
        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // checking for type intent filter
                if (intent.action == Config.REGISTRATION_COMPLETE) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL)


                } else if (intent.action == Config.PUSH_NOTIFICATION) {
                    // new push notification is received

                    val JSON_DATA = sessionManager.pushJson


                    try {
                        val jsonObject = JSONObject(JSON_DATA)

                        if (jsonObject.getJSONObject("custom").has("begin_trip")) {
                            //String trip_id = jsonObject.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id");
                            cancel.isClickable = false
                            cancel.isEnabled = false
                            cancel_txt.setTextColor(ContextCompat.getColor(context,R.color.cancel_text_color))

                        }


                    } catch (e: JSONException) {

                    }

                }
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
    }

    public override fun onResume() {


        super.onResume()

        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver!!,
                IntentFilter(Config.REGISTRATION_COMPLETE))

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver!!,
                IntentFilter(Config.PUSH_NOTIFICATION))

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
    }

    companion object {

        lateinit var tripDetailsModel: TripDetailsModel
        lateinit var riderdetails: Riders

    }


}
