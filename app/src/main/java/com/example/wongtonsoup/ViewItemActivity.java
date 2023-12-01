package com.example.wongtonsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final int ADD_EDIT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Intent intent = getIntent();

        // Display details
        TextView description = findViewById(R.id.item_view_description);
        String descriptionText = intent.getStringExtra("Description");
        description.setText(descriptionText);

        TextView make = findViewById(R.id.view_make);
        String makeText = intent.getStringExtra("Make");
        make.setText(makeText);

        TextView model = findViewById(R.id.view_model);
        String modelText = intent.getStringExtra("Model");
        model.setText(modelText);

        TextView comment = findViewById(R.id.view_comment);
        String commentText = intent.getStringExtra("Comment");
        comment.setText(commentText);

        TextView date = findViewById(R.id.view_date);
        String dateText = intent.getStringExtra("Date");
        date.setText(dateText);

        TextView serial = findViewById(R.id.view_serial);
        String serialText = intent.getStringExtra("Serial");
        serial.setText(serialText);

        TextView price = findViewById(R.id.item_view_price);
        String priceText = intent.getStringExtra("Price");
        price.setText(priceText);

        // Go to edit
        Button edit = findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(ViewItemActivity.this, AddEditActivity.class);
                newIntent.putExtra("Description", descriptionText);
                newIntent.putExtra("Make", makeText);
                newIntent.putExtra("Model", modelText);
                newIntent.putExtra("Comment", commentText);
                newIntent.putExtra("Date", dateText);
                newIntent.putExtra("Price", priceText);
                newIntent.putExtra("Serial", serialText);
                String id = intent.getStringExtra("ID");
                Log.d("ViewItemActivity", "Passing Item ID: " + id); // Log to confirm ID is received
                newIntent.putExtra("ID", id);
                startActivityForResult(newIntent, ADD_EDIT_REQUEST_CODE);
            }
        });

        // Go back to main
        Button back = findViewById(R.id.view_back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void finishAndPassItem(Item item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultItem", item);
        resultIntent.putExtra("itemID", item.getID()); // Pass the ID back
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");
                String itemID = data.getStringExtra("itemID"); // Receive the ID back
                Log.d("ViewItemActivity RECEIVE", "Received Item ID: " + itemID); // Log to confirm ID is received
                resultItem.setID(itemID); // Set the ID to the resultItem
                finishAndPassItem(resultItem);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Check if the request code matches and the result is OK
//            if (data != null && data.hasExtra("resultItem")) {
//                Item resultItem = (Item) data.getSerializableExtra("resultItem");
//                finishAndPassItem(resultItem);
//            }
//        }
//    }
}