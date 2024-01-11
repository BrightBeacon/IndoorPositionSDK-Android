package com.brtbeacon.map3d.demo.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.brtbeacon.map3d.demo.R;

public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MESSAGE = "arg_message";

    private String mTitle;
    private String mMessage;
    private DialogFragmentListener listener;

    public ProgressDialogFragment() {

    }

    public static ProgressDialogFragment newInstance(String title, String message) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mMessage = getArguments().getString(ARG_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_progress_dialog, container, false);
        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        TextView tvMessage = rootView.findViewById(R.id.tv_message);
        tvTitle.setText(mTitle);
        tvMessage.setText(mMessage);
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogFragmentListener) {
            listener = (DialogFragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (listener != null) {
            listener.onDialogCancel(this);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDialogDismiss(this);
        }
    }
}
