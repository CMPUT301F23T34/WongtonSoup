package com.example.wongtonsoup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class TagDialog extends DialogFragment {}

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Button addTagButton = getActivity().findViewById(R.id.add_tag_button);
//        addTagButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText tagInput = getView().findViewById(R.id.tag_input);
//                String tagName = tagInput.getText().toString().trim();
