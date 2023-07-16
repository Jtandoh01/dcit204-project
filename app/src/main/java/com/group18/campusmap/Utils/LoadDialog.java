package com.group18.campusmap.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.group18.campusmap.R;
import com.group18.campusmap.databinding.DialogLayoutBinding;

public class LoadDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.loadingDialogStyle);
        DialogLayoutBinding binding = DialogLayoutBinding.inflate(LayoutInflater.from(activity), null, false);
        builder.setView(binding.getRoot());
        builder.setCancelable(false);
        alertDialog = builder.create();
        binding.rotateLoading.progressiveStart();
        alertDialog.show();
    }

    public void stopLoading() {
        alertDialog.dismiss();
    }
}
