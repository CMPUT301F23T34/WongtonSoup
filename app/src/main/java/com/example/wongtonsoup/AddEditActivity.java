package com.example.wongtonsoup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class AddEditActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 301;
    // Assume we have item with associated tags lise
    // private Item item;
    private EditText expenseDescription;
    private EditText expenseDate;
    private EditText expenseValue;
    private EditText expenseComment;
    private EditText expenseSerialNumber;
    private EditText expenseMake;
    private EditText expenseModel;

    private TagList existing_tags;
    private TagList selected_tags;
    private TagDialog tagDialog;

    private Uri currentPhotoUri;
    private final List<Uri> imageUris = new ArrayList<>();


    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int OPEN_CAMERA_REQUEST = 102;
    private static final int MAX_TOTAL_PHOTOS = 3;
    private int totalPhotoCounter = 0;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private Item current_item = new Item();

    /**
     * Creates an Item object with data from EditText fields and performs actions when the button is enabled.
     */
    private void popuateItemFields() {
        String description = expenseDescription.getText().toString();
        String date = expenseDate.getText().toString();
        String str_value = expenseValue.getText().toString();
        String comment = expenseComment.getText().toString();
        String make = expenseMake.getText().toString();
        String model = expenseModel.getText().toString();
        String serialNumber = expenseSerialNumber.getText().toString();

        // Get device ID
        @SuppressLint("HardwareIds") String owner = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // generate random hex address
        String id = java.util.UUID.randomUUID().toString();

        if (getIntent().hasExtra("ID")) {  // if editing preserve item id
            Log.d("AddEditActivity INSIDE", "createItemFromFields: " + id);
            id = getIntent().getStringExtra("ID");
            Log.d("AddEditActivity INSIDE AFTER", "createItemFromFields: " + id);
        }
        Log.d("AddEditActivity", "createItemFromFields: " + id);
        // convert charge to a float object
        Float value = Float.valueOf(str_value);

        // Create an Item object with the gathered data
        current_item.setID(id);
        current_item.setDescription(description);
        current_item.setPurchaseDate(date);
        current_item.setMake(make);
        current_item.setModel(model);
        current_item.setValue(value);
        current_item.setComment(comment);
        current_item.setSerialNumber(serialNumber);
        current_item.setOwner(owner);
    }

    /**
     * Validates a date string in the format dd_mm_yyyy.
     *
     * @param date The date string to be validated.
     * @return True if the date is valid, false otherwise.
     */
    public boolean isValidDate(String date) {
        // Define a regular expression for the dd_mm_yyyy format
        String datePattern = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$";

        // Check if the entered date matches the pattern
        if (!date.matches(datePattern)) {
            return false;
        }

        // Extract day, month, and year from the entered date
        String[] parts = date.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        // Check if the day is in the valid range (1 to 31) and the month is in the valid range (1 to 12)
        return (day >= 1 && day <= 31) && (month >= 1 && month <= 12);
    }

    /**
     * Validates a value string is a valid currency.
     * @param str_value string to be validated
     * @return true if value is valid, false otherwise.
     */
    private boolean isValidValue(String str_value) {
        // Define expression for any number of int digits and an optional one or two digits after decimal
        String valuePattern ="^\\d+(\\.\\d{1,2})?$";
        return str_value.matches(valuePattern);
    }

    /**
     * Updates the state of the Add/Edit Check Button based on the validity of input fields.
     *
     * @param addEditCheckButton The Button whose state needs to be updated.
     */
    private void updateAddEditCheckButtonState(Button addEditCheckButton) {
        boolean isExpenseDescriptionEmpty = TextUtils.isEmpty(expenseDescription.getText().toString());
        boolean isExpenseDescriptionInvalid = !(expenseDescription.getText().length() <= 15);

        // clear error message if neither error bool is true.
        if (!isExpenseDescriptionEmpty && isExpenseDescriptionInvalid){
            expenseDescription.setError(null); // clear error
        } else if(isExpenseDescriptionInvalid){
            expenseDescription.setError("Name cannot exceed 15 characters");
        } else if (isExpenseDescriptionEmpty){
            expenseDescription.setError("Expense name cannot be empty");
        }

        boolean isExpenseValueInvalid = !isValidValue(expenseValue.getText().toString());
        boolean isExpenseValueEmpty = TextUtils.isEmpty(expenseValue.getText().toString());

        if (!isExpenseValueEmpty && !isExpenseValueInvalid){
            expenseValue.setError(null); // clear error
        }
        else if (isExpenseValueEmpty){
            expenseValue.setError("Expense value cannot be empty");
        }
        else{
            expenseValue.setError("Expense value can only contain two digits after the decimal");
        }

        boolean isExpenseDateEmpty = TextUtils.isEmpty(expenseDate.getText().toString());
        boolean isExpenseDateInvalid = !isValidDate(expenseDate.getText().toString());

        if (!isExpenseDateEmpty && !isExpenseDateInvalid){
            expenseDate.setError(null);
        }
        else if (isExpenseDateEmpty){
            expenseDate.setError("Expense Date cannot be empty");
        }
        else{
            expenseDate.setError("Invalid date format. Please use dd-mm-yyyy");
        }

       boolean isExpenseCommentInvalid = !(expenseComment.getText().length() <= 40);

       if (!isExpenseCommentInvalid){
           expenseComment.setError(null);
       }
       else{
           expenseComment.setError("Comment cannot exceed 40 characters");
       }

       boolean isExpenseMakeEmpty = TextUtils.isEmpty(expenseMake.getText().toString());
       boolean isExpenseMakeInvalid = !(expenseMake.getText().length() <= 15);

       if (!isExpenseMakeEmpty && !isExpenseMakeInvalid){
           expenseMake.setError(null); // Clear the error
       }
       else if (isExpenseMakeEmpty){
           expenseMake.setError("Expense make cannot be empty");
       }
       else{
           expenseMake.setError("Make cannot exceed 15 characters");
       }

       boolean isExpenseModelInvalid = !(expenseModel.getText().length() <= 15);
       boolean isExpenseModelEmpty = TextUtils.isEmpty(expenseModel.getText().toString());

       if (!isExpenseModelEmpty && !isExpenseModelInvalid){
           expenseModel.setError(null);
       }
       else if (isExpenseModelEmpty){
           expenseModel.setError("Expense model cannot be empty");
       }
       else{
           expenseModel.setError("Model cannot exceed 15 characters");
       }

        boolean isButtonEnabled = !isExpenseDescriptionEmpty && !isExpenseValueEmpty && !isExpenseDateEmpty && !isExpenseDateInvalid && !isExpenseDescriptionInvalid && !isExpenseCommentInvalid
                && !isExpenseMakeEmpty && !isExpenseModelEmpty && !isExpenseMakeInvalid && !isExpenseModelInvalid && !isExpenseValueInvalid;

        addEditCheckButton.setEnabled(isButtonEnabled);
    }

    /**
     * Sets up a TextWatcher for an EditText to automatically update the Add/Edit Check Button state.
     *
     * @param editText           The EditText for which the TextWatcher is set up.
     * @param addEditCheckButton The Button whose state needs to be updated.
     */
    private void setupTextWatcher(EditText editText, Button addEditCheckButton) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateAddEditCheckButtonState(addEditCheckButton);
            }
        });
    }

    /**
     * Passes the created Item back to MainActivity and finishes the AddEditActivity.
     * @param item item to pass
     */

    private void finishAndPassItem(Item item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultItem", item);
        resultIntent.putExtra("itemID", item.getID()); // Pass the ID back
        String itemID = item.getID();
        Log.d("ViewItemActivity PASS", "PASS Item ID: " + itemID); // Log to confirm ID is received
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    @SuppressLint("IntentReset")
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void updateImageView(Uri imageUri, int position) {
        // Update the corresponding ImageView based on the position
        switch (position) {
            case 0:
                ImageView imageView_1 = findViewById(R.id.photo1);
                imageView_1.setImageURI(imageUri);
                imageView_1.setVisibility(View.VISIBLE);
                break;
            case 1:
                ImageView imageView_2 = findViewById(R.id.photo2);
                imageView_2.setImageURI(imageUri);
                imageView_2.setVisibility(View.VISIBLE);
                break;
            case 2:
                ImageView imageView_3 = findViewById(R.id.photo3);
                imageView_3.setImageURI(imageUri);
                imageView_3.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                // User selected multiple images
                for (int i = 0; i < Math.min(3, clipData.getItemCount()); i++) {
                    Uri selectedImageUri = clipData.getItemAt(i).getUri();
                    // Update the corresponding ImageView based on the totalPhotoCounter
                    updateImageView(selectedImageUri, totalPhotoCounter);

                    imageUris.add(selectedImageUri);

                    // Increment the total photo counter
                    totalPhotoCounter++;
                }
            }
        }
        else if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK){
            // update the corresponding ImageView based on the totalPhotoCounter
            updateImageView(currentPhotoUri, totalPhotoCounter);

            imageUris.add(currentPhotoUri);

            // Increment the total photo counter
            totalPhotoCounter++;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @SuppressLint("HardwareIds") String owner = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        existing_tags = new TagList();
        selected_tags = new TagList();
        getOwnerTags(owner);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_add_edit);
        Button addEditCheckButton = findViewById(R.id.add_edit_check);
        Button back = findViewById(R.id.add_edit_back_button);

        Button addTagButton = findViewById(R.id.add_tag_button);

        Button addEditCameraButton = findViewById(R.id.add_edit_camera_button);
        Button addEditGalleryButton = findViewById(R.id.add_edit_gallery_button);

        expenseDescription = findViewById(R.id.add_edit_description);
        expenseDate = findViewById(R.id.add_edit_date);
        expenseValue = findViewById(R.id.add_edit_price);
        expenseComment = findViewById(R.id.add_edit_comment);
        expenseSerialNumber = findViewById(R.id.add_edit_serial);
        expenseMake = findViewById(R.id.add_edit_make);
        expenseModel = findViewById(R.id.add_edit_model);

        // Fill out fields if editing
        Intent intent = getIntent();
        expenseDescription.setText(intent.getStringExtra("Description"));
        expenseDate.setText(intent.getStringExtra("Date"));
        expenseValue.setText(intent.getStringExtra("Price"));
        expenseComment.setText(intent.getStringExtra("Comment"));
        expenseSerialNumber.setText(intent.getStringExtra("Serial"));
        expenseMake.setText(intent.getStringExtra("Make"));
        expenseModel.setText(intent.getStringExtra("Model"));

        // To disable the button
        addEditCheckButton.setEnabled(false);

        //set up watcher
        setupTextWatcher(expenseDescription, addEditCheckButton);
        setupTextWatcher(expenseValue, addEditCheckButton);
        setupTextWatcher(expenseDate, addEditCheckButton);
        setupTextWatcher(expenseComment, addEditCheckButton);
        setupTextWatcher(expenseSerialNumber, addEditCheckButton);
        setupTextWatcher(expenseMake, addEditCheckButton);
        setupTextWatcher(expenseModel, addEditCheckButton);


        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addTagButton.isEnabled()) {
                    getOwnerTags(owner);
                    tagDialog = new TagDialog(AddEditActivity.this, existing_tags, selected_tags, current_item);
                    tagDialog.show();
                }
            }
        });

        // Create a dismiss listener for TagDialog. This way we can ensure that existing_tags updates after tag dialog.
        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getOwnerTags(owner); // update the existing tags to the database
                selected_tags = current_item.getTags(); // update the selected tags
            }
        };

        // Check if tagDialog is not null and set the dismiss listener
        if (tagDialog != null) {
            tagDialog.setOnDismissListener(dismissListener);
        }

        addEditCheckButton.setOnClickListener(view -> {
            // Check if the button is enabled before performing actions
            if (addEditCheckButton.isEnabled()) {
                popuateItemFields();
                if (imageUris != null){
                    uploadImagesAndUpdateItem(current_item, imageUris);
                    for (Uri imageUri : imageUris){
                        current_item.setDisplayImage(imageUri.toString());
                    }
                }
                // Pass the createdItem back to MainActivity
                finishAndPassItem(current_item);
            }
        });

        addEditGalleryButton.setOnClickListener(v -> {
            if (totalPhotoCounter < MAX_TOTAL_PHOTOS) {
                openGallery();
            } else {
                Toast.makeText(AddEditActivity.this, "Maximum of " + MAX_TOTAL_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
            }
        });

        addEditCameraButton.setOnClickListener(v -> {
            if (totalPhotoCounter < MAX_TOTAL_PHOTOS) {
                askCameraPermissions();
            } else {
                Toast.makeText(AddEditActivity.this, "Maximum of " + MAX_TOTAL_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> finish());
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            // request permissions from user
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else {
            // already have permissions
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // user accepted camera permission request
                openCamera();
            } else {
                // user denied camera permission request
                Toast.makeText(this, "Camera Permission is required to use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        // create a file
        String fileName = "photo" + totalPhotoCounter;
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri imageUri = null;

        try {
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory); // this throws exceptions
            String currentPhotoPath = imageFile.getAbsolutePath();

            imageUri = FileProvider.getUriForFile(AddEditActivity.this, "com.example.wongtonsoup.fileprovider", imageFile);
            currentPhotoUri = imageUri;

            // start an image capture intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, OPEN_CAMERA_REQUEST);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // Method to upload images and update item
    public void uploadImagesAndUpdateItem(Item item, List<Uri> imageUris) {
        if (imageUris != null && !imageUris.isEmpty()) {
            for (Uri imageUri : imageUris) {
                uploadImageToFirebaseStorage(item, imageUri);
            }
        } else {
            updateItemInFirestore(item);
        }
    }

    private void uploadImageToFirebaseStorage(Item item, Uri imageUri) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("items/" + item.getID() + "/" + imageUri.getLastPathSegment());

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            item.setDisplayImage(downloadUrl.toString());
            updateItemInFirestore(item);
        })).addOnFailureListener(e -> {
            // Handle unsuccessful uploads
            Toast.makeText(AddEditActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateItemInFirestore(Item item) {
        // Update item in Firestore
        db.collection("items").document(item.getID()).set(item)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
    }

    private void getOwnerTags(String device_id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tags")
                .whereEqualTo("owner", device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String name = document.getString("name");
                                String owner = document.getString("owner");
                                String id = document.getString("id");

                                Tag tag = new Tag(name);
                                tag.setOwner(owner);
                                tag.setUuid(id);

                                existing_tags.addTag(tag);

                            } catch (Exception e) {
                                Log.e("MainActivity", "Error parsing item: " + e.getMessage());
                            }
                        }
                    } else {
                        Log.d("MainActivity", "Error getting documents: ", task.getException());
                    }

                    Log.d("MainActivity", "Logging item IDs from DB:");
                });
    }

}
