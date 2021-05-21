package com.thesis.distanceguard.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.thesis.distanceguard.factory.ViewModelFactory
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AndroidDialogUtil
import timber.log.Timber
import javax.inject.Inject

abstract class BaseFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    protected abstract fun getResLayoutId(): Int
    protected abstract fun onMyViewCreated(view: View)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        val view = inflater.inflate(getResLayoutId(), container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onMyViewCreated(view)
    }

    fun setupToolbarTitle(title: String) {
        val mainActivity = activity as MainActivity
        mainActivity.setToolbarTitle(title)
    }

    fun replaceFragment(fragment: Fragment, tag: String, containerId: Int) {
        Timber.d("replaceFragment: name=${fragment.javaClass.name}")
        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
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

    fun showWarningDialog(message: String) {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().showWarningDialog(activity, message)

        }
    }

    fun showSuccessDialog(message: String) {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().showSuccessDialog(activity, message)

        }
    }


    fun showFailedDialog(message: String) {
        activity?.runOnUiThread {
            AndroidDialogUtil.getInstance().showFailureDialog(activity, message)

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