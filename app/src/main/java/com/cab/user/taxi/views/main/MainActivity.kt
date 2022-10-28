package com.cab.user.taxi.views.main

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage -
 * @category MainActivity
 * @author SMR IT Solutions
 *
 */

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.net.http.AndroidHttpClient
import android.os.*
import android.provider.Settings
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.custompalette.CustomTypefaceSpan
import com.cab.user.common.database.SqLiteDb
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.drawpolyline.DirectionsJSONParser
import com.cab.user.common.helper.Constants
import com.cab.user.common.helper.Constants.Req_TYPE_NORMAL
import com.cab.user.common.helper.Constants.Req_TYPE_POOL
import com.cab.user.common.helper.Constants.scheduledDuration
import com.cab.user.common.helper.LatLngInterpolator
import com.cab.user.common.helper.Permission
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.map.AppUtils
import com.cab.user.common.network.AppController
import com.cab.user.common.placesearch.InfoWindowData
import com.cab.user.common.pushnotification.Config
import com.cab.user.common.pushnotification.NotificationUtils
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogD
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogE
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogI
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogV
import com.cab.user.common.utils.CommonMethods.Companion.startChatActivityFrom
import com.cab.user.common.utils.Enums.REG_GET_PAYMENTMETHOD
import com.cab.user.common.utils.Enums.REQ_COMMON_DATA
import com.cab.user.common.utils.Enums.REQ_GET_DRIVER
import com.cab.user.common.utils.Enums.REQ_GET_RIDER_PROFILE
import com.cab.user.common.utils.Enums.REQ_NEAREST_CARS
import com.cab.user.common.utils.Enums.REQ_SEARCH_CARS
import com.cab.user.common.utils.Enums.REQ_SEND_REQUEST
import com.cab.user.common.utils.Enums.REQ_TRIP_DETAIL
import com.cab.user.common.utils.Enums.REQ_UPDATE_LOCATION
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.utils.RuntimePermissionDialogFragment
import com.cab.user.common.views.CommonActivity
import com.cab.user.common.views.SupportActivityCommon
import com.cab.user.taxi.ScheduleRideDetailActivity
import com.cab.user.taxi.adapters.CarDetailsListAdapter
import com.cab.user.taxi.adapters.SeatsListAdapter
import com.cab.user.taxi.database.AddFirebaseDatabase
import com.cab.user.taxi.datamodels.DriverLocation
import com.cab.user.taxi.datamodels.PaymentMethodsModel
import com.cab.user.taxi.datamodels.RiderProfile
import com.cab.user.taxi.datamodels.StepsClass
import com.cab.user.taxi.datamodels.main.NearByCarsModel
import com.cab.user.taxi.datamodels.main.NearestCar
import com.cab.user.taxi.datamodels.trip.Riders
import com.cab.user.taxi.datamodels.trip.TripDetailsModel
import com.cab.user.taxi.firebase_keys.FirebaseDbKeys
import com.cab.user.taxi.firebase_keys.FirebaseDbKeys.LIVE_TRACKING_NODE
import com.cab.user.taxi.firebase_keys.FirebaseDbKeys.TRIP_PAYMENT_NODE
import com.cab.user.taxi.sendrequest.DriverRatingActivity
import com.cab.user.taxi.sendrequest.PaymentAmountPage
import com.cab.user.taxi.sendrequest.SendingRequestActivity
import com.cab.user.taxi.sidebar.EnRoute
import com.cab.user.taxi.sidebar.FareBreakdown
import com.cab.user.taxi.sidebar.Profile
import com.cab.user.taxi.sidebar.Setting
import com.cab.user.taxi.sidebar.payment.AddWalletActivity
import com.cab.user.taxi.sidebar.payment.PaymentPage
import com.cab.user.taxi.sidebar.referral.ShowReferralOptions
import com.cab.user.taxi.sidebar.trips.YourTrips
import com.cab.user.taxi.singledateandtimepicker.SingleDateAndTimePicker
import com.cab.user.taxi.views.customize.CustomDialog
import com.cab.user.taxi.views.emergency.EmergencyContact
import com.cab.user.taxi.views.emergency.SosActivity
import com.cab.user.taxi.views.main.filter.FeatureSelectListener
import com.cab.user.taxi.views.main.filter.FeaturesInCarModel
import com.cab.user.taxi.views.main.filter.FeaturesInVehicleAdapter
import com.cab.user.taxi.views.peakPricing.PeakPricing
import com.cab.user.taxi.views.search.PlaceSearchActivity
import com.cab.user.taxi.views.splash.SplashActivity.Companion.checkVersionModel
import com.cab.user.common.network.PermissionCamer
import kotlinx.android.synthetic.main.app_bar_home_toolbar.*
import kotlinx.android.synthetic.main.app_content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.Serializable
import java.net.URL
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


/* ************************************************************
  After rider login this page will shown its main page for rider
   *********************************************************** */

class MainActivity : CommonActivity(), SeatsListAdapter.OnClickListener, NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ServiceListener, RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback, FeatureSelectListener {
    @Inject
    lateinit var dbHelper: SqLiteDb
    private var isViewUpdatedWithLocalDB: Boolean = false

    /**
     * Coroutine variables
     */


    val uiScope = CoroutineScope(Dispatchers.Main)


    var driveridlist = ArrayList<String>()
    private var movepoints = ArrayList<LatLng>()
    var polylinepoints = ArrayList<LatLng>()
    lateinit var polylineOptions: PolylineOptions
    var overviewPolylines: String = ""
    val ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(MainActivity::class.java.name)
    internal lateinit var scheduleIntent: Intent
    internal var isSchedule = ""
    internal var scheduleFilter = "0"
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

    @BindView(R.id.rideicon)
    lateinit var rideicon: ImageView

    @BindView(R.id.tv_waiting_charge_fee_for_accepted_car)
    lateinit var tvWaititingChargeFeeForAcceptedCar: TextView

    @BindView(R.id.whereto_and_schedule)
    lateinit var whereto_and_schedule: RelativeLayout

    @BindView(R.id.rlt_change_location)
    lateinit var rltChangeLocation: RelativeLayout

    @BindView(R.id.rlt_welcome)
    lateinit var rltWelcome: RelativeLayout

    @BindView(R.id.tv_change_location)
    lateinit var tvChangeLocation: TextView

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.nav_view)
    lateinit var navigationView: NavigationView

    @BindView(R.id.drawer_layout)
    lateinit var drawer: DrawerLayout

    @BindView(R.id.whereto)
    lateinit var whereto: TextView        // Where to search page button

    @BindView(R.id.tvAppVersion)
    lateinit var appVersion: TextView

    @BindView(R.id.no_car_txt)
    lateinit var no_car_txt: TextView

    @BindView(R.id.car_details_list)
    lateinit var listView: RecyclerView        // Search car list

    @BindView(R.id.paymentmethod)
    lateinit var paymentmethod: RelativeLayout        // Payment method selection layout

    @BindView(R.id.paymentmethod_img)
    lateinit var paymentmethod_img: ImageView        // Cash or paypal iamge show for payment method

    @BindView(R.id.wallet_img)
    lateinit var wallet_img: ImageView        // If wallet choose show in image

    @BindView(R.id.sos)
    lateinit var sos: TextView

    @BindView(R.id.paymentmethod_type)
    lateinit var paymentmethod_type: TextView// Payment method type show in text view

    @BindView(R.id.pickup_location)
    lateinit var pickup_location: TextView

    @BindView(R.id.driver_name)
    lateinit var driver_name: TextView

    @BindView(R.id.driver_car_name)
    lateinit var driver_car_name: TextView

    @BindView(R.id.driver_rating)
    lateinit var driver_rating: TextView

    @BindView(R.id.driver_car_number)
    lateinit var driver_car_number: TextView

    @BindView(R.id.driver_otp)
    lateinit var tvDriverOTP: TextView

    @BindView(R.id.profile_image1)
    lateinit var driver_image: ImageView

    @BindView(R.id.rlt_navi)
    lateinit var arriveTimeImage: RelativeLayout

    @BindView(R.id.fab_startChat)
    lateinit var startChat: TextView

    @BindView(R.id.iv_filter)
    lateinit var ivFilter: ImageView

    @BindView(R.id.tv_welcome_text)
    lateinit var tvWelcomeText: TextView

    var polylinefromFirebase: String = ""
    var updateRouteFromPush: Boolean = false
    var isPushReceived: Boolean = false
    var isDriverForeground: Boolean = false

    var pickuplocation: Location? = null
    var etatime: String = "1"
    var seatCount: String = "1"
    var ETACalculatingwithDistance: Double = 0.0
    var EtaTime: String = "1"

    var isDownloadUrl = false

    private var mLastClickTime: Long = 0

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    @BindView(R.id.meetlayout)
    lateinit var meetlayout: RelativeLayout        // Show address after trip start

    @BindView(R.id.bottomlayout)
    lateinit var bottomlayout: RelativeLayout//Search car bottom layout

    @BindView(R.id.bottomlayout1)
    lateinit var bottomlayout1: RelativeLayout        //Search car bottom layout

    @BindView(R.id.content_main)
    lateinit var content_main: RelativeLayout

    @BindView(R.id.no_car)
    lateinit var no_car: RelativeLayout

    @BindView(R.id.loading_car)
    lateinit var loading_car: RelativeLayout

    @BindView(R.id.requestubers)
    lateinit var requestubers: RelativeLayout

    @BindView(R.id.edit_map)
    lateinit var edit_map: RelativeLayout

    @BindView(R.id.scheduleride)
    lateinit var scheduleride: ImageView        // Schedule ride button

    @BindView(R.id.requestuber)
    lateinit var requestuber: TextView        // Reqest button

    @BindView(R.id.tv_waiting_charge_fee)
    lateinit var tvWaitingChargeInfo: TextView        // waiting charge

    @BindView(R.id.textview1)
    lateinit var textView1: TextView

    @BindView(R.id.activity_fare_estimation)
    lateinit var request_view: ViewGroup//Fare estimate

    @BindView(R.id.layout_request_seat)
    lateinit var show_seat_availability: ViewGroup

    @BindView(R.id.rlt_contact_admin)
    lateinit var rltContactAdmin: RelativeLayout

    @BindView(R.id.tv_eta)
    lateinit var tvEta: TextView

    @BindView(R.id.pool_rate)
    lateinit var poolRate: TextView

    lateinit var username: TextView
    lateinit var userprofile: ImageView
    lateinit var singleDateAndTimePicker: SingleDateAndTimePicker
    lateinit var scheduleridetext: TextView
    lateinit var scheduleride_text: TextView
    lateinit var locationHashMap: HashMap<String, String>
    lateinit var now: Date
    lateinit var carmarker: Marker
    private lateinit var auth: FirebaseAuth
    var i = 0
    var startbear = 0f
    var endbear = 0f
    var totalcar = 0
    var count1 = 0
    lateinit var marker: Marker
    lateinit var trip_path: String
    lateinit var driverlatlng: LatLng
    var clickedcar: String? = null // = "2";
    var clickeddriverid: String? = null // = "2";
    var isPeakPriceAppliedForClickedCar = CommonKeys.NO
    lateinit var minimumFareForClickedCar: String
    lateinit var perKMForClickedCar: String
    lateinit var perMinForClickedCar: String
    lateinit var peakPriceforClickedCar: String
    lateinit var peakPriceIdForClickedCar: String
    lateinit var locationIDForSelectedCar: String
    lateinit var peakIDForSelectedCar: String
    lateinit var riderProfile: RiderProfile


    var clickedCarName: String = ""
    lateinit var waitingMin: String
    lateinit var waitingAmount: String
    lateinit var nearest_carObj: JSONObject
    var latLong: LatLng? = null
    var first_name: String? = ""
    var last_name: String? = ""
    var profile_image: String? = ""
    var mobile_number: String? = ""
    var country_code: String? = ""
    var email_id: String? = ""
    var country: String? = null
    var pickupaddresss: String? = ""
    var dropaddress: String? = ""
    lateinit var mapFragment: SupportMapFragment
    private var mLastLocation: Location? = null
    lateinit var mContext: Context
    var carmarkers: MutableList<Marker> = ArrayList()
    lateinit var addressList: MutableList<Address>
    var address: String? = null
    lateinit var countrys: String
    var getLocations: LatLng? = null
    var nearestCars = ArrayList<NearestCar>()
    lateinit var adapter: CarDetailsListAdapter
    var firstloop = true
    var samelocation = false
    var lastposition: Int = 0// = 1;
    var peekPricePercentage: String = ""
    var peekPrice: Float = 0F
    var alreadyRunning = true
    var polyline: Polyline? = null
    lateinit var bottomUp: Animation
    lateinit var slide_let_Right: Animation
    lateinit var bottomDown: Animation
    var geturlCount = 0
    var isPooled = true
    var isPooledOnTrip = false
    var totalMins: String = ""

    var stepPointsList = ArrayList<StepsClass>()
    var overallDuration = 0

    lateinit var toggle: ActionBarDrawerToggle
    var isRequest = false
    var isTrip = false
    var isActivityCreated = false
    var isTripBegin = false
    var home: String? = null
    var work: String? = null
    var speed = 13f
    var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0F, 1F)
    lateinit var query: Query
    lateinit var querypolyline: Query
    lateinit var queryEta: Query
    lateinit var headerview: View
    lateinit var profilelayout: LinearLayout
    var markerPoints: ArrayList<LatLng>? = null
    protected var isInternetAvailable: Boolean = false
    private var backPressed = 0    // used by onBackPressed()
    private lateinit var mMap: GoogleMap
    internal lateinit var slideUpAnimation: Animation
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var mToolBarNavigationListenerIsRegistered = false
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    private var lastLocation: Location? = null
    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseDatabasePolylines: DatabaseReference
    private var mSearchedLocationReferenceListener: ValueEventListener? = null
    private var mSearchedpolylineReferenceListener: ValueEventListener? = null
    private var mSearchedEtaReferenceListener: ValueEventListener? = null
    private lateinit var caramount: String
    private lateinit var finalDay: String
    private lateinit var time: String
    private var date: String? = null
    internal var delay = 0 // delay for 0 sec.
    internal var periodDirection = 15000 // repeat every 15 sec.
    private lateinit var timerDirection: Timer
    private lateinit var timerDirectionTask: TimerTask
    var isCheckDirection = true
    private val handler = Handler()
    var isEtaFromPolyline = false

    // Creating MarkerOptions
    internal var pickupOptions = MarkerOptions()
    var ref: DatabaseReference? = null
    var geoFire: GeoFire? = null
    var geoQuery: GeoQuery? = null
    var geoqueryfor_filtercars: GeoQuery? = null
    private val addFirebaseDatabase = AddFirebaseDatabase()
    lateinit var mindate: Date
    lateinit var mintime: String
    lateinit var presenttime: String
    lateinit var presentDatetime: Date
    lateinit var scheduleupdatedTime: String


    @OnClick(R.id.rlt_contact_admin)
    fun setRltContactAdmin() {
        if (!TextUtils.isEmpty(sessionManager.adminContact)) {
            createAdminpopup(1)
        } else {
            createAdminpopup(0)
        }
    }

    @OnClick(R.id.layout_request_seat)
    fun rltSeatClick() {

    }

    @OnClick(R.id.fab_startChat)
    fun startChatActivity() {
        startChatActivityFrom(this)
    }

    @OnClick(R.id.sos)
    fun sos() {
        val intent = Intent(this, SosActivity::class.java)
        intent.putExtra("latitude", latLong!!.latitude)
        intent.putExtra("longitude", latLong!!.longitude)
        startActivity(intent)
    }

    @OnClick(R.id.back_pool)
    fun backPool() {
        bottomDown = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_down)
        layout_request_seat.startAnimation(bottomDown)
        layout_request_seat.visibility = View.GONE
    }

    @OnClick(R.id.tv_change_location)
    fun editMap() {
        println("pickupaddresss " + pickupaddresss)

        if (latLong == null)
            return

        val intent = Intent(this, PlaceSearchActivity::class.java)
        intent.putExtra("Latlng", latLong)
        intent.putExtra("Country", country)
        intent.putExtra("PickupAddress", pickupaddresss)
        intent.putExtra("DropAddress", dropaddress)
        intent.putExtra("PickupDrop", markerPoints)
        intent.putExtra("Home", home)
        if (TextUtils.isEmpty(sessionManager.scheduleDate)) {
            intent.putExtra("Schedule", "")
        } else {
            intent.putExtra("date", sessionManager.scheduledDateAndTime)
            intent.putExtra("Schedule", "Schedule")
        }
        intent.putExtra("Work", work)
        startActivity(intent)
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
    }

    @OnClick(R.id.scheduleride)
    fun schedulerideSearchCar() {
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (!isInternetAvailable) {
            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
        } else {
            val pickup = markerPoints?.get(0)
            pickuplocation = Location("")
            pickuplocation!!.latitude = pickup?.latitude!!
            pickuplocation!!.longitude = pickup?.longitude!!
            val drop = markerPoints?.get(1)
            // drawRoute(markerPoints)
            if (!sessionManager.scheduleDate.toString().equals(""))
                scheduleFilter = "1"
            else
                scheduleFilter = "0"

            locationHashMap = HashMap()
            locationHashMap["pickup_latitude"] = pickup?.latitude.toString()
            locationHashMap["pickup_longitude"] = pickup?.longitude.toString()
            locationHashMap["drop_latitude"] = drop?.latitude.toString()
            locationHashMap["drop_longitude"] = drop?.longitude.toString()
            locationHashMap["schedule_date"] = sessionManager.scheduleDate.toString()
            locationHashMap["schedule_time"] = sessionManager.presentTime.toString()
            locationHashMap["user_type"] = sessionManager.type.toString()
            locationHashMap["token"] = sessionManager.accessToken.toString()
            locationHashMap["timezone"] = TimeZone.getDefault().id
            locationHashMap["polyline"] = overviewPolylines
            locationHashMap["is_schedule"] = scheduleFilter

            searchCars()
            requestubers.visibility = View.INVISIBLE
            edit_map.visibility = View.INVISIBLE
            rltContactAdmin.visibility = View.GONE
            requestuber.visibility = View.INVISIBLE
            listView.visibility = View.GONE
            loading_car.visibility = View.VISIBLE
            no_car.visibility = View.GONE
            requestuber.isEnabled = false
            hideWaitingTimeDescription()
        }

    }


    @OnClick(R.id.confirm_seat)
    fun PoolRequest() {
        if (sessionManager.isCovidFeature!!) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            covidFeatureDialog(Req_TYPE_POOL)
        } else {
            isPooled = false
            requestCar()
        }

    }

    @OnClick(R.id.requestuber)
    fun requestuber() {
        if (isPooled) {
            poolUi()
        } else {
            if (sessionManager.isCovidFeature!!) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                covidFeatureDialog(Req_TYPE_NORMAL)
            } else {
                seatCount = ""
                requestCar()
            }
        }


    }


    fun requestCar() {
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (!isInternetAvailable) {
            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
        } else {
            val iswallet: String
            val pickup = markerPoints?.get(0)
            pickuplocation = Location("")
            pickuplocation!!.latitude = pickup?.latitude!!
            pickuplocation!!.longitude = pickup?.longitude!!
            val drop = markerPoints?.get(1)
            if ("Schedule" == intent.getStringExtra("Schedule")) {
                if ("" == pickupaddresss || "" == dropaddress) {
                    commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.Pickup_or_Drop_Address_missing_please_try_again))
                } else {
                    pickupaddresss = pickupaddresss!!.replace("null".toRegex(), "")
                    dropaddress = dropaddress!!.replace("null".toRegex(), "")


                    if (sessionManager.isWallet) {
                        if (java.lang.Float.valueOf(sessionManager.walletAmount) > 0)
                            iswallet = "Yes"
                        else
                            iswallet = "No"
                    } else {
                        iswallet = "No"
                    }
                    println("ScheduleDetails" + clickedcar + iswallet)
                    scheduleIntent = Intent(this@MainActivity, ScheduleRideDetailActivity::class.java)
                    scheduleIntent.putExtra("Schedule", "Schedule")
                    scheduleIntent.putExtra("clicked_car", clickedcar)
                    scheduleIntent.putExtra("pickup_latitude", pickup?.latitude)
                    scheduleIntent.putExtra("pickup_longitude", pickup?.longitude)
                    scheduleIntent.putExtra("drop_latitude", drop?.latitude)
                    scheduleIntent.putExtra("drop_longitude", drop?.longitude)
                    scheduleIntent.putExtra("fare_estimation", caramount)
                    scheduleIntent.putExtra("pickup_location", pickupaddresss)
                    scheduleIntent.putExtra("drop_location", dropaddress)
                    scheduleIntent.putExtra("location_id", locationIDForSelectedCar)
                    scheduleIntent.putExtra("peak_id", peakIDForSelectedCar)
                    scheduleIntent.putExtra("polyline", overviewPolylines)
                    scheduleIntent.putExtra("is_wallet", iswallet)

                    isSchedule = CommonKeys.ISSCHEDULE
                    if (isPeakPriceAppliedForClickedCar == CommonKeys.YES) {
                        startPeakPriceingActivity()
                    } else {
                        startActivity(scheduleIntent)
                    }
                }
            } else {
                if ("" == pickupaddresss || "" == dropaddress) {
                    commonMethods.showMessage(this@MainActivity, dialog, "Pickup or Drop Address missing please try again...")
                } else {
                    pickupaddresss = pickupaddresss!!.replace("null".toRegex(), "")
                    dropaddress = dropaddress!!.replace("null".toRegex(), "")


                    if (sessionManager.isWallet) {
                        if (java.lang.Float.valueOf(sessionManager.walletAmount) > 0)
                            iswallet = "Yes"
                        else
                            iswallet = "No"
                    } else {
                        iswallet = "No"
                    }
                    println("paymentMethod" + sessionManager.paymentMethod)
                    if (sessionManager.paymentMethodkey!!.isEmpty() || (sessionManager.paymentMethodkey!!.contains(resources.getString(R.string.stripe)) && !sessionManager.paymentMethodkey.equals(CommonKeys.PAYMENT_CARD))) {
                        Toast.makeText(applicationContext, resources.getString(R.string.choose_payment_type), Toast.LENGTH_SHORT).show()
                    } else {
                        val tz = TimeZone.getDefault()


                        println("seat Count: $seatCount")
                        locationHashMap = HashMap()
                        locationHashMap["car_id"] = clickedcar.toString()
                        locationHashMap["pickup_latitude"] = pickup?.latitude.toString()
                        locationHashMap["pickup_longitude"] = pickup?.longitude.toString()
                        locationHashMap["drop_latitude"] = drop?.latitude.toString()
                        locationHashMap["drop_longitude"] = drop?.longitude.toString()
                        locationHashMap["pickup_location"] = pickupaddresss!!
                        locationHashMap["seat_count"] = seatCount
                        locationHashMap["drop_location"] = dropaddress!!
                        locationHashMap["payment_method"] = sessionManager.paymentMethodkey.toString()
                        locationHashMap["is_wallet"] = iswallet
                        locationHashMap["user_type"] = sessionManager.type.toString()
                        locationHashMap["device_type"] = sessionManager.deviceType.toString()
                        locationHashMap["device_id"] = sessionManager.deviceId.toString()
                        locationHashMap["token"] = sessionManager.accessToken.toString()
                        locationHashMap["timezone"] = tz.id
                        locationHashMap["location_id"] = locationIDForSelectedCar
                        locationHashMap["polyline"] = overviewPolylines
                        if (isPeakPriceAppliedForClickedCar == CommonKeys.YES) {
                            startPeakPriceingActivity()
                        } else {

                            if (!isPooled) {
                                sendRequest()
                            } else {
                                val seatslist = ArrayList<String>()
                                seatslist.add("1")
                                seatslist.add("2")
                                val adapter = SeatsListAdapter(this, this, seatslist)
                                rv_seatlist.adapter = adapter
                                adapter.notifyDataSetChanged()

                                slide_let_Right = AnimationUtils.loadAnimation(applicationContext, // Animation for bottom up and down
                                        R.anim.slide_left_right)
                                layout_request_seat.startAnimation(slide_let_Right)
                                layout_request_seat.visibility = View.VISIBLE

                            }


                        }
                    }


                }
            }
        }
    }

    fun poolUi() {
        bottomUp = AnimationUtils.loadAnimation(applicationContext, // Animation for bottom up and down
                R.anim.bottom_up)
        show_seat_availability.startAnimation(bottomUp)
        show_seat_availability.visibility = View.VISIBLE
        layout_request_seat.visibility = View.VISIBLE
        var seatslist = ArrayList<String>()
        seatslist.add("1")
        seatslist.add("2")
        var adapter = SeatsListAdapter(this, this, seatslist)
        rv_seatlist.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    @OnClick(R.id.whereto)
    fun whereto() {
        /**
         * Where to button for search car
         */
        sessionManager.scheduleDate = ""
        sessionManager.presentTime = ""
        schedulewhere("", "")
    }

    fun schedulewhere(date: String, sched: String) {

        if (latLong == null)
            return

        val intent = Intent(this, PlaceSearchActivity::class.java)
        intent.putExtra("Latlng", latLong)
        intent.putExtra("Country", country)
        intent.putExtra("Home", home)
        intent.putExtra("date", date)
        intent.putExtra("Schedule", sched)
        intent.putExtra("Work", work)
        intent.putExtra("PickupAddress", sessionManager.currentAddress)
        //   intent.putExtra("PickupAddress", pickupaddresss)
        intent.putExtra("DropAddress", dropaddress)
        startActivity(intent)
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
    }

    @OnClick(R.id.drivewithuber)
    fun drivewithuber() {
        /**
         * Open driver app or Google play link
         */
        val managerclock = packageManager
        var i = managerclock.getLaunchIntentForPackage(resources.getString(R.string.package_driver))

        if (i == null) {
            i = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + resources.getString(R.string.package_driver)))
        } else {
            i.addCategory(Intent.CATEGORY_LAUNCHER)

        }
        startActivity(i)
    }

    @OnClick(R.id.rideicon)
    fun ride() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        scheduleRide()//schedule ride date picker
        scheduleMethod()
    }

    @OnClick(R.id.bottomlayout1)
    fun bottomlayout() {
        /**
         * Driver details after trip started
         */
        println("sessionManager.tripStatus ${sessionManager.tripStatus}")
        val intent = Intent(this, EnRoute::class.java)
        intent.putExtra("driverDetails", tripDetailsModel)
        intent.putExtra("duration", tvEta.text.toString())
        startActivity(intent)
    }

    @OnClick(R.id.paymentmethod_change)
    fun paymentchange() {
        /**
         * Payment method Changed
         */
        gotoPaymentpage()

    }

    private lateinit var rvfilterList: RecyclerView
    private lateinit var tvsaveFilter: TextView
    private lateinit var featuresInVehicleAdapter: FeaturesInVehicleAdapter
    private var selectedIds = LinkedHashSet<Int>()
    private var isRequestView = false

    private lateinit var pickupForSearchCars: LatLng
    private lateinit var dropForSearchCars: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        setContentView(R.layout.app_activity_main)

        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        isActivityCreated = true
        dialog = commonMethods.getAlertDialog(this)
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        headerViewset()
        isEtaFromPolyline = true
        getCommonDataAPI()
