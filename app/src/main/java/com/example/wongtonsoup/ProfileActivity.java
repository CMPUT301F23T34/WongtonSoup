package com.example.wongtonsoup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ProfileActivity extends AppCompatActivity {
    private int clickedPfp = 0;
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

        ImageView userPfp = findViewById(R.id.userPfp); // Get the ImageView for the profile pic
        KonfettiView konfettiView = findViewById(R.id.viewKonfetti); // Get the KonfettiView for the confetti

        userPfp.setOnClickListener(new View.OnClickListener() {  // Is this an Easter Egg??? Wow cool 0_0
            @Override
            public void onClick(View v) {
                clickedPfp++;
                if (clickedPfp >= 5) {  // Surely if you click the pfp >= 5 times nothing will happen ;)
                    // Trigger confetti
                    konfettiView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(2000L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(12, 5))
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(300, 5000L);

                    clickedPfp = 0; // Reset the counter
                }
            }
        });
    }
}