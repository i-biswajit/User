package com.cab.user.taxi.sendrequest

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage sendrequest
 * @category DriverNotAcceptActivity
 * @author SMR IT Solutions
 *
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogV
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.main.MainActivity
import java.util.*
import javax.inject.Inject

/* ************************************************************
   Drivers Not Accept the request rider can give request again otherwise goto home page
    *************************************************************** */
class DriverNotAcceptActivity : CommonActivity(), ServiceListener {

    lateinit var dialog: AlertDialog
    var requestSend: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var gson: Gson

    @BindView(R.id.try_again)
    lateinit var try_again: TextView

    @BindView(R.id.drivernotaccept_back)
    lateinit var drivernotaccept_back: ImageView

    private var overviewPolylines: String = ""

    @BindView(R.id.tv_call)
    lateinit var tv_call: TextView

    var polylinepoints = ArrayList<LatLng>()


    @BindView(R.id.rlt_contact_admin)
    lateinit var rlt_contact_admin: RelativeLayout

    lateinit var locationHashMap: HashMap<String, String>
    protected var isInternetAvailable: Boolean = false

    @OnClick(R.id.rlt_contact_admin)
    fun callAdmin() {
        val callnumber = sessionManager.adminContact
        val intent2 = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callnumber"))
        startActivity(intent2)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_driver_not_accept)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        dialog = commonMethods.getAlertDialog(this)
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        locationHashMap = intent.getSerializableExtra("hashMap") as HashMap<String, String>


        // Back button click
        if (!TextUtils.isEmpty(sessionManager.adminContact)) {
            tv_call.text = resources.getString(R.string.call_admin, sessionManager.adminContact)
        } else {
            tv_call.text = resources.getString(R.string.no_contact_found)
            rlt_contact_admin.isEnabled = false
        }

        drivernotaccept_back = findViewById<View>(R.id.drivernotaccept_back) as ImageView
        drivernotaccept_back.setOnClickListener { activityBackPressHandler() }

        // Try again button click
        try_again = findViewById<View>(R.id.try_again) as TextView
        try_again.setOnClickListener {
            if (!isInternetAvailable) {
                commonMethods.showMessage(this@DriverNotAcceptActivity, dialog, resources.getString(R.string.no_connection))
            } else {

                if (!requestSend) {
                    requestSend = true
                    sendRequest()
                }
                //new SendRequest().execute(getIntent().getStringExtra("url"));
            }
        }
    }

    /**
     * Send car request to rider again
     */
    private fun sendRequest() {
        // commonMethods.showProgressDialog(this);
        val sendrequst = Intent(this, SendingRequestActivity::class.java)
        sendrequst.putExtra("loadData", "load")
        sendrequst.putExtra("carname", intent.getStringExtra("carname"))
        sendrequst.putExtra("url", intent.getStringExtra("url"))
        //        sendrequst.putExtra("fare_estimation", getIntent().getStringExtra("fare_estimation"));
        //sendrequst.putExtra("mapurl", getIntent().getStringExtra("mapurl"));
        sendrequst.putExtra("totalcar", intent.getIntExtra("totalcar", 0))
        sendrequst.putExtra("hashMap", locationHashMap)
        startActivity(sendrequst)
        //        if (locationHashMap != null) {
        //            locationHashMap.put("fare_estimation", getIntent().getStringExtra("fare_estimation"));
        //        }
        apiService.sendRequest(locationHashMap).enqueue(RequestCallback(this))
        finish()
    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        DebuggableLogV("DriverNotAccept", "jsonResp")
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        DebuggableLogV("DriverNotAccept", "jsonResp" + jsonResp.statusMsg)
    }

    override fun onBackPressed() {
        activityBackPressHandler()
    }


    override fun onResume() {
        super.onResume()
        requestSend = false
    }

    private fun activityBackPressHandler() {
        sessionManager.isrequest = false
        val main = Intent(this, MainActivity::class.java)
        main.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(main)
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        finish()
    }
}
