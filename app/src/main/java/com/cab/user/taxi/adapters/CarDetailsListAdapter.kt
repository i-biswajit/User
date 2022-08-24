package com.cab.user.taxi.adapters

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage cardetails
 * @category cardetailslist Adapter
 * @author SMR IT Solutions
 * 
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.cab.user.R
import com.cab.user.common.configs.SessionManager
import com.cab.user.taxi.datamodels.main.NearestCar
import com.cab.user.common.network.AppController
import com.cab.user.common.utils.CommonMethods.Companion.DebuggableLogE
import java.util.*
import javax.inject.Inject


/* ************************************************************
                Car details adapter
get car category list from adapter
*************************************************************** */

class CarDetailsListAdapter
/**
 * Constructor for Car details list adapter
 */
(var context: Context, private val modelItems: ArrayList<NearestCar>) : RecyclerView.Adapter<CarDetailsListAdapter.ViewHolder>() {

    @Inject
    lateinit var sessionManager: SessionManager// To save and get data from localsharedpreference
    var selectedItem = -1  // Check selected item from list
    var selectcar = true  // Set selected car
    var first = true  // Check car is first


    init {
        AppController.appComponent.inject(this)
    }

    /**
     * View holder
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.app_car_details, viewGroup, false)
        return ViewHolder(view)
    }

    /**
     * Bind View holder
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val currentItem = getItem(i)


        DebuggableLogE("check 69", modelItems.toString())


        viewHolder.car_min.text = currentItem.minTime
        // check car min is no cabs or not
        if (currentItem.minTime != "No cabs") {

            if (selectcar) {

                viewHolder.itemView.isSelected = true
                //selectedItem =viewHolder.getLayoutPosition();
                selectcar = false
            } else {
                viewHolder.itemView.isSelected = false
            }
            viewHolder.lltView.alpha = 1f
        } else {
            viewHolder.car_min.text = context.resources.getString(R.string.nocabs)
            viewHolder.lltView.alpha = .3f
        }

        if(currentItem.isPool){
            viewHolder.image_pool.visibility = View.VISIBLE
        }else{
            viewHolder.image_pool.visibility = View.GONE
        }

        // set default select car
        viewHolder.itemView.isSelected = selectedItem == i

        if (first && currentItem.getCarIsSelected()) {
            viewHolder.itemView.isSelected = true
            first = false
        }

        println("currentItem.getFareEstimation() : " + currentItem.fareEstimation)
        viewHolder.car_amount.text = sessionManager.currencySymbol + currentItem.fareEstimation
        viewHolder.car_name.text = currentItem.carName

        if (viewHolder.itemView.isSelected) {
            selectedItem = viewHolder.layoutPosition
            //            viewHolder.car_amount.setTextColor(context.getResources().getColor(R.color.text_black));
            //            viewHolder.car_min.setTextColor(context.getResources().getColor(R.color.text_black));
            //            viewHolder.car_name.setTextColor(context.getResources().getColor(R.color.text_black));
            //viewHolder.carimage.setBackground(context.getResources().getDrawable(R.drawable.car));
            Picasso.get().load(currentItem.carActiveImage).error(R.drawable.car).into(viewHolder.carimage)

        } else {
            //            viewHolder.car_amount.setTextColor(context.getResources().getColor(R.color.text_light_gray));
            //            viewHolder.car_min.setTextColor(context.getResources().getColor(R.color.text_light_gray));
            //            viewHolder.car_name.setTextColor(context.getResources().getColor(R.color.text_light_gray));
            //viewHolder.carimage.setBackground(context.getResources().getDrawable(R.drawable.cartwo));
            Picasso.get().load(currentItem.carImage).error(R.drawable.car).into(viewHolder.carimage)
        }


        viewHolder.itemView.setOnClickListener {
            notifyItemChanged(selectedItem)
            selectedItem = viewHolder.layoutPosition
            notifyItemChanged(selectedItem)
        }
    }

    override fun getItemCount(): Int {

        return modelItems.size
    }

    /**
     * Get Car details for given position
     */
    private fun getItem(position: Int): NearestCar {
        return modelItems[position]
    }

    /**
     * Reload list
     */
    fun notifyDataChanged() {
        selectedItem = -1
        selectcar = true
        first = true
        notifyDataSetChanged()
    }

    /**
     * View holder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var carimage: ImageView
        val car_min: TextView
        val car_amount: TextView
        val car_name: TextView
        val image_pool: TextView
        val lltView: LinearLayout

        init {
            car_name = view.findViewById<View>(R.id.car_name) as TextView
            car_min = view.findViewById<View>(R.id.car_min) as TextView
            car_amount = view.findViewById<View>(R.id.car_amount) as TextView
            image_pool = view.findViewById<View>(R.id.image_pool) as TextView
            carimage = view.findViewById<View>(R.id.car_image) as ImageView
            lltView = view.findViewById<View>(R.id.llt_view) as LinearLayout
        }
    }

}
