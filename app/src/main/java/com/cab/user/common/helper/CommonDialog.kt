package com.cab.user.common.helper

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage helper
 * @category CommonDialog
 * @author SMR IT Solutions
 *
 */

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import butterknife.ButterKnife
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods
import com.cab.user.taxi.views.main.MainActivity
import kotlinx.android.synthetic.main.activity_common_dialog.*
import java.util.*
import javax.inject.Inject

/* ************************************************************
                CommonDialog
Common dialog for firebase service its show dialog like (Arrive now , Begin trip, Payment)
*************************************************************** */
class CommonDialog : Activity(), View.OnClickListener {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var commonMethods: CommonMethods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        this.setFinishOnTouchOutside(false)

        setContentView(R.layout.activity_common_dialog)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        message.text = intent.getStringExtra("message")
        val ok_btn = findViewById<View>(R.id.ok_btn_id) as Button
        ok_btn.text = resources.getString(R.string.ok_c)
        ok_btn.setOnClickListener(this)
    }

    override fun attachBaseContext(newBase: Context?) {
        AppController.appComponent.inject(this)
        super.attachBaseContext(updateLocale(newBase))
    }

    fun updateLocale(newBase: Context?): Context? {
        var newBase = newBase
        val lang: String = sessionManager.languageCode!! // your language or load from SharedPref
        val locale = Locale(lang)
        val config = Configuration(newBase?.resources?.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)
        newBase = newBase?.createConfigurationContext(config)
        newBase?.resources?.updateConfiguration(config, newBase.resources.displayMetrics)
        return newBase
    }

    /*
    *  Get driver rating and feed back details API Called
    */
    override fun onClick(v: View) {

        when (v.id) {
            R.id.ok_btn_id -> {
                if (intent.getIntExtra("type", 0) == 0) {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = true
                } else {
                    sessionManager.isrequest = false
                    sessionManager.isTrip = false
                    sessionManager.isDriverAndRiderAbleToChat = false
                    CommonMethods.stopFirebaseChatListenerService(this)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

                this.finish()
            }
        }
    }

    override fun onBackPressed() {

    }
}
