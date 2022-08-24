package com.cab.user.taxi.sidebar

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage Side_Bar
 * @category Setting
 * @author SMR IT Solutions
 * 
 */

import android.app.Dialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.http.AndroidHttpClient
import android.os.AsyncTask
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.datamodels.settings.CurrencyListModel
import com.cab.user.common.datamodels.settings.CurrencyModelList
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.taxi.sidebar.currency.CurrencyModel
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogI
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogV
import com.cab.user.common.utils.CommonMethods.Companion.stopSinchService
import com.cab.user.common.utils.Enums
import com.cab.user.common.utils.Enums.REQ_CURRENCYLIST
import com.cab.user.common.utils.Enums.REQ_GET_RIDER_PROFILE
import com.cab.user.common.utils.Enums.REQ_LOGOUT
import com.cab.user.common.utils.Enums.REQ_UPDATE_CURR
import com.cab.user.common.utils.Enums.REQ_UPDATE_LANG
import com.cab.user.common.utils.Enums.REQ_UPDATE_LOCATION
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.utils.userchoice.UserChoice
import com.cab.user.common.utils.userchoice.UserChoiceSuccessResponse
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import com.cab.user.taxi.views.signinsignup.SigninSignupActivity
import com.cab.user.taxi.views.splash.SplashActivity
import kotlinx.android.synthetic.main.app_activity_add_wallet.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/* ************************************************************
   Rider setting page contain add home and work location and logout
    *********************************************************** */
class Setting : CommonActivity(), ServiceListener,UserChoiceSuccessResponse {
    private var mLastClickTime: Long = 0
    val ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(Setting::class.java.name)
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
    @Inject
    lateinit var userChoice: UserChoice

    lateinit var locationHashMap: HashMap<String, String>
    @BindView(R.id.nametext)
    lateinit var nametext: TextView
    @BindView(R.id.textadress)
    lateinit var textadress: TextView
    @BindView(R.id.worktext)
    lateinit var worktext: TextView
    @BindView(R.id.rlt_work)
    lateinit var rltWork: RelativeLayout
    @BindView(R.id.language)
    lateinit var language: TextView
    @BindView(R.id.tv_phone)
    lateinit var tvPhone: TextView
    @BindView(R.id.tv_email)
    lateinit var tvEmail: TextView
    @BindView(R.id.profile_image1)
    lateinit var profile_image: ImageView
    @BindView(R.id.hometext)
    lateinit var hometext: TextView
    @BindView(R.id.home_lay)
    lateinit var home_lay: RelativeLayout
    @BindView(R.id.currencylayout)
    lateinit var currencylayout: RelativeLayout
    @BindView(R.id.languagelayout)
    lateinit var languagelayout: RelativeLayout
    @BindView(R.id.currency_code)
    lateinit var currency_code: TextView
    @BindView(R.id.common_profile)
    lateinit var commonProfile: View
    lateinit var recyclerView1: RecyclerView
    lateinit var languageView: RecyclerView
    lateinit var languagelist: MutableList<CurrencyModel>
    lateinit var currency: String
    lateinit var Language: String
    lateinit var LanguageCode: String
    var currencyList = ArrayList<CurrencyListModel>()
    lateinit var countrys: String
    lateinit var getAddress: String
    lateinit var lat: String
    lateinit var log: String
    lateinit var homegettext: String
    lateinit var workgettext: String
    protected var isInternetAvailable: Boolean = false

    @BindView(R.id.ccp)
    lateinit var ccp: CountryCodePicker
    @OnClick(R.id.back)
    fun back() {
        onBackPressed()
    }

    @OnClick(R.id.workaddress)
    fun workaddress() {
        val intent = Intent(this, AddHome::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("workaddress", "workaddress")
        startActivity(intent)
        finish()
    }

    @OnClick(R.id.rlt_work)
    fun goWorkAddress() {
        workaddress()
    }

    @OnClick(R.id.imglatout)
    fun profile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

    @OnClick(R.id.logoutlayout)
    fun logOut() {
        val dialog2 = Dialog(this@Setting)
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setContentView(R.layout.dialog_logout)

        val cancel = dialog2.findViewById<View>(R.id.signout_cancel) as TextView // set the custom dialog components - text, image and button
        val signout = dialog2.findViewById<View>(R.id.signout_signout) as TextView

        cancel.setOnClickListener { dialog2.dismiss() } // if button is clicked, close the custom dialog

        signout.setOnClickListener {
            dialog2.dismiss()
            if (!isInternetAvailable) {
                commonMethods.showMessage(this@Setting, dialog, resources.getString(R.string.no_connection))
            } else {
                logout()
            }
        }
        dialog2.show()
    }

    /**
     * Home address text clicked
     */
    @OnClick(R.id.hometext)
    fun hometext() {
        val intent = Intent(this, AddHome::class.java)
        intent.putExtra("settinghome", "settinghome")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    @OnClick(R.id.home_lay)
    fun AddHome() {
        hometext()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_setting)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        dialog = commonMethods.getAlertDialog(this)


        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.settings),common_header)
        /**
         * To change home and work address
         */

