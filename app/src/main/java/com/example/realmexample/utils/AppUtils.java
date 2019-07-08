package com.example.realmexample.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmexample.R;

public class AppUtils {
    private static AppUtils appUtilityInstance;
    public static AppUtils getInstance() {
        if (appUtilityInstance == null) {
            appUtilityInstance = new AppUtils();
        }
        return appUtilityInstance;
    }

    public void openAnimatedDialog(Dialog dialog, AppCompatActivity mContext, byte mFrom) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(mFrom==1)

            dialog.setContentView(R.layout.dialog_add_update);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = null;
        if (window != null) {
            wlp = window.getAttributes();
        }
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.dimAmount = 0.1f;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (wlp != null) {
            wlp.gravity = Gravity.CENTER;
        }

        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