//        getPaymentListApi()
        getNearestDrivers()
        initNavitgaionview()

        slideUpAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up_animation)

        textView1.movementMethod = ScrollingMovementMethod()

        getintent()

        sessionGetset() //getset Session Values

        if (!isInternetAvailable) {
            dialogfunction() //no internet avaliabilty dialog
        }

        val mFirebaseInstance = FirebaseDatabase.getInstance()

        mFirebaseDatabase = mFirebaseInstance.getReference(getString(R.string.real_time_db)) // get reference to 'Driver Location'
        mFirebaseDatabasePolylines = mFirebaseInstance.getReference(getString(R.string.real_time_db))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionDialog() // Do something for lollipop and above versions
        } else {
            checkGPSEnable()  // do something for phones running on SDK before lollipop
        }



        googleMapfunc()

        ivFilter.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            chooseFilters()
        }

        if (sessionManager.isDialogShown.equals("")) {
            disclaimerDialog()
        }

    }

    private fun disclaimerDialog() {
        try {
            val dialog= Dialog(this,R.style.DialogCustomTheme)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.disclaimer_dialog)

            val tvDisclaimer = dialog.findViewById<TextView>(R.id.tvDisclaimer)
            val tvAccept = dialog.findViewById<TextView>(R.id.tvAccept)

            tvDisclaimer.setText(getString(R.string.location_disclaimer))

            tvAccept.setOnClickListener {
                sessionManager.isDialogShown = "yes"
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            if (!dialog.isShowing)
                dialog.show()

        }catch (e:Exception){
            Log.i("TAG", "disclaimerDialog: Error=${e.localizedMessage}")
        }

    }



    private fun signinFirebase() {
        val auth = Firebase.auth
        sessionManager.firebaseCustomToken?.let {
            if (it.isNotEmpty()) {
                auth.signInWithCustomToken(it)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                sessionManager.isFirebaseTokenUpdated = true
                                AddFirebaseDatabase().firebasePushLisener(this)
                                println("signInWithCustomToken:Success")
                            } else {
                                println("signInWithCustomToken:failure" + task.exception)
                            }
                        }
            } else {
                println("firebaseCustomToken: Empty")
            }
        }

    }

    private fun getPaymentListApi() {
        isMainActivity = true
        CommonKeys.isFirstSetpaymentMethod = true
        apiService.getPaymentMethodlist(sessionManager.accessToken!!, CommonKeys.isWallet).enqueue(RequestCallback(REG_GET_PAYMENTMETHOD, this))

    }

    private fun initMapPlaceAPI() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))
        }
    }

    fun getintent() {
        print("DATE get from Login: " + intent.getStringExtra("date"))
        date = intent.getStringExtra("date")
        val extrass = intent.extras
        if (extrass != null) {
            markerPoints = (extrass.getSerializable("PickupDrop") as? ArrayList<LatLng>)

        }

        if (intent.hasExtra(CommonKeys.PickupAddress))
            pickupaddresss = intent.getStringExtra(CommonKeys.PickupAddress)

        if (intent.hasExtra(CommonKeys.DropAddress))
            dropaddress = intent.getStringExtra(CommonKeys.DropAddress)

    }

    fun sessionGetset() {
        isRequest = sessionManager.isrequest
        isTrip = sessionManager.isTrip
        println("Payment method One : " + sessionManager.paymentMethod)
        /*  if (sessionManager.paymentMethod == "") {
              sessionManager.paymentMethod = CommonKeys.PAYMENT_PAYPAL
              sessionManager.isWallet=true
          }*/
        mContext = this
    }

    fun initNavitgaionview() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.elevation = 0f

        // get App version from gradle, and assigning to Textview
        // here V indicates Version and it will not changable according to Language
        appVersion.text = "V" + CommonMethods.getAppVersionNameFromGradle()

        val laydir = resources.getString(R.string.layout_direction)
        val scale = resources.displayMetrics.density
        val sizeInDp: Int
        if ("1" == laydir) {
            sizeInDp = 70
        } else {
            sizeInDp = 60
        }
        val dpAsPixels = (sizeInDp * scale + 0.5f).toInt()
        no_car.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels)

        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
        updateHomeToolBar()
        toggle.toolbarNavigationClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }
        }


        navigationView.setNavigationItemSelectedListener(this)

//        navigationView.menu.findItem(R.id.nav_support).isVisible = checkVersionModel.support.isNotEmpty()
        try {
            navigationView.menu.findItem(R.id.nav_support).isVisible = checkVersionModel.support.isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: UninitializedPropertyAccessException) {
            e.printStackTrace()
        }

        val m = navigationView.menu
        for (i in 0 until m.size()) {
            val mi = m.getItem(i)
            //for aapplying a font to subMenu ...
            val subMenu = mi.subMenu
            if (subMenu != null && subMenu.size() > 0) {
                for (j in 0 until subMenu.size()) {
                    val subMenuItem = subMenu.getItem(j)
                    applyFontToMenuItem(subMenuItem)
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi)
        }

    }

    fun googleMapfunc() {
        /* ********************************   Google MAP function Start *************************** */

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        receivePushNotification()

        if (!isRequest && isTrip) {


            val extras = intent.extras
            if (extras != null) {
                tripDetailsModel = intent.getSerializableExtra("driverDetails") as TripDetailsModel //Obtaining data
                ridersDetails = tripDetailsModel!!.riders.get(0)
                isPooledOnTrip = tripDetailsModel!!.isPool!!
                hideAndShowEta()
                resetToRide()
            }
            isTripBegin = intent.getBooleanExtra("isTripBegin", false)

            if (isTripBegin) {
                firstloop = true
            }
        }

        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (!CommonKeys.IS_ALREADY_IN_TRIP) {
            if (isInternetAvailable) {
                commonMethods.showProgressDialog(this@MainActivity)
                apiService.getTripDetails(sessionManager.accessToken!!, "").enqueue(RequestCallback(REQ_TRIP_DETAIL, this))
            } else {

                commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
            }
        }
    }

    private fun headerViewset() {
        headerview = navigationView.getHeaderView(0)
        /**
         * After trip driver profile details page
         */
        profilelayout = headerview.findViewById<View>(R.id.profilelayout) as LinearLayout
        username = headerview.findViewById<View>(R.id.username) as TextView
        //val view = headerview.findViewById<View>(R.id.v_profile)
        userprofile = headerview.findViewById<View>(R.id.profile_image1) as ImageView
        profilelayout.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("first_name", first_name)
            intent.putExtra("last_name", last_name)
            intent.putExtra("profile_image", profile_image)
            intent.putExtra("mobile_number", mobile_number)
            intent.putExtra("country_code", country_code)
            intent.putExtra("email_id", email_id)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
            drawer.closeDrawer(GravityCompat.START)
        }

    }

    /**
     * Check GPS enable or not
     */
    fun checkGPSEnable() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                val dialog = AlertDialog.Builder(mContext)
                dialog.setMessage(resources.getString(R.string.location_not_enabled))
                dialog.setPositiveButton(resources.getString(R.string.location_settings)) { _, _ ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                }
                dialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                    // TODO Auto-generated method stub
                }
                dialog.show()
            }
            buildGoogleApiClient()
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Navigation view for rider
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item click here.
        val id = item.itemId

        if (id == R.id.nav_payment) {
            gotoPaymentpage()
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("first_name", first_name)
            intent.putExtra("last_name", last_name)
            intent.putExtra("profile_image", profile_image)
            intent.putExtra("mobile_number", mobile_number)
            intent.putExtra("country_code", country_code)
            intent.putExtra("email_id", email_id)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
            drawer.closeDrawer(GravityCompat.START)
        } else if (id == R.id.nav_yourtrips) {
            val intent = Intent(this, YourTrips::class.java)
            intent.putExtra("upcome", "")
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else if (id == R.id.nav_wallet) {
            val intent = Intent(this, AddWalletActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else if (id == R.id.nav_settings) {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else if (id == R.id.emg_contact) {
            val intent = Intent(this, EmergencyContact::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else if (id == R.id.manual_booking) {
            if (!TextUtils.isEmpty(sessionManager.adminContact)) {
                createAdminpopup(1)
            } else {
                createAdminpopup(0)
            }
        } else if (id == R.id.nav_referral) {
            val intent = Intent(this, ShowReferralOptions::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else if (id == R.id.nav_support) {
            val intent = Intent(this, SupportActivityCommon::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return false
    }

    private fun createAdminpopup(type: Int) {
        val builder1 = AlertDialog.Builder(this)
        if (type == 1) {
            val ReverseNumber = StringBuilder(sessionManager.adminContact)
            if (ViewCompat.getLayoutDirection(whereto) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                // The view has RTL layout
                ReverseNumber.reverse()
                println("get Reverse $ReverseNumber")
            }
            builder1.setTitle(resources.getString(R.string.dial, sessionManager.adminContact))
            builder1.setMessage(resources.getString(R.string.contact_admin_for_manual_booking))
            builder1.setCancelable(false)

            builder1.setPositiveButton(resources.getString(R.string.call)) { dialog, _ ->
                dialog.cancel()
                val callnumber = sessionManager.adminContact
                val intent2 = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callnumber"))
                startActivity(intent2)
            }

            builder1.setNegativeButton(resources.getString(R.string.cancel_s)) { dialog, _ -> dialog.cancel() }
        } else {
            builder1.setMessage(resources.getString(R.string.no_contact_found))
            builder1.setCancelable(false)

            builder1.setPositiveButton(resources.getString(R.string.ok_c)) { dialog, _ -> dialog.cancel() }
        }

        val alert11 = builder1.create()
        if (alert11.window != null)
            alert11.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
        alert11.show()
    }

    /**
     * Apply font
     */
    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(assets, resources.getString(R.string.fonts_UBERMedium))
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypefaceSpan("", font), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
        try {
            val parent = mapFragment.requireView().findViewWithTag<View>("GoogleMapMyLocationButton").parent as ViewGroup
            parent.post {
                try {
                    val r = resources
                    //convert our dp margin into pixels
                    val marginPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35f, r.displayMetrics).toInt()
                    val marginPixelsTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, r.displayMetrics).toInt()
                    // Get the map compass view
                    val mapCompass = parent.getChildAt(4)

                    // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                    val rlp = RelativeLayout.LayoutParams(mapCompass.height, mapCompass.height)
                    // position on top right
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
                    //give compass margin
                    rlp.setMargins(25, marginPixelsTop, marginPixels, marginPixels)
                    mapCompass.layoutParams = rlp
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        try {
            print("isRequestisRequest$isRequest")
            if (isRequest) {
                if (!CommonKeys.isFirstSetpaymentMethod) {
                    getPaymentListApi()
                }
                enableViews(true, false)
            }
        } catch (e: Resources.NotFoundException) {
            DebuggableLogE("Cabme", "Can't find style. Error: ", e)
        }

        /**
         * map on Camera change listener
         */
        mMap.setOnCameraIdleListener {

            val mCenterLatLong = googleMap.cameraPosition.target
            try {
                val mLocation = Location("")
                mLocation.latitude = mCenterLatLong.latitude
                mLocation.longitude = mCenterLatLong.longitude
                currentLat = mCenterLatLong.latitude
                currentLng = mCenterLatLong.longitude
                isInternetAvailable = commonMethods.isOnline(applicationContext)
                if (!isInternetAvailable) {
                    commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
                } else {

                    if (mCenterLatLong.longitude > 0.0 && mCenterLatLong.latitude > 0.0 && sessionManager.isUpdateLocation == 0) {
                        sessionManager.isUpdateLocation = 1
                        locationHashMap = HashMap()
                        locationHashMap["latitude"] = mCenterLatLong.latitude.toString()
                        locationHashMap["longitude"] = mCenterLatLong.longitude.toString()
                        locationHashMap["user_type"] = sessionManager.type.toString()
                        locationHashMap["token"] = sessionManager.accessToken.toString()
                        locationHashMap["polyline"] = overviewPolylines
                        if (isForFirstTime) {
                            isForFirstTime = false
                             getNearestDrivers()
                        }
                        updateRiderLoc()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * get direction for given origin, dest
     */
    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {

        try {
            print("sad")
        } catch (e: Exception) {

        }

        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude

        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor&$mode"

        // Output format
        val output = "json"

        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + sessionManager.googleMapKey
        DebuggableLogD("dir direction URL", url)

        return url
    }

    fun verifyAccessPermission(requestPermissionFor: Array<String>, requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {

        RuntimePermissionDialogFragment.checkPermissionStatus(this, supportFragmentManager, this, requestPermissionFor, requestCodeForCallbackIdentificationCode, requestCodeForCallbackIdentificationCodeSubDivision)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onConnected(bundle: Bundle?) {
        /*
        * Here we removed ACCESS_COARSE LOCATION by the advice of google developer console
        * statement mentioned by google: If you are using both NETWORK_PROVIDER and GPS_PROVIDER, then you need to request only the ACCESS_FINE_LOCATION permission, because it includes permission for both providers. Permission for ACCESS_COARSE_LOCATION allows access only to NETWORK_PROVIDER.
          Link:
         */

        // Todo Location Already on  ... start
        /*val manager : LocationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            //Toast.makeText(this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            buildAlertMessageNoGps()
            return
        }*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            try {
                val mLocationRequest = LocationRequest()
                mLocationRequest.interval = 10000
                mLocationRequest.fastestInterval = 5000
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

                val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest)


// ...

                val client: SettingsClient = LocationServices.getSettingsClient(this)
                val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

                task.addOnSuccessListener { locationSettingsResponse ->
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                    try {
                        var
                                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                        if (Constants.changedPickupLatlng != null) {
                            val targetLocation = Location("")
                            targetLocation.latitude = Constants.changedPickupLatlng!!.latitude
                            targetLocation.longitude = Constants.changedPickupLatlng!!.longitude
                            changeMap(targetLocation)
                            println("Step 1" + carmarkers.size)
                            println("Step 1 isRequest" + isRequest)
                            if (!isRequest)
                                UpdateDriverLocationUisngGeoFire(targetLocation)

                        } else {
                            changeMap(mLastLocation)
                            println("Step 1 else" + carmarkers.size)
                            println("Step isRequest" + isRequest)
                            if (!isRequest)
                                UpdateDriverLocationUisngGeoFire(mLastLocation)
                            DebuggableLogD(TAG, "ON connected")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                task.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            exception.startResolutionForResult(this@MainActivity,
                                    REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }

    private fun UpdateDriverLocationUisngGeoFire(updatedLocation: Location?) {
        //geoqueryfor_filtercars?.removeAllListeners()
        removeMarkers(carmarkers)
        var iskeyexited = false
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.real_time_db)).child(FirebaseDbKeys.GEOFIRE)
        geoFire = GeoFire(ref)
        geoQuery = geoFire?.queryAtLocation(updatedLocation?.latitude?.let { GeoLocation(it, updatedLocation.longitude) }, CommonKeys.car_radius.toDouble())
        geoQuery!!.removeAllListeners()
        if (geoqueryfor_filtercars != null) {
            geoqueryfor_filtercars!!.removeAllListeners()
        }
        //carmarkers.clear()

        geoQuery?.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onGeoQueryReady() {

                println("carmarkers size onGeoReady size" + carmarkers.size)
            }

            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                isTrip = sessionManager.isTrip

                println("entered the search area at" + key)
                if (!isTrip) {

                    if (iskeyexited && carmarkers.size > 0) {
                        val itr = carmarkers.iterator()
                        while (itr.hasNext()) {
                            val marker = itr.next()
                            val name = marker.title
                            println("Name" + name + "key" + key)
                            if (name.equals(key, ignoreCase = true)) {
                                itr.remove()
                                marker.remove()
                                println("carmarkers remove" + carmarkers.size)
                            }
                            iskeyexited = false
                            println("carmarkers size  onKey Exited" + carmarkers.size)
                        }
                    }
                    drawMarker(LatLng(location!!.latitude, location.longitude), "", key)

                }
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                if (isTrip) {
                    try {
                        geoQuery!!.removeGeoQueryEventListener(this)
                    } catch (e: Exception) {

                    }
                } else {
                    for (marker: Marker in carmarkers) {

                        if (marker.title.equals(key, ignoreCase = true)) {
                            addDynamicMarker(carmarkers.indexOf(marker), LatLng(location!!.latitude, location.longitude))
                        }
                    }

                }


            }

            override fun onKeyExited(key: String?) {
                System.out.println("Key exited out of the search area to " + key)
                val itr = carmarkers.iterator()
                while (itr.hasNext()) {
                    val marker = itr.next()
                    val name = marker.title
                    println("Name" + name + "key" + key)
                    if (name.equals(key, ignoreCase = true)) {
                        itr.remove()
                        marker.remove()
                        println("carmarkers remove" + carmarkers.size)
                    }
                    iskeyexited = true
                    println("carmarkers size  onKey Exited" + carmarkers.size)
                }
            }

            override fun onGeoQueryError(error: DatabaseError?) {

            }

        })


    }

    private fun addDynamicMarker(position: Int, currentLatLng: LatLng) {
        try {
            val carmarker = carmarkers[position]
            val startbearlocation = Location(LocationManager.GPS_PROVIDER)
            val endbearlocation = Location(LocationManager.GPS_PROVIDER)
            val oldLatLng = carmarker.position
            startbearlocation.latitude = oldLatLng.latitude
            startbearlocation.longitude = oldLatLng.longitude
            endbearlocation.latitude = currentLatLng.latitude
            endbearlocation.longitude = currentLatLng.longitude
            if (endbear.toDouble() != 0.0) {
                startbear = endbear
            }

            var calculatedSpeed = 0f
            var distance = 0f

            if (lastLocation != null) {

                val elapsedTime = ((startbearlocation.time - (endbearlocation?.time)!!) / 1000).toDouble() // Convert milliseconds to seconds
                if (elapsedTime > 0)
                    calculatedSpeed = (endbearlocation!!.distanceTo(startbearlocation) / elapsedTime).toFloat()
                else
                    calculatedSpeed = endbearlocation!!.distanceTo(startbearlocation) / 1
                //calculatedSpeed= Float.valueOf(twoDForm.format(calculatedSpeed));
                distance = startbearlocation.distanceTo(endbearlocation)

            }
            lastLocation = startbearlocation
            /* if (!java.lang.Float.isNaN(calculatedSpeed) && !java.lang.Float.isInfinite(calculatedSpeed)) {
                 speed = (speed + calculatedSpeed) / 2
             }

             if (speed <= 5) speed = 5f*/
            //carmarker.setPosition(latlng);
// carmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview));
            carmarker.isFlat = true
            carmarker.setAnchor(0.5f, 0.5f)
            marker = carmarker
            // Move map while marker gone
            // ensureMarkerOnBounds(oldLatLng, "updated");
            endbear = bearing(startbearlocation, endbearlocation).toFloat()
            endbear = (endbear * (180.0 / 3.14)).toFloat()
            //double distance = Double.valueOf(twoDForm.format(startbearlocation.distanceTo(endbearlocation)));
            // val distance = java.lang.Double.valueOf(startbearlocation.distanceTo(endbearlocation).toDouble())
            if (distance > 0)
                animateMarker(currentLatLng, marker, speed, endbear)
        } catch (n: NullPointerException) {
            n.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }


/* fun getLocation(){
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)

        mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()



    if (mGoogleApiClient != null) {
        mGoogleApiClient.connect()
    }
}*/

    override fun onConnectionSuspended(i: Int) {
        DebuggableLogI(TAG, "Connection suspended")
        try {
            mGoogleApiClient.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * update while location changed
     */
    override fun onLocationChanged(location: Location?) {
        try {
            if (location != null) {
                mLastLocation = location
            }
            DebuggableLogI(TAG, "On Location Changed...")
            if (Constants.changedPickupLatlng != null) {
                val targetLocation = Location("")
                targetLocation.latitude = Constants.changedPickupLatlng!!.latitude
                targetLocation.longitude = Constants.changedPickupLatlng!!.longitude
                changeMap(targetLocation)
            } else if (location != null) {
                //Toast.makeText(getActivity(), "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();
                changeMap(location)
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
            /* removeMarkers(carmarkers)
             println("Step 3 :"+carmarkers.size)
             location?.let { UpdateDriverLocationUisngGeoFire(it) }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        DebuggableLogV(TAG, "onConnectionFailed")
    }

    /**
     * Google API client called
     */
    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build()

        try {
            mGoogleApiClient.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        isMainActivity = true
        try {
            mGoogleApiClient.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        stopTimer()
        DebuggableLogE(TAG, "Driver Location data removed!")
        if (mSearchedLocationReferenceListener != null) {
            query.removeEventListener(mSearchedLocationReferenceListener!!)
            mFirebaseDatabase.removeEventListener(mSearchedLocationReferenceListener!!)
            mSearchedLocationReferenceListener = null

            DebuggableLogE(TAG, "Driver Location data removed! success")
        } else {
            DebuggableLogE(TAG, "Driver Location data removed! Failed")
        }

        if (mSearchedpolylineReferenceListener != null) {
            querypolyline.removeEventListener(mSearchedpolylineReferenceListener!!)
            mFirebaseDatabasePolylines.removeEventListener(mSearchedpolylineReferenceListener!!)
            mSearchedpolylineReferenceListener = null

            DebuggableLogE(TAG, "Driver Location data removed! success")
        } else {
            DebuggableLogE(TAG, "Driver Location data removed! Failed")
        }

        if (mSearchedEtaReferenceListener != null) {
            queryEta.removeEventListener(mSearchedEtaReferenceListener!!)
            mFirebaseDatabasePolylines.removeEventListener(mSearchedEtaReferenceListener!!)
            mSearchedEtaReferenceListener = null

            DebuggableLogE(TAG, "Driver Location data removed! success")
        } else {
            DebuggableLogE(TAG, "Driver Location data removed! Failed")
        }


        isMainActivity = false
        if (mGoogleApiClient.isConnected) {
            mGoogleApiClient.disconnect()
        }
    }

    /**
     * Check google API service or not
     */
    private fun checkPlayServices(): Boolean {
        //code updated due to deprication, code updated by the reference of @link: https://stackoverflow.com/a/31016761/6899791
        //int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this); this is commented due to depricated
        val googleAPI = GoogleApiAvailability.getInstance()
        val resultCode = googleAPI.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show()
            }
            return false
        }
        return true
    }

    /**
     * change google map current location
     */
    private fun changeMap(location: Location?) {
        DebuggableLogD(TAG, "Reaching map" + mMap)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // check if map is created successfully or not
            mMap.uiSettings.isZoomControlsEnabled = false
            mMap.uiSettings.isCompassEnabled = false

            if (location != null) {
                mLastLocation = location
            }

            latLong = LatLng(location?.latitude!!, location.longitude)
            currentLat = location.latitude
            currentLng = location.longitude
            if (!isInternetAvailable) {
                commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
            } else {
                if (alreadyRunning) {
                    fetchAddress(latLong, "pickupaddress")
                }
            }
            if (overallDuration > 0 && isDownloadUrl) {
                if (polylinefromFirebase.isEmpty() || polylinefromFirebase.equals("0") || tvEta.text.toString().equals("")) {

                    calculateEta(location)
                }

            }
            /*   var cameraPosition :CameraPosition
               if(sessionManager.isTrip)
               {*/
            val cameraPosition = CameraPosition.Builder().target(latLong).zoom(16.5f).tilt(0f).build()
            /*   } else
               {
                    cameraPosition = CameraPosition.Builder().target(latLong).zoom(15.5f).tilt(0f).build()
               }*/
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            if (mapFragment.view != null && mapFragment.requireView().findViewById<View>("1".toInt()) != null) {
                val locationButton: View = (mapFragment.requireView().findViewById<View>("1".toInt()).getParent() as View).findViewById("2".toInt())
                val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                layoutParams.setMargins(0, 0, 20, 180)
            }
            /*val locationButton = (mapFragment.view?.findViewById<View>("1".toInt())?.getParent() as View).findViewById("2".toInt()) as ImageView
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            locationButton.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.app_ic_current_location))
            locationButton.background = ContextCompat.getDrawable(this,R.drawable.circular_border)

            locationButton.layoutParams.width = 35
            locationButton.layoutParams.height = 35
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 30, 30, 0)
            locationButton.layoutParams = rlp
            */
            isRequest = sessionManager.isrequest
            isTrip = sessionManager.isTrip
            if (!isRequest && isTrip) {
                mMap.isMyLocationEnabled = true
                if (tripDetailsModel != null) {
                    isTripFunc()
                } else {
                    getTripDetails()  //get accepted driver details
                }
            } else {
                mMap.isMyLocationEnabled = true
                isRequestFunc()
            }

            if (!isRequest && (!isTrip || isActivityCreated)) {
                isActivityCreated = false
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }


        }
    }


    private fun calculateEta(location: Location) {
        if (!isDriverForeground) {
            val pos = ArrayList<Int>()

            for (j in stepPointsList.indices) {


                if (location.distanceTo(stepPointsList.get(j).location) < 30) {
                    pos.add(j)
                    overallDuration = overallDuration - stepPointsList.get(j).time.toInt()
                }

            }

            for (j in pos.indices) {

                try {
                    stepPointsList.removeAt(pos.get(j))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            totalMins = TimeUnit.SECONDS.toMinutes(overallDuration.toLong()).toString()



            if (TimeUnit.SECONDS.toMinutes(overallDuration.toLong()) >= 1L) {
                val min = totalMins.toLong() % 60
                println("Eta From Calc")
                println("totalMins " + totalMins)
                println("etatime " + etatime)
                println("totalMins.toLong()+etatime.toInt() " + totalMins.toLong() + etatime.toInt())
                val tM = totalMins.toLong()
                val times = etatime.toInt()

                println("tM " + tM)
                println("times " + times)

                val avgtime = (tM + times) / 2

                println("avgtime " + avgtime)


                val correctavgtime = avgtime
                println("ETACalculatingwithDistance " + ETACalculatingwithDistance)
                if (ETACalculatingwithDistance > 0.5) {
                    if ((correctavgtime * 60).toInt() > 1) {
                        EtaTime = correctavgtime.toString()
                        println("EtaTime " + EtaTime)
                        if (correctavgtime > 60) {
                            val hours = TimeUnit.MINUTES.toHours(correctavgtime.toLong())
                            val remainMinutes = correctavgtime - TimeUnit.HOURS.toMinutes(hours)

                            println("hours " + hours)
                            println("remainMinutes " + remainMinutes)


                            //      tvEta.text = """${resources.getQuantityString(R.plurals.hours, hours.toInt(), hours.toInt())}${resources.getQuantityString(R.plurals.minutes, remainMinutes.toInt(), remainMinutes.toInt())}"""
                            tvEta.text = resources.getQuantityString(R.plurals.hours, hours.toInt(), hours.toInt()) + "\n" + resources.getQuantityString(R.plurals.minutes, remainMinutes.toInt(), remainMinutes.toInt())

                        } else {
                            tvEta.text = resources.getQuantityString(R.plurals.minutes, correctavgtime.toInt(), correctavgtime.toInt())
                        }
                    } else {
                        EtaTime = "1"
                        tvEta.text = resources.getQuantityString(R.plurals.minutes, EtaTime.toInt(), EtaTime.toInt())
                    }
                } else {
                    EtaTime = "1"
                    tvEta.text = resources.getQuantityString(R.plurals.minutes, EtaTime.toInt(), EtaTime.toInt())
                }

                println("Minutes to hours : " + TimeUnit.MINUTES.toHours(correctavgtime.toLong()).toString())

            } else {
                EtaTime = "1"
                tvEta.text = resources.getQuantityString(R.plurals.minutes, EtaTime.toInt(), EtaTime.toInt())
            }
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    fun startPeakPriceingActivity() {
        val peakHoursIntent = Intent(this, PeakPricing::class.java)
        peakHoursIntent.putExtra(CommonKeys.KEY_PEAK_PRICE, peakPriceforClickedCar)
        peakHoursIntent.putExtra(CommonKeys.KEY_MIN_FARE, minimumFareForClickedCar)
        peakHoursIntent.putExtra(CommonKeys.KEY_PER_KM, perKMForClickedCar)
        peakHoursIntent.putExtra(CommonKeys.KEY_PER_MINUTES, perMinForClickedCar)
        startActivityForResult(peakHoursIntent, CommonKeys.ACTIVITY_REQUEST_CODE_SHOW_PEAK_HOURS_DETAILS)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == Activity.RESULT_OK) {
            // Get the user's selected place from the Intent.

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            }

        } else if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_SHOW_PEAK_HOURS_DETAILS) {
            if (resultCode == CommonKeys.PEAK_PRICE_ACCEPTED) {

                if (isSchedule == CommonKeys.ISSCHEDULE) {
                    startActivity(scheduleIntent)

                } else {
                    locationHashMap["peak_id"] = peakPriceIdForClickedCar
                    sendRequest()
                }
            }
        }
    }


    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            backPressed = 0


        } else {
            if (backPressed >= 1) {
                CommonKeys.IS_ALREADY_IN_TRIP = false
                finishAffinity()
                super.onBackPressed()       // bye
            } else {
                // clean up
                backPressed = backPressed + 1
                Toast.makeText(this, resources.getString(R.string.press_again_toexit), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* **********************************************************************************  */
    /*                           Car List and Bottom View                                  */
    /* **********************************************************************************  */

    /**
     * Check location permission
     */
    private fun showPermissionDialog() {
        if (!Permission.checkPermission(this)) {
            verifyAccessPermission(arrayOf(RuntimePermissionDialogFragment.LOCATION_PERMISSION), RuntimePermissionDialogFragment.locationCallbackCode, 0)
        }
        buildGoogleApiClient()
    }

    private fun showLocationPermissionDialog() {
        if (!PermissionCamer.checkPermission(this)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    4)
            //displayLocationSettingsRequest()
        } else {
            buildGoogleApiClient()
            // displayLocationSettingsRequest(mContext);
        }
        displayLocationSettingsRequest()
    }

    fun getCarList() {

        adapter = CarDetailsListAdapter(this, nearestCars)

        listView.setHasFixedSize(false)
        listView.isNestedScrollingEnabled = true
        listView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        listView.adapter = adapter
        listView.isSelected = true
        listView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            var gestureDetector = GestureDetector(applicationContext, object : GestureDetector.SimpleOnGestureListener() {

                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

            })

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                //countries.get(position)
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    val position = rv.getChildAdapterPosition(child)
                    CommonMethods.DebuggableLogI("car recycler view", "clicked")

                    // requestuber.setText(getResources().getString(R.string.requestuber) + searchlist.get(position).getCarName());
                    if (lastposition == Integer.parseInt(nearestCars[position].carId)) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return false
                        }
                        mLastClickTime = SystemClock.elapsedRealtime()
                        fareEstimate(position, nearestCars)
                    } else {
                        CommonMethods.DebuggableLogI("car recycler view", nearestCars[position].applyPeak)

                        isPooled = nearestCars[position].isPool
                        caramount = nearestCars[position].fareEstimation

                        peekPricePercentage = nearestCars[position].peakPricePercentage

                        if (isPooled) {
                            if (!caramount.equals("")) {
                                peekPrice = caramount.toFloat()
                                String.format("%.2f", caramount.toFloat())
                                poolRate.text = sessionManager.currencySymbol + String.format("%.2f", caramount.toFloat())
                            }
                        }
                        //reqCarTxtChange()

                        //isPooled= true
                        lastposition = Integer.parseInt(nearestCars[position].carId)
                        clickedcar = nearestCars[position].carId
                        clickeddriverid = nearestCars[position].driverId
                        waitingAmount = nearestCars[position].waitingCharge
                        waitingMin = nearestCars[position].waitingTime
                        updateWaitingTime(waitingMin, waitingAmount)
                        clickedCarName = nearestCars[position].carName
                        isPeakPriceAppliedForClickedCar = nearestCars[position].applyPeak
                        peakPriceIdForClickedCar = nearestCars[position].peakId
                        minimumFareForClickedCar = nearestCars[position].minFare
                        perKMForClickedCar = nearestCars[position].perKm
                        perMinForClickedCar = nearestCars[position].perMin
                        peakPriceforClickedCar = nearestCars[position].peakPrice
                        getCar(nearest_carObj, nearestCars[position].carId)
                        locationIDForSelectedCar = nearestCars[position].locationID
                        peakIDForSelectedCar = nearestCars[position].peak_id
                        //Toast.makeText(getApplicationContext(),"Current position "+searchlist.get(position).getCarName(),Toast.LENGTH_SHORT).show();
                    }

                    reqCarTxtChange()

                }

                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                DebuggableLogV(TAG, e.toString())
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                DebuggableLogV(TAG, "disallowIntercept")
            }
        })
    }


    private fun reqCarTxtChange() {
        if (intent.getStringExtra("Schedule") == "Schedule") {
            date = sessionManager.scheduleDateTime
            requestuber.text = resources.getString(R.string.schedule) + clickedCarName + "\n" + date
        } else if (isPooled) {
            confirm_seat.text = resources.getString(R.string.requestuber) + clickedCarName
            requestuber.text = resources.getString(R.string.confirm_seats)
        } else {
            requestuber.text = resources.getString(R.string.requestuber) + clickedCarName
        }

    }


    private fun updateWaitingTime(waitingMins: String, waitingAmount: String) {
        if (waitingAmount != "0.00") {
            tvWaitingChargeInfo.visibility = View.VISIBLE
            val waitingChargeInfo = sessionManager.currencySymbol + resources.getString(R.string.waiting_charge_description, waitingAmount, waitingMins)
            tvWaitingChargeInfo.text = waitingChargeInfo
        } else {
            hideWaitingTimeDescription()
        }

    }

    private fun hideWaitingTimeDescription() {
        tvWaitingChargeInfo.visibility = View.GONE
    }

    private fun updateWaitingTimeAfterAcceptingTrip(waitingMins: String?, waitingAmount: String?) {
        if (!TextUtils.isEmpty(waitingAmount) && waitingAmount != "0.00") {
            tvWaititingChargeFeeForAcceptedCar.visibility = View.VISIBLE
            val waitingChargeInfo = sessionManager.currencySymbol + " " + resources.getString(R.string.waiting_charge_description, waitingAmount, waitingMins)
            tvWaititingChargeFeeForAcceptedCar.text = waitingChargeInfo
        } else {
            hideWaitingTimeDescription()
        }
    }

    private fun hideWaitingTimeAfterAcceptingTrip() {
        tvWaititingChargeFeeForAcceptedCar.visibility = View.GONE
    }


    /**
     * Show hide views based on trip or not
     */
    private fun enableViews(enable: Boolean, resetToRide: Boolean) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {


            // Adding new item to the ArrayList
            var pickup: LatLng? = null
            var drop: LatLng? = null
            try {
                if (intent.hasExtra("PickupDrop"))
                    markerPoints = intent.getSerializableExtra("PickupDrop") as ArrayList<LatLng>

                pickup = markerPoints?.get(0)
                pickuplocation = Location("")
                pickuplocation!!.latitude = pickup?.latitude!!
                pickuplocation!!.longitude = pickup?.longitude!!
                drop = markerPoints?.get(1)

                pickupForSearchCars = pickup
                dropForSearchCars = drop!!
            } catch (e: Exception) {
                e.printStackTrace()
                commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
            }

            getCarList()

            /**
             * Draw route
             */
            drawRoute(markerPoints)
            isInternetAvailable = commonMethods.isOnline(applicationContext)
            searchViewset(pickup, drop)
            toggle.isDrawerIndicatorEnabled = false
            toggle.setHomeAsUpIndicator(R.drawable.app_ic_back_arrow_toolbar)
            updateSearchCarToolBar()
            // Show back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.toolbarNavigationClickListener = View.OnClickListener {
                    // Doesn't have to be onBackPressed
                    //onBackPressed();
                    isRequest = false
                    Constants.changedPickupLatlng = null
                     getNearestDrivers()
                    //  UpdateDriverLocationUisngGeoFire(mLastLocation)
                    enableViews(false, false)
                    markerPoints?.clear()
                    pickupaddresss = ""
                    dropaddress = ""
                    bottomDown = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_down)
                    request_view.startAnimation(bottomDown)
                    request_view.visibility = View.GONE
                    if (layout_request_seat.visibility == View.VISIBLE) {
                        layout_request_seat.startAnimation(bottomDown)
                        layout_request_seat.visibility = View.GONE
                    }
                    //show_seat_availability.startAnimation(bottomDown)
                    whereto.visibility = View.VISIBLE
                    whereto_and_schedule.visibility = View.VISIBLE

                    rltContactAdmin.visibility = View.GONE
                    edit_map.visibility = View.INVISIBLE
                }

                mToolBarNavigationListenerIsRegistered = true
            }

        } else {


            mMap.clear()
            if (resetToRide) {
                resetViewsToRide()
            }
            alreadyRunning = true
            if (mLastLocation != null) {
                changeMap(mLastLocation)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showLocationPermissionDialog() // Do something for lollipop and above versions
                } else {
                    checkGPSEnable()  // do something for phones running on SDK before lollipop
                }
            }

            removeMarkers(carmarkers)
            println("Step 2 :" + carmarkers.size)
            println("Step2 isRequest" + isRequest)
            if (mLastLocation != null) {
                changeMap(mLastLocation)
                removeMarkers(carmarkers)
                UpdateDriverLocationUisngGeoFire(mLastLocation)
            }
            DebuggableLogD(TAG, "ON connected")
            // Remove back button
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            // Show hamburger
            toggle.isDrawerIndicatorEnabled = false
            toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
            updateHomeToolBar()
            // Remove the/any drawer toggle listener
            toggle.toolbarNavigationClickListener = null
            mToolBarNavigationListenerIsRegistered = false
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.toolbarNavigationClickListener = object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (drawer.isDrawerVisible(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START)
                        } else {
                            drawer.openDrawer(GravityCompat.START)
                        }
                    }
                }
            }
            sessionManager.isrequest = false
        }

        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

    private fun searchViewset(pickup: LatLng?, drop: LatLng?) {
        if (!isInternetAvailable) {
            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
        } else {
            if (pickup != null && drop != null) {

                if (!sessionManager.scheduleDate.toString().equals(""))
                    scheduleFilter = "1"
                else
                    scheduleFilter = "0"


                fetchAddress(drop, "dropfulladdress")
                locationHashMap = HashMap()
                locationHashMap["pickup_latitude"] = pickup.latitude.toString()
                locationHashMap["pickup_longitude"] = pickup.longitude.toString()
                locationHashMap["drop_latitude"] = drop.latitude.toString()
                locationHashMap["drop_longitude"] = drop.longitude.toString()
                locationHashMap["schedule_date"] = sessionManager.scheduleDate.toString()
                locationHashMap["schedule_time"] = sessionManager.presentTime.toString()
                locationHashMap["user_type"] = sessionManager.type.toString()
                locationHashMap["token"] = sessionManager.accessToken.toString()
                locationHashMap["timezone"] = TimeZone.getDefault().id
                locationHashMap["polyline"] = overviewPolylines
                locationHashMap["is_schedule"] = scheduleFilter
                searchCars()
            }
        }
        requestubers.visibility = View.INVISIBLE
        edit_map.visibility = View.INVISIBLE
        rltContactAdmin.visibility = View.GONE
        listView.visibility = View.GONE
        paymentmethod.visibility = View.GONE
        requestuber.visibility = View.INVISIBLE
        loading_car.visibility = View.VISIBLE
        hideWaitingTimeDescription()
        no_car.visibility = View.GONE
        requestuber.isEnabled = false

        bottomUp = AnimationUtils.loadAnimation(applicationContext, // Animation for bottom up and down
                R.anim.bottom_up)
        request_view.startAnimation(bottomUp)
        request_view.visibility = View.VISIBLE

        whereto.visibility = View.GONE
        whereto_and_schedule.visibility = View.GONE
    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {

        if (!jsonResp.isOnline) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data)
            return
        }
        when (jsonResp.requestCode) {
            // Get Rider Profile
            REQ_GET_RIDER_PROFILE -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                dbHelper.insertWithUpdate(Constants.DB_KEY_USERDETAILS.toString(), jsonResp.strResponse)
                onSuccessProfile(jsonResp.strResponse)

            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }

            // Update Rider Location
            REQ_UPDATE_LOCATION,
                // Send Request
            REQ_SEND_REQUEST -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REQ_GET_DRIVER -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                onSuccessDriver(jsonResp)
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }

            REQ_COMMON_DATA -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                onSuccessCommonData(jsonResp)
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REG_GET_PAYMENTMETHOD -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                onSuccessPaymentList(jsonResp)
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }

            // Search Cars
            REQ_SEARCH_CARS -> {

                try {
                    removeMarkers(carmarkers)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                DebuggableLogE(TAG,"REQ_SEARCH_CARS=${Gson().toJson(jsonResp)}")

                updateSearchCarToolBar()
                if (jsonResp.isSuccess) {
                    if (jsonResp.statusCode == "1") {
                        commonMethods.hideProgressDialog()
                        onSuccessSearchCar(jsonResp)
                    } else if (jsonResp.statusCode == "2") {
                        commonMethods.hideProgressDialog()
                        no_car_txt.text = jsonResp.statusMsg
                        requestubers.visibility = View.VISIBLE
                        edit_map.visibility = View.INVISIBLE
                        listView.visibility = View.GONE
                        paymentmethod.visibility = View.GONE
                        requestuber.visibility = View.VISIBLE
                        no_car.visibility = View.VISIBLE
                        loading_car.visibility = View.GONE
                        requestuber.isEnabled = false
                    }
                } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                    commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
                    requestubers.visibility = View.VISIBLE
                    edit_map.visibility = View.INVISIBLE
                    listView.visibility = View.GONE
                    paymentmethod.visibility = View.GONE
                    requestuber.visibility = View.VISIBLE
                    no_car.visibility = View.VISIBLE
                    loading_car.visibility = View.GONE
                    requestuber.isEnabled = false
                    no_car_txt.text = resources.getString(R.string.car_unavailable)
                    commonMethods.hideProgressDialog()
                }
            }
            REQ_TRIP_DETAIL -> {
                commonMethods.hideProgressDialog()
                if (jsonResp.isSuccess) {
                    println("JSON Response: ${jsonResp.strResponse}")
                    addFirebaseDatabase.removeRequestTable()
                    resetToRide()
                    ResumeRiderTipsResponse(jsonResp)
                }
            }
            REQ_NEAREST_CARS -> {
                if (jsonResp.isSuccess) {
                    val nearByCarsModel = gson.fromJson(jsonResp.strResponse, NearByCarsModel::class.java)
                    removeMarkers(carmarkers)
                    DebuggableLogE(TAG,"REQ_NEAREST_CARS=${Gson().toJson(jsonResp)}")
                    if (nearByCarsModel.data.size > 0) {
                        for (i in 0 until nearByCarsModel.data.size) {
                            val latitude = nearByCarsModel.data[i].latitude
                            val longitude = nearByCarsModel.data[i].longitude
                            val cartype = nearByCarsModel.data[i].vehicleId.toString()
                            val latLng = LatLng(java.lang.Double.parseDouble(latitude), java.lang.Double.parseDouble(longitude))
                            drawMarker(latLng, cartype, nearByCarsModel.data[i].driverId.toString())
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun onSuccessPaymentList(jsonResp: JsonResponse) {
        var isDefaultPaymentmethod: String = ""
        var defaultImage = ""
        val paymentmodel = gson.fromJson(jsonResp.strResponse, PaymentMethodsModel::class.java)

        /*  if(sessionManager.paymentMethod.isNotEmpty()  )  CommonKeys.isSetPaymentMethod=true
                  sessionManager.paymentMethodImage=defaultImage
                  sessionManager.walletPaymentMethod=isDefaultPaymentmethod
                  sessionManager.paymentMethod=isDefaultPaymentmethod

          {*/

        if (sessionManager.paymentMethodkey!!.isNotEmpty()) {
            for (i in 0 until paymentmodel.paymentlist.size) {
                CommonKeys.isSetPaymentMethod = true
                if (sessionManager.paymentMethodkey.equals(paymentmodel.paymentlist.get(i).paymenMethodKey)) {
                    Picasso.get().load(paymentmodel.paymentlist[i].paymenMethodIcon).into(paymentmethod_img)
                    sessionManager.paymentMethod = paymentmodel.paymentlist[i].paymenMethodvalue
                    if (sessionManager.promoCount > 0)
                        paymentmethod_type.text = resources.getString(R.string.promo_applied)
                    else paymentmethod_type.text = paymentmodel.paymentlist[i].paymenMethodvalue
                    return
                } else {
                    if (paymentmodel.paymentlist[i].isDefaultPaymentMethod) {
                        CommonKeys.isSetPaymentMethod = false
                        Picasso.get().load(paymentmodel.paymentlist[i].paymenMethodIcon).into(paymentmethod_img)
                        if (sessionManager.promoCount > 0)
                            paymentmethod_type.text = resources.getString(R.string.promo_applied)
                        else
                            paymentmethod_type.text = paymentmodel.paymentlist[i].paymenMethodvalue

                    }
                }
            }
        } else {
            for (i in 0 until paymentmodel.paymentlist.size) {
                if (paymentmodel.paymentlist[i].isDefaultPaymentMethod) {

                    if (paymentmodel.paymentlist[i].paymenMethodKey.contains(resources.getString(R.string.stripe), ignoreCase = true) && !paymentmodel.paymentlist[i].paymenMethodKey.equals(CommonKeys.PAYMENT_CARD)) {
                        CommonKeys.isSetPaymentMethod = false
                        Picasso.get().load(paymentmodel.paymentlist[i].paymenMethodIcon).into(paymentmethod_img)
                        if (sessionManager.promoCount > 0)
                            paymentmethod_type.text = resources.getString(R.string.promo_applied)
                        else
                            paymentmethod_type.text = paymentmodel.paymentlist[i].paymenMethodvalue

                    } else {
                        CommonKeys.isSetPaymentMethod = true

                        sessionManager.paymentMethod = paymentmodel.paymentlist[i].paymenMethodvalue
                        sessionManager.paymentMethodkey = paymentmodel.paymentlist[i].paymenMethodKey
                        sessionManager.paymentMethodImage = paymentmodel.paymentlist[i].paymenMethodIcon
                        Picasso.get().load(paymentmodel.paymentlist[i].paymenMethodIcon).into(paymentmethod_img)
                        if (sessionManager.promoCount > 0)
                            paymentmethod_type.text = resources.getString(R.string.promo_applied)
                        else
                            paymentmethod_type.text = paymentmodel.paymentlist[i].paymenMethodvalue

                    }


                }
            }

        }
        /*for (i in 0..paymentmodel.paymentlist.size) {


            *//*  if(isDefault)
              {
                  isDefaultPaymentmethod=setdefaultkey
                  defaultImage=setPaymentmethodImage
              }
              if(sessionManager.paymentMethod.isNotEmpty()  )
              {
                  if(sessionManager.paymentMethod.equals(setdefaultkey) && sessionManager.walletPaymentMethod.equals(setdefaultkey))
                  {
                      sessionManager.paymentMethod=setdefaultkey
                      sessionManager.walletPaymentMethod=setdefaultkey
                      sessionManager.paymentMethodImage=setPaymentmethodImage
                      CommonKeys.isSetPaymentMethod=true
                      return
                  }
              }else
              {
                  CommonKeys.isSetPaymentMethod=true
                  sessionManager.paymentMethodImage=defaultImage
                  sessionManager.walletPaymentMethod=isDefaultPaymentmethod
                  sessionManager.paymentMethod=isDefaultPaymentmethod
              }*//*

        }
        *//*paymentmethod_type.text=sessionManager.paymentMethod
      Picasso.with(this).load(sessionManager.paymentMethodImage).into(paymentmethod_img)*//*
        // sessionManager.walletPaymentMethod=isDefaultPaymentmethod
        *//*}else
        {
          //  sessionManager.walletPaymentMethod=isDefaultPaymentmethod
            sessionManager.paymentMethod=isDefaultPaymentmethod
            CommonKeys.isSetPaymentMethod=false
        }*//*
*/
    }

    private fun onSuccessCommonData(jsonResp: JsonResponse) {

        Log.i(TAG, "onSuccessCommonData: DATA=${Gson().toJson(jsonResp)}")
        sessionManager.sinchSecret = commonMethods.getJsonValue(jsonResp.strResponse, "sinch_secret_key", String::class.java) as String
        sessionManager.sinchKey = commonMethods.getJsonValue(jsonResp.strResponse, "sinch_key", String::class.java) as String
        sessionManager.googleMapKey = resources.getString(R.string.google_key_url)
        val adminContact = commonMethods.getJsonValue(jsonResp.strResponse, "admin_contact", String::class.java) as String
        sessionManager.stripePublishKey = commonMethods.getJsonValue(jsonResp.strResponse, "stripe_publish_key", String::class.java) as String
        sessionManager.paypal_mode = commonMethods.getJsonValue(jsonResp.strResponse, "paypal_mode", String::class.java) as Int
        sessionManager.paypal_app_id = commonMethods.getJsonValue(jsonResp.strResponse, "paypal_client", String::class.java) as String
        sessionManager.cardBrand = commonMethods.getJsonValue(jsonResp.strResponse, "brand", String::class.java) as String
        sessionManager.cardValue = commonMethods.getJsonValue(jsonResp.strResponse, "last4", String::class.java) as String
        sessionManager.firebaseCustomToken = commonMethods.getJsonValue(jsonResp.strResponse, "firebase_token", String::class.java) as String
        sessionManager.isCovidFeature = commonMethods.getJsonValue(jsonResp.strResponse, "covid_future", Boolean::class.java) as Boolean
        //sessionManager.defaultPayment = commonMethods.getJsonValue(jsonResp.strResponse, "trip_default", String::class.java) as String
        //CommonKeys.car_radius = commonMethods.getJsonValue(jsonResp.strResponse, "driver_km", String::class.java) as String
        commonMethods.initStripeData(applicationContext)
        sessionManager.adminContact = adminContact
        try {
            sessionManager.payementModeWebView = commonMethods.getJsonValue(jsonResp.strResponse, "is_web_payment", Boolean::class.java) as Boolean
        } catch (e: Exception) {
            sessionManager.payementModeWebView = false
            e.printStackTrace()
        }
        initMapPlaceAPI()
        //Firebase Auth
        signinFirebase()
    }

    private fun resetToRide() {
        toggle.isDrawerIndicatorEnabled = false
        // Show back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.app_ic_back_arrow_toolbar)

        // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
        // clicks are disabled i.e. the UP button will not work.
        // We need to add a listener, as in below, so DrawerToggle will forward
        if (!mToolBarNavigationListenerIsRegistered) {
            toggle.toolbarNavigationClickListener = View.OnClickListener {
                // Doesn't have to be onBackPressed
                //onBackPressed();
                try {
                    isRequest = false
                    enableViews(false, true)
                    CommonKeys.IS_ALREADY_IN_TRIP = true
                    movepoints.clear()
                    Constants.changedPickupLatlng = null
                    polyline?.remove()
                    pickupaddresss = ""
                    dropaddress = ""
                    bottomDown = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_down)
                    request_view.startAnimation(bottomDown)
                    request_view.visibility = View.GONE
                    if (layout_request_seat.visibility == View.VISIBLE) {
                        layout_request_seat.startAnimation(bottomDown)
                        layout_request_seat.visibility = View.GONE
                    }
                    whereto.visibility = View.VISIBLE
                    whereto_and_schedule.visibility = View.VISIBLE
                    edit_map.visibility = View.INVISIBLE
                    rltContactAdmin.visibility = View.GONE
                    ivFilter.visibility = View.VISIBLE

                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
            }

            mToolBarNavigationListenerIsRegistered = true
        }
    }

    private fun resetViewsToRide() {
        whereto.visibility = View.VISIBLE
        whereto_and_schedule.visibility = View.VISIBLE
        meetlayout.visibility = View.GONE
        tvWaititingChargeFeeForAcceptedCar.visibility = View.GONE

        bottomlayout.visibility = View.GONE

        pickupOptions.visible(false)
        sos.visibility = View.GONE
        sessionManager.isTrip = false
        sessionManager.tripId = ""
        sessionManager.tripStatus = ""
        isTripBegin = false
        getNearestDrivers()
        sessionManager.isDriverAndRiderAbleToChat = false
    }

    private fun ResumeRiderTipsResponse(jsonResp: JsonResponse) {

        tripDetailsModel = gson.fromJson(jsonResp.strResponse, TripDetailsModel::class.java)
        ridersDetails = tripDetailsModel!!.riders.get(0)
        var tripStatus = ridersDetails!!.status
        sessionManager.tripId = ridersDetails!!.tripId.toString()
        var invoiceModels = tripDetailsModel!!.riders.get(0).invoice
        sessionManager.isrequest = false
        sessionManager.isTrip = true
        startDistanceTimer()
        isPooledOnTrip = tripDetailsModel!!.isPool!!


        // If trips status is schedule or begin trip or endtrip to open the trips page ( Map Page)
        if ("Scheduled" == tripStatus || "Begin trip" == tripStatus || "End trip" == tripStatus) {
            commonMethods.hideProgressDialog()
            if ("Scheduled" == tripStatus) {
                sessionManager.tripStatus = "arrive_now"

                isTripBegin = false
            } else if ("Begin trip" == tripStatus) {
                sessionManager.tripStatus = "arrive_now"
                isTripBegin = false
            } else if ("End trip" == tripStatus) {
                sessionManager.tripStatus = "end_trip"
                isTripBegin = true
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled = true
            }
            sessionManager.isDriverAndRiderAbleToChat = true
            isTripFunc()
        } else if ("Rating" == tripStatus) {  // To open rating page
            sessionManager.isDriverAndRiderAbleToChat = false
            CommonMethods.stopFirebaseChatListenerService(this)
            val rating = Intent(this, DriverRatingActivity::class.java)
            rating.putExtra("imgprofile", tripDetailsModel!!.driverThumbImage)
            commonMethods.hideProgressDialog()
            startActivity(rating)

        } else if ("Payment" == tripStatus) {  // To open the payment page
            sessionManager.isrequest = false
            sessionManager.isTrip = false
            val bundle = Bundle()
            bundle.putSerializable("invoiceModels", invoiceModels)
            val main = Intent(this, PaymentAmountPage::class.java)
            main.putExtra("AmountDetails", jsonResp.strResponse)
            //main.putExtra("paymentDetails", paymentDetails);
            main.putExtra("driverDetails", tripDetailsModel)
            main.putExtras(bundle)
            commonMethods.hideProgressDialog()
            startActivity(main)
        }
        hideAndShowEta()
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)

    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        DebuggableLogV(TAG, "onFailure")
        Log.i(TAG, "onFailure: Errorr jsonResp=${Gson().toJson(jsonResp)}")
        Log.i(TAG, "onFailure: Errorr DATA=$data")
    }

    fun onSuccessDriver(jsonResp: JsonResponse) {
        println("Trip Model: ${jsonResp.strResponse}")
        tripDetailsModel = gson.fromJson(jsonResp.strResponse, TripDetailsModel::class.java)
        ridersDetails = tripDetailsModel!!.riders.get(0)
        try {
            sessionManager.driverName = tripDetailsModel!!.driverName
            sessionManager.driverRating = tripDetailsModel!!.rating
            sessionManager.driverProfilePic = tripDetailsModel!!.driverThumbImage
            sessionManager.driverId = tripDetailsModel!!.driverId.toString()
            sessionManager.bookingType = ridersDetails!!.bookingType
            isPooledOnTrip = tripDetailsModel!!.isPool!!
            hideAndShowEta()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onSuccessProfile(jsonResp: String) {
        riderProfile = gson.fromJson(jsonResp, RiderProfile::class.java)
        val currency_code = riderProfile.currencyCode
        var currency_symbol = riderProfile.currencySymbol
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currency_symbol = Html.fromHtml(currency_symbol, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            currency_symbol = Html.fromHtml(currency_symbol).toString()
        }
        val first_name = riderProfile.firstName
        val last_name = riderProfile.lastName
        val user_thumb_image = riderProfile.profileImage
        home = riderProfile.home
        work = riderProfile.work

        username.text = "$first_name $last_name"
        Picasso.get().load(user_thumb_image).into(userprofile)
        if (sessionManager.firstName.isNullOrEmpty()) {
            sessionManager.firstName = riderProfile.firstName
            sessionManager.lastName = riderProfile.lastName
            updateHomeToolBar()
        }
        //updateHomeToolBar()
        //tvWelcomeText.text = resources.getString(R.string.hey_user, username.text.toString())
        sessionManager.currencyCode = currency_code
        sessionManager.currencySymbol = currency_symbol
        sessionManager.homeAddress = home.toString()
        sessionManager.workAddress = work.toString()
        sessionManager.profileDetail = jsonResp
        sessionManager.walletAmount = riderProfile.walletAmount
        sessionManager.phoneNumber = riderProfile.mobileNumber
        sessionManager.firstName = riderProfile.firstName
        sessionManager.lastName = riderProfile.lastName


        val isFilterSelected = riderProfile.requestOptions!!.filter { featuresInCarModel: FeaturesInCarModel -> featuresInCarModel.isSelected }

        for (i in isFilterSelected.indices) {
            selectedIds.add(isFilterSelected[i].id)
        }

        if (isFilterSelected.isNotEmpty()) {
            ivFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_filter_active))
        } else {
            ivFilter.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_filter))
        }
        if (isRequestView) {
            isRequestView = false
            searchViewset(pickupForSearchCars, dropForSearchCars)
        }

        try {
            val response = JSONObject(jsonResp)
            if (response.has("promo_details")) {
                val promocount = response.getJSONArray("promo_details").length()
                sessionManager.promoDetail = response.getString("promo_details")
                sessionManager.promoCount = promocount
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isViewUpdatedWithLocalDB) {
            isViewUpdatedWithLocalDB = false
            getUserDetail()
        }

    }

    fun onSuccessSearchCar(jsonResp: JsonResponse) {
        this@MainActivity.runOnUiThread {
            requestubers.visibility = View.INVISIBLE
            edit_map.visibility = View.INVISIBLE
            rltContactAdmin.visibility = View.GONE
            listView.visibility = View.GONE
            paymentmethod.visibility = View.GONE
            requestuber.visibility = View.INVISIBLE
            loading_car.visibility = View.VISIBLE
            hideWaitingTimeDescription()
            no_car.visibility = View.GONE
            requestuber.isEnabled = false
        }
        nearestCars.clear()

        requestubers.visibility = View.VISIBLE
        edit_map.visibility = View.INVISIBLE
        updateSearchCarToolBar()
        listView.visibility = View.VISIBLE
        paymentmethod.visibility = View.VISIBLE
        requestuber.visibility = View.VISIBLE
        no_car.visibility = View.GONE
        loading_car.visibility = View.GONE
        requestuber.isEnabled = true
        requestuber.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_curve_button_yellow))

        adapter.notifyDataChanged()
        if (intent.getStringExtra("Schedule") == "Schedule") {
            date = sessionManager.scheduleDateTime
            requestuber.text = resources.getString(R.string.schedule) + clickedCarName + "\n" + date
        } else if (isPooled) {
            confirm_seat.text = resources.getString(R.string.requestuber) + clickedCarName
            requestuber.text = resources.getString(R.string.confirm_seats)
        } else {
            requestuber.text = resources.getString(R.string.requestuber) + clickedCarName
        }

        try {
            val response = JSONObject(jsonResp.strResponse)
            val statuscode: String
            statuscode = response.getString("status_code")
            val statusmessage = response.getString("status_message")

            DebuggableLogD("OUTPUT IS", statuscode)
            DebuggableLogD("OUTPUT IS", statusmessage)


            if (statuscode.matches("1".toRegex())) {
                requestubers.visibility = View.VISIBLE
                edit_map.visibility = View.INVISIBLE
                listView.visibility = View.VISIBLE
                paymentmethod.visibility = View.VISIBLE
                requestuber.visibility = View.VISIBLE
                no_car.visibility = View.GONE
                loading_car.visibility = View.GONE
                requestuber.isEnabled = true
                requestuber.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_curve_button_yellow))
                nearest_carObj = response.getJSONObject("nearest_car")

                val iterator = nearest_carObj.keys()

                var selectedcar = true
                while (iterator.hasNext()) {
                    val key = iterator.next() as String
                    if (nearest_carObj.has(key)) {

                        val car = nearest_carObj.getJSONObject(key)

                        val listdata = NearestCar()
                        listdata.carId = car.getString("car_id")
                        listdata.carName = car.getString("car_name")

                        if (car.getString("min_time") == "No cabs" || car.getString("min_time") == "") {
                            // listdata.minTime = resources.getString(R.string.no_cabs)
                            listdata.minTime = "No cabs"
                            listdata.setCarIsSelected(false)
                        } else {
                            val carminutes = car.getString("min_time")
                            listdata.minTime = resources.getQuantityString(R.plurals.minutes, carminutes.toInt(), carminutes.toInt())
                            if (selectedcar) {
                                listdata.setCarIsSelected(true)
                                clickedCarName = car.getString("car_name")
                                waitingAmount = car.getString("waiting_charge")
                                waitingMin = car.getString("waiting_time")
                                updateWaitingTime(waitingMin, waitingAmount)
                                clickedcar = car.getString("car_id")
                                clickeddriverid = car.getString("driver_id")
                                isPeakPriceAppliedForClickedCar = car.getString("apply_peak")
                                peakPriceIdForClickedCar = car.getString("peak_id")
                                minimumFareForClickedCar = car.getString("min_fare")
                                perKMForClickedCar = car.getString("per_km")
                                perMinForClickedCar = car.getString("per_min")
                                peakPriceforClickedCar = car.getString("peak_price")
                                locationIDForSelectedCar = car.getString("location_id")
                                peakIDForSelectedCar = car.getString("peak_id")

                                caramount = car.getString("fare_estimation")

                                lastposition = Integer.parseInt(car.getString("car_id"))
                                isPooled = car.getBoolean("is_pool")
                                peekPricePercentage = car.getString("additional_rider_percentage")

                                if (isPooled) {
                                    if (!caramount.equals("")) {
                                        peekPrice = caramount.toFloat()
                                        poolRate.text = sessionManager.currencySymbol + String.format("%.2f", caramount.toFloat())

                                    }
                                }
                                //isPooled = true
                                selectedcar = false
                            } else {
                                listdata.setCarIsSelected(false)
                            }
                        }
                        listdata.fareEstimation = car.getString("fare_estimation")
                        listdata.baseFare = car.getString("base_fare")
                        listdata.minFare = car.getString("min_fare")
                        listdata.perMin = car.getString("per_min")
                        listdata.perKm = car.getString("per_km")
                        listdata.capacity = car.getString("capacity")
                        listdata.applyPeak = car.getString("apply_peak")
                        listdata.peakPrice = car.getString("peak_price")
                        listdata.peakId = car.getString("peak_id")
                        listdata.carActiveImage = car.getString("car_active_image")
                        listdata.carImage = car.getString("car_image")
                        listdata.locationID = car.getString("location_id")
                        listdata.peak_id = car.getString("peak_id")
                        listdata.isPool = car.getBoolean("is_pool")
                        listdata.peakPricePercentage = car.getString("additional_rider_percentage")
                        listdata.waitingCharge = car.getString("waiting_charge")
                        listdata.waitingTime = car.getString("waiting_time")
                        nearestCars.add(listdata)
                    }
                }
                adapter.notifyDataChanged()
                if (intent.getStringExtra("Schedule") == "Schedule") {
                    date = sessionManager.scheduleDateTime
                    requestuber.text = resources.getString(R.string.schedule) + clickedCarName + "\n" + date
                } else if (isPooled) {
                    confirm_seat.text = resources.getString(R.string.requestuber) + clickedCarName
                    requestuber.text = resources.getString(R.string.confirm_seats)
                } else {
                    requestuber.text = resources.getString(R.string.requestuber) + clickedCarName
                }

                for (i in nearestCars.indices) {
                    if (nearestCars[i].minTime.equals("No cabs", ignoreCase = true)) {
                        if (rltContactAdmin.visibility != View.VISIBLE) {
                            rltContactAdmin.visibility = View.VISIBLE
                            requestuber.isEnabled = false
                            requestuber.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_curve_button_yellow_disable))
                            requestuber.text = resources.getString(R.string.no_cars)
                        }
                    } else {
                        rltContactAdmin.visibility = View.GONE
                        break
                    }
                }

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        getCar(nearest_carObj, clickedcar)
    }

    /**
     * Get nearest API called
     */
    fun getCar(nearest_carObj: JSONObject, cartype: String?) {

        try {
            val car = nearest_carObj.getJSONObject(cartype)
            var driverid = ""

            val location = car.getJSONArray("location")
            val drivers = car.getJSONArray("drivers")
            removeMarkers(carmarkers)
            driveridlist.clear()
            if (location.length() == 0) {
                if (this.geoQuery != null) {
                    geoQuery!!.removeAllListeners()
                }
                requestuber.isEnabled = false
                val statusmessage = resources.getString(R.string.no_cars)
                commonMethods.showMessage(this, dialog, statusmessage)
            } else {
                requestuber.isEnabled = true
                requestuber.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.app_curve_button_yellow))
                totalcar = location.length()

                for (i in 0 until drivers.length()) {
                    driverid = drivers.getJSONObject(i).getString("id")
                    driveridlist.add(driverid)
                }

                UpdateDriverbyTypeLocationUisngGeoFire(pickuplocation!!)

                for (j in 0 until location.length()) {
                    val cardata = location.getJSONObject(j)
                    val latitude = cardata.getString("latitude")
                    val longitude = cardata.getString("longitude")
                    val latLng = LatLng(java.lang.Double.parseDouble(latitude), java.lang.Double.parseDouble(longitude))

                    drawMarker(latLng, cartype, driverid)
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    private fun UpdateDriverbyTypeLocationUisngGeoFire(driverlocation: Location) {
        ref = FirebaseDatabase.getInstance().getReference(getString(R.string.real_time_db)).child(FirebaseDbKeys.GEOFIRE)
        geoFire = GeoFire(ref)
        var iskeyexited = false
        geoqueryfor_filtercars = geoFire?.queryAtLocation(GeoLocation(driverlocation.latitude, driverlocation.longitude), CommonKeys.car_radius.toDouble())
        geoqueryfor_filtercars!!.removeAllListeners()
        if (geoQuery != null) {
            geoQuery!!.removeAllListeners()
        }
        geoqueryfor_filtercars?.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onGeoQueryReady() {
                println("carmarkers size onGeoReady size" + carmarkers.size)
            }

            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                isTrip = sessionManager.isTrip

                if (geoQuery != null) {
                    geoQuery!!.removeAllListeners()
                }
                if (!isTrip) {

                    if (iskeyexited && carmarkers.size > 0) {
                        val itr = carmarkers.iterator()
                        while (itr.hasNext()) {
                            val marker = itr.next()
                            val name = marker.title
                            println("Name" + name + "key" + key)
                            if (name.equals(key, ignoreCase = true)) {
                                itr.remove()
                                marker.remove()
                                println("carmarkers remove" + carmarkers.size)
                            }
                            iskeyexited = false
                            println("carmarkers size  onKey Exited" + carmarkers.size)
                        }
                    }
                    val itr = driveridlist.iterator()
                    while (itr.hasNext()) {
                        val driverid = itr.next()
                        println("driverid" + driverid + "key" + key)
                        if (driverid.equals(key, ignoreCase = true)) {
                            println("entered the search area at UpdateDriverbyTypeLocationUisngGeoFire" + key)
                            //drawMarker(LatLng(location!!.latitude, location.longitude), "", key)
                        }

                    }

                }
            }

            override fun onKeyMoved(key: String?, location: GeoLocation?) {
                if (isTrip) {
                    try {
                        geoqueryfor_filtercars!!.removeGeoQueryEventListener(this)

                    } catch (e: Exception) {

                    }

                } else {
                    for (marker: Marker in carmarkers) {
                        if (marker.title.equals(key, ignoreCase = true)) {
                            addDynamicMarker(carmarkers.indexOf(marker), LatLng(location!!.latitude, location.longitude))
                        }
                    }

                }


            }

            override fun onKeyExited(key: String?) {
                System.out.println("Key exited out of the search area to " + key)
                val itr = carmarkers.iterator()
                while (itr.hasNext()) {
                    val marker = itr.next()
                    val name = marker.title
                    println("Name" + name + "key" + key)
                    if (name.equals(key, ignoreCase = true)) {
                        itr.remove()
                        marker.remove()
                        println("carmarkers remove" + carmarkers.size)
                    }
                    iskeyexited = true
                    println("carmarkers size  onKey Exited" + carmarkers.size)
                }
            }

            override fun onGeoQueryError(error: DatabaseError?) {

            }

        })


    }

    /**
     * Show marker
     */
    private fun drawMarker(point: LatLng, cartype: String?, driverid: String?) {

        println("markertitle:" + driverid)
        // Creating an instance of MarkerOptions
        val markerOptions = MarkerOptions()

        // Setting latitude and longitude for the marker
        markerOptions.position(point)
        markerOptions.title(driverid)

        if ("1" == cartype) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_car_x))
        } else if ("2" == cartype) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_car_go))
        } else if ("3" == cartype) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_car_xl))
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.app_car_go))
        }
        markerOptions.anchor(0.5f, 0.5f)

        // Adding marker on the Google Map
        val marker = mMap.addMarker(markerOptions)
        carmarkers.add(marker)

        mMap.setOnMarkerClickListener { p0 ->
            p0?.hideInfoWindow()
            true
        }


    }

    /**
     * Remove marker
     */
    private fun removeMarkers(mMarkers: MutableList<Marker>) {
        for (marker in mMarkers) {
            marker.remove()
        }
        mMarkers.clear()
    }

    /**
     * Draw route (Poliline)
     */
    fun drawRoute(markerPoints: ArrayList<*>?) {
        // Creating MarkerOptions
        // Setting the position of the marker
        val pickupmarker: MarkerOptions
        val dropmarker: MarkerOptions
        if (null != markerPoints && markerPoints.size >= 1) {
            val info = InfoWindowData()
            info.image = "snowqualmie"
            info.hotel = "Hotel : excellent hotels available"
            info.food = "Food : all types of restaurants available"
            info.transport = "Reach the site by bus, car and train."

            pickupmarker = MarkerOptions()
            pickupmarker.position(markerPoints[0] as LatLng)
            pickupmarker.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_pickup_small, this))

            //val customInfoWindow = CustomInfoWindowGoogleMap(this)
            //mMap.setInfoWindowAdapter(customInfoWindow);

            mMap.addMarker(pickupmarker)


            /*Marker m = mMap.addMarker(pickupmarker);
            m.setTag(info);
            m.showInfoWindow();*/

            dropmarker = MarkerOptions()
            dropmarker.position(markerPoints[1] as LatLng)
            dropmarker.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_drop_small, this))
            mMap.addMarker(dropmarker)

            val builder = LatLngBounds.Builder()

            //the include method will calculate the min and max bound.
            builder.include(pickupmarker.position)
            builder.include(dropmarker.position)

            val bounds = builder.build()

            val width = resources.displayMetrics.widthPixels / 2
            val height = resources.displayMetrics.heightPixels / 2
            val padding = (width * 0.08).toInt() // offset from edges of the map 10% of screen

            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)


            mMap.moveCamera(cu)

            val zoom = CameraUpdateFactory.zoomTo(14f)
            mMap.animateCamera(zoom)


        }
        var tripStatus = sessionManager.tripStatus
        // isPooledOnTrip = tripDetailsModel!!.isPool!!

        println("trip pool or not : " + tripDetailsModel?.isPool + ":" + isPooledOnTrip)
        /* if (isPooledOnTrip && (tripStatus.equals("begin_trip") || tripStatus.equals("arrive_now"))) {
             polyline?.remove()
             return
         }*/
        // Checks, whether start and end locations are captured
        if (markerPoints!!.size >= 2) {
            val origin = markerPoints[0] as LatLng
            val dest = markerPoints[1] as LatLng
            // Getting URL to the Google Directions API
            val url = getDirectionsUrl(origin, dest)


            uiScope.launch {
                downloadTask(CommonKeys.DownloadTask.DrawRoute, origin, dest)
            }

        }
    }


    private suspend fun downloadTask(polyLineType: String, origin: LatLng, dest: LatLng) {

        withContext(Dispatchers.IO) {


            var result: String? = null


            val polylineurl = getDirectionsUrl(origin, dest)

            result = downloadUrl(polylineurl)


            val jObject: JSONObject

            var routes: List<List<HashMap<String, String>>>? = null
            var distances = ""
            var overviewPolyline = ""
            var stepPoints = java.util.ArrayList<StepsClass>()
            var totalDuration: Int = 0


            //  if(result!=null){

            try {
                jObject = JSONObject(result)
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
                distances = parser.parseDistance(jObject)
                overviewPolyline = parser.parseOverviewPolyline(jObject)
                stepPoints = parser.parseStepPoints(jObject)
                totalDuration = parser.parseDuration(jObject)
                overallDuration = totalDuration

            } catch (e: Exception) {
                e.printStackTrace()
            }

            println("Distances : " + distances)


            val points: java.util.ArrayList<LatLng> = java.util.ArrayList<LatLng>()
            val lineOptions: PolylineOptions = PolylineOptions()
            if (routes != null) {
                for (i in routes.indices) {

                    val path = routes[i]

                    for (j in path.indices) {
                        val point = path[j]

                        val lat = java.lang.Double.parseDouble(point["lat"]!!)
                        val lng = java.lang.Double.parseDouble(point["lng"]!!)
                        val position = LatLng(lat, lng)
                        points.add(position)
                    }

                    lineOptions.addAll(points)
                    lineOptions.width(8f)
                    lineOptions.color(ContextCompat.getColor(this@MainActivity, R.color.app_primary_color))
                    lineOptions.geodesic(true)

                }

            }


            withContext(Dispatchers.Main) {
                if (polyLineType.equals(CommonKeys.DownloadTask.DrawRoute)) {

                    drawRoutePolyline(lineOptions, points, distances, totalDuration, overviewPolyline)

                } else if (polyLineType.equals(CommonKeys.DownloadTask.MoveMarker)) {

                    moveMarkerPolyline(lineOptions, points, distances, totalDuration, overviewPolyline, stepPoints)

                }
            }


        }


    }

    private fun moveMarkerPolyline(lineOptions: PolylineOptions, points: ArrayList<LatLng>, distances: String, totalDuration: Int, overviewPolyline: String, stepPoints: ArrayList<StepsClass>) {

        val count = (CommonKeys.getUrlCount++).toString()
        polylinepoints.clear()
        polylinepoints.addAll(points)
        tv_count.text = count.toString()
        isDownloadUrl = true

        overallDuration = totalDuration

        stepPointsList.clear()
        stepPointsList.addAll(stepPoints)

        try {
            calculateEtaFromPolyline(distances.toDouble())
            polylineOptions = PolylineOptions()
            polylineOptions = lineOptions
            polyline?.remove()
            polyline = mMap.addPolyline(lineOptions)
            val getLocation = Location(LocationManager.GPS_PROVIDER).apply {
                latitude = latLong?.latitude!!
                longitude = latLong?.longitude!!
            }


            if (polylinefromFirebase.isEmpty() || polylinefromFirebase.equals("0") || tvEta.text.toString().equals("")) {

                calculateEta(getLocation)
            }
            //calculateEta(getLocation)


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun drawRoutePolyline(lineOptions: PolylineOptions, points: ArrayList<LatLng>, distances: String, totalDuration: Int, overviewPolyline: String) {

        mMap.addPolyline(lineOptions)

        val count = (CommonKeys.getUrlCount++).toString()
        tv_count.text = (count).toString()
        polylinepoints.clear()
        polylinepoints.addAll(points)

        try {
            calculateEtaFromPolyline(distances.toDouble())
        } catch (e: Exception) {

        }

        polylineOptions = PolylineOptions()
        polylineOptions.addAll(points)

        overallDuration = totalDuration
        isDownloadUrl = true
        overviewPolylines = overviewPolyline


        if (points.size > 0) {

            val encodedPathLocations = PolyUtil.encode(points)
            trip_path = encodedPathLocations


        }
    }


    suspend fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpsURLConnection? = null
        try {
            val url = URL(strUrl)

            urlConnection = url.openConnection() as HttpsURLConnection

            urlConnection.connect()

            iStream = urlConnection.inputStream


            val sb = iStream.bufferedReader().use(BufferedReader::readText)


            data = sb


        } catch (e: Exception) {
            CommonMethods.DebuggableLogD("Exception", e.toString())
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }


    /**
     * Fare estimate function`
     */
    fun fareEstimate(position: Int, searchlist: ArrayList<NearestCar>) {
        val dialog2 = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        dialog2.setContentView(R.layout.activity_cars_available)

        val carname: TextView
        val amount: TextView
        val people: TextView
        val carImage: ImageView?

        carname = (dialog2.findViewById<View>(R.id.carname) as TextView?)!!
        amount = (dialog2.findViewById<View>(R.id.amount) as TextView?)!!
        people = (dialog2.findViewById<View>(R.id.people) as TextView?)!!
        carImage = dialog2.findViewById(R.id.car2)

        carname.text = searchlist[position].carName
        amount.text = sessionManager.currencySymbol + " " + searchlist[position].fareEstimation
        people.text = searchlist[position].capacity + " " + resources.getString(R.string.people)
        Picasso.get().load(searchlist[position].carActiveImage).error(R.drawable.car).into(carImage)

        val faredetails = dialog2.findViewById<View>(R.id.faredeatils) as ImageView?
        val done = dialog2.findViewById<View>(R.id.done) as RelativeLayout?

        done!!.setOnClickListener { dialog2.dismiss() }
        faredetails!!.setOnClickListener {
            val intent = Intent(this, FareBreakdown::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list", searchlist as Serializable)
            startActivity(intent)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        }
        if (!dialog2.isShowing) {
            dialog2.show()
        }
    }

    /**
     * schedule ride function
     */
    fun scheduleRide() {
        val dialog3 = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        dialog3.setContentView(R.layout.app_activity_schedule_ride)
        scheduleridetext = (dialog3.findViewById<View>(R.id.time_date) as TextView?)!!
        scheduleride_text = (dialog3.findViewById<View>(R.id.scheduleride_text) as TextView?)!!
        singleDateAndTimePicker = (dialog3.findViewById<View>(R.id.single_day_picker) as SingleDateAndTimePicker?)!!
        val set_pickup_window = dialog3.findViewById<View>(R.id.set_pickup_window) as Button?
        set_pickup_window!!.setOnClickListener {
            val sdf = SimpleDateFormat("EEE, MMM dd '${resources.getString(R.string.at)}' hh:mm a", Locale.getDefault())
            val currentDateandTime = sdf.format(Date())
            val aHeadCurrentDateandTime = sdf.format(Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(scheduledDuration.toLong())))
            try {
                val date: Date = sdf.parse(currentDateandTime)
                val selectedDate: Date = sdf.parse(finalDay)
                val aHeadCurrentDate: Date = sdf.parse(aHeadCurrentDateandTime)

                if (selectedDate.after(date) && (selectedDate.equals(aHeadCurrentDate) || selectedDate.after(aHeadCurrentDate))) {
                    val date = "$finalDay - $time"
                    sessionManager.scheduledDateAndTime = date
                    schedulewhere(date, "Schedule")
                } else {
                    Toast.makeText(this, resources.getString(R.string.valid_time), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            dialog3.dismiss()
        }
        if (!dialog3.isShowing) {
            dialog3.show()
        }
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

                        if (jsonObject.getJSONObject("custom").has("arrive_now")) {
                            isTripBegin = false
                            statusDialog(resources.getString(R.string.driverarrrive), 0)
                            val trip_id = jsonObject.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id")
                            sessionManager.tripId = trip_id
                            sessionManager.tripStatus = "arrive_now"
                            getPushRelatedTrip(trip_id)
                            //   req_id = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id");
                            //   pickup_address= jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickupLocation");

                        } else if (jsonObject.getJSONObject("custom").has("begin_trip")) {
                            tvDriverOTP.visibility = View.GONE
                            tvEta.visibility = View.GONE
                            hideWaitingTimeAfterAcceptingTrip()
                            pickup_location.text = ridersDetails?.drop
                            isTripBegin = true
                            polylinepoints.clear()
                            statusDialog(resources.getString(R.string.yourtripbegin), 0)
                            val trip_id = jsonObject.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id")
                            sessionManager.tripId = trip_id
                            sessionManager.tripStatus = "end_trip"
                            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return
                            }
                            mMap.isMyLocationEnabled = true
                            getPushRelatedTrip(trip_id)

                        } else if (jsonObject.getJSONObject("custom").has("end_trip")) {
                            isTrip = false
                            hideWaitingTimeAfterAcceptingTrip()
                            //statusDialog("Your Trip Ends Now...");
                            val trip_id = jsonObject.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id")
                            val driver_image = jsonObject.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image")
                            sessionManager.tripId = trip_id
                            sessionManager.driverProfilePic = driver_image
                            sessionManager.tripStatus = "end_trip"

                            val rating = Intent(this@MainActivity, DriverRatingActivity::class.java)
                            if (tripDetailsModel?.driverThumbImage != "")
                                rating.putExtra("imgprofile", tripDetailsModel?.driverThumbImage)
                            startActivity(rating)
                        } else if (jsonObject.getJSONObject("custom").has("cancel_trip")) {
                            sessionManager.isrequest = false
                            sessionManager.isTrip = false
                            statusDialog(resources.getString(R.string.yourtripcancelledbydriver), 1)
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }

    private fun getPushRelatedTrip(tripId: String) {
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (isInternetAvailable) {
            apiService.getTripDetails(sessionManager.accessToken!!, tripId).enqueue(RequestCallback(REQ_TRIP_DETAIL, this))
        } else {
            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
        }
    }

    /**
     * Trip status dialog ( like Arrive now, Begin trip, Payment completed )
     */
    fun statusDialog(message: String, show: Int) {
        val builder = android.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.addphoto_header, null)
        val tl = view.findViewById<View>(R.id.header) as TextView
        tl.text = message
        builder.setCancelable(false)
        builder.setCustomTitle(view)
        builder.setPositiveButton(resources.getString(R.string.ok_c)) { dialog, _ ->
            dialog.dismiss()
            if (show == 0) {
                if (!isRequest && sessionManager.isTrip) {

                    isPushReceived = true
                    isTripFunc()
                } else {
                    isRequestFunc()
                }
            } else {
                sessionManager.isrequest = false
                sessionManager.isTrip = false
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        val dialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val positiveButtonLL = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLL.gravity = Gravity.END
        positiveButton.layoutParams = positiveButtonLL
    }

    /**
     * Mainactivity called from other activity
     */
    public override fun onResume() {


        println("onResume")
        super.onResume()
        isMainActivity = true
        // CommonKeys.isFirstGeofireinitialize=true

        if (!TextUtils.isEmpty(sessionManager.tripId) && mSearchedpolylineReferenceListener == null)
            addPolyLineChangeListener()

        if (!TextUtils.isEmpty(sessionManager.tripId) && mSearchedLocationReferenceListener == null)
            addLatLngChangeListener()


        if (!TextUtils.isEmpty(sessionManager.tripId) && mSearchedEtaReferenceListener == null)
            addEtaChangeListner()


        /*  if (sessionManager.paymentMethod == "") {
              sessionManager.paymentMethod = CommonKeys.PAYMENT_PAYPAL
              sessionManager.isWallet=true
          }
  */




        if (sessionManager.promoCount > 0) {

            paymentmethod_type.background = ContextCompat.getDrawable(this, R.drawable.app_curve_button_green)
            paymentmethod_type.text = resources.getString(R.string.promo_applied)
            paymentmethod_type.setTextColor(ContextCompat.getColor(this, R.color.cabme_app_white))
            if (sessionManager.paymentMethodImage!!.isEmpty() || sessionManager.paymentMethodImage.equals("")) {
                if (sessionManager.paymentMethodkey == CommonKeys.PAYMENT_CASH) {
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_ic_currency))
                } else if (sessionManager.paymentMethodkey == CommonKeys.PAYMENT_CARD) {
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_ic_card))
                    //paymentmethod_type.setText(("**** " + sessionManager.getCardValue()));
                } else if (sessionManager.paymentMethodkey == CommonKeys.PAYMENT_BRAINTREE) {
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.app_ic_card))
                    //paymentmethod_type.setText(("**** " + sessionManager.getCardValue()));
                } else if (sessionManager.paymentMethodkey == CommonKeys.PAYMENT_PAYPAL) {
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.paypal))
                }
            } else {
                Picasso.get().load(sessionManager.paymentMethodImage).into(paymentmethod_img)
            }


            /*if (sessionManager.promoCount > 0) {
                if (sessionManager.paymentMethod == CommonKeys.PAYMENT_CASH) {
                    paymentmethod_img.setImageDrawable(sessionManager.paymentMethodImage)
                    paymentmethod_type.background = ContextCompat.getDrawable(this,R.drawable.d_green_fillboarder)
                    paymentmethod_type.setTextColor(ContextCompat.getColor(this,R.color.white))
                    paymentmethod_type.text = resources.getString(R.string.promo_applied)
                    paymentmethod_type.isAllCaps = false
                } else if (sessionManager.paymentMethod == CommonKeys.PAYMENT_CARD) {
                    paymentmethod_img.setImageDrawable(CommonMethods.getCardImage(sessionManager.cardBrand, resources))
                    paymentmethod_type.background = ContextCompat.getDrawable(this,R.drawable.d_green_fillboarder)
                    paymentmethod_type.setTextColor(ContextCompat.getColor(this,R.color.white))
                    paymentmethod_type.text = resources.getString(R.string.promo_applied)
                    paymentmethod_type.isAllCaps = false

                } else if (sessionManager.paymentMethod == CommonKeys.PAYMENT_BRAINTREE) {
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.online_payment))
                    paymentmethod_type.background = ContextCompat.getDrawable(this,R.drawable.d_green_fillboarder)
                    paymentmethod_type.setTextColor(ContextCompat.getColor(this,R.color.white))
                    paymentmethod_type.text = resources.getString(R.string.promo_applied)
                    paymentmethod_type.isAllCaps = false

                }
                else if (sessionManager.paymentMethod == CommonKeys.PAYMENT_BRAINTREE){
                    paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.paypal))
                    paymentmethod_type.background = ContextCompat.getDrawable(this,R.drawable.d_green_fillboarder)
                    paymentmethod_type.setTextColor(ContextCompat.getColor(this,R.color.white))
                    paymentmethod_type.isAllCaps = false
                    paymentmethod_type.text = resources.getString(R.string.promo_applied)
                }else {
                    *//*paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.paypal))
                paymentmethod_type.background = ContextCompat.getDrawable(this,R.drawable.d_green_fillboarder)
                paymentmethod_type.setTextColor(ContextCompat.getColor(this,R.color.white))
                paymentmethod_type.isAllCaps = false
                paymentmethod_type.text = resources.getString(R.string.promo_applied)*//*
                paymentmethod_type.text =""
            }

        } else {
            if (sessionManager.paymentMethod == CommonKeys.PAYMENT_CASH) {
                paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.cash))
                paymentmethod_type.text = resources.getString(R.string.cash)
            } else if (sessionManager.paymentMethod == CommonKeys.PAYMENT_CARD) {
                paymentmethod_img.setImageDrawable(CommonMethods.getCardImage(sessionManager.cardBrand, resources))
                //paymentmethod_type.setText(("**** " + sessionManager.getCardValue()));
                paymentmethod_type.text = resources.getString(R.string.card)
            } else if (sessionManager.paymentMethod == CommonKeys.PAYMENT_BRAINTREE) {
                paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.online_payment))
                //paymentmethod_type.setText(("**** " + sessionManager.getCardValue()));
                paymentmethod_type.text = resources.getString(R.string.onlinepayment)
            }*//* else {
                paymentmethod_img.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.paypal))
                paymentmethod_type.text = resources.getString(R.string.paypal)
            }*/
        } else {
            if (sessionManager.paymentMethodkey!!.isNotEmpty()) {
                paymentmethod_type.text = sessionManager.paymentMethod
                if (sessionManager.paymentMethodImage!!.isNotEmpty())
                    Picasso.get().load(sessionManager.paymentMethodImage).into(paymentmethod_img)
                else
                    paymentmethod_img.setImageResource(R.drawable.card)
            }
        }

        if (sessionManager.isWallet) {
            DebuggableLogV("isWallet", "iswallet" + sessionManager.walletAmount)
            if ("" != sessionManager.walletAmount) {
                if (java.lang.Float.valueOf(sessionManager.walletAmount) > 0)
                    wallet_img.visibility = View.VISIBLE
                else
                    wallet_img.visibility = View.GONE
            }

        } else {
            wallet_img.visibility = View.GONE
        }

        /**
         * Get Rider Profile page
         */
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (!isInternetAvailable) {
            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.no_connection))
        } else {
            getRiderDetails()
        }

        // register FCM registration complete receiver
        mRegistrationBroadcastReceiver?.let { LocalBroadcastManager.getInstance(this).registerReceiver(it, IntentFilter(Config.REGISTRATION_COMPLETE)) }
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        mRegistrationBroadcastReceiver?.let { LocalBroadcastManager.getInstance(this).registerReceiver(it, IntentFilter(Config.PUSH_NOTIFICATION)) }

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
        // code to initiate firebase chat service
        /*if (!TextUtils.isEmpty(sessionManager.tripId) && sessionManager.isDriverAndRiderAbleToChat && !CommonMethods.isMyBackgroundServiceRunning(FirebaseChatNotificationService::class.java, this)) {
            startFirebaseChatListenerService(this)
        }*/

        if (sessionManager.isTrip) {
            startDistanceTimer()
        }

        getAndroid10backgroundAndKilledScenatio()

    }


    private fun getAndroid10backgroundAndKilledScenatio() {
        if (CommonKeys.isRequestAccepted) {
            CommonKeys.isRequestAccepted = false
            sessionManager.tripId?.let { getPushRelatedTrip(it) }
        }
        if (CommonKeys.isPaymentOrCancel) {
            CommonKeys.isPaymentOrCancel = false
            enableViews(false, true)
        }

        if (CommonKeys.isArrowNow) {
            isTripBegin = false
            CommonKeys.isArrowNow = false
            sessionManager.tripId?.let { getPushRelatedTrip(it) }
        }

        if (CommonKeys.isBeginTrip) {
            CommonKeys.isBeginTrip = false
            tvDriverOTP.visibility = View.GONE
            tvEta.visibility = View.GONE
            hideWaitingTimeAfterAcceptingTrip()
            pickup_location.text = ridersDetails?.drop
            isTripBegin = true
            polylinepoints.clear()
            sessionManager.tripId?.let { getPushRelatedTrip(it) }
        }

        if (CommonKeys.isEndTrip) {
            CommonKeys.isEndTrip = false
            isTrip = false
            hideWaitingTimeAfterAcceptingTrip()
            val rating = Intent(this, DriverRatingActivity::class.java)
            if (tripDetailsModel?.driverThumbImage != "")
                rating.putExtra("imgprofile", tripDetailsModel?.driverThumbImage)
            startActivity(rating)
        }
    }

    /**
     * startTimer
     */
    private fun startDistanceTimer() {

        if (this::timerDirection.isInitialized) {
            timerDirection.cancel()
            timerDirection.purge()
        }

        timerDirection = Timer()
        timerDirectionTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    println("TimerTask")
                    isCheckDirection = true
                }
            }
        }
        timerDirection!!.scheduleAtFixedRate(timerDirectionTask, delay.toLong(), periodDirection.toLong())
        //new Timer().scheduleAtFixedRate(task, after, interval);

    }

    /**
     * To stop timer
     */
    private fun stopTimer() {
        try {
            timerDirection!!.cancel()
            timerDirection!!.purge()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * getNearest Cars
     */
    private fun getNearestDrivers() {
        if (!sessionManager.isrequest && !sessionManager.isTrip && currentLat != 0.0 && currentLng != 0.0)
            apiService.getNearestVehicles(currentLat.toString(), currentLng.toString(), sessionManager.accessToken!!).enqueue(RequestCallback(REQ_NEAREST_CARS, this))
    }

    /* ***************************************************************** */
    /*                 Trip   Functionality                              */
    /* ***************************************************************** */

    public override fun onPause() {
        super.onPause()
        stopTimer()
        mRegistrationBroadcastReceiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
    }

    fun isTripFunc() {
        whereto.visibility = View.GONE
        whereto_and_schedule.visibility = View.GONE
        meetlayout.visibility = View.VISIBLE
        //rltWelcome.visibility = View.GONE
        bottomlayout.visibility = View.VISIBLE
        ivFilter.visibility = View.GONE

        //commonMethods.setheaderText(resources.getString(R.string.enrote),common_header)
        tv_welcome_text_slogan.visibility = View.GONE
        rltWelcome.visibility = View.VISIBLE
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.app_ic_back_arrow_toolbar)
        tvWelcomeText.text = resources.getString(R.string.enrote)

        val status = sessionManager.tripStatus
        if (status == "accept_request" || status == "arrive_now" || status == "begin_trip") {
            if (!status.equals(CommonKeys.TripStatus.BeginTrip, ignoreCase = true)) {
                if (!ridersDetails?.otp.equals("null", ignoreCase = true)) {
                    tvDriverOTP.visibility = View.VISIBLE
                    tvDriverOTP.text = resources.getString(R.string.OTP) + ridersDetails?.otp
                }

                updateWaitingTimeAfterAcceptingTrip(ridersDetails?.waitingTime, ridersDetails?.waitingCharge)
            } else {
                hideWaitingTimeAfterAcceptingTrip()
                tvDriverOTP.visibility = View.GONE
                tvEta.visibility = View.GONE
            }
            /*if (!TextUtils.isEmpty(sessionManager.tripId) && sessionManager.isDriverAndRiderAbleToChat && !CommonMethods.isMyBackgroundServiceRunning(FirebaseChatNotificationService::class.java, this)) {
                startFirebaseChatListenerService(this)
            }*/
        } else {
            tvDriverOTP.visibility = View.GONE
            tvEta.visibility = View.GONE
            hideWaitingTimeAfterAcceptingTrip()
        }

        /*setting driver details for chat page*/
        sessionManager.driverName = tripDetailsModel!!.driverName
        sessionManager.driverId = tripDetailsModel!!.driverId.toString()
        sessionManager.bookingType = ridersDetails!!.bookingType
        sessionManager.driverRating = tripDetailsModel!!.rating
        sessionManager.driverProfilePic = tripDetailsModel!!.driverThumbImage

        if (sessionManager.bookingType.equals("Manual Booking", ignoreCase = true)) {
            startChat.visibility = View.INVISIBLE
        } else
            startChat.visibility = View.VISIBLE

        driver_name.text = tripDetailsModel?.driverName
        driver_car_name.text = tripDetailsModel?.vehicleName
        tripDetailsModel?.rating
        if (tripDetailsModel?.rating == "" || tripDetailsModel?.rating == "0.0") {
            driver_rating.visibility = View.GONE
        } else {
            driver_rating.text = tripDetailsModel?.rating
        }
        driver_car_number.text = tripDetailsModel?.vehicleNumber
        //driverCarName.setText(tripDetailsModel.getVehicleName());

        Picasso.get().load(tripDetailsModel?.driverThumbImage).into(driver_image)


        val pickuplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.pickupLat), java.lang.Double.valueOf(ridersDetails?.pickupLng))
        val droplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.dropLat), java.lang.Double.valueOf(ridersDetails?.dropLng))

        if ("" != sessionManager.tripStatus && sessionManager.tripStatus == "begin_trip" || sessionManager.tripStatus == "end_trip") {
            pickup_location.text = ridersDetails?.drop
        } else {
            pickup_location.text = ridersDetails?.pickup
        }


        val pickupPoints = ArrayList<LatLng>()
        if (isTripBegin) {
            // Adding new item to the ArrayList
            if (firstloop) {
                driverlatlng = LatLng(java.lang.Double.valueOf(tripDetailsModel?.driverLatitude), java.lang.Double.valueOf(tripDetailsModel?.driverLongitude))
                movepoints.add(0, driverlatlng)
                movepoints.add(1, driverlatlng)
                firstloop = false
            }
            pickupPoints.add(0, driverlatlng)
            pickupPoints.add(1, droplatlng)

        } else {
            if (firstloop) {

                driverlatlng = LatLng(java.lang.Double.valueOf(tripDetailsModel?.driverLatitude), java.lang.Double.valueOf(tripDetailsModel?.driverLongitude))
                movepoints.add(0, driverlatlng)
                movepoints.add(1, driverlatlng)
                firstloop = false
            }
            pickupPoints.add(0, driverlatlng)
            pickupPoints.add(1, pickuplatlng)
        }

        updateRoute(driverlatlng)
        //  moveMarker(driverlatlng)
        Updatepolyline(driverlatlng)
        if ("begin_trip" == status || "end_trip" == status) {
            sos.visibility = View.VISIBLE
        } else {
            sos.visibility = View.GONE
        }
    }

    private fun Updatepolyline(driverlatlng: LatLng) {

        var isonPath = false

        val lineOptions = PolylineOptions()
        if (polylinepoints.size > 0) {
            val tolerance: Double = 80.toDouble()
            isonPath = PolyUtil.isLocationOnPath(driverlatlng, polylinepoints, true, tolerance)
        }
        println("isonPath  $isonPath")
        println("updateRouteFromPush  $updateRouteFromPush")
        if (!isonPath) {
            updateRouteFromPush = false
            moveMarker(driverlatlng)
        } else {

            val location = Location(LocationManager.GPS_PROVIDER).apply {
                latitude = driverlatlng.latitude
                longitude = driverlatlng.longitude
            }
            isDownloadUrl = false
            val points = polylinepoints
            val newPoints = ArrayList<LatLng>()
            var isContracted = false
            for (j in points.indices) {

                if (!isContracted) {
                    val distance = location.distanceTo(Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = points[j].latitude
                        longitude = points[j].longitude
                    })
                    if (distance < 30.toFloat()) {
                        isContracted = true
                    }
                }
                if (isContracted) {

                    newPoints.add(points[j])
                }
            }

            if (newPoints.size > 0) {
                var oldloc = newPoints.first()

                var locations = Location(LocationManager.GPS_PROVIDER).apply {
                    latitude = oldloc.latitude
                    longitude = oldloc.longitude
                }
                var distance = 0f

                for (k in 1 until newPoints.size step 5) {
                    val newdistance = locations.distanceTo(Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = newPoints[k].latitude
                        longitude = newPoints[k].longitude
                    })
                    oldloc = newPoints[k]
                    locations = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = oldloc.latitude
                        longitude = oldloc.longitude
                    }
                    distance += newdistance
                }


                try {
                    val dKM = distance / 1000
                    println("Distance per KM $dKM")
                    if (speed < 5f) speed = 5f
                    val SKPH = speed * 3.6

                    val times = dKM / SKPH
                    ETACalculatingwithDistance = String.format("%.2f", dKM).toDouble()
                    println("EtaCalculation $ETACalculatingwithDistance")


                    etatime = (times * 60).toInt().toString()

                    if ((times * 60).toInt() < 1)
                        etatime = "1"
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }



            if (newPoints.size > 0) {
                lineOptions.addAll(newPoints)
                lineOptions.width(8f)
                lineOptions.color(ContextCompat.getColor(this, R.color.app_primary_color))
                lineOptions.geodesic(true)
                polyline?.remove()
                // polyline.points=newPoints
                polyline = mMap.addPolyline(lineOptions)
                newPoints.clear()
            }
        }
    }

    /**
     * Update route while trip (Car moving)
     */
    private fun updateRoute(driverlatlng: LatLng) {
        mMap.clear()
        val pickuplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.pickupLat), java.lang.Double.valueOf(ridersDetails?.pickupLng))
        val droplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.dropLat), java.lang.Double.valueOf(ridersDetails?.dropLng))

        // Setting the position of the marker

        if (isTripBegin) {
            pickupOptions.position(droplatlng)
            pickupOptions.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_drop_small, this))
        } else {
            pickupOptions.position(pickuplatlng)
            pickupOptions.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_pickup_small, this))
        }
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(pickupOptions)

        // Creating MarkerOptions
        val dropOptions = MarkerOptions()
        // Setting the position of the marker
        dropOptions.position(driverlatlng)
        dropOptions.anchor(0.5f, 0.5f)
        dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview))
        // Add new marker to the Google Map Android API V2
        carmarker = mMap.addMarker(dropOptions)
    }

    /**
     * Update route while trip (Car moving)
     */
    fun moveMarker(driverlatlng: LatLng) {

        if (ridersDetails == null || ridersDetails?.pickupLat == null || ridersDetails?.pickupLat.equals("") || ridersDetails?.pickupLat.equals("null"))
            return

        val pickuplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.pickupLat.toString()), java.lang.Double.valueOf(ridersDetails?.pickupLng.toString()))
        val droplatlng = LatLng(java.lang.Double.valueOf(ridersDetails?.dropLat.toString()), java.lang.Double.valueOf(ridersDetails?.dropLng.toString()))

        // Creating MarkerOptions
        val pickupOptions = MarkerOptions()

        // Setting the position of the marker

        if (isTripBegin) {
            pickupOptions.position(droplatlng)
            pickupOptions.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_drop_small, this))
        } else {
            pickupOptions.position(pickuplatlng)
            pickupOptions.icon(commonMethods.vectorToBitmap(R.drawable.app_ic_pickup_small, this))
        }
        // Add new marker to the Google Map Android API V2

        // Creating MarkerOptions
        val dropOptions = MarkerOptions()
        // Setting the position of the marker
        dropOptions.position(driverlatlng)
        dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview))
        // Add new marker to the Google Map Android API V2
        val builder = LatLngBounds.Builder()

        //the include method will calculate the min and max bound.
        builder.include(driverlatlng)
        if (isTripBegin) {
            builder.include(droplatlng)
        } else {
            builder.include(pickuplatlng)
        }
        val bounds = builder.build()

        val width = resources.displayMetrics.widthPixels / 2
        val height = resources.displayMetrics.heightPixels / 2
        val padding = (width * 0.08).toInt() // offset from edges of the map 10% of screen

        if (firstloop) {
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding)
            mMap.moveCamera(cu)
        }

        /*var tripStatus = sessionManager.tripStatus
        if (isPooledOnTrip && (tripStatus.equals("begin_trip") || tripStatus.equals("arrive_now"))) {
            return
        }*/



        if (polylinefromFirebase.isNotEmpty() && !polylinefromFirebase.equals("0")) {
            val polylinepointsfromfirebase: List<LatLng> = PolyUtil.decode(polylinefromFirebase)
            polylinepoints.clear()
            polylinepoints.addAll(polylinepointsfromfirebase)
            isDownloadUrl = false
            val lineOptions = PolylineOptions()
            if (polylinepoints.size > 0) {
                lineOptions.addAll(polylinepoints)
                lineOptions.width(8f)
                lineOptions.color(ContextCompat.getColor(this, R.color.app_primary_color))
                lineOptions.geodesic(true)
                polyline?.remove()
                // polyline.points=newPoints
                polyline = mMap.addPolyline(lineOptions)

            }

        } else {
            if (isCheckDirection) {
                println("isCheckDirection")
                isCheckDirection = false
                val url: String

                uiScope.launch {
                    if (isTripBegin) {
                        println("droplatlng URL $isTripBegin")
                        // Getting URL to the Google Directions API
                        downloadTask(CommonKeys.DownloadTask.MoveMarker, driverlatlng, droplatlng)

                    } else {
                        println("pickuplatlng URL $isTripBegin")
                        downloadTask(CommonKeys.DownloadTask.MoveMarker, driverlatlng, pickuplatlng)
                    }
                }

            }
        }
    }

    private fun calculateEtaFromPolyline(distance: Double) {

        if (isEtaFromPolyline) {
            isEtaFromPolyline = false
            ETACalculatingwithDistance = distance.toDouble()

        }
        println("Distance calclulation : " + ETACalculatingwithDistance)

    }

    fun isRequestFunc() {
        meetlayout.visibility = View.GONE
        bottomlayout.visibility = View.GONE
    }

    /**
     * Get Rider profile API called
     */
    private fun getRiderDetails() {
        val allHomeDataCursor: Cursor = dbHelper.getDocument(Constants.DB_KEY_USERDETAILS.toString())
        if (allHomeDataCursor.moveToFirst()) {
            isViewUpdatedWithLocalDB = true
            //tvOfflineAnnouncement.setVisibility(View.VISIBLE)
            try {
                onSuccessProfile(allHomeDataCursor.getString(0))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            followProcedureForNoDataPresentInDB()
        }

        isMainActivity = true
    }

    private fun getUserDetail() {
        if (commonMethods.isOnline(this)) {
            apiService.getRiderProfile(commonMethods.getRiderProfile(false, null)).enqueue(RequestCallback(REQ_GET_RIDER_PROFILE, this))
        } else {
            CommonMethods.showInternetNotAvailableForStoredDataViewer(this)
        }
    }

    fun followProcedureForNoDataPresentInDB() {
        if (commonMethods.isOnline(this)) {
            getUserDetail()
        } else {
            CommonMethods.showNoInternetAlert(this, object : CommonMethods.INoInternetCustomAlertCallback {
                override fun onOkayClicked() {
                    finish()
                }

                override fun onRetryClicked() {
                    followProcedureForNoDataPresentInDB()
                }

            })
        }
    }

    fun getCommonDataAPI() {

        isMainActivity = true
        apiService.getCommonData(sessionManager.accessToken!!).enqueue(RequestCallback(REQ_COMMON_DATA, this))
    }

    /**
     * Update Rider Location
     */
    fun updateRiderLoc() {
        apiService.updateLocation(locationHashMap).enqueue(RequestCallback(REQ_UPDATE_LOCATION, this))
    }

    /**
     * Search car API called
     */
    fun searchCars() {
        apiService.searchCars(locationHashMap).enqueue(RequestCallback(REQ_SEARCH_CARS, this))
    }

    /**
     * Send request API called
     */
    fun sendRequest() {
        val sendrequst = Intent(this, SendingRequestActivity::class.java)
        sendrequst.putExtra("loadData", "load")
        sendrequst.putExtra("carname", clickedCarName)
        sendrequst.putExtra("url", "")
        sendrequst.putExtra("totalcar", totalcar)
        sendrequst.putExtra("hashMap", locationHashMap)
        startActivity(sendrequst)
        apiService.sendRequest(locationHashMap).enqueue(RequestCallback(REQ_SEND_REQUEST, this))
        finish()

    }

    /**
     * Get Accepted driver details
     */
    fun getTripDetails() {
        apiService.getTripDetails(sessionManager.accessToken!!, "").enqueue(RequestCallback(REQ_GET_DRIVER, this))
    }


    /**
     * Network error
     */
    fun dialogfunction() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.turnoninternet)).setCancelable(false).setPositiveButton(resources.getString(R.string.ok_c)) { dialog, _ -> dialog.dismiss() }

        val alert = builder.create()
        alert.show()
    }

    /**
     * Fetch address from location
     */
    fun fetchAddress(location: LatLng?, type: String) {
        alreadyRunning = false
        address = null
        getLocations = location

        object : AsyncTask<Void, Void, String>() {

            override fun doInBackground(vararg params: Void): String? {

                if (Geocoder.isPresent()) {
                    println("Check GeoCoder ${Geocoder.isPresent()}")
                    try {
                        val locale = Locale(sessionManager.languageCode)
                        val geocoder = Geocoder(applicationContext, locale)
                        val addresses = geocoder.getFromLocation(getLocations!!.latitude, getLocations!!.longitude, 1)
                        if (addresses != null) {
                            countrys = addresses[0].countryName

                            val adress0 = addresses[0].getAddressLine(0)
                            //val adress1 = addresses[0].getAddressLine(1)

                            //address = "$adress0 $adress1" // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            address = "$adress0" // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }
                    } catch (ignored: Exception) {
                        ignored.printStackTrace()
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }

                }

                return if (address != null)
                // i.e., Geocoder succeed
                {

                    address
                } else
                // i.e., Geocoder failed
                {
                    fetchAddressUsingGoogleMap()
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private fun fetchAddressUsingGoogleMap(): String? {
                addressList = ArrayList()
                val googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLocations!!.latitude + "," + getLocations!!.longitude + "&sensor=false" + "&key=" + sessionManager.googleMapKey
                println("GoogleMapUrl Url : $googleMapUrl")
                try {
                    val googleMapResponse = JSONObject(ANDROID_HTTP_CLIENT.execute(HttpGet(googleMapUrl), BasicResponseHandler()))

                    // many nested loops.. not great -> use expression instead
                    // loop among all results
                    val results = googleMapResponse.get("results") as JSONArray
                    for (i in 0 until results.length()) {
                        val result = results.getJSONObject(i)
                        val indiStr = result.getString("formatted_address")
                        val locale = Locale(sessionManager.languageCode)
                        val addr = Address(locale)
                        addr.setAddressLine(0, indiStr)
                        addressList.add(addr)
                    }
                    countrys = (googleMapResponse.get("results") as JSONArray).getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name")
                    val len = (googleMapResponse.get("results") as JSONArray).getJSONObject(0).getJSONArray("address_components").length()
                    for (i in 0 until len) {
                        if ((googleMapResponse.get("results") as JSONArray).getJSONObject(0).getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0) == "country") {
                            countrys = (googleMapResponse.get("results") as JSONArray).getJSONObject(0).getJSONArray("address_components").getJSONObject(i).getString("long_name")
                        }
                    }

                    val adress0 = addressList[0].getAddressLine(0)
                    address = adress0 // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    address!!.replace("null".toRegex(), "")
                    if (address != null) {
                        return address
                    }

                } catch (ignored: Exception) {
                    alreadyRunning = false
                    ignored.printStackTrace()
                }

                return null
            }

            override fun onPostExecute(address: String?) {

                if (address != null) {
                    country = countrys
                    // Do something with cityName
                    DebuggableLogI("GeocoderHelper", address)
                    if ("pickupaddress" == type) {
                        if (!intent.hasExtra(CommonKeys.PickupAddress)) {
                            pickupaddresss = address
                        }
                        if (Constants.changedPickupLatlng == null) {
                            sessionManager.currentAddress = address
                        }


                        country = countrys
                    } else if ("dropaddress" == type) {
                        if (!intent.hasExtra(CommonKeys.DropAddress)) {
                            dropaddress = address
                        }
                    } else if ("dropfulladdress" == type) {
                        if (!intent.hasExtra(CommonKeys.DropAddress)) {
                            dropaddress = address
                        }
                        try {
                            val pickup = markerPoints?.get(0)
                            fetchAddress(pickup, "pickupfulladdress")
                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else if ("pickupfulladdress" == type) {

                        if (!intent.hasExtra(CommonKeys.PickupAddress)) {
                            pickupaddresss = address
                        }
                        if (Constants.changedPickupLatlng == null) {
                            sessionManager.currentAddress = address
                        }


                    }
                } else {
                    alreadyRunning = true
                    commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.unable_to_get_location))
                }
            }
        }.execute()
    }

    /**
     * Location permission request permission result
     */
    /* ***************************************************************** */
    /*                  Animate Marker for Live Tracking                 */
    /* ***************************************************************** */
    /**
     * Location enable dialog
     */
    private fun displayLocationSettingsRequest() {
        val googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> DebuggableLogI(TAG, "All location settings are satisfied.")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    DebuggableLogI(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ")

                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(mContext as Activity, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        DebuggableLogI(TAG, "PendingIntent unable to execute request.")
                    }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> DebuggableLogI(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.")
                else -> {
                }
            }
        }
    }

    /*
     *  Move marker for given locations
     **/
    fun adddefaultMarker(latlng: LatLng, latlng1: LatLng) {
        try {


            val startbearlocation = Location(LocationManager.GPS_PROVIDER)
            val endbearlocation = Location(LocationManager.GPS_PROVIDER)

            startbearlocation.latitude = latlng.latitude
            startbearlocation.longitude = latlng.longitude

            endbearlocation.latitude = latlng1.latitude
            endbearlocation.longitude = latlng1.longitude

            if (endbear.toDouble() != 0.0) {
                startbear = endbear
            }

            carmarker.isFlat = true
            carmarker.setAnchor(0.5f, 0.5f)
            marker = carmarker
            // Move map while marker gone
            ensureMarkerOnBounds(latlng, "updated", startbearlocation.bearingTo(endbearlocation))

            endbear = bearing(startbearlocation, endbearlocation).toFloat()
            endbear = (endbear * (180.0 / 3.14)).toFloat()

            val distance = java.lang.Double.valueOf(startbearlocation.distanceTo(endbearlocation).toDouble())

            if (distance > 0 && distance < 700) animateMarker(latlng1, marker, speed, endbear)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun animateMarker(destination: LatLng, marker: Marker?, speed: Float, endbear: Float) {

        val newPosition = arrayOfNulls<LatLng>(1)
        if (marker != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            var duration: Long
            val newLoc = Location(LocationManager.GPS_PROVIDER)
            newLoc.latitude = startPosition.latitude
            newLoc.longitude = startPosition.longitude
            val prevLoc = Location(LocationManager.GPS_PROVIDER)
            prevLoc.latitude = endPosition.latitude
            prevLoc.longitude = endPosition.longitude

            //final double distance = Double.valueOf(twoDForm.format(newLoc.distanceTo(prevLoc)));
            val distance = java.lang.Double.valueOf(newLoc.distanceTo(prevLoc).toDouble())

            duration = (distance / speed * 950).toLong()

            duration = 2000
            val startRotation = marker.rotation

            val latLngInterpolator = LatLngInterpolator.LinearFixed()
            valueAnimator.cancel()
            valueAnimator.end()
            valueAnimator = ValueAnimator.ofFloat(0F, 1F)
            valueAnimator.duration = duration
            valueAnimator.interpolator = LinearInterpolator()
            valueAnimator.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    newPosition[0] = latLngInterpolator.interpolate(v, startPosition, endPosition)
                    marker.position = newPosition[0]!! // Move Marker
                    marker.rotation = computeRotation(v, startRotation, endbear) // Rotate Marker
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            valueAnimator.start()
        }
    }

    /*
     *  Find GPS rotate position
     **/
    private fun bearing(startPoint: Location, endPoint: Location): Double {
        val deltaLongitude = endPoint.longitude - startPoint.longitude
        val deltaLatitude = endPoint.latitude - startPoint.latitude
        val angle = 3.14 * .5f - Math.atan(deltaLatitude / deltaLongitude)

        if (deltaLongitude > 0)
            return angle
        else if (deltaLongitude < 0)
            return angle + 3.14
        else if (deltaLatitude < 0) return 3.14

        return 0.0
    }

    /*
     *  move map to center position while marker hide
     **/
    private fun ensureMarkerOnBounds(toPosition: LatLng, type: String, bearing: Float) {
        val currentZoomLevel = mMap.cameraPosition.zoom
        //val bearing = mMap.cameraPosition.bearing
        val cameraPosition = CameraPosition.Builder().target(toPosition).zoom(currentZoomLevel).bearing(bearing).build()
        if ("updated" == type) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            if (!mMap.projection.visibleRegion.latLngBounds.contains(toPosition)) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }


    /**
     * Driver Polylinepoints change listener
     */
    private fun addPolyLineChangeListener() {
        DebuggableLogE(TAG, "Driver Location data called!")
        // User data change listener
        if (::mFirebaseDatabasePolylines.isInitialized) {
            querypolyline = mFirebaseDatabasePolylines.child(TRIP_PAYMENT_NODE).child(sessionManager.tripId!!).child(FirebaseDbKeys.TRIPLIVEPOLYLINE)
            mSearchedpolylineReferenceListener = querypolyline.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    DebuggableLogE(TAG, "Driver Location data called! success $dataSnapshot")
                    if (sessionManager.tripId != "") {
                        val polyline = dataSnapshot.getValue(String::class.java)
                        println("FirebasePolyLine $polyline")
                        isDriverForeground = !polyline.equals("0")

                        if (polyline != null) {
                            polylinefromFirebase = polyline
                            if (isPushReceived) {

                                updateRouteFromPush = true
                                isPushReceived = false
                                isTripFunc()
                            }
                        }


                    } else {
                        query.removeEventListener(this)
                        mFirebaseDatabasePolylines.removeEventListener(this)
                        mFirebaseDatabasePolylines.onDisconnect()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    DebuggableLogE(TAG, "Failed to read user", error.toException())
                }
            })
        } else {
            Toast.makeText(this, "Firabase not iniliazited", Toast.LENGTH_SHORT).show()
        }


    }

    /**
     * Driver Polylinepoints change listener
     */
    private fun addEtaChangeListner() {
        DebuggableLogE(TAG, "Driver Location data called!")
        // User data change listener
        if (::mFirebaseDatabasePolylines.isInitialized) {
            queryEta = mFirebaseDatabasePolylines.child(FirebaseDbKeys.TRIP_PAYMENT_NODE).child(sessionManager.tripId!!).child(FirebaseDbKeys.TRIPETA)
            mSearchedEtaReferenceListener = queryEta.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    DebuggableLogE(TAG, "Driver Location data called! success $dataSnapshot")
                    if (!polylinefromFirebase.isEmpty() && !polylinefromFirebase.equals("0")) {
                        if (sessionManager.tripId != "") {
                            val eta = dataSnapshot.getValue(String::class.java)
                            println("is Eta Updated From Firebase")
                            println("get Eta $eta")
                            if (eta != null) {
                                val min = eta.toLong()
                                println("get min $min")
                                if (eta.toLong() > 60) {
                                    val hours = TimeUnit.MINUTES.toHours(min.toLong())
                                    val remainMinutes = min - TimeUnit.HOURS.toMinutes(hours)

                                    println("get hours $hours")
                                    println("get remainMinutes $remainMinutes")
                                    tvEta.text = "${resources.getQuantityString(R.plurals.hours, hours.toInt(), hours.toInt())} ${"\n"} ${resources.getQuantityString(R.plurals.minutes, remainMinutes.toInt(), remainMinutes.toInt())} "
                                } else {

                                    tvEta.text = resources.getQuantityString(R.plurals.minutes, min.toInt(), min.toInt())
                                }
                            } else {

                                tvEta.text = resources.getQuantityString(R.plurals.minutes, 1, 1)
                            }
                        } else {
                            query.removeEventListener(this)
                            mFirebaseDatabasePolylines.removeEventListener(this)
                            mFirebaseDatabasePolylines.onDisconnect()
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    DebuggableLogE(TAG, "Failed to read user", error.toException())
                }
            })
        } else {
            Toast.makeText(this, "Firabase not iniliazited", Toast.LENGTH_SHORT).show()
        }


    }

    /**
     * Driver Location change listener
     */
    private fun addLatLngChangeListener() {
        DebuggableLogE(TAG, "Driver Location data called!")
        // User data change listener
        query = mFirebaseDatabase.child(LIVE_TRACKING_NODE).child(sessionManager.tripId!!)

        mSearchedLocationReferenceListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                DebuggableLogE(TAG, "Driver Location data called! success")
                if (sessionManager.tripId != "") {
                    val driverLocation = dataSnapshot.getValue(DriverLocation::class.java)

                    // Check for null
                    if (driverLocation == null) {
                        DebuggableLogE(TAG, "Driver Location data is null!")
                        return
                    }
                    showLatLng(java.lang.Double.valueOf(driverLocation.lat), java.lang.Double.valueOf(driverLocation.lng), 0.0)
                    liveTrackingFirebase(java.lang.Double.valueOf(driverLocation.lat), java.lang.Double.valueOf(driverLocation.lng))
                    DebuggableLogE(TAG, "Driver Location data is changed!" + driverLocation.lat + ", " + driverLocation.lng)

                    val getLocation = Location(LocationManager.GPS_PROVIDER).apply {
                        latitude = java.lang.Double.valueOf(driverLocation.lat)
                        longitude = java.lang.Double.valueOf(driverLocation.lng)
                    }


                    hideAndShowEta()


                } else {
                    query.removeEventListener(this)
                    mFirebaseDatabase.removeEventListener(this)
                    mFirebaseDatabase.onDisconnect()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                DebuggableLogE(TAG, "Failed to read user", error.toException())
            }
        })
    }


    fun hideAndShowEta() {
        if (sessionManager.tripStatus.equals("end_trip")) {
            tvEta.visibility = View.GONE
        } else {
            tvEta.visibility = View.VISIBLE
        }
    }

    /**
     * Live tracking function
     */
    fun liveTrackingFirebase(driverlat: Double, driverlong: Double) {

        driverlatlng = LatLng(driverlat, driverlong)

        var lineOptions = PolylineOptions()
        if (movepoints.size < 1) {
            movepoints.add(0, driverlatlng)
            movepoints.add(1, driverlatlng)

        } else {
            movepoints.set(1, movepoints.get(0))
            movepoints.set(0, driverlatlng)
        }
        val twoDForm = DecimalFormat("#.#######", DecimalFormatSymbols.getInstance(Locale.getDefault()))
        val dfs = DecimalFormatSymbols()
        dfs.decimalSeparator = '.'
        twoDForm.decimalFormatSymbols = dfs
        val zerolat = twoDForm.format((movepoints.get(0)).latitude)
        val zerolng = twoDForm.format((movepoints.get(0)).longitude)

        val onelat = twoDForm.format((movepoints.get(1)).latitude)
        val onelng = twoDForm.format((movepoints.get(1)).longitude)

        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = (movepoints.get(1)).latitude
        location.longitude = (movepoints.get(1)).longitude
        location.time = System.currentTimeMillis()

        var calculatedSpeed = 0f
        var distance = 0f

        if (lastLocation != null) {

            val elapsedTime = ((location.time - (lastLocation?.time)!!) / 1000).toDouble() // Convert milliseconds to seconds
            if (elapsedTime > 0)
                calculatedSpeed = (lastLocation!!.distanceTo(location) / elapsedTime).toFloat()
            else
                calculatedSpeed = lastLocation!!.distanceTo(location) / 1
            //calculatedSpeed= Float.valueOf(twoDForm.format(calculatedSpeed));
            distance = location.distanceTo(lastLocation)

        }

        lastLocation = location

        val driverLocation = Location("") //provider name is unnecessary

        driverLocation.setLatitude(driverlatlng.latitude) //your coords of course

        driverLocation.setLongitude(driverlatlng.longitude)

        val speedcheck = if (location.hasSpeed()) location.speed else calculatedSpeed
        println("location.hasSpeed() : " + location.hasSpeed())
        println("location.speed : " + location.hasSpeed())
        println("calculatedSpeed : " + calculatedSpeed)
        println("speed : " + speed)
        if (!java.lang.Float.isNaN(speedcheck) && !java.lang.Float.isInfinite(speedcheck)) {
            speed = (speed + speedcheck) / 2
        }

        if (speed <= 5) speed = 5f



        Updatepolyline(driverlatlng)
        adddefaultMarker(movepoints.get(1), movepoints.get(0))
        samelocation = false
        /*if ((zerolat != onelat || zerolng != onelng) && distance < 30) {
            Updatepolyline(driverlatlng)
            adddefaultMarker(movepoints.get(1), movepoints.get(0))
            samelocation = false
        }*/

        if (polylinefromFirebase.isEmpty() || polylinefromFirebase.equals("0") || tvEta.text.toString().equals("")) {

        }
        calculateEta(location)
    }

    fun scheduleRideDate(dateToSelect: Date): Date {
        val calendarMax = Calendar.getInstance(Locale.getDefault())
        val df = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)  //Normal date Format
        var newTime = ""
        try {
            val d = df.parse(dateToSelect.toString())
            calendarMax.time = d
            calendarMax.add(Calendar.MINUTE, scheduledDuration)       //Setting Maximum Duration upto 15.
            calendarMax.add(Calendar.DATE, 30)          //Setting Maximum Date upto 30.
            newTime = df.format(calendarMax.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val maxDate = calendarMax.time

        now = Calendar.getInstance().time

        val input_date = dateToSelect.toString()
        val new_date = newTime
        var dt1: Date? = null
        var dt2: Date? = null
        try {
            dt1 = df.parse(input_date)
            dt2 = df.parse(new_date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val format2 = SimpleDateFormat("EEE, MMM dd '${resources.getString(R.string.at)}' hh:mm a", Locale.getDefault()) //Getting the date format from normal present time
        finalDay = format2.format(dt1)


        val format4 = SimpleDateFormat("HH:mm", Locale.getDefault()) //Getting normal time with seconds
        val presenttime = format4.format(dt1)

        val format3 = SimpleDateFormat("hh:mm a", Locale.getDefault()) //Getting the time format from 15 mins time
        val time_15 = format3.format(dt2)

        time = time_15

        val format5 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val ScheduleRidedate = format5.format(dt1)

        val ans = "$finalDay - $time"
        scheduleridetext.text = ans
        sessionManager.scheduleDateTime = ans
        sessionManager.scheduleDate = ScheduleRidedate
        sessionManager.presentTime = presenttime
        return maxDate
    }


    fun showLatLng(lat: Double?, lng: Double?, distance: Double?) {
        if (lat!! <= 0) {
            textView1.append(" + $distance")
        } else {
            count1++
            val text = "$lat -- $lng"
            textView1.append("\n$count1 * $text")
        }
    }

    fun scheduleMethod() {
        /**
         * Schedule a ride wheel picker
         */
        val mindate = Calendar.getInstance().time           //Getting Today's date
        mindate.minutes = mindate.minutes + scheduledDuration
        val calendar = Calendar.getInstance()
        calendar.time = mindate


        singleDateAndTimePicker.setDefaultDate(mindate)
        singleDateAndTimePicker.selectDate(calendar)
        singleDateAndTimePicker.setDisplayDays(true)
        singleDateAndTimePicker.setIsAmPm(true)
        singleDateAndTimePicker.setMustBeOnFuture(true)
        singleDateAndTimePicker.mustBeOnFuture()                               //Setting date only to future
        singleDateAndTimePicker.minDate = mindate                           // MIn date ie-Today date
        singleDateAndTimePicker.maxDate = scheduleRideDate(mindate)        //Max date upto 30 dates
        singleDateAndTimePicker.setStepMinutes(1)                           //Setting minute intervals to 1min
        singleDateAndTimePicker.toString()
        singleDateAndTimePicker.addOnDateChangedListener { _, date ->
            /**
             * When scrolling the picker
             */
            /**
             * When scrolling the picker
             */
            scheduleRideDate(date)
        }
    }


    public override fun onDestroy() {
        super.onDestroy()

        if (geoQuery != null) {
            geoqueryfor_filtercars?.removeAllListeners()
        }
        if (geoqueryfor_filtercars != null) {
            geoqueryfor_filtercars?.removeAllListeners()
        }
        stopTimer()
        try {
            ANDROID_HTTP_CLIENT.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun permissionGranted(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {
        when (requestCodeForCallbackIdentificationCode) {
            RuntimePermissionDialogFragment.locationCallbackCode -> {
                run {
                    if (requestCodeForCallbackIdentificationCodeSubDivision == 0) {
                        buildGoogleApiClient()
                        displayLocationSettingsRequest()
                    } else if (requestCodeForCallbackIdentificationCodeSubDivision == 1) {
                        if (!AppUtils.isLocationEnabled(mContext)) {
                            checkGPSEnable()  // Check GPS Enable or not
                        } else {
                            fetchAddress(latLong, "pickupaddress")
                            commonMethods.showMessage(this@MainActivity, dialog, resources.getString(R.string.gettinglocate))
                        }
                    }
                }
                run { }
            }
            else -> {
            }
        }
    }

    override fun permissionDenied(requestCodeForCallbackIdentificationCode: Int, requestCodeForCallbackIdentificationCodeSubDivision: Int) {

    }

    override fun onClick(count: String) {
        try {
            seatCount = count

            if (seatCount.toInt() == 2) {

                val poolPrice = (peekPrice + (peekPrice * (peekPricePercentage.toFloat() / 100.0f))).toString()

                poolRate.text = sessionManager.currencySymbol + String.format("%.2f", poolPrice.toFloat())

            } else {
                poolRate.text = sessionManager.currencySymbol + String.format("%.2f", caramount.toFloat())
            }



            println("seatCount onClick: $seatCount")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun gotoPaymentpage() {
        if (commonMethods.isOnline(this)) {
            val intent = Intent(this, PaymentPage::class.java)
            intent.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CommonKeys.StatusCode.startPaymentActivityForView)
            startActivityForResult(intent, CommonKeys.ChangePaymentOpetionBeforeRequestCarApi)
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        } else {
            CommonMethods.showUserMessage(resources.getString(R.string.turnoninternet))
        }
    }

    companion object {

        protected val REQUEST_CHECK_SETTINGS = 0x1
        private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
        private val REQUEST_CODE_AUTOCOMPLETE = 1
        var isMainActivity = true
        var ridersDetails: Riders? = null//
        var tripDetailsModel: TripDetailsModel? = null//=new AcceptedTripId();
        private val TAG = "MAP LOCATION"
        var staticMapURL: String = ""

        private var currentLat: Double = 0.toDouble()
        private var currentLng: Double = 0.toDouble()
        private var isForFirstTime = true

        /*
     *  Rotate marker
     **/
        private fun computeRotation(fraction: Float, start: Float, end: Float): Float {
            val normalizeEnd = end - start // rotate start to 0
            val normalizedEndAbs = (normalizeEnd + 360) % 360

            val direction = (if (normalizedEndAbs > 180) -1 else 1).toFloat() // -1 = anticlockwise, 1 = clockwise
            val rotation: Float
            if (direction > 0) {
                rotation = normalizedEndAbs
            } else {
                rotation = normalizedEndAbs - 360
            }

            val result = fraction * rotation + start
            return (result + 360) % 360
        }
    }

    private fun chooseFilters() {
        val dialog3 = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        dialog3.setContentView(R.layout.filter_view)
        rvfilterList = (dialog3.findViewById<View>(R.id.rv_filter_list) as RecyclerView?)!!
        tvsaveFilter = (dialog3.findViewById<View>(R.id.tv_save_filter) as TextView?)!!

        featuresInVehicleAdapter = FeaturesInVehicleAdapter(riderProfile.requestOptions!!, this)
        rvfilterList.adapter = featuresInVehicleAdapter

        tvsaveFilter.setOnClickListener {
            commonMethods.showProgressDialog(this)
            apiService.getRiderProfile(commonMethods.getRiderProfile(true, selectedIds)).enqueue(RequestCallback(REQ_GET_RIDER_PROFILE, this))
            if (request_view.visibility == View.VISIBLE) {
                isRequestView = true
            }
            dialog3.dismiss()
        }

        if (!dialog3.isShowing) {
            dialog3.show()
        }
    }

    override fun onFeatureChoosed(id: Int, isSelected: Boolean) {
        if (!isSelected) {
            selectedIds.remove(id)
        } else {
            selectedIds.add(id)
        }
    }


    private fun updateSearchCarToolBar() {
        rltChangeLocation.visibility = View.VISIBLE
        rltWelcome.visibility = View.GONE
        toolbar.setPadding(4, 4, 4, 4)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
    }

    private fun updateHomeToolBar() {
        rltChangeLocation.visibility = View.GONE
        rltWelcome.visibility = View.VISIBLE
        if (!sessionManager.isTrip) {
            tvWelcomeText.text = resources.getString(R.string.hey_user, sessionManager.firstName + " " + sessionManager.lastName)
            tv_welcome_text_slogan.visibility = View.VISIBLE
            rltWelcome.visibility = View.VISIBLE
        }
        toolbar.setPadding(10, 10, 10, 10)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_primary_color))
    }


    /**
     *  Covid Feature Dialog
     */
    private fun covidFeatureDialog(requestType: String) {
        val dialog2 = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialogTheme)
        dialog2.setContentView(R.layout.covid_feature_view)

        val cfCancel = dialog2.findViewById<Button>(R.id.cf_cancel)
        val cfProceed = dialog2.findViewById<Button>(R.id.cf_proceed)
        cfCancel!!.setOnClickListener { dialog2.dismiss() }
        cfProceed!!.setOnClickListener {
            if (requestType.equals(Req_TYPE_POOL, true)) {
                isPooled = false
                requestCar()
            } else if (requestType.equals(Req_TYPE_NORMAL, true)) {
                seatCount = ""
                requestCar()
            }
            dialog2.dismiss()
        }
        if (!dialog2.isShowing) {
            dialog2.show()
        }
    }
}