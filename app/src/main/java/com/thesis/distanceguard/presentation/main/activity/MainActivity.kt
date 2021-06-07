package com.thesis.distanceguard.presentation.main.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
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


    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
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
//        val broadcastIntent = Intent()
//        broadcastIntent.action = "restartService"
//        broadcastIntent.setClass(this, Restarter::class.java)
//        this.sendBroadcast(broadcastIntent)
        super.onDestroy()

    }

    lateinit var serviceIntent : Intent
    private lateinit var  distanceGuardService: DistanceGuardService

     fun startDistanceGuardService(){
        Timber.d("startDistanceGuardService")
        distanceGuardService = DistanceGuardService()
        serviceIntent = Intent(this,distanceGuardService::class.java)
        if(!isServiceRunning(distanceGuardService::class.java)){
            startService(serviceIntent)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean{
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