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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class for the current list of valid tags.
 * @author rlann
 * @version 1.0
 * @since 11/01/2023
 */
public class ViewItemActivity extends AppCompatActivity {

    private static final int ADD_EDIT_REQUEST_CODE = 1;

    private String dateText;
    private String descriptionText;
    private String makeText;
    private String modelText;
    private String priceText;
    private String commentText;
    private String serialText;
    private String displayImage;
    private TagList tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String ItemID = intent.getStringExtra("ID");

        @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        tags = new TagList();

        db.collection("items")
                .whereEqualTo("owner", device_id)
                .whereEqualTo("id", ItemID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String dateText = document.getString("purchaseDate");
                                String descriptionText = document.getString("description");
                                String makeText = document.getString("make");
                                String modelText = document.getString("model");
                                @SuppressLint("DefaultLocale") String priceText = String.format("%.2f", Objects.requireNonNull(document.getDouble("value")));
                                String commentText = document.getString("comment");
                                String serialText = document.getString("serialNumber");
                                String displayImage = document.getString("displayImage");

                                // tags are stored kinda weird, here's how we access
                                Map<String, Object> taglist_map = (Map<String, Object>) document.get("tags");
                                ArrayList list_of_tags = (ArrayList) taglist_map.get("tags");

                                for (int i = 0 ; i < list_of_tags.size() ; i++){
                                    HashMap<String, String> tag = (HashMap<String, String>) list_of_tags.get(i);
                                    String name = tag.get("name");
                                    String tag_id = tag.get("uuid");
                                    String tag_owner = tag.get("owner");

                                    Tag new_tag = new Tag(name);
                                    new_tag.setOwner(tag_owner);
                                    new_tag.setUuid(tag_id);
                                    tags.addTag(new_tag);
                                }

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
                        // Display tags
                        LinearLayoutManager layoutManagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        RecyclerView recyclerViewAdd = findViewById(R.id.recyclerViewViewItem);
                        recyclerViewAdd.setLayoutManager(layoutManagerItem);
                        TagListAdapter tagAdapter = new TagListAdapter(this, tags);
                        recyclerViewAdd.setAdapter(tagAdapter);
                    }
                });

        // Go to edit
        Button edit = findViewById(R.id.edit_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewItemActivity", "Edit button clicked");
                Intent newIntent = new Intent(ViewItemActivity.this, AddEditActivity.class);
                newIntent.putExtra("Description" , getIntent().getStringExtra("Description"));

                newIntent.putExtra("Description", getIntent().getStringExtra("Description"));
                newIntent.putExtra("Make", getIntent().getStringExtra("Make"));
                newIntent.putExtra("Model", getIntent().getStringExtra("Model"));
                newIntent.putExtra("Comment", getIntent().getStringExtra("Comment"));
                newIntent.putExtra("Date", getIntent().getStringExtra("Date"));
                newIntent.putExtra("Price", getIntent().getStringExtra("Price"));
                newIntent.putExtra("Serial", getIntent().getStringExtra("Serial"));
                String id = getIntent().getStringExtra("ID");
                newIntent.putExtra("ID", id);

//                //Log all of the above for debug purposes
                Log.d("ViewItemActivity", "Description: " + getIntent().getStringExtra("Description"));
                Log.d("ViewItemActivity", "Make: " + getIntent().getStringExtra("Make"));
                Log.d("ViewItemActivity", "Model: " + getIntent().getStringExtra("Model"));
                Log.d("ViewItemActivity", "Comment: " + getIntent().getStringExtra("Comment"));
                Log.d("ViewItemActivity", "Date: " + getIntent().getStringExtra("Date"));
                Log.d("ViewItemActivity", "Price: " + getIntent().getStringExtra("Price"));
                Log.d("ViewItemActivity", "Serial: " + getIntent().getStringExtra("Serial"));
                Log.d("ViewItemActivity", "ID: " + getIntent().getStringExtra("ID"));


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
