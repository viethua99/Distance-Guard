package com.thesis.distanceguard.util;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AndroidDialogUtil {

    private AndroidDialogUtil() {

    }

    private static AndroidDialogUtil INSTANCE;
    private SweetAlertDialog sweetAlertDialog;

    public static AndroidDialogUtil getInstance() {
        AndroidDialogUtil localInstance;
        if (INSTANCE == null) {
            synchronized (AndroidDialogUtil.class) {
                if (INSTANCE == null) {
                    localInstance = new AndroidDialogUtil();
                    INSTANCE = localInstance;
                }
            }
        }
        return INSTANCE;
    }

    public void showWarningDialogWithConfirm(Context context, String message, SweetAlertDialog.OnSweetClickListener confirmListener) {
        hideDialog();
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog
                .setTitleText(message)
                .setConfirmText("Leave")
                .setConfirmClickListener(confirmListener)
                .show();
    }


    public void showLoadingDialog(Context context, String message) {
        hideDialog();
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog
                .setTitleText(message)
                .setCancelable(false);
        sweetAlertDialog.show();
    }

    public void hideDialog() {
        if (sweetAlertDialog != null) {
            sweetAlertDialog.dismiss();
        }
        sweetAlertDialog = null;
    }

}