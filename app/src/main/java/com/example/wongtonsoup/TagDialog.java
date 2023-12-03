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
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TagDialog extends Dialog {

    private Context context;
    private TagList existingTags;
    private ChipGroup chipGroup;
    private EditText tagInput;
    private TagList selectedTags;
    private Item current_item;


    public TagDialog(Context context, TagList existingTags, TagList selectedTags, Item current_item) {
        super(context);
        this.context = context;
        this.existingTags = existingTags;
        this.selectedTags = selectedTags;
        this.current_item = current_item;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_dialog_layout);

        tagInput = findViewById(R.id.tag_input);
        chipGroup = findViewById(R.id.tag_dialog_chip_group);

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

        Button submitButton = findViewById(R.id.submit_tags_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add selected tags to selectedTags
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedTags.addTagDB(chip.getText().toString());
                    }
                }
                current_item.setTags(selectedTags);

                Toast.makeText(context, "tags: " + existingTags.toString(), Toast.LENGTH_SHORT).show();
                dismiss();
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
        chip.setCheckable(true);

        chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Tag new_tag = new Tag(chip.getText().toString());
                    UUID uuid = java.util.UUID.randomUUID();
                    new_tag.setUuid(uuid);
                    selectedTags.addTag(new_tag);

                } else {
                    selectedTags.removeTag(chip.getText().toString());
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
    public TagList getSelectedTags() {
        return selectedTags;
    }

}
