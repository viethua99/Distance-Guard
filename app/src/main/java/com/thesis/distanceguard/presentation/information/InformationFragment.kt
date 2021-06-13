package com.thesis.distanceguard.presentation.information

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.thesis.distanceguard.R
import com.thesis.distanceguard.databinding.FragmentInformationBinding
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class InformationFragment : BaseFragment<FragmentInformationBinding>(){

    companion object {
        const val TAG = "InformationFragment"
    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_information
    }

    override fun onMyViewCreated(view: View) {
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.GONE
        mainActivity.window.statusBarColor = ContextCompat.getColor(context!!,R.color.header_red)
        binding.btnCall.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:19009095"))
            startActivity(callIntent)
        }

        binding.btnSms.setOnClickListener {
            val sendMessageIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:19009095"))
            startActivity(sendMessageIntent)
        }
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.VISIBLE
        mainActivity.window.statusBarColor = ContextCompat.getColor(context!!,R.color.gray_background_color)

    }
    override fun onResume() {
        super.onResume()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.GONE
        mainActivity.window.statusBarColor = ContextCompat.getColor(context!!,R.color.header_red)
    }
}