package com.example.wongtonsoup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageDecoder;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AddEditActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_CODE = 301;
    private static final int REQUEST_CAMERA_FOR_SERIAL = 399;

    // Assume we have item with associated tags lise
    // private Item item;
    private EditText expenseDescription;
    private EditText expenseDate;
    private EditText expenseValue;
    private EditText expenseComment;
    private EditText expenseSerialNumber;
    private EditText expenseMake;
    private EditText expenseModel;

    private Uri currentPhotoUri;
    private final List<Uri> imageUris = new ArrayList<>();


    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int OPEN_CAMERA_REQUEST = 102;
    private static final int MAX_TOTAL_PHOTOS = 3;
    private int totalPhotoCounter = 0;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    TextRecognizer recognizer;

    //List<Tag> tags = item.getTags();

    // Find the ChipGroup view in layout
//    ChipGroup chipGroup = findViewById(R.id.add_edit_chip_group);
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

    /**
     * Creates an Item object with data from EditText fields and performs actions when the button is enabled.
     *
     * @return The Item object created from EditText fields.
     */
    private Item createItemFromFields() {
        String description = expenseDescription.getText().toString();
        String date = expenseDate.getText().toString();
        String str_value = expenseValue.getText().toString();
        String comment = expenseComment.getText().toString();
        String make = expenseMake.getText().toString();
        String model = expenseModel.getText().toString();
        String serialNumber = expenseSerialNumber.getText().toString();
        TagList existingTags = new TagList();
        TagList selectedTags = new TagList();

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
        return new Item(id, date, description, make, model, value, comment, serialNumber, owner, selectedTags);

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
     *
     * @param str_value string to be validated
     * @return true if value is valid, false otherwise.
     */
    private boolean isValidValue(String str_value) {
        // Define expression for any number of int digits and an optional one or two digits after decimal
        String valuePattern = "^\\d+(\\.\\d{1,2})?$";
        return str_value.matches(valuePattern);
    }

    /**
     * Updates the state of the Add/Edit Check Button based on the validity of input fields.
     *
     * @param addEditCheckButton The Button whose state needs to be updated.
     */
    private void updateAddEditCheckButtonState(Button addEditCheckButton) {
        boolean isExpenseDescriptionEmpty = TextUtils.isEmpty(expenseDescription.getText().toString());
        boolean isExpenseDescriptionInvalid = expenseDescription.getText().length() > 15;
        if (isExpenseDescriptionEmpty) {
            expenseDescription.setError("Expense name cannot be empty");
        } else if (isExpenseDescriptionInvalid) {
            expenseDescription.setError("Name cannot exceed 15 characters");
        } else {
            expenseDescription.setError(null); // clear error
        }


        boolean isExpenseValueInvalid = !isValidValue(expenseValue.getText().toString());
        boolean isExpenseValueEmpty = TextUtils.isEmpty(expenseValue.getText().toString());

        if (!isExpenseValueEmpty && !isExpenseValueInvalid) {
            expenseValue.setError(null); // clear error
        } else if (isExpenseValueEmpty) {
            expenseValue.setError("Expense value cannot be empty");
        } else {
            expenseValue.setError("Expense value can only contain two digits after the decimal");
        }

        boolean isExpenseDateEmpty = TextUtils.isEmpty(expenseDate.getText().toString());
        boolean isExpenseDateInvalid = !isValidDate(expenseDate.getText().toString());

        if (!isExpenseDateEmpty && !isExpenseDateInvalid) {
            expenseDate.setError(null);
        } else if (isExpenseDateEmpty) {
            expenseDate.setError("Expense Date cannot be empty");
        } else {
            expenseDate.setError("Invalid date format. Please use dd-mm-yyyy");
        }

        boolean isExpenseCommentInvalid = !(expenseComment.getText().length() <= 40);

        if (!isExpenseCommentInvalid) {
            expenseComment.setError(null);
        } else {
            expenseComment.setError("Comment cannot exceed 40 characters");
        }

        boolean isExpenseMakeEmpty = TextUtils.isEmpty(expenseMake.getText().toString());
        boolean isExpenseMakeInvalid = !(expenseMake.getText().length() <= 15);

        if (!isExpenseMakeEmpty && !isExpenseMakeInvalid) {
            expenseMake.setError(null); // Clear the error
        } else if (isExpenseMakeEmpty) {
            expenseMake.setError("Expense make cannot be empty");
        } else {
            expenseMake.setError("Make cannot exceed 15 characters");
        }

        boolean isExpenseModelInvalid = !(expenseModel.getText().length() <= 15);
        boolean isExpenseModelEmpty = TextUtils.isEmpty(expenseModel.getText().toString());

        if (!isExpenseModelEmpty && !isExpenseModelInvalid) {
            expenseModel.setError(null);
        } else if (isExpenseModelEmpty) {
            expenseModel.setError("Expense model cannot be empty");
        } else {
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
     *
     * @param item item to pass
     */

    private void finishAndPassItem(Item item) {
        Intent resultIntent = new Intent();
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
        } else if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK) {
            // update the corresponding ImageView based on the totalPhotoCounter
            updateImageView(currentPhotoUri, totalPhotoCounter);

            imageUris.add(currentPhotoUri);

            // Increment the total photo counter
            totalPhotoCounter++;
        }

        if (requestCode == REQUEST_CAMERA_FOR_SERIAL && resultCode == RESULT_OK) {
//            Log.d("AddEditActivity", "onActivityResult: " + requestCode);
//            Log.d("AddEditActivity", "onActivityResult: " + resultCode);

            try {
                InputImage image = InputImage.fromFilePath(this, currentPhotoUri);
//
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text text) {
                                        // Task completed successfully
                                        // ...
                                        String resultText = text.getText();
                                        expenseSerialNumber.setText(resultText);
                                        Log.d("AddEditActivity SERIAL", "onActivityResult: " + resultText);
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                                Toast.makeText(AddEditActivity.this, "Failed to recognize serial number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("AddEditActivity FAILURE", "onActivityResult: " + e.getMessage());
                                            }
                                        }
                                );
            } catch (IOException e) {
                e.printStackTrace();
            }


//                if (recognizer != null) {
//                    recognizer.process(image)
//                            .addOnSuccessListener(visionText -> {
//                                String resultText = visionText.getText();
//                                expenseSerialNumber.setText(resultText);
//                                Log.d("AddEditActivity SERIAL", "onActivityResult: " + resultText);
//                            })
//                            .addOnFailureListener(e -> {
//                                Toast.makeText(AddEditActivity.this, "Failed to recognize serial number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            });
//                } else {
//                    Toast.makeText(AddEditActivity.this, "Failed to recognize serial number", Toast.LENGTH_SHORT).show();
//                }



            // Extract the image and process it as needed
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Existing code for handling gallery images
        } else if (requestCode == OPEN_CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Existing code for handling general camera images
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_add_edit);
        Button addEditCheckButton = findViewById(R.id.add_edit_check);
        Button back = findViewById(R.id.add_edit_back_button);

        Button addTagButton = findViewById(R.id.add_tag_button);

        Button addEditCameraButton = findViewById(R.id.add_edit_camera_button);
        Button addEditGalleryButton = findViewById(R.id.add_edit_gallery_button);

        // set up serial camera button for text recognition
        Button serialCameraButton = findViewById(R.id.add_edit_serial_button);
        serialCameraButton.setOnClickListener(v -> askCameraPermissions(true));

        expenseDescription = findViewById(R.id.add_edit_description);
        expenseDate = findViewById(R.id.add_edit_date);
        expenseValue = findViewById(R.id.add_edit_price);
        expenseComment = findViewById(R.id.add_edit_comment);
        expenseSerialNumber = findViewById(R.id.add_edit_serial);
        expenseMake = findViewById(R.id.add_edit_make);
        expenseModel = findViewById(R.id.add_edit_model);

        // Fill out fields if editing
        Intent intent = getIntent();
        Log.d("AddEditActivity", "onCreate desc: " + intent.getStringExtra("Description"));
        expenseDescription.setText(intent.getStringExtra("Description"));
        expenseDate.setText(intent.getStringExtra("Date"));
        expenseValue.setText(intent.getStringExtra("Price"));
        expenseComment.setText(intent.getStringExtra("Comment"));
        expenseSerialNumber.setText(intent.getStringExtra("Serial"));
        expenseMake.setText(intent.getStringExtra("Make"));
        expenseModel.setText(intent.getStringExtra("Model"));

//        expenseDescription.setText("desc");
//        expenseDate.setText("11-11-1111");
//        expenseValue.setText("10.00");
//        expenseMake.setText("make");
//        expenseMake.setText("model");

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

        expenseDate.addTextChangedListener(new DateInputWatcher());


        // Display tags
        TagList tags = new TagList(); //Replace with db tags
        LinearLayoutManager layoutManagerItem = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewEdit = findViewById(R.id.recyclerViewEdit);
        recyclerViewEdit.setLayoutManager(layoutManagerItem);
        TagListAdapter tagAdapter = new TagListAdapter(this, tags);
        recyclerViewEdit.setAdapter(tagAdapter);

        // set up click listener for add tag button
        addTagButton.setOnClickListener(view -> {
            // Check if the button is enabled before performing actions
            if (addTagButton.isEnabled()) {
                TagList existingTags = new TagList();
                TagList selectedTags = new TagList();
                TagDialog tagDialog = new TagDialog(AddEditActivity.this, existingTags, selectedTags);
                tagDialog.show();
            }
        });


        addEditCheckButton.setOnClickListener(view -> {
            // Check if the button is enabled before performing actions
            if (addEditCheckButton.isEnabled()) {
                Item createdItem = createItemFromFields();
                uploadImagesAndUpdateItem(createdItem, imageUris);

                // Pass the createdItem back to MainActivity
//                finishAndPassItem(createdItem);
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
                askCameraPermissions(false);
            } else {
                Toast.makeText(AddEditActivity.this, "Maximum of " + MAX_TOTAL_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> finish());

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    private void askCameraPermissions(boolean forSerial) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, forSerial ? REQUEST_CAMERA_FOR_SERIAL : CAMERA_PERMISSION_CODE);
        } else {
            if (forSerial) {
                openCameraForSerial();
            } else {
                openCamera();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("AddEditActivity", "onRequestPermissionsResult: " + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CAMERA_FOR_SERIAL) {
                openCameraForSerial();
            } else if (requestCode == CAMERA_PERMISSION_CODE) {
                openCamera();
            }
        } else {
            Toast.makeText(this, "Camera Permission is required to use camera", Toast.LENGTH_SHORT).show();
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCameraForSerial() {
        // create a file
        String fileName = "serial";
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
            startActivityForResult(intent, REQUEST_CAMERA_FOR_SERIAL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* TextWatcher for date input field
    *   Automatically adds hyphens to the date input field
    * */
    private class DateInputWatcher implements TextWatcher {

        private boolean isUpdating = false;
        private String oldText = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ...
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ...
        }

        @Override
        public void afterTextChanged(Editable s) {
            String newText = s.toString();
            if (isUpdating) {
                oldText = newText;
                isUpdating = false;
                return;
            }

            String strippedText = newText.replace("-", "");

            String formattedText;
            if (strippedText.length() <= 2) {
                formattedText = strippedText;
            } else if (strippedText.length() <= 4) {
                formattedText = strippedText.substring(0, 2) + "-" + strippedText.substring(2);
            } else {
                formattedText = strippedText.substring(0, 2) + "-" + strippedText.substring(2, 4) + "-" + strippedText.substring(4, Math.min(8, strippedText.length()));
            }

            isUpdating = true;
            s.replace(0, s.length(), formattedText);
        }
    }


    // Method to upload images and update item
    public void uploadImagesAndUpdateItem(Item item, List<Uri> imageUris) {
        if (imageUris != null && !imageUris.isEmpty()) {
            int totalImages = imageUris.size();
            AtomicInteger uploadedImages = new AtomicInteger(0);

            for (Uri imageUri : imageUris) {
                uploadImageToFirebaseStorage(item, imageUri, totalImages, uploadedImages);
            }
        } else {
            // No images to upload, directly update item in Firestore
            updateItemInFirestore(item);
        }
    }

    private void uploadImageToFirebaseStorage(Item item, Uri imageUri, int totalImages, AtomicInteger uploadedImages) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("items/" + item.getID() + "/" + imageUri.getLastPathSegment());

        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            item.setDisplayImage(downloadUrl.toString());

            int currentUploadedImages = uploadedImages.incrementAndGet();
            if (currentUploadedImages == totalImages) {
                // All images uploaded, update item in Firestore
                updateItemInFirestore(item);
            }
        })).addOnFailureListener(e -> {
            // Handle unsuccessful uploads
            Toast.makeText(AddEditActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateItemInFirestore(Item item) {
        // Update item in Firestore
        db.collection("items").document(item.getID()).set(item)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    // Call finishAndPassItem here as the update is successful
                    finishAndPassItem(item);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error updating document", e);
                    // Handle the failure if needed
                });
    }
}