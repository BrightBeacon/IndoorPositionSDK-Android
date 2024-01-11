package com.brtbeacon.map3d.demo.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.brtbeacon.map3d.demo.dialog.DialogFragmentListener;
import com.brtbeacon.map3d.demo.dialog.ProgressDialogFragment;

public abstract class BaseActivity extends AppCompatActivity implements DialogFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Toast mToast;
    public void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showProgressDialog(String title, String message, String tag) {
        closeProgressDialog(tag);
        ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(title, message);
        fragment.show(getSupportFragmentManager(), tag);
    }

    public void closeProgressDialog(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment!=null && fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                dialogFragment.dismiss();
            }
        }
    }

    @Override
    public void onDialogCancel(DialogFragment fragment) {

    }

    @Override
    public void onDialogDismiss(DialogFragment fragment) {

    }

    @Override
    public void onDialogResult(DialogFragment fragment, Bundle result) {

    }
}
