package com.cab.user.taxi.views.facebookAccountKit

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.hbb20.CountryCodePicker
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.custompalette.FontButton
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_NAME_CODE_KEY
import com.cab.user.common.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogD
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogI
import com.cab.user.common.utils.Enums
import com.cab.user.common.utils.Enums.REQ_OTP_VERIFIACTION
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import com.cab.user.taxi.views.splash.SplashActivity.Companion.checkVersionModel
import java.util.*
import javax.inject.Inject

class FacebookAccountKitActivity : CommonActivity(), ServiceListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var customDialog: CustomDialog

    private var isPhoneNumberLayoutIsVisible = true

    @BindView(R.id.tv_mobile_heading)
    lateinit var mobileNumberHeading: TextView

    @BindView(R.id.tv_otp_resend_label)
    lateinit var tvResendOTPLabel: TextView

    @BindView(R.id.tv_otp_resend_countdown)
    lateinit var tvResendOTPCountdown: TextView

    @BindView(R.id.tv_resend_button)
    lateinit var tvResendOTP: TextView

    @BindView(R.id.tv_otp_error_field)
    lateinit var tvOTPErrorMessage: TextView

    @BindView(R.id.cl_phone_number_input)
    lateinit var ctlPhoneNumber: ConstraintLayout

    @BindView(R.id.cl_otp_input)
    lateinit var ctlOTP: ConstraintLayout

    @BindView(R.id.pb_number_verification)
    lateinit var pbNumberVerification: ProgressBar

    @BindView(R.id.ph_number_verification)
    lateinit var phNumberVerification: ProgressBar

    @BindView(R.id.imgv_next)
    lateinit var imgvArrow: ImageView

    @BindView(R.id.img_next)
    lateinit var imgArrow: ImageView

    @BindView(R.id.rl_edittexts)
    lateinit var rlEdittexts: RelativeLayout

    @BindView(R.id.one)
    lateinit var edtxOne: EditText

    @BindView(R.id.two)
    lateinit var edtxTwo: EditText

    @BindView(R.id.three)
    lateinit var edtxThree: EditText

    @BindView(R.id.four)
    lateinit var edtxFour: EditText

    @BindView(R.id.phone)
    lateinit var edtxPhoneNumber: EditText

    @BindView(R.id.ccp)
    lateinit var ccp: CountryCodePicker

    @BindView(R.id.fab_verify)
    lateinit var cvNext: CardView

    @BindView(R.id.otp_verify)
    lateinit var cvvNext: CardView

/*    @BindView(R.id.tv_back_phone_arrow) lateinit var tvPhoneBack: TextView

    @BindView(R.id.tv_back_otp_arrow) lateinit var tvOTPback: TextView*/

    /* @BindView(R.id.imgv_mobile_verify_backarrow) lateinit var mobileBackArrow: ImageView*/

    private var isForForgotPassword = 0
    private var otp = ""
    private lateinit var receivedOTPFromServer: String
    private val resendOTPWaitingSecond: Long = 120000
    private var resentCountdownTimer: CountDownTimer? = null
    private var backPressCounter: CountDownTimer? = null
    private var isDeletable = true
    var dialog: AlertDialog? = null
    private var isInternetAvailable: Boolean = false


    private lateinit var tvTermConditions: TextView
    private lateinit var iAgreeCheckBox: CheckBox
    private var iAgree: Boolean = false

    @OnClick(R.id.fab_verify)
    fun startAnimationd() {
        //startAnimation();
        if (isPhoneNumberLayoutIsVisible && edtxPhoneNumber.text.toString().length > 5) {

            isInternetAvailable = commonMethods.isOnline(this)
            if (isInternetAvailable) {

                if (checkVersionModel.otpEnabled) {
                    callSendOTPAPI()
                } else {
                    if (iAgree) {
                        redirectionActivity()
                    }else Toast.makeText(this, "Please agree to continue", Toast.LENGTH_SHORT).show()
                }

            } else {
                commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))

            }
        }
    }

    @OnClick(R.id.otp_verify)
    fun startAnimation() {
        //startAnimation();
        if (!isPhoneNumberLayoutIsVisible) {
            verifyOTP()
        }
    }

    private fun redirectionActivity() {
        val returnIntent = Intent()
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, edtxPhoneNumber.text.toString().trim { it <= ' ' })
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, ccp.selectedCountryCodeWithPlus.replace("\\+".toRegex(), ""))
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, ccp.selectedCountryCodeWithPlus.replace("\\+".toRegex(), ""))
        returnIntent.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_NAME_CODE_KEY, ccp.selectedCountryNameCode.replace("\\+".toRegex(), ""))
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    @OnClick(R.id.tv_resend_button)
    fun resendOTP() {
        isInternetAvailable = commonMethods.isOnline(this)


        isInternetAvailable = commonMethods.isOnline(this)
        if (isInternetAvailable) {
            callSendOTPAPI()
        } else {
            commonMethods.showMessage(this, dialog, resources.getString(R.string.no_connection))

        }
    }

    private fun verifyOTP() {
        showProgressBarAndHideArrow(true)
        otpVerificationAPI()

    }

    private fun otpVerificationAPI() {
        val otpParams = HashMap<String, String>()
        otpParams["otp"] = otp
        otpParams["country_code"] = ccp.selectedCountryCode
        otpParams["mobile_number"] = edtxPhoneNumber.text.toString()
        apiService.otpVerification(otpParams).enqueue(RequestCallback(Enums.REQ_OTP_VERIFIACTION, this))

    }


    private fun shakeEdittexts() {
        val shake = TranslateAnimation(0f, 20f, 0f, 0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(3f)
        rlEdittexts.startAnimation(shake)
    }

    private fun showOTPMismatchIssue() {
        shakeEdittexts()
        tvOTPErrorMessage.visibility = View.VISIBLE
    }

    private fun runCountdownTimer() {
        tvResendOTP.visibility = View.GONE
        tvResendOTPCountdown.visibility = View.VISIBLE
        tvResendOTPLabel.text = resources.getString(R.string.send_OTP_again_in)
        if (resentCountdownTimer != null) {
            resentCountdownTimer?.cancel()
        }
        resentCountdownTimer = object : CountDownTimer(resendOTPWaitingSecond, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                tvResendOTPCountdown.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                tvResendOTPCountdown.visibility = View.GONE
                tvResendOTPLabel.text = resources.getString(R.string.resend_otp)
                tvResendOTP.visibility = View.VISIBLE
            }
        }.start()
    }


    @OnClick(R.id.back)
    fun showPhoneNumberField() {
        if (ctlOTP.visibility == View.VISIBLE) {
            ctlPhoneNumber.visibility = View.VISIBLE
            ctlOTP.visibility = View.GONE
            isPhoneNumberLayoutIsVisible = true
            tvResendOTP.visibility = View.GONE
            tvResendOTPLabel.visibility = View.GONE
            tvResendOTPCountdown.visibility = View.GONE
            resentCountdownTimer?.cancel()
            if (edtxPhoneNumber.text.toString().length > 5) {
                cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_color))
                //cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.light_blue_button_color))
                //cvNext.setCardBackgroundColor(resources.getColor(R.color.light_blue_button_color))
            } else {
                //cvNext.setCardBackgroundColor(resources.getColor(R.color.quantum_grey400))
                cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_disable))
                //cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.quantum_grey400))
            }
        } else {
            /* val intent = Intent(this, SigninSignupActivity::class.java)
             startActivity(intent)
             overridePendingTransition(R.anim.ub__slide_in_right,R.anim.ub__slide_out_left)
             finish()*/
            super.onBackPressed()
        }
    }

    /* @OnClick(R.id.tv_back_phone_arrow)
     fun finishThisActivity() {
         super.onBackPressed()
     }*/

    private fun showOTPfield() {
        ctlPhoneNumber.visibility = View.GONE
        ctlOTP.visibility = View.VISIBLE
        isPhoneNumberLayoutIsVisible = false
        runCountdownTimer()
        tvResendOTPLabel.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_number_verfication)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        /**Commmon Header Text View */
        //commonMethods.setheaderText(resources.getString(R.string.register),common_header)
        initViews()
        initOTPTextviewListener()
    }

    private fun initViews() {
        dialog = commonMethods.getAlertDialog(this)
        getIntentValues()
        ccp.setAutoDetectedCountry(true)
        if (Locale.getDefault().language == "fa" || Locale.getDefault().language == "ar") {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC)
        }
        edtxPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (edtxPhoneNumber.text.toString().length > 5) {
                    cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_color))
                    //cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.cabme_app_yellow))
                } else {
                    cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_disable))
                    //cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.quantum_grey400))
                }
            }
        })

        initDirectionChanges()

        if (isForForgotPassword==0) {
            findViewById<LinearLayout?>(R.id.ll_term_condition).visibility = View.VISIBLE
            termConditionsDialog()
        }

        iAgreeCheckBox = findViewById(R.id.i_agree_box)
        iAgreeCheckBox.isChecked = true

        tvTermConditions = findViewById(R.id.tvTermConditions)
        customTextView(tvTermConditions)


    }

    private fun customTextView(view: TextView) {
        val spanTxt = SpannableStringBuilder(
            resources.getString(R.string.sigin_terms1)
        )
        spanTxt.append(resources.getString(R.string.sigin_terms2))
        spanTxt.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val url = resources.getString(R.string.terms)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)

                }
            },
            spanTxt.length - resources.getString(R.string.sigin_terms2).length,
            spanTxt.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanTxt.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_text_color)),
            spanTxt.length - resources.getString(R.string.sigin_terms2).length,
            spanTxt.length,
            0
        )
        spanTxt.append(resources.getString(R.string.sigin_terms3))
        spanTxt.append(resources.getString(R.string.sigin_terms4))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val url = resources.getString(R.string.privacy_policy)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
        }, spanTxt.length - resources.getString(R.string.sigin_terms4).length, spanTxt.length, 0)
        spanTxt.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue_text_color)),
            spanTxt.length - resources.getString(R.string.sigin_terms4).length,
            spanTxt.length,
            0
        )
        spanTxt.append(".")
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }


    private fun termConditionsDialog() {
        try {
            val dialog= Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_term_condition)


            val webView = dialog.findViewById<WebView>(R.id.web_view_terms)  // set the custom dialog components - text, image and button
            val btnAccept = dialog.findViewById<FontButton>(R.id.btnAccept)

            webView.loadUrl(getString(R.string.privacy_policy))
            webView.settings.javaScriptEnabled=true


            btnAccept.setOnClickListener {
                iAgreeCheckBox.isChecked = true
                iAgreeCheckBox.isEnabled=false
                iAgree=true
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            if (!dialog.isShowing)
                dialog.show()

        }catch (e:Exception){
            Log.i("TAG", "termConditionsDialog: Error=${e.localizedMessage}")
        }
    }


    private fun initDirectionChanges() {
        val laydir = resources.getString(R.string.layout_direction)

        if ("1" == laydir) {
            cvNext.rotation = 180f
            cvvNext.rotation = 180f
            /*tvPhoneBack.rotation = 180f
            tvOTPback.rotation = 180f*/
        }
    }

    private fun getIntentValues() {
        try {
            isForForgotPassword = intent.getIntExtra("usableType", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initOTPTextviewListener() {
        edtxOne.addTextChangedListener(OtpTextWatcher())
        edtxTwo.addTextChangedListener(OtpTextWatcher())
        edtxThree.addTextChangedListener(OtpTextWatcher())
        edtxFour.addTextChangedListener(OtpTextWatcher())

        edtxOne.setOnKeyListener(OtpTextBackWatcher())
        edtxTwo.setOnKeyListener(OtpTextBackWatcher())
        edtxThree.setOnKeyListener(OtpTextBackWatcher())
        edtxFour.setOnKeyListener(OtpTextBackWatcher())
    }

    private inner class OtpTextWatcher : TextWatcher {


        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            DebuggableLogI("Cabme", "Textchange")
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            DebuggableLogI("Cabme", "Textchange")
            if (edtxOne.isFocused) {
                if (edtxOne.text.toString().isNotEmpty())
                //size as per your requirement
                {
                    edtxTwo.requestFocus()
                    edtxTwo.setSelectAllOnFocus(true)
                }
            } else if (edtxTwo.isFocused) {
                if (edtxTwo.text.toString().isNotEmpty())
                //size as per your requirement
                {
                    edtxThree.requestFocus()
                    edtxThree.setSelectAllOnFocus(true)
                } else {
                    edtxOne.requestFocus()
                    edtxOne.setSelectAllOnFocus(true)
                }
            } else if (edtxThree.isFocused) {
                if (edtxThree.text.toString().isNotEmpty())
                //size as per your requirement
                {
                    edtxFour.requestFocus()
                    edtxFour.setSelectAllOnFocus(true)
                } else {
                    edtxTwo.requestFocus()
                    edtxTwo.setSelectAllOnFocus(true)
                }
            } else if (edtxFour.isFocused) {
                if (edtxFour.text.toString().isEmpty()) {
                    edtxThree.requestFocus()
                }
            }

            if (edtxOne.text.toString().trim { it <= ' ' }.isNotEmpty() && edtxTwo.text.toString().trim { it <= ' ' }.isNotEmpty() && edtxThree.text.toString().trim { it <= ' ' }.isNotEmpty() && edtxFour.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                otp = edtxOne.text.toString().trim { it <= ' ' } + edtxTwo.text.toString().trim { it <= ' ' } + edtxThree.text.toString().trim { it <= ' ' } + edtxFour.text.toString().trim { it <= ' ' }
                //cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.light_blue_button_color))
                cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_color))
            } else {
                otp = ""
                //cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.quantum_grey400))
                cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_disable))
            }
            tvOTPErrorMessage.visibility = View.GONE
        }

        override fun afterTextChanged(editable: Editable) {
            DebuggableLogI("Cabme", "Textchange")

        }
    }

    private inner class OtpTextBackWatcher : View.OnKeyListener {

        override fun onKey(v: View, keyCode: Int, event: KeyEvent): Boolean {
            DebuggableLogD("keycode", keyCode.toString() + "")
            DebuggableLogD("keyEvent", event.toString())
            if (keyCode == KeyEvent.KEYCODE_DEL && isDeletable) {
                when (v.id) {
                    R.id.one -> {
                        edtxOne.text.clear()
                    }
                    R.id.two -> {
                        edtxTwo.text.clear()
                        edtxOne.requestFocus()
                        edtxOne.setSelectAllOnFocus(true)
                    }
                    R.id.three -> {
                        edtxThree.text.clear()
                        edtxTwo.requestFocus()
                        edtxTwo.setSelectAllOnFocus(true)
                    }
                    R.id.four -> {
                        edtxFour.text.clear()
                        edtxThree.requestFocus()
                        edtxThree.setSelectAllOnFocus(true)
                    }//edtxThree.setSelection(1);
                }
                countdownTimerForOTPBackpress()
                return true
            } else {
                return false
            }

        }
    }

    fun countdownTimerForOTPBackpress() {
        isDeletable = false
        if (backPressCounter != null) backPressCounter!!.cancel()
        backPressCounter = object : CountDownTimer(100, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                //tvResendOTPCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            override fun onFinish() {
                isDeletable = true
            }
        }.start()
    }

    private fun callSendOTPAPI() {
        showProgressBarAndHideArrow(true)
        apiService.numbervalidation(edtxPhoneNumber.text.toString(), ccp.selectedCountryNameCode.replace("\\+".toRegex(), ""), sessionManager.type!!, sessionManager.languageCode!!, isForForgotPassword.toString()).enqueue(RequestCallback(this))
    }

    private fun showProgressBarAndHideArrow(status: Boolean) {
        if (status) {
            pbNumberVerification.visibility = View.VISIBLE
            phNumberVerification.visibility = View.VISIBLE
            imgvArrow.visibility = View.GONE
            imgArrow.visibility = View.GONE
        } else {
            pbNumberVerification.visibility = View.GONE
            phNumberVerification.visibility = View.GONE
            imgvArrow.visibility = View.VISIBLE
            imgArrow.visibility = View.VISIBLE
        }
    }


    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        showProgressBarAndHideArrow(false)

        when (jsonResp.requestCode) {
            REQ_OTP_VERIFIACTION -> {
                if (jsonResp.isSuccess) {
                    redirectionActivity()
                } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                    commonMethods.hideProgressDialog()
                    commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
                    showSettingsAlert(jsonResp.statusMsg)
                }
            }
            else -> {
                if (jsonResp.isSuccess) {
                    clearEditText()
                    receivedOTPFromServer = commonMethods.getJsonValue(jsonResp.strResponse, "otp", String::class.java) as String
                    //cvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.quantum_grey400))
                    cvvNext.setCardBackgroundColor(ContextCompat.getColor(applicationContext, R.color.app_primary_disable))
                    showOTPfield()

                    if (receivedOTPFromServer.isNotEmpty() && resources.getString(R.string.show_otp).equals("true", true)) {
                        setOtp(receivedOTPFromServer)
                    } else {
                        edtxOne.requestFocus()
                    }

                } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
                    commonMethods.hideProgressDialog()
                    commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
                    showSettingsAlert(jsonResp.statusMsg)
                }
            }
        }

    }

    private fun clearEditText() {
        edtxOne.setText("")
        edtxTwo.setText("")
        edtxThree.setText("")
        edtxFour.setText("")
    }

    /**
     * Setting otp on the Fields
     * @param otp
     */
    private fun setOtp(otp: String?) {
        if (otp != null) {
            edtxOne.setText(otp.substring(0, 1))
            edtxTwo.setText(otp.substring(1, 2))
            edtxThree.setText(otp.substring(2, 3))
            edtxFour.setText(otp.substring(3, 4))
        }
    }

    private fun showSettingsAlert(statusMsg: String) {
        val alertDialog = AlertDialog.Builder(
                this)
        //alertDialog.setTitle(statusMsg);
        alertDialog.setMessage(statusMsg)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(resources.getString(R.string.ok_c)
        ) { _, _ -> finish() }

        alertDialog.show()
    }


    override fun onFailure(jsonResp: JsonResponse, data: String) {
        commonMethods.hideProgressDialog()
        showErrorMessageAndCloseActivity()
    }

    private fun showErrorMessageAndCloseActivity() {
        CommonMethods.showServerInternalErrorMessage(this)
        finish()
    }

    override fun onBackPressed() {
        if (isPhoneNumberLayoutIsVisible) {
            super.onBackPressed()
        } else {
            showPhoneNumberField()
        }
    }

    companion object {
        fun openFacebookAccountKitActivity(activity: Activity, type: Int) {
            val facebookIntent = Intent(activity, FacebookAccountKitActivity::class.java)
            facebookIntent.putExtra("usableType", type)
            activity.startActivityForResult(facebookIntent, CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT)
        }
    }
}