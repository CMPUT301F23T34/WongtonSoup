package com.example.wongtonsoup;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class TagDialog extends Dialog {

    private Context context;
    private TagList existingTags;
    private ChipGroup chipGroup;
    private EditText tagInput;

    private ArrayList<String> selectedTags = new ArrayList<>();

    public TagDialog(Context context, TagList existingTags) {
        super(context);
        this.context = context;
        this.existingTags = existingTags;
        this.selectedTags = new ArrayList<>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_dialog_layout);

        tagInput = findViewById(R.id.tag_input);
        chipGroup = findViewById(R.id.content_chip_group);

        // Add existing tags to ChipGroup
        for (Tag tag : existingTags) {
            addChip(tag.getName());
        }

        Button addTagButton = findViewById(R.id.add_tag_dialog_button);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTag = tagInput.getText().toString().trim();
                if (!newTag.isEmpty()) {
                    // Check if the tag already exists
                    if (existingTags.find(newTag)==-1) {
                        addChip(newTag);
                        existingTags.addTag(newTag);
                        tagInput.getText().clear();
                    } else {
                        // Handle tag already
                        tagInput.setError("Tag already exists");
                    }
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancel_tag_dialog_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void addChip(final String tag) {
        final Chip chip = new Chip(context);
        chip.setText(tag);
        chip.setCloseIconVisible(true);

        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Add or remove tag from selectedTags
                if (isChecked) {
                    selectedTags.add(tag);
                } else {
                    selectedTags.remove(tag);
                }
            }
        });
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                existingTags.removeTag(tag);
            }
        });

        chipGroup.addView(chip);
    }
    /**
     * get selected tags
     * @return selected tags
     */
    public ArrayList<String> getSelectedTags() {
        return selectedTags;

}
}
