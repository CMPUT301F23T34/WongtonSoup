package com.example.wongtonsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AddEditActivity extends AppCompatActivity {
    // Assume we have item with associated tags lise
    private Item item;
    //List<Tag> tags = item.getTags();

    // Find the ChipGroup view in layout
    ChipGroup chipGroup = findViewById(R.id.add_edit_chip_group);
//    //Iterate through tags and add a chip for each one
//    for (Tag tag : tags) {
//        Chip chip = new Chip(this);
//        chip.setText(tag.getName());
//        chip.setCloseIconVisible(true);
//    //Add a click listener to handle interactions with the tag
//    chip.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onLongClick(View view) {
//            // Handle tag click, e.g., remove the tag from the item
//        }
//    });
//
//    // Add the Chip to the ChipGroup
//    chipGroup.addView(chip);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Button back = findViewById(R.id.add_edit_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}