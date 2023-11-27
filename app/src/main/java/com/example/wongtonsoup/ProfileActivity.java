package com.example.wongtonsoup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_main); // Set the content view to your profile layout

        String UID = getIntent().getStringExtra("USER_ID"); // Get the UID from the intent
        String joinDate = getIntent().getStringExtra("JOIN_DATE"); // Get the joined date from the intent
        String name = getIntent().getStringExtra("USER_NAME"); // Get the joined date from the intent

        TextView uidTextView = findViewById(R.id.UID); // Get the TextView for the UID
        uidTextView.setText(UID); // Set the text of the TextView to the UID

        TextView joinDateTextView = findViewById(R.id.user_since_date); // Get the TextView for the joined date
        joinDateTextView.setText(String.format("User Since: %s", joinDate)); // Set the text of the TextView to the joined date

        TextView nameTextView = findViewById(R.id.user_name); // Get the TextView for the name
        nameTextView.setText(String.format(name)); // Set the text of the TextView to the name

        // TODO: logic for editing username here

        Button backButton = findViewById(R.id.view_back_button); // Get the back button
        backButton.setOnClickListener(v -> finish()); // Set the onClickListener to finish the activity
    }
}