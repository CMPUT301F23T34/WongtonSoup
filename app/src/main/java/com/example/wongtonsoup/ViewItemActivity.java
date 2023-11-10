package com.example.wongtonsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Class for the current list of valid tags.
 * @author rlann
 * @version 1.0
 * @since 11/01/2023
 */

public class ViewItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();

        TextView description = findViewById(R.id.item_view_description);
        description.setText(intent.getStringExtra("Description"));

        TextView make = findViewById(R.id.view_make);
        make.setText(intent.getStringExtra("Make"));

        TextView model = findViewById(R.id.view_model);
        model.setText(intent.getStringExtra("Model"));

        TextView comment = findViewById(R.id.view_comment);
        comment.setText(intent.getStringExtra("Comment"));

        TextView date = findViewById(R.id.view_date);
        comment.setText(intent.getStringExtra("date"));

        TextView serial = findViewById(R.id.view_serial);
        serial.setText(intent.getStringExtra("Serial"));

        TextView price = findViewById(R.id.item_view_price);
        price.setText(intent.getStringExtra("Price"));

        Button back = findViewById(R.id.view_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}