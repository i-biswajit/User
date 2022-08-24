package com.cab.user.taxi

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage -
 * @category MainActivity
 * @author SMR IT Solutions
 *
 */

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.helper.Constants
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.sidebar.trips.YourTrips
import com.cab.user.taxi.views.customize.CustomDialog
import kotlinx.android.synthetic.main.app_activity_add_wallet.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/* ************************************************************
   To call Api for Schedule ride
    *********************************************************** */


class ScheduleRideDetailActivity : CommonActivity(), ServiceListener {

    lateinit var dialog: AlertDialog

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


    @BindView(R.id.trip_date_time)
    lateinit var tripDateTime: TextView

    @BindView(R.id.amountscheduled)
    lateinit var amountScheduled: TextView

    private lateinit var date: String
    private var overviewPolylines: String = ""
    private var locationId: String? = null
    private var peakId: String? = null
    private var clickedCar: String? = null
    private var pickupLocation: String? = null
    private var dropLocation: String? = null
    private var isWallet: String? = null
    private var fare: String? = null
    private var pickupLatitude: Double = 0.toDouble()
    private var pickupLongitude: Double = 0.toDouble()
    private var dropLatitude: Double = 0.toDouble()
    private var dropLongitude: Double = 0.toDouble()
    private var isInternetAvailable: Boolean = false
    private lateinit var scheduleHashMap: HashMap<String, String>

    @OnClick(R.id.back)
    fun back() {
        onBackPressed()
    }

    @OnClick(R.id.done_layout)
    fun done() {
        val finalDateTime = sessionManager.scheduleDate + " " + resources.getString(R.string.at) + " " + sessionManager.presentTime
        val sdf = SimpleDateFormat("YYYY-MM-dd '${resources.getString(R.string.at)}' HH:mm", Locale.getDefault())
        val currentDateandTime = sdf.format(Date())
        val aHeadCurrentDateandTime = sdf.format(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Constants.scheduledDuration.toLong())))
        try {
            val date: Date = sdf.parse(currentDateandTime)
            val selectedDate: Date = sdf.parse(finalDateTime)
            val aHeadCurrentDate: Date = sdf.parse(aHeadCurrentDateandTime)
            if (selectedDate.after(date) && (selectedDate.equals(aHeadCurrentDate) || selectedDate.after(aHeadCurrentDate))) {
                saveScheduleRide()
            } else {
                Toast.makeText(this, resources.getString(R.string.valid_time), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_schedule_ride_detail_)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        dialog = commonMethods.getAlertDialog(this)
        isInternetAvailable = commonMethods.isOnline(applicationContext)  //Check internet access

        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.UpcomingRideSet), common_header)

        println("thisSchedule" + intent.getStringExtra("is_wallet"))
        date = sessionManager.scheduleDateTime.toString()
        locationId = intent.getStringExtra("location_id")
        peakId = intent.getStringExtra("peak_id")
        clickedCar = intent.getStringExtra("clicked_car")
        pickupLocation = intent.getStringExtra("pickup_location")
        dropLocation = intent.getStringExtra("drop_location")
        isWallet = intent.getStringExtra("is_wallet")
        fare = intent.getStringExtra("fare_estimation")
        pickupLatitude = intent.getDoubleExtra("pickup_latitude", 0.0)
        pickupLongitude = intent.getDoubleExtra("pickup_longitude", 0.0)
        dropLatitude = intent.getDoubleExtra("drop_latitude", 0.0)
        dropLongitude = intent.getDoubleExtra("drop_longitude", 0.0)
        overviewPolylines = intent.getStringExtra("polyline").toString()
        tripDateTime.text = date
        amountScheduled.text = sessionManager.currencySymbol + fare

    }

    /**
     * To save schedule by calling api
     */

    private fun saveScheduleRide() {
        if (isInternetAvailable) {
            sessionManager.isrequest = false
            val tz = TimeZone.getDefault()
            scheduleHashMap = HashMap()

            scheduleHashMap["schedule_date"] = sessionManager.scheduleDate.toString()
            scheduleHashMap["schedule_time"] = sessionManager.presentTime.toString()
            scheduleHashMap["car_id"] = clickedCar.toString()
            scheduleHashMap["pickup_latitude"] = pickupLatitude.toString()
            scheduleHashMap["pickup_longitude"] = pickupLongitude.toString()
            scheduleHashMap["drop_latitude"] = dropLatitude.toString()
            scheduleHashMap["drop_longitude"] = dropLongitude.toString()
            scheduleHashMap["pickup_location"] = pickupLocation.toString()
            scheduleHashMap["drop_location"] = dropLocation.toString()
            scheduleHashMap["payment_method"] = sessionManager.paymentMethod.toString()
            scheduleHashMap["is_wallet"] = isWallet.toString()
            scheduleHashMap["user_type"] = sessionManager.type.toString()
            scheduleHashMap["device_type"] = sessionManager.deviceType.toString()
            scheduleHashMap["device_id"] = sessionManager.deviceId.toString()
            scheduleHashMap["token"] = sessionManager.accessToken.toString()
            scheduleHashMap["timezone"] = tz.id
            scheduleHashMap["polyline"] = overviewPolylines
            scheduleHashMap["location_id"] = locationId.toString()
            scheduleHashMap["peak_id"] = peakId.toString()
            scheduleRide()
        } else {
            commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))
        }
    }


    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        if (jsonResp.isSuccess) {
            commonMethods.hideProgressDialog()
            val intent = Intent(this@ScheduleRideDetailActivity, YourTrips::class.java)
            intent.putExtra("upcome", "upcome")
            startActivity(intent)
            finish()
        } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            commonMethods.hideProgressDialog()
        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
        commonMethods.hideProgressDialog()
    }

    /**
     * Call ScheduleRide API
     */
    private fun scheduleRide() {
        commonMethods.showProgressDialog(this)
        apiService.scheduleRide(scheduleHashMap).enqueue(RequestCallback(this))
    }
}