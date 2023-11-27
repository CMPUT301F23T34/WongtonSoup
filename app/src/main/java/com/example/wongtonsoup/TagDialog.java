package com.example.wongtonsoup;

<<<<<<< Updated upstream
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
=======
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

public class TagDialog extends DialogFragment {
    private TagList tags;
    private ChipGroup chipGroup;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        public void onClick(View view) {
            EditText tagInput = findViewById(R.id.tag_input);

        }
    Button addTagButton = findViewById(R.id.add_tag_button);


}
>>>>>>> Stashed changes
