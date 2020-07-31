package com.liviugheorghe.pcc_client.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liviugheorghe.pcc_client.R;


public class FileInformationFragment extends Fragment {

    private EditText nameEditText;
    private TextView sizeTextView;
    private TextView typeTextView;
    private String name;
    private String size;
    private String type;

    FileInformationFragment(String name, String size, String type) {
        this.name = name;
        this.size = size;
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameEditText = getView().findViewById(R.id.file_information_name_edit_text);
        sizeTextView = getView().findViewById(R.id.file_information_size_text);
        typeTextView = getView().findViewById(R.id.file_information_type_text);
        nameEditText.setText(name);
        sizeTextView.setText(String.format("%s : %s", getResources().getString(R.string.selected_file_size_text), size));
        typeTextView.setText(String.format("%s : %s", getResources().getString(R.string.selected_file_type_text), type));
    }
}