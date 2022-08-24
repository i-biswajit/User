package com.cab.user.taxi.sidebar.payment

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage Side_Bar.payment
 * @category PaymentPage
 * @author SMR IT Solutions
 * 
 */

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.views.CommonActivity
import com.cab.user.taxi.views.customize.CustomDialog
import kotlinx.android.synthetic.main.app_activity_add_wallet.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import javax.inject.Inject

/* ************************************************************
    Promo code and amount detsils list
    *********************************************************** */
class PromoAmountActivity : CommonActivity() {
    lateinit var dialog: AlertDialog
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var commonMethods: CommonMethods
    @Inject
    lateinit var customDialog: CustomDialog

    @BindView(R.id.promotions_list)
    lateinit var listView: RecyclerView

    lateinit var context: Context
    lateinit var promoAmountAdapter: PromoAmountAdapter
    protected var isInternetAvailable: Boolean = false

    @OnClick(R.id.arrow)
    fun onback() {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_activity_promo_details)
        ButterKnife.bind(this)
        AppController.appComponent.inject(this)

        context = this
        /**Commmon Header Text View */
        commonMethods.setheaderText(resources.getString(R.string.promotions),common_header)
        dialog = commonMethods.getAlertDialog(this)
        isInternetAvailable = commonMethods.isOnline(applicationContext)

        /**
         * List to show the promo code and amount details
         */
        listView.setHasFixedSize(true)
        listView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        //recyclerView.addItemDecoration(new VerticalLineDecorator(2));

        /*listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    // tripStatus=tripdetailarraylist.get(position).getStatus();

                    //Toast.makeText(getApplicationContext(), countries.get(position), Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });*/

        loadData()

    }

    /**
     * Get and show the promo code and amount details
     */

    fun loadData() {
        promoAmountModelList.clear()

        try {
            val json = JSONArray(sessionManager.promoDetail)
            if (json.length() > 0) {
                for (i in 0 until json.length()) {
                    val jsonObj = json.getJSONObject(i)
                    val id = jsonObj.getString("id")
                    val promo_amount = jsonObj.getString("amount")
                    val promo_code = jsonObj.getString("code")
                    val promo_exp = jsonObj.getString("expire_date")

                    promoAmountModel = PromoAmountModel(promo_amount, id, promo_code, promo_exp)
                    promoAmountModelList.add(promoAmountModel)

                    val adapter = PromoAmountAdapter(promoAmountModelList, applicationContext)
                    listView.adapter = adapter

                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    override fun onResume() {
        super.onResume()
        isInternetAvailable = commonMethods.isOnline(applicationContext)
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection))
        }

    }

    companion object {

        var promoAmountModelList = ArrayList<PromoAmountModel>()
        var promoAmountModel = PromoAmountModel()
    }


}