package com.example.wongtonsoup;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.core.content.ContextCompat;

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
                        Tag tag = new Tag(newTag);
                        UUID id = UUID.randomUUID();

                        @SuppressLint("HardwareIds") String owner = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        tag.setUuid(id.toString());
                        tag.setOwner(owner);

                        addChip(newTag);
                        existingTags.addTag(tag);
                        existingTags.addTagDB(tag);
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
                        if (existingTags.find(chip.getText().toString()) == -1){
                            // throw an error if they somehow selected a chip that we don't have in DB yet
                            assert(0 == 1);
                        }
                        else {
                            Tag tag = existingTags.getTags().get(existingTags.find(chip.getText().toString()));
                            selectedTags.addTag(tag);
                        }
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

    private void addChip(final String tag, boolean selected) {
        final Chip chip = new Chip(context);
        chip.setText(tag);
        chip.setCloseIconVisible(true);
        chip.setCheckable(true);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                existingTags.removeTag(tag);
            }
        });
        chip.setChecked(selected);

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
