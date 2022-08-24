package com.cab.user.taxi.views.firebaseChat

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.datamodels.JsonResponse
import com.cab.user.common.helper.Constants.chatPageVisible
import com.cab.user.common.interfaces.ApiService
import com.cab.user.common.interfaces.ServiceListener
import com.cab.user.common.network.AppController
import com.cab.user.common.pushnotification.NotificationUtils
import com.cab.user.common.utils.CommonKeys
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import javax.inject.Inject


class ActivityChat : CommonActivity(), FirebaseChatHandler.FirebaseChatHandlerInterface, ServiceListener {


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

    @BindView(R.id.edt_new_msg)
    lateinit var newMessage: EditText

    @BindView(R.id.rv_chat)
    lateinit var rv: RecyclerView

    @BindView(R.id.tv_profile_rating)
    lateinit var driverRating: TextView

    @BindView(R.id.tv_profile_name)
    lateinit var driverName: TextView

    @BindView(R.id.imgvu_driver_profile)
    lateinit var driverProfilePicture: ImageView

    @BindView(R.id.imgvu_emptychat)
    lateinit var noChats: ImageView

    /*   @BindView(R.id.chat_layout)
       lateinit var chatLayout: RelativeLayout*/


    private lateinit var adapterFirebaseRecylcerview: AdapterFirebaseRecylcerview
    private lateinit var firebaseChatHandler: FirebaseChatHandler
    private var sourceActivityCode: Int = 0

    @OnClick(R.id.imgvu_back)
    fun backPressed() {
        this.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        chatPageVisible = true
    }


    override fun onSuccess(jsonResp: JsonResponse, data: String) {
        if (jsonResp.isSuccess) {
            //commonMethods.hideProgressDialog()
            //onSuccessChat(jsonResp)
        } else if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            //commonMethods.hideProgressDialog()
            //commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }


    override fun onFailure(jsonResp: JsonResponse, data: String) {
        if (!TextUtils.isEmpty(jsonResp.statusMsg)) {
            //commonMethods.hideProgressDialog()
            //commonMethods.showMessage(this, dialog, jsonResp.statusMsg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_chat)

        ButterKnife.bind(this)
        AppController.appComponent.inject(this)


    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseChatHandler.unRegister()
    }

    override fun onPause() {
        chatPageVisible = false
        firebaseChatHandler.unRegister()
        /*if (!TextUtils.isEmpty(sessionManager.tripId) && sessionManager.isDriverAndRiderAbleToChat) {
            startFirebaseChatListenerService(this)
        }*/

        super.onPause()
    }

    private fun updateDriverProfileOnHeader() {
        val driverProfilePic = sessionManager.driverProfilePic
        val driverName = sessionManager.driverName
        val driverRating = sessionManager.driverRating
        if (!TextUtils.isEmpty(driverProfilePic)) {
            Picasso.get().load(driverProfilePic).error(R.drawable.car)
                    .into(driverProfilePicture)
        }

        if (!TextUtils.isEmpty(driverName)) {
            this.driverName.text = driverName
        } else {
            this.driverName.text = resources.getString(R.string.driver)
        }


        try {
            if (!driverRating.isNullOrEmpty() && driverRating.toFloat() > 0) {
                this.driverRating.visibility = View.VISIBLE
                this.driverRating.text = driverRating
            } else {
                this.driverRating.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            this.driverRating.visibility = View.GONE
        }
    }

    override fun pushMessage(firebaseChatModelClass: FirebaseChatModelClass?) {
        adapterFirebaseRecylcerview.updateChat(firebaseChatModelClass!!)
        rv.scrollToPosition(adapterFirebaseRecylcerview.itemCount - 1)
        rv.visibility = View.VISIBLE
        noChats.visibility = View.GONE
    }

    @OnClick(R.id.iv_send)
    fun sendMessage() {
        if (commonMethods.isOnline(this)) {
            apiService.updateChat(getChatParams()).enqueue(RequestCallback(this))
            firebaseChatHandler.addMessage(newMessage.text.toString().trim { it <= ' ' })
            newMessage.text.clear()
        } else {
            Toast.makeText(this, resources.getString(R.string.network_failure), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getChatParams(): HashMap<String, String> {

        val chatHashMap: java.util.HashMap<String, String> = HashMap()
        chatHashMap["message"] = newMessage.text.toString()
        chatHashMap["trip_id"] = sessionManager.tripId.toString()
        chatHashMap["receiver_id"] = sessionManager.driverId.toString()
        chatHashMap["token"] = sessionManager.accessToken.toString()
        return chatHashMap

    }

    override fun onResume() {
        super.onResume()
        //stopFirebaseChatListenerService(this)
        NotificationUtils.clearNotifications(this)

        sourceActivityCode = intent.getIntExtra(CommonKeys.FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE, CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_NOTIFICATION)

        updateDriverProfileOnHeader()
        rv.layoutManager = LinearLayoutManager(this)
        adapterFirebaseRecylcerview = AdapterFirebaseRecylcerview(this)
        rv.adapter = adapterFirebaseRecylcerview
        firebaseChatHandler = FirebaseChatHandler(this, CommonKeys.FirebaseChatserviceTriggeredFrom.chatActivity)
        rv.visibility = View.GONE
        noChats.visibility = View.VISIBLE
        rv.addOnLayoutChangeListener(View.OnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                rv.postDelayed(Runnable { rv.scrollToPosition(adapterFirebaseRecylcerview.itemCount - 1) }, 100)
            }
        })
    }
}