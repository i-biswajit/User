package com.cab.user.taxi.sidebar.referral

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.View
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.BindView
import butterknife.OnClick
import com.google.gson.Gson
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.taxi.sidebar.referral.model.ReferredFriendsModel
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.RequestCallback
import com.cab.user.taxi.views.customize.CustomDialog

import javax.inject.Inject

import com.cab.user.common.utils.CommonKeys.CompletedReferralArray
import com.cab.user.common.utils.CommonKeys.IncompleteReferralArray
import com.cab.user.common.views.CommonActivity
import kotlinx.android.synthetic.main.app_activity_add_wallet.*

@Suppress("DEPRECATION")
class ShowReferralOptions : CommonActivity(), ServiceListener {

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

    @BindView(R.id.rv_in_completed_referrals)
    lateinit var rvIncompletedReferrals: RecyclerView

    @BindView(R.id.rv_completed_referrals)
    lateinit var rvCompletedReferrals: RecyclerView


    @BindView(R.id.constraintLayout_in_completed_friends)
    lateinit var cvIncompleteFriends: ConstraintLayout

    @BindView(R.id.scv_referal)
    lateinit var scvReferal: ScrollView


    @BindView(R.id.constraintLayout_completed_friends)
    lateinit var cvCompleteFriends: ConstraintLayout

    @BindView(R.id.constraintLayout_referral_code)
    lateinit var cvReferralHeader: ConstraintLayout

    /*MRH5VKSZ6T*/


    @BindView(R.id.tv_referral_code)
    lateinit var tvReferralCode: TextView


    @BindView(R.id.tv_total_earned)
    lateinit var tvTotalEarned: TextView

    @BindView(R.id.tv_amount)
    lateinit var tvEarnedAmount: TextView

    @BindView(R.id.tv_referral_benifit_text)
    lateinit var tvReferralBenifitStatement: TextView

    @BindView(R.id.rlt_share)
    lateinit var rltShare: RelativeLayout

 /*   @BindView(R.id.imag_share)
    lateinit var imgShare: ImageView*/


    @BindView(R.id.tv_no_referrals_yet)
    lateinit var tvNoReferralsYet: TextView

    private var referralCode = ""
    private var referralLink = ""

    lateinit private var referredFriendsModel: ReferredFriendsModel
    @OnClick(R.id.imag_share)
    fun share() {
        shareMyReferralCode()
    }

   /* @OnClick(R.id.rlt_share)
    fun share() {
        shareMyReferralCode()
    }*/

    @OnClick(R.id.arrow)
    fun backPressed() {
        onBackPressed()
    }

    @OnClick(R.id.imag_copy)
    fun connect() {
        CommonMethods.copyContentToClipboard(this, referralCode)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_show_referral_option)

        ButterKnife.bind(this)
        AppController.appComponent.inject(this)
        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.referral),common_header)
        dialog = commonMethods.getAlertDialog(this)
        scvReferal.visibility = View.GONE
        initView()

    }

    fun shareMyReferralCode() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name) + " " + resources.getString(R.string.referral))
        share.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.invite_msg) + " " + spannableString(referralCode) + " " + referralLink)

        startActivity(Intent.createChooser(share, resources.getString(R.string.share_code)))
    }

    private fun spannableString(referralCode: String): String {
        val ss = SpannableString(referralCode)
        ss.setSpan(StyleSpan(Typeface.BOLD), 0, referralCode.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss.toString()
    }

    private fun initView() {
        showOrHideReferralAccordingToSessionData()

        getReferralInformationFromAPI()
    }

    private fun showOrHideReferralAccordingToSessionData() {
        if (sessionManager.isReferralOptionEnabled) {
            cvReferralHeader.visibility = View.VISIBLE
        } else {
            cvReferralHeader.visibility = View.GONE
        }
    }

    private fun getReferralInformationFromAPI() {
        commonMethods.showProgressDialog(this)
        apiService.getReferralDetails(sessionManager.accessToken!!).enqueue(RequestCallback(this))
    }

    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        commonMethods.hideProgressDialog()
        if (jsonResp.isSuccess) {
            scvReferal.visibility = View.VISIBLE
            onSuccessResult(jsonResp.strResponse)
        } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            commonMethods.showMessage(this, dialog, jsonResp.statusMsg)

        }
    }

    private fun onSuccessResult(strResponse: String) {
        referredFriendsModel = gson.fromJson(strResponse, ReferredFriendsModel::class.java)
        updateReferralCodeInUI()
        if (referredFriendsModel.pendingReferrals.size != 0 || referredFriendsModel.completedReferrals.size != 0) {

            showReferralsNotAvailable(true)

            proceedCompleteReferralDetails()
            proceedIncompleteReferralDetails()
        } else {
            showReferralsNotAvailable(false)
        }
    }

    private fun updateReferralCodeInUI() {
        referralCode = referredFriendsModel.referralCode
        referralLink = referredFriendsModel.referralLink
        tvReferralCode.text = referralCode
        if ("1".equals(resources.getString(R.string.layout_direction), ignoreCase = true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvReferralBenifitStatement.text = resources.getString(R.string.max_referral_earning_statement, setCurrencyFrontForRTL(Html.fromHtml(referredFriendsModel.referralAmount,Html.FROM_HTML_MODE_LEGACY).toString()))
            }else {
                tvReferralBenifitStatement.text = resources.getString(R.string.max_referral_earning_statement, setCurrencyFrontForRTL(Html.fromHtml(referredFriendsModel.referralAmount).toString()))
            }
        } else {
            tvReferralBenifitStatement.text = resources.getString(R.string.max_referral_earning_statement, Html.fromHtml(referredFriendsModel.referralAmount).toString())
        }
        //tvTotalEarned.append(referredFriendsModel.getTotalEarning().toString());
        tvEarnedAmount.text = referredFriendsModel.totalEarning+")"
    }

    private fun setCurrencyFrontForRTL(amount: String): String {
        println("amount $amount")
        val currency = amount[0].toString()
        println("currency $currency")
        return amount.replace(currency, " ") + currency
    }

    private fun showReferralsNotAvailable(show: Boolean) {
        if (show) {
            cvIncompleteFriends.visibility = View.VISIBLE
            cvCompleteFriends.visibility = View.VISIBLE
            tvNoReferralsYet.visibility = View.GONE
        } else {
            cvIncompleteFriends.visibility = View.GONE
            cvCompleteFriends.visibility = View.GONE
            tvNoReferralsYet.visibility = View.VISIBLE
        }

    }

    private fun proceedIncompleteReferralDetails() {
        if (referredFriendsModel.pendingReferrals.size != 0) {
            rvIncompletedReferrals.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(this)
            rvIncompletedReferrals.layoutManager = layoutManager
            rvIncompletedReferrals.adapter = ReferralFriendsListRecyclerViewAdapter(this, referredFriendsModel.pendingReferrals, IncompleteReferralArray)
        } else {
            cvIncompleteFriends.visibility = View.GONE
        }
    }

    private fun proceedCompleteReferralDetails() {
        if (referredFriendsModel.completedReferrals.size != 0) {
            rvCompletedReferrals.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(this)
            rvCompletedReferrals.layoutManager = layoutManager
            rvCompletedReferrals.adapter = ReferralFriendsListRecyclerViewAdapter(this, referredFriendsModel.completedReferrals, CompletedReferralArray)
        } else {
            cvCompleteFriends.visibility = View.GONE

        }
    }

    override fun onFailure(jsonResp: JsonResponse, data: String) {

    }
}
