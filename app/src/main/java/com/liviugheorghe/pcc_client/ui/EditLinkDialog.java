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

public class EditLinkDialog extends DialogFragment {


    private String initialLink;
    private String currentLink;
    EditText input;


    public String getUpdatedLink() {
        return currentLink;
    }

    public void setCurrentLink(String currentLink) {
        this.currentLink = currentLink;
    }

    public EditLinkDialog(String initialLink) {
        this.initialLink = initialLink;
        currentLink = initialLink;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        input = createEditLinkTextInput(initialLink);
        builder.setTitle("Edit link")
                .setPositiveButton("OK", (dialog,which) -> {
                    currentLink = input.getText().toString();
                })
                .setNegativeButton("Cancel", (dialog,which) ->{

                });
        return builder.create();
    }


    private EditText createEditLinkTextInput(String text) {
        EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setSingleLine();
        input.setText(text);
        return input;
    }
}