package com.cab.user.taxi.views.splash

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage splash
 * @category SplashActivity
 * @author SMR IT Solutions
 *
 */

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.google.gson.Gson
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.CheckVersionModel
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.helper.Constants
import com.cab.user.common.helper.Constants.FORCE_UPDATE
import com.cab.user.common.helper.Constants.SKIP_UPDATE
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.Enums.REG_GET_CHECK_VERSION
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.main.MainActivity
import com.cab.user.taxi.views.signinsignup.SigninSignupActivity
import org.json.JSONException
import java.util.*
import javax.inject.Inject


/* ************************************************************
   Splash screen for rider
    *********************************************************** */
class SplashActivity : CommonActivity(), ServiceListener {
    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var apiService: ApiService

    @Inject
    lateinit var commonMethods: CommonMethods

    @Inject
    lateinit var gson: Gson
    lateinit var dialog: AlertDialog

    private lateinit var scaleDown: Animation
    private var userId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        AppController.appComponent.inject(this)

        dialog = commonMethods.getAlertDialog(this)
        sessionManager.type = "rider"
        sessionManager.deviceType = "2"
        sessionManager.isUpdateLocation = 0
        Constants.changedPickupLatlng = null

        userId = sessionManager.accessToken
        print("userId===" + userId!!)
        scaleDown = AnimationUtils.loadAnimation(this, R.anim.ub__fade_out_scale_down)

        setLocale()
        callForceUpdateAPI()
    }

    /**
     * Check Force Update
     */
    private fun callForceUpdateAPI() {
        if (!commonMethods.isOnline(applicationContext)) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection))
        } else {
            apiService.checkVersion(CommonMethods.getAppVersionNameFromGradle(), sessionManager.type!!, CommonKeys.DeviceTypeAndroid).enqueue(RequestCallback(REG_GET_CHECK_VERSION, this))
        }
    }

    public override fun onResume() {
        super.onResume()
    }


    /**
     * set language
     */
    private fun setLocale() {
        val lang = sessionManager.language

        if (lang != "") {
            val langC = sessionManager.languageCode
            val locale = Locale(langC)
            val res: Resources = resources
            val configuration: Configuration = res.getConfiguration()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else
                configuration.setLocale(locale)

            createConfigurationContext(configuration)
            this@SplashActivity.resources.updateConfiguration(configuration, this@SplashActivity.resources.displayMetrics)
        } else {
            sessionManager.language = "English"
            sessionManager.languageCode = "en"
        }


    }


    override fun onSuccess(jsonResp: JsonResponse, data: String) {

        when (jsonResp.requestCode) {
            REG_GET_CHECK_VERSION -> {
                checkVersionModel = gson.fromJson(jsonResp.strResponse, CheckVersionModel::class.java);
                if (checkVersionModel.statusCode.equals("1")) {
                    onSuccessCheckVersion(checkVersionModel)
                } else if (!TextUtils.isEmpty(checkVersionModel.statusMessage)) {
                    commonMethods.showMessage(this, dialog, checkVersionModel.statusMessage!!)
                }
            }
        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {
        commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
    }

    private fun onSuccessCheckVersion(checkVersionModel: CheckVersionModel) {
        try {
            sessionManager.isReferralOptionEnabled = checkVersionModel.enableReferral
            sessionManager.appleLoginClientId = checkVersionModel.clientId

            val foreceUpdate = checkVersionModel.forceUpdate
            when {
                foreceUpdate!! == SKIP_UPDATE || foreceUpdate == FORCE_UPDATE -> {
                    showAlertDialogButtonClicked(foreceUpdate)
                }
                else -> {
                    moveToNextScreen()
                }
            }
        } catch (j: JSONException) {
            j.printStackTrace()
        }
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(
                ContextThemeWrapper(this, R.style.AlertDialogCustom))
        alertDialog.setCancelable(false)
        //alertDialog.setTitle("Update");
        alertDialog.setMessage("Please update our app to enjoy the latest features!")
        alertDialog.setPositiveButton("Visit play store"
        ) { _, _ ->
            CommonMethods.openPlayStore(this)
            this.finish()
        }
        alertDialog.show()
    }

    private fun showAlertDialogButtonClicked(updateType: String?) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater

        val dialogView: View = inflater.inflate(R.layout.update_view, null)
        val title = dialogView.findViewById(R.id.tv_title) as TextView
        val description = dialogView.findViewById(R.id.tv_desc) as TextView
        val skip = dialogView.findViewById(R.id.tv_skip) as TextView
        val update = dialogView.findViewById(R.id.tv_update) as TextView
        val titleValue: String
        val descriptionValue: String
        if (updateType.equals(SKIP_UPDATE)) {
            titleValue = resources.getString(R.string.new_version_available)
            descriptionValue = resources.getString(R.string.update_desc)
        } else {
            skip.visibility = View.GONE
            titleValue = resources.getString(R.string.new_version_available)
            descriptionValue = resources.getString(R.string.update_desc1)
        }
        title.text = titleValue
        description.text = descriptionValue
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(false)
        skip.setOnClickListener {
            alertDialog.dismiss()
            moveToNextScreen()
        }
        update.setOnClickListener {
            CommonMethods.openPlayStore(this)
            this.finish()
        }
        alertDialog.show()
    }

    private fun moveToNextScreen() {
        if (userId != null && "" != userId) {
            sessionManager.isrequest = false
            sessionManager.isTrip = false
            val x = Intent(this, MainActivity::class.java)
            val b = Bundle()
            b.putSerializable("PickupDrop", null)
            //x.putExtras(b)
            val bndlanimation = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.cb_fade_in, R.anim.cb_face_out).toBundle()
            startActivity(x, bndlanimation)
            finish()
        } else {
            val x = Intent(this, SigninSignupActivity::class.java)
            try {
                val bndlanimation = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.cb_fade_in, R.anim.cb_face_out).toBundle()
                startActivity(x, bndlanimation)
            } catch (e: Exception) {
                startActivity(x)
            }
            //startActivity(x);
            finish()
        }
    }

    companion object {
        lateinit var checkVersionModel: CheckVersionModel
        private val SPLASH_TIME_OUT = 2000
    }
}
