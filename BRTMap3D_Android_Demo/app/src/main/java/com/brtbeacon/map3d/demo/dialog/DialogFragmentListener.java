package com.brtbeacon.map3d.demo.dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public interface DialogFragmentListener {
    void onDialogCancel(DialogFragment fragment);
    void onDialogDismiss(DialogFragment fragment);
    void onDialogResult(DialogFragment fragment, Bundle result);
}