        homegettext = sessionManager.homeAddress.toString()
        workgettext = sessionManager.workAddress.toString()
        currency = sessionManager.currencyCode.toString()
        currency_code.text =sessionManager.currencySymbol  +" "+ currency
        val hometextstr = intent.getStringExtra("hometextstr")
        val worktextstr = intent.getStringExtra("worktextstr")
        language.text = "English"
        if (hometextstr == null) {
            hometext.text = getString(R.string.home)
        }


        val bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up_lay)
        val rlParentLay = findViewById<View>(R.id.parent_lay) as ViewGroup
        //rlParentLay.startAnimation(bottomUp)
        rlParentLay.visibility = View.VISIBLE




        if (worktextstr == null) {
            worktext.text = getString(R.string.addwork)
            worktext.visibility = View.GONE
        }
        if (hometextstr != null && hometextstr != "") {


            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))
            } else {
                fetchLocation(homegettext, "homeclick")

            }

        }
        if (worktextstr != null && worktextstr != "") {
            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))
            } else {

                fetchLocation(workgettext, "workclick")
            }
        }


        /**
         * Change currency button clicked
         */
        currencylayout.setOnClickListener {
          /*  currencylayout.isClickable = false
            currency_list() // Show curtency list*/
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isInternetAvailable) {
                currencyList()
            } else {
                commonMethods.showMessage(this@Setting, dialog, resources.getString(R.string.no_connection))
            }
        }
        languagelayout.setOnClickListener {
            //languagelayout.isClickable = false
            //languagelist() // Show curtency list
            languagelist = ArrayList()
            loadlang()
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            userChoice.getUsersLanguages(this,languagelist, Enums.USER_CHOICE_LANGUAGE,this)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        ANDROID_HTTP_CLIENT.close()
    }

    /**
     * Back button to close
     */
    override fun onBackPressed() {
        //super.onBackPressed();
        finish()
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right)
    }

    /**
     * Clear application data while logout
     */
    fun clearApplicationData() {
        val cache = cacheDir
        val appDir = File(cache.parent)
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children) {
                if ("lib" != s) {
                    deleteDir(File(appDir, s))
                    DebuggableLogI("TAG", "**************** File /data/data/APP_PACKAGE/$s DELETED *******************")

                    // clearApplicationData();
                }
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Language = sessionManager.language!!
        language.text = Language
        val profiledetails = sessionManager.profileDetail
        DebuggableLogV("ProfileArraydetail", "ProfileArraydetail$profiledetails")
        if (!TextUtils.isEmpty(profiledetails)) {
            loaddata(profiledetails.toString())
        } else {

            /**
             * Get Rider profile
             */
            if (!isInternetAvailable) {
                //commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
                commonMethods.showMessage(this@Setting, dialog, resources.getString(R.string.no_connection))
            } else {
                getRiderDetails()
            }

        }

    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        commonMethods.hideProgressDialog()
        if (!jsonResp.isOnline) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data)
            return
        }
        when (jsonResp.requestCode) {

            // Get Rider Profile
            REQ_GET_RIDER_PROFILE -> {
                commonMethods.hideProgressDialog()
                if (jsonResp.isSuccess) {
                    onSuccessGetProf(jsonResp)
                } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                    commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
                }
            }

            // Update Rider Location
            REQ_UPDATE_LOCATION -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                val statuscode = jsonResp.statusCode
                if (statuscode.matches("1".toRegex())) {
                    getRiderDetails()
                }
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REQ_CURRENCYLIST -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                onSuccessGetCurr(jsonResp)
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REQ_UPDATE_CURR -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                //Integer wallet_amount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);

                var wallet_amount = commonMethods.getJsonValue(jsonResp.strResponse, "wallet_amount", String::class.java) as String
                val result = java.lang.Float.parseFloat(wallet_amount)
                val result1 = result.toInt()
                wallet_amount = Integer.toString(result1)

                println("wallet amout : $wallet_amount")
                sessionManager.walletAmount = wallet_amount
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REQ_UPDATE_LANG -> if (jsonResp.isSuccess) {
                DebuggableLogI("Settings", "Language")
                commonMethods.hideProgressDialog()
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            REQ_LOGOUT -> if (jsonResp.isSuccess) {
                commonMethods.hideProgressDialog()
                onSuccessLogout()
            } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                commonMethods.hideProgressDialog()
                commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
            }
            else -> commonMethods.hideProgressDialog()
        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.hideProgressDialog()
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }

    fun onSuccessGetProf(jsonResp: JsonResponse) {
        if (jsonResp.statusCode.matches("1".toRegex())) {
            sessionManager.profileDetail = jsonResp.strResponse
            loaddata(jsonResp.strResponse)
        } else if (jsonResp.statusCode.matches("2".toRegex())) {
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)

        }
    }


    fun onSuccessLogout() {
        val lang = sessionManager.language
        val langCode = sessionManager.languageCode
        val clientId = sessionManager.appleLoginClientId
        stopSinchService(this)

        sessionManager.clearToken()
        sessionManager.clearAll()
        sessionManager.clearPaymentType()
        CommonKeys.isFirstSetpaymentMethod=false
        Firebase.auth.signOut()

        // Clear local saved data
        clearApplicationData() // Clear cache data

        sessionManager.language = lang
        sessionManager.languageCode = langCode
        sessionManager.appleLoginClientId = clientId

        val intent = Intent(this, SigninSignupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    /**
     * Show rider details and address
     */
    fun loaddata(profiledetails: String) {
        try {
            val jsonObj = JSONObject(profiledetails)
            val first_name = jsonObj.getString("first_name")
            val last_name = jsonObj.getString("last_name")
            val mobile_number = jsonObj.getString("mobile_number")
            val email_id = jsonObj.getString("email_id")
            val user_thumb_image = jsonObj.getString("profile_image")
            val country_code = jsonObj.getString("country_code")

            ccp.setCountryForNameCode(country_code)
            ccp.setCcpClickable(false)
            tvPhone.setText(mobile_number)
            tvEmail.setText(email_id)
            nametext.text = "$first_name $last_name"
            Picasso.get().load(user_thumb_image).into(profile_image)

            val home = jsonObj.getString("home")
            val work = jsonObj.getString("work")

            if (!home.equals("") && !home.equals(null)){
                textadress.visibility = View.VISIBLE
                textadress.text = home
            }

            if (!work.equals("") && !work.equals(null)){
                worktext.visibility = View.VISIBLE
                worktext.text = work
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    /**
     * Logout user API called
     */
    fun logout() {
        commonMethods.showProgressDialog(this)
        apiService.logOut(sessionManager.accessToken!!, sessionManager.type!!).enqueue(RequestCallback(REQ_LOGOUT, this))

    }

    /**
     * Get rider details
     */
    fun getRiderDetails() {
        //        commonMethods.showProgressDialog(this);
        apiService.getRiderProfile(commonMethods.getRiderProfile(false,null)).enqueue(RequestCallback(REQ_GET_RIDER_PROFILE, this))

    }

    /**
     * Get Rider home and work address
     */
    fun updateRiderLoc() {
        commonMethods.showProgressDialog(this)
        apiService.uploadRiderLocation(locationHashMap).enqueue(RequestCallback(REQ_UPDATE_LOCATION, this))
    }


    /**
     * Fetch location from address if geocode or google
     */
    fun fetchLocation(addresss: String, type: String) {
        getAddress = addresss

        object : AsyncTask<Void, Void, String>() {
            internal var locations: String? = null

            override fun doInBackground(vararg params: Void): String? {

                if (Geocoder.isPresent()) {
                    try {
                        val geocoder = Geocoder(applicationContext, Locale.getDefault())
                        val address: List<Address>?

                        // May throw an IOException
                        address = geocoder.getFromLocationName(getAddress, 5)
                        if (address == null) {
                            return null
                        }
                        val location = address[0]

                        countrys = address[0].countryName

                        location.latitude
                        location.longitude

                        lat = location.latitude.toString()
                        log = location.longitude.toString()
                        locations = "$lat,$log"
                    } catch (ignored: Exception) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }

                }

                return if (locations != null)
                // i.e., Geocoder succeed
                {
                    locations
                } else
                // i.e., Geocoder failed
                {
                    fetchLocationUsingGoogleMap()
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private fun fetchLocationUsingGoogleMap(): String? {
                getAddress = getAddress.replace(" ".toRegex(), "%20")
                val googleMapUrl = "https://maps.google.com/maps/api/geocode/json?address=" + getAddress + "&sensor=false" + "&key=" + sessionManager.googleMapKey

                try {
                    val googleMapResponse = JSONObject(ANDROID_HTTP_CLIENT.execute(HttpGet(googleMapUrl),
                            BasicResponseHandler()))

                    // many nested loops.. not great -> use expression instead
                    // loop among all results


                    val longitute = (googleMapResponse.get("results") as JSONArray).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getString("lng")

                    val latitude = (googleMapResponse.get("results") as JSONArray).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getString("lat")

                    val len = (googleMapResponse.get("results") as JSONArray).getJSONObject(0)
                            .getJSONArray("address_components").length()
                    for (i in 0 until len) {
                        if ((googleMapResponse.get("results") as JSONArray).getJSONObject(0)
                                        .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0) == "country") {
                            countrys = (googleMapResponse.get("results") as JSONArray).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name")

                        }
                    }
                    /*countrys = ((JSONArray)googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(3).getString("long_name");*/

                    return "$latitude,$longitute"


                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }

                return null
            }

            override fun onPostExecute(location: String?) {
                if (location != null) {
                    val parts = location.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val lat = java.lang.Double.valueOf(parts[0])
                    val lng = java.lang.Double.valueOf(parts[1])
                    val latLng = LatLng(lat, lng)

                    if ("homeclick" == type) {
                        locationHashMap = HashMap()
                        locationHashMap["latitude"] = latLng.latitude.toString()
                        locationHashMap["longitude"] = latLng.longitude.toString()
                        locationHashMap["home"] = homegettext
                        locationHashMap["token"] = sessionManager.accessToken.toString()
                        updateRiderLoc()
                    } else if ("workclick" == type) {
                        locationHashMap = HashMap()
                        locationHashMap["latitude"] = latLng.latitude.toString()
                        locationHashMap["longitude"] = latLng.longitude.toString()
                        locationHashMap["work"] = workgettext
                        locationHashMap["token"] = sessionManager.accessToken.toString()
                        updateRiderLoc()
                    }
                }
            }
        }.execute()
    }

    private fun loadlang() {
        val languages = resources.getStringArray(R.array.language)
        val langCode = resources.getStringArray(R.array.languageCode)
        for (i in languages.indices) {
            val listdata = CurrencyModel()
            listdata.currencyName = languages[i]
            listdata.currencySymbol = langCode[i]
            languagelist.add(listdata)

        }
    }

    fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    // Get currency list from API
    fun currencyList() {
        commonMethods.showProgressDialog(this)
        apiService.currencyList(sessionManager.accessToken!!).enqueue(RequestCallback(REQ_CURRENCYLIST, this))
    }

    fun onSuccessGetCurr(jsonResp: JsonResponse) {
        val currencyModel = gson.fromJson(jsonResp.strResponse, CurrencyModelList::class.java)
        val currencyListModel = currencyModel.currencyList
        userChoice.getUserCurrency(this,currencyListModel,Enums.USER_CHOICE_CURRENCY,this)
        /*currencyList.clear()
        currencyList.addAll(currencyListModel)
        currencyListAdapter.notifyDataChanged()*/

    }

    // After Get currency to Update currency
    fun updateCurrency() {
        commonMethods.showProgressDialog(this)
        currency = sessionManager.currencyCode!!
        currency_code.text =sessionManager.currencySymbol +" "+ currency
        apiService.updateCurrency(sessionManager.accessToken!!, currency).enqueue(RequestCallback(REQ_UPDATE_CURR, this))

    }

    fun updateLanguage() {
        // commonMethods.showProgressDialog(this);
        Language = sessionManager.language!!
        LanguageCode = sessionManager.languageCode!!

        language.text = Language
        apiService.updateLanguage(sessionManager.accessToken!!, LanguageCode).enqueue(RequestCallback(REQ_UPDATE_LANG, this))

    }

    companion object {

        var langclick: Boolean? = false
        var currencyclick: Boolean? = false
        var alertDialogStores1: android.app.AlertDialog? = null
        var alertDialogStores2: android.app.AlertDialog? = null

        /**
         * delete cache data while delete
         */
        fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                if (children != null) {
                    for (i in children.indices) {
                        val success = deleteDir(File(dir, children[i]))
                        if (!success) {
                            return false
                        }
                    }
                }
            }

            return dir!!.delete()
        }
    }

    override fun onSuccessUserSelected(type: String?, userChoiceData: String?, userChoiceCode: String?) {
        if (type.equals(Enums.USER_CHOICE_LANGUAGE)){
            val langocde = sessionManager.languageCode
            val lang = sessionManager.language
            language.text = lang
            updateLanguage()
            setLocale(langocde!!)
            CommonKeys.isFirstSetpaymentMethod=false
            recreate()
            val intent = Intent(this@Setting, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
       else if (type.equals(Enums.USER_CHOICE_CURRENCY)){
            currency = sessionManager.currencyCode!!
            currency_code.text =sessionManager.currencySymbol +" "+ currency
            updateCurrency()
        }
    }


}