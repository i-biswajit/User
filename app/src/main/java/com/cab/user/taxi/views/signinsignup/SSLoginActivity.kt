package com.cab.user.taxi.views.signinsignup

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.taxi.datamodels.signinsignup.SigninResult
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_NAME_CODE_KEY
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import com.cab.user.taxi.views.facebookAccountKit.FacebookAccountKitActivity
import com.cab.user.taxi.views.main.MainActivity
import kotlinx.android.synthetic.main.app_activity_add_wallet.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@Suppress("DEPRECATION")
class SSLoginActivity : CommonActivity(), ServiceListener {

    lateinit var dialog: AlertDialog
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var apiService: ApiService
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var customDialog: CustomDialog

    @BindView(R.id.input_layout_mobile)
    lateinit var input_layout_mobile: TextInputLayout
    @BindView(R.id.input_layout_passsword)
    lateinit var input_layout_passsword: TextInputLayout
    @BindView(R.id.phone)
    lateinit var phone: EditText
    @BindView(R.id.ccp)
    lateinit var ccp: CountryCodePicker
    @BindView(R.id.password_edit)
    lateinit var password_edit: EditText
    @BindView(R.id.sigin_button)
    lateinit var sigin_button: Button
    @BindView(R.id.forgot_password)
    lateinit var forgot_password: RelativeLayout

    protected var isInternetAvailable: Boolean = false
    lateinit var signinResult: SigninResult

    @OnClick(R.id.forgot_password)
    fun forgetPassword() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this, 1)
    }

    @OnClick(R.id.back)
    fun backPressed() {
        super.onBackPressed()
    }

    @OnClick(R.id.sigin_button)
    fun signIn() {

        isInternetAvailable = commonMethods.isOnline(this)


        isInternetAvailable = commonMethods.isOnline(this)
        if (isInternetAvailable) {


            if (!validateMobile("check")) {
                input_layout_mobile.error = getString(R.string.InvalidMobileNumber)
            }
            initLoginApi()
        } else {
            commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_sslogin)

        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.signin),common_header)

        dialog = commonMethods.getAlertDialog(this)
        ccp.setAutoDetectedCountry(true)
        if (Locale.getDefault().language == "fa" || Locale.getDefault().language == "ar") {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC)
        }
        sigin_button.isEnabled = false
        sigin_button.setBackgroundDrawable(ContextCompat.getDrawable(baseContext,R.drawable.app_curve_button_yellow_disable))
        password_edit.addTextChangedListener(NameTextWatcher(password_edit))
        phone.addTextChangedListener(NameTextWatcher(phone))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) {
            if (resultCode == Activity.RESULT_OK) {
                openPasswordResetActivity(data!!.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY)!!, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY)!!, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_NAME_CODE_KEY)!!)
            }
        }
    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        commonMethods.hideProgressDialog()
        if (jsonResp.isSuccess) {
            onSuccessLogin(jsonResp)
        } else {
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.hideProgressDialog()
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }


    private inner class NameTextWatcher(private val view: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            if (validatePassword("validate") && validateMobile("validate")) {
                sigin_button.isEnabled = true
                sigin_button.setBackgroundDrawable(ContextCompat.getDrawable(baseContext,R.drawable.app_curve_button_yellow))
                //sigin_button.setBackgroundColor(ContextCompat.getColor(baseContext,R.color.cabme_app_yellow))
            } else {
                sigin_button.isEnabled = false
                sigin_button.setBackgroundDrawable(ContextCompat.getDrawable(baseContext,R.drawable.app_curve_button_yellow_disable))
                //sigin_button.setBackgroundColor(ContextCompat.getColor(baseContext,R.color.button_disable_color))
            }
          /*  if (phone.text.toString().startsWith("0")) {
                phone.setText("")
            }*/
        }

        override fun afterTextChanged(editable: Editable) {
            when (view.id) {
                R.id.password_edit -> validatePassword("check")
                R.id.phone -> validateMobile("check")
            }
        }
    }

    private fun validateMobile(type: String): Boolean {
        if (phone.text.toString().trim { it <= ' ' }.isEmpty() || phone.text.length < 6) {
            if ("check" == type) {
                requestFocus(phone)
            }
            return false
        } else {
            input_layout_mobile.isErrorEnabled = false
        }
        return true
    }

    /*
     *   validate password
     */
    private fun validatePassword(type: String): Boolean {
        if (password_edit.text.toString().trim { it <= ' ' }.isEmpty() || password_edit.text.length < 6) {
            if ("check" == type) {
                requestFocus(password_edit)
            }
            return false
        } else {
            input_layout_passsword.isErrorEnabled = false
        }
        return true
    }

    /*
     *   focus edit text
     */
    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    private fun openPasswordResetActivity(phoneNumber: String, countryCode: String,countryNameCode:String) {
        val signin = Intent(this, SSResetPassword::class.java)
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, phoneNumber)
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, countryCode)
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_NAME_CODE_KEY, countryNameCode)
        startActivity(signin)
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
        finish()
    }


    fun initLoginApi() {
        commonMethods.showProgressDialog(this)
        apiService.login(phone.text.toString().trim { it <= ' ' },
                ccp.selectedCountryNameCode.replace("\\+".toRegex(), ""),
                sessionManager.type!!, password_edit.text.toString().trim { it <= ' ' }, sessionManager.deviceType!!, sessionManager.deviceId!!, sessionManager.languageCode!!).enqueue(RequestCallback(this))
    }

    fun onSuccessLogin(jsonResp: JsonResponse) {
        signinResult = gson.fromJson(jsonResp.strResponse, SigninResult::class.java)
        sessionManager.isrequest = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sessionManager.currencySymbol = Html.fromHtml(signinResult.currencySymbol,Html.FROM_HTML_MODE_LEGACY).toString()
        }
        else {
            sessionManager.currencySymbol = Html.fromHtml(signinResult.currencySymbol).toString()
        }
        sessionManager.currencyCode = signinResult.currencyCode
        sessionManager.accessToken=signinResult.token
        sessionManager.walletAmount = signinResult.walletAmount
        sessionManager.userId = signinResult.userId
        commonMethods.hideProgressDialog()

        try {
            val response = JSONObject(jsonResp.strResponse)
            if (response.has("promo_details")) {
                val promocount = response.getJSONArray("promo_details").length()
                sessionManager.promoDetail = response.getString("promo_details")
                sessionManager.promoCount = promocount
            }
        } catch (j: JSONException) {
            j.printStackTrace()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left)
    }
}