package com.thesis.distanceguard.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import cn.pedant.SweetAlert.SweetAlertDialog
import com.thesis.distanceguard.factory.ViewModelFactory
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AndroidDialogUtil
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment<VB: ViewDataBinding>: Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected abstract fun getResLayoutId(): Int
    protected abstract fun onMyViewCreated(view: View)

    lateinit var binding : VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        setupViewDataBinding(inflater,container)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onMyViewCreated(view)
    }

    private fun setupViewDataBinding(inflater: LayoutInflater, container: ViewGroup?){
        binding = DataBindingUtil.inflate(inflater, getResLayoutId(), container, false)
        binding.lifecycleOwner = this
    }

    fun setupToolbarTitle(title: String) {
        val mainActivity = activity as MainActivity
        mainActivity.setToolbarTitle(title)
    }


    fun showProgressDialog(message: String) {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().showLoadingDialog(activity, message)

        }
    }

    fun showWarningDialogWithConfirm(message: String, confirmListener: SweetAlertDialog.OnSweetClickListener
    ) {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().showWarningDialogWithConfirm(activity, message,confirmListener)

        }
    }

    fun hideDialog() {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().hideDialog()

        }
    }

    fun showToastMessage(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()

        }
    }

}