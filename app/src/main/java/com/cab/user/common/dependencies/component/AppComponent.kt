package com.cab.user.common.dependencies.component

/**
 * @package com.cloneappsolutions.cabmeuser
 * @subpackage dependencies.component
 * @category AppComponent
 * @author SMR IT Solutions
 *
 */


import com.cab.user.taxi.ScheduleRideDetailActivity
import com.cab.user.taxi.adapters.CarDetailsListAdapter

import com.cab.user.taxi.adapters.PastTripsPaginationAdapter
import com.cab.user.taxi.adapters.PriceRecycleAdapter
import com.cab.user.taxi.adapters.UpcomingAdapter
import com.cab.user.taxi.adapters.UpcomingTripsPaginationAdapter
import com.cab.user.common.backgroundtask.ImageCompressAsyncTask
import com.cab.user.common.configs.RunTimePermission
import com.cab.user.common.configs.SessionManager
import com.cab.user.taxi.database.AddFirebaseDatabase
import com.cab.user.common.dependencies.module.AppContainerModule
import com.cab.user.common.dependencies.module.ApplicationModule
import com.cab.user.common.dependencies.module.NetworkModule
import com.cab.user.common.helper.CommonDialog
import com.cab.user.common.drawpolyline.DownloadTask
import com.cab.user.common.pushnotification.MyFirebaseInstanceIDService
import com.cab.user.common.pushnotification.MyFirebaseMessagingService
import com.cab.user.taxi.sendrequest.CancelYourTripActivity
import com.cab.user.taxi.sendrequest.DriverNotAcceptActivity
import com.cab.user.taxi.sendrequest.DriverRatingActivity
import com.cab.user.taxi.sendrequest.PaymentAmountPage
import com.cab.user.taxi.sendrequest.SendingRequestActivity
import com.cab.user.taxi.sidebar.AddHome
import com.cab.user.taxi.sidebar.DriverContactActivity
import com.cab.user.taxi.sidebar.EnRoute
import com.cab.user.taxi.sidebar.FareBreakdown
import com.cab.user.taxi.sidebar.Profile
import com.cab.user.taxi.sidebar.Setting
import com.cab.user.taxi.sidebar.payment.*
import com.cab.user.taxi.sidebar.referral.ShowReferralOptions
import com.cab.user.taxi.sidebar.trips.Past
import com.cab.user.taxi.sidebar.trips.Receipt
import com.cab.user.taxi.sidebar.trips.TripDetails
import com.cab.user.taxi.sidebar.trips.Upcoming
import com.cab.user.taxi.sidebar.trips.YourTrips
import com.cab.user.common.utils.CommonMethods
import com.cab.user.common.utils.RequestCallback
import com.cab.user.common.utils.userchoice.UserChoice
import com.cab.user.common.views.*
import com.cab.user.taxi.views.addCardDetails.AddCardActivity
import com.cab.user.taxi.views.facebookAccountKit.FacebookAccountKitActivity
import com.cab.user.taxi.views.emergency.EmergencyContact
import com.cab.user.taxi.views.emergency.SosActivity
import com.cab.user.taxi.views.firebaseChat.ActivityChat
import com.cab.user.taxi.views.firebaseChat.AdapterFirebaseRecylcerview
import com.cab.user.taxi.views.firebaseChat.FirebaseChatHandler
import com.cab.user.taxi.views.main.MainActivity
import com.cab.user.taxi.views.main.filter.FeaturesInVehicleAdapter
import com.cab.user.taxi.views.peakPricing.PeakPricing
import com.cab.user.taxi.views.search.PlaceSearchActivity
import com.cab.user.taxi.views.signinsignup.*
import com.cab.user.taxi.views.splash.SplashActivity
import com.cab.user.taxi.views.voip.CallProcessingActivity
import com.cab.user.taxi.views.voip.CabmeSinchService

import javax.inject.Singleton

import dagger.Component


/*****************************************************************
 * App Component
 */
@Singleton
@Component(modules = [NetworkModule::class, ApplicationModule::class, AppContainerModule::class])
interface AppComponent {

    fun inject(splashActivity: SplashActivity)

    fun inject(mainActivity: MainActivity)

    fun inject(scheduleRideDetailActivity: ScheduleRideDetailActivity)

    fun inject(sendingRequestActivity: SendingRequestActivity)

    fun inject(driverNotAcceptActivity: DriverNotAcceptActivity)

    fun inject(mainActivity: PlaceSearchActivity)

    fun inject(signin_signup_activity: SigninSignupActivity)

    fun inject(ssResetPassword: SSResetPassword)

    fun inject(ssSocialDetailsActivity: SSRegisterActivity)

    fun inject(driverContactActivity: DriverContactActivity)

    fun inject(addHome: AddHome)

    fun inject(paymentPage: PaymentPage)

    fun inject(paymentAmountPage: PaymentAmountPage)

    fun inject(fareBreakdown: FareBreakdown)

    fun inject(addWalletActivity: AddWalletActivity)

    fun inject(promoAmountActivity: PromoAmountActivity)

    fun inject(yourTrips: YourTrips)

    fun inject(tripDetails: TripDetails)

    fun inject(enRoute: EnRoute)

    fun inject(sos_activity: SosActivity)

    fun inject(driverRatingActivity: DriverRatingActivity)

    fun inject(cancelYourTripActivity: CancelYourTripActivity)

    fun inject(commonDialog: CommonDialog)

    fun inject(setting: Setting)

    fun inject(profile: Profile)


    fun inject(emergencyContact: EmergencyContact)

    fun inject(activityChat: ActivityChat)

    fun inject(facebookAccountKitActivity: FacebookAccountKitActivity)

    fun inject(loginActivity: SSLoginActivity)

    fun inject(peakPricing: PeakPricing)

    fun inject(showReferralOptions: ShowReferralOptions)

    fun inject(callProcessingActivity: CallProcessingActivity)

    fun inject(CabmeSinchService: CabmeSinchService)

    // Fragments
    fun inject(past: Past)

    fun inject(upcoming: Upcoming)

    fun inject(receipt: Receipt)

    // Utilities
    fun inject(runTimePermission: RunTimePermission)

    fun inject(sessionManager: SessionManager)

    fun inject(commonMethods: CommonMethods)

    fun inject(requestCallback: RequestCallback)

    // Adapters

    fun inject(upcomingAdapter: UpcomingAdapter)


    fun inject(promoAmountAdapter: PromoAmountAdapter)

    fun inject(carDetailsListAdapter: CarDetailsListAdapter)

    fun inject(myFirebaseMessagingService: MyFirebaseMessagingService)

    fun inject(myFirebaseInstanceIDService: MyFirebaseInstanceIDService)

    fun inject(firebaseChatHandler: FirebaseChatHandler)

    fun inject(adapterFirebaseRecylcerview: AdapterFirebaseRecylcerview)


    // AsyncTask
    fun inject(imageCompressAsyncTask: ImageCompressAsyncTask)

    fun inject(downloadTask: DownloadTask)

    fun inject(firebaseDatabase: AddFirebaseDatabase)


    fun inject(priceRecycleAdapter: PriceRecycleAdapter)

    fun inject(pastTripsPaginationAdapter: PastTripsPaginationAdapter)

    fun inject(upcomingTripsPaginationAdapter: UpcomingTripsPaginationAdapter)
     fun inject(addCardActivity: AddCardActivity)
    fun inject(paymentMethodAdapter: PaymentMethodAdapter)
    fun inject(featuresInVehicleAdapter: FeaturesInVehicleAdapter)

    fun inject(supportActivityCommon: SupportActivityCommon)

    fun inject(supportAdapter: SupportAdapter)

    fun inject(paymentWebViewActivity: PaymentWebViewActivity)

    fun inject(commonActivity: CommonActivity)

    fun inject(userChoice: UserChoice)

}
