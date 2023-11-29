package com.example.wongtonsoup;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {
    // Assume we have item with associated tags lise
//    private Item item;
    private EditText expenseDescription;
    private EditText expenseDate;
    private EditText expenseValue;
    private EditText expenseComment;
    private EditText expenseSerialNumber;
    private EditText expenseMake;
    private EditText expenseModel;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_TOTAL_PHOTOS = 3;
    private int totalPhotoCounter = 0;

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

        // convert charge to a float object
        Float value = Float.valueOf(str_value);

        // Create an Item object with the gathered data
        return new Item(date, description, make, model, value, comment, serialNumber);
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

    private void finishAndPassItem(Item item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultItem", item);
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
            case 1:
                ImageView imageView_1 = findViewById(R.id.photo1);
                imageView_1.setImageURI(imageUri);
                imageView_1.setVisibility(View.VISIBLE);
                break;
            case 2:
                ImageView imageView_2 = findViewById(R.id.photo2);
                imageView_2.setImageURI(imageUri);
                imageView_2.setVisibility(View.VISIBLE);
                break;
            case 3:
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
                    updateImageView(selectedImageUri, totalPhotoCounter + 1);

                    // Increment the total photo counter
                    totalPhotoCounter++;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Button addEditCheckButton = findViewById(R.id.add_edit_check);
        Button back = findViewById(R.id.add_edit_back_button);
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

        addEditCheckButton.setOnClickListener(view -> {
            // Check if the button is enabled before performing actions
            if (addEditCheckButton.isEnabled()) {
                Item createdItem = createItemFromFields();
                // Pass the createdItem back to MainActivity
                finishAndPassItem(createdItem);
            }
        });

        addEditGalleryButton.setOnClickListener(v -> {
            if (totalPhotoCounter < MAX_TOTAL_PHOTOS) {
                openGallery();
            } else {
                Toast.makeText(AddEditActivity.this, "Maximum of " + MAX_TOTAL_PHOTOS + " photos allowed", Toast.LENGTH_SHORT).show();
            }
        });

        back.setOnClickListener(v -> finish());
    }
}