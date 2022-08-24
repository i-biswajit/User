package com.cab.user.taxi.sidebar

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage Side_Bar
 * @category DriverContactActivity
 * @author SMR IT Solutions
 * 
 */

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonKeys.KEY_CALLER_ID
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.voip.CallProcessingActivity
import kotlinx.android.synthetic.main.app_activity_payment_page.*
import javax.inject.Inject

/* ************************************************************
   Current trip driver details and contact page
    *********************************************************** */
/*
* Editor:Umasankar
* on: 24/12/2018
 * Edited: Android action call permission removed and moved to Dial (Intent.ACTION_CALL -> Intent.ACTION_DIAL)
* purpose to reduse the no.of permission
* */
class DriverContactActivity : CommonActivity() {


    @BindView(R.id.driver_name_contact)
    lateinit var driver_name_contact: TextView
    @BindView(R.id.driver_phone_contact)
    lateinit var driver_phone_contact: TextView
    @BindView(R.id.callbutton)
    lateinit var callbutton: LinearLayout
    @BindView(R.id.messageButton)
    lateinit var messageButton: LinearLayout
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var commonMethods: CommonMethods

    var callerId = ""
    @OnClick(R.id.back)
    fun onBack() {
        onBackPressed()
    }

    @OnClick(R.id.messageButton)
    fun messageDriver() {
        CommonMethods.startChatActivityFrom(this)
    }

    @OnClick(R.id.callbutton)
    fun initializeCall() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_driver_contact)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        commonMethods.setheaderText(resources.getString(R.string.contact),common_header)

        driver_name_contact.text = intent.getStringExtra("drivername")
        driver_phone_contact.text = intent.getStringExtra("drivernumber")

        if (sessionManager.bookingType.equals("Manual Booking",ignoreCase = true)) {
            messageButton.visibility = View.GONE
        }
        /**
         * Call driver
         */
        callerId = intent.getStringExtra(KEY_CALLER_ID).toString()
        println("Caller is : "+callerId)

        callbutton.setOnClickListener {

            if (sessionManager.bookingType.equals("Manual Booking",ignoreCase = true)) {
                val intent =  Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driver_phone_contact.getText().toString()));
                startActivity(intent);
            }else {
                val callScreenIntent = Intent(this@DriverContactActivity, CallProcessingActivity::class.java)
                callScreenIntent.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CallProcessingActivity.CallActivityType.CallProcessing)
                callScreenIntent.putExtra(KEY_CALLER_ID, callerId)
                startActivity(callScreenIntent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right)
        finish()
    }

    companion object {

        val CALL = 0x2
    }

}
