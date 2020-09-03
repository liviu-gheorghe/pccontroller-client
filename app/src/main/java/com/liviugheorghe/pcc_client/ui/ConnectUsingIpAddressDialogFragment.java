package com.liviugheorghe.pcc_client.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.liviugheorghe.pcc_client.R;

public class ConnectUsingIpAddressDialogFragment extends DialogFragment {

    public interface DialogListener {
        void onDialogPositiveClick(ConnectUsingIpAddressDialogFragment dialog);
        void onDialogNegativeClick(ConnectUsingIpAddressDialogFragment dialog);
    }

    DialogListener listener;
    EditText input;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        input = createIpAddressTextInput();
        builder.setTitle(R.string.connect_using_ip_dialog_title)
                .setView(input)
                .setPositiveButton(R.string.connect_using_ip_dialog_ok, (dialog, which) -> listener.onDialogPositiveClick(ConnectUsingIpAddressDialogFragment.this))
                .setNegativeButton(R.string.connect_using_ip_dialog_cancel, (dialog, which) -> listener.onDialogNegativeClick(ConnectUsingIpAddressDialogFragment.this));
        return builder.create();
    }

    public String getInputText() {
        return input.getText().toString();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (DialogListener) context;
        } catch(ClassCastException e) {
            e.printStackTrace();
        }
    }

    private EditText createIpAddressTextInput() {
        EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setSingleLine();
        return input;
    }
}