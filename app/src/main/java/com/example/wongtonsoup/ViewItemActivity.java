package com.example.wongtonsoup;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.provider.Settings;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * Class for the current list of valid tags.
 * @author rlann
 * @version 1.0
 * @since 11/01/2023
 */
public class ViewItemActivity extends AppCompatActivity {

    private static final int ADD_EDIT_REQUEST_CODE = 1;
    String dateText;
    String descriptionText;
    String makeText;
    String modelText;
    String priceText;
    String commentText;
    String serialText;
    String displayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String ItemID = intent.getStringExtra("ID");

        @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        db.collection("items")
                .whereEqualTo("owner", device_id)
                .whereEqualTo("id", ItemID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                dateText = document.getString("purchaseDate");
                                descriptionText = document.getString("description");
                                makeText = document.getString("make");
                                modelText = document.getString("model");
                                priceText = String.valueOf(Objects.requireNonNull(document.getDouble("value")).floatValue());
                                commentText = document.getString("comment");
                                serialText = document.getString("serial");
                                displayImage = document.getString("displayImage");

                                List<?> rawImageUrls = (List<?>) document.get("imagePathsCopy");


                                // Display details
                                TextView description = findViewById(R.id.item_view_description);
                                description.setText(descriptionText);

                                TextView make = findViewById(R.id.view_make);
                                make.setText(makeText);

                                TextView model = findViewById(R.id.view_model);
                                model.setText(modelText);

                                TextView comment = findViewById(R.id.view_comment);
                                comment.setText(commentText);

                                TextView date = findViewById(R.id.view_date);
                                date.setText(dateText);

                                TextView serial = findViewById(R.id.view_serial);
                                serial.setText(serialText);

                                TextView price = findViewById(R.id.item_view_price);
                                price.setText(priceText);


                                if (rawImageUrls != null) {
                                    int count = 0;
                                    for (Object rawImageUrl : rawImageUrls) {
                                        if (rawImageUrl instanceof String) {
                                            updateImageView((String) rawImageUrl,count);
                                            count++;
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("MainActivity", "Error parsing item: " + e.getMessage());
                            }
                        }
                    }
                });


        // Display tags
        TagList tags = new TagList(); //Replace with db tags
        LinearLayoutManager layoutManagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewAdd = findViewById(R.id.recyclerViewViewItem);
        recyclerViewAdd.setLayoutManager(layoutManagerItem);
        TagListAdapter tagAdapter = new TagListAdapter(this, tags);
        recyclerViewAdd.setAdapter(tagAdapter);

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
        back.setOnClickListener(v -> finish());
    }
    private void finishAndPassItem(Item item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultItem", item);
        resultIntent.putExtra("itemID", item.getID()); // Pass the ID back
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateImageView(String Url, int position) {
        // Update the corresponding ImageView based on the position
        switch (position) {
            case 0:
                ImageView imageView_1 = findViewById(R.id.view_image1);
                Picasso.get().load(Url).into(imageView_1);
                break;
            case 1:
                ImageView imageView_2 = findViewById(R.id.view_image2);
                Picasso.get().load(Url).into(imageView_2);
                break;
            case 2:
                ImageView imageView_3 = findViewById(R.id.view_image3);
                Picasso.get().load(Url).into(imageView_3);
                break;
        }
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
