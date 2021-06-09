package com.thesis.distanceguard.presentation.main.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.myapp.Constants
import com.thesis.distanceguard.presentation.base.BaseActivity
import com.thesis.distanceguard.presentation.main.fragment.MainFragment
import com.thesis.distanceguard.presentation.onboarding.OnboardingContainerFragment
import com.thesis.distanceguard.service.DistanceGuardService
import com.thesis.distanceguard.service.Restarter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {
    private lateinit var mainActivityViewModel: MainActivityViewModel

    private lateinit var serviceIntent: Intent
    var distanceGuardService: DistanceGuardService? = null
    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setupDistanceGuardService()

        setupViewModel()
        setupViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (supportFragmentManager.backStackEntryCount > 0) {
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun setToolbarTitle(title:String){
        tv_toolbar_title.text = title
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidInjection.inject(this)
        mainActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
    }

    private fun setupViews(){
        Timber.d("setupViews")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        moveToDashboard()


    }

    private fun moveToDashboard() {
        val sharedPrefs = applicationContext?.getSharedPreferences(
            Constants.FILE_NAME_PREFERENCE, Context.MODE_PRIVATE
        )

        if (sharedPrefs == null || !sharedPrefs.getBoolean(Constants.IS_ONBOARD_COMPLETED_KEY, false)) {
            appBarLayout.visibility = View.GONE
            replaceFragmentWithoutAddToBackStack(OnboardingContainerFragment(), OnboardingContainerFragment.TAG, R.id.container_main)
        } else {
            appBarLayout.visibility = View.VISIBLE
            replaceFragmentWithoutAddToBackStack(MainFragment(), MainFragment.TAG, R.id.container_main)
        }
    }


    override fun onDestroy() {
        if (isServiceRunning(distanceGuardService!!::class.java)) {
            stopService(serviceIntent)
            val broadcastIntent = Intent()
            broadcastIntent.action = "restartService"
            broadcastIntent.setClass(this, Restarter::class.java)
            this.sendBroadcast(broadcastIntent)
        }
        super.onDestroy()

    }


    private fun setupDistanceGuardService() {
        distanceGuardService = DistanceGuardService()
        serviceIntent = Intent(this, distanceGuardService!!::class.java)
    }

     fun triggerDistanceGuardService() {
        Timber.d("triggerDistanceGuardService")
         if (!isServiceRunning(distanceGuardService!!::class.java)) {
             startService(serviceIntent)
         } else {
             stopService(serviceIntent)
         }
    }

    fun stopService(){
        if(isServiceRunning(distanceGuardService!!::class.java)){
            stopService(serviceIntent)
        }
    }

    fun isServiceRunning(serviceClass: Class<*>): Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for(service in manager.getRunningServices(Int.MAX_VALUE)){
            if(serviceClass.name == service.service.className){
                Timber.d("Service status: Running")
                return true
            }
        }
        Timber.d("Service status: Not running")
        return false
    }

}