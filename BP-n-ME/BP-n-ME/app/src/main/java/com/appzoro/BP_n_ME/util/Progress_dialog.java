package com.appzoro.BP_n_ME.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.appzoro.BP_n_ME.R;

/**
 * Created by Appzoro_ 5 on 7/27/2017.
 */

public class Progress_dialog {
    public static Progress_dialog s_m_oCShowProgress;
    public static Context m_Context;
    public Dialog m_Dialog;

    public Progress_dialog(Context m_Context) {
        Progress_dialog.m_Context = m_Context;
    }

    public static Progress_dialog getInstance() {
        if (s_m_oCShowProgress == null) {
            s_m_oCShowProgress = new Progress_dialog(m_Context);
        }
        return s_m_oCShowProgress;
    }

    public void showProgress(Context m_Context) {
        m_Dialog = new Dialog(m_Context);
        m_Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        m_Dialog.setContentView(R.layout.dialog_progress_bar);
        m_Dialog.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
        m_Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_Dialog.setCancelable(false);
        m_Dialog.setCanceledOnTouchOutside(false);
        m_Dialog.show();
    }

    public void hideProgress() {
        if (m_Dialog != null) {
            m_Dialog.dismiss();
            m_Dialog = null;
        }
    }
}
