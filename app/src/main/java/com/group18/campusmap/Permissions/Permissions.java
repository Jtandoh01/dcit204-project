package com.group18.campusmap.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.group18.campusmap.Constant.Constants;

public class Permissions {
    public boolean isStorageOk(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestStoragePermission(Activity activIty){
        ActivityCompat.requestPermissions(activIty, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},Constants.STORAGE_REQUEST_CODE);
    }



}
