package com.example.wongtonsoup;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditActivity extends AppCompatActivity {
    // Assume we have item with associated tags lise
//    private Item item;
    private EditText expenseName;
    private EditText expenseDate;
    private EditText expenseCharge;
    private EditText expenseComment;
    private EditText expenseSerialNumber;
    private EditText expenseMake;
    private EditText expenseModel;

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
        String description = expenseName.getText().toString();
        String str_date = expenseDate.getText().toString();
        String str_value = expenseCharge.getText().toString();
        String comment = expenseComment.getText().toString();
        String make = expenseMake.getText().toString();
        String model = expenseModel.getText().toString();
        String serialNumber = expenseSerialNumber.getText().toString();

        // convert str_date to a Date object
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        try {
            date = format.parse(str_date);
        } catch (ParseException e){
            assert(0 == 1); // throw an error, UI should ensure dates are correctly formatted once received here.
        }

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
    private boolean isValidDate(String date) {
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
     * Updates the state of the Add/Edit Check Button based on the validity of input fields.
     *
     * @param addEditCheckButton The Button whose state needs to be updated.
     */
    private void updateAddEditCheckButtonState(Button addEditCheckButton) {
        boolean isExpenseNameEmpty = TextUtils.isEmpty(expenseName.getText().toString());
        boolean isExpenseChargeEmpty = TextUtils.isEmpty(expenseCharge.getText().toString());
        boolean isExpenseDateEmpty = TextUtils.isEmpty(expenseDate.getText().toString());
        boolean isExpenseDateInvalid = !isValidDate(expenseDate.getText().toString());
        boolean isExpenseNameInvalid = !(expenseName.getText().length() <= 15);
        boolean isExpenseCommentInvalid = !(expenseComment.getText().length() <= 20);
        boolean isExpenseSerialNUmberEmpty = TextUtils.isEmpty(expenseSerialNumber.getText().toString());
        boolean isExpenseMakeEmpty = TextUtils.isEmpty(expenseMake.getText().toString());
        boolean isExpenseModelEmpty = TextUtils.isEmpty(expenseModel.getText().toString());


        if (isExpenseNameEmpty) {
            expenseName.setError("Expense name cannot be empty");
        } else {
            expenseName.setError(null); // Clear the error
        }

        if (isExpenseChargeEmpty) {
            expenseCharge.setError("Expense price cannot be empty");
        } else {
            expenseCharge.setError(null); // Clear the error
        }

        if (isExpenseChargeEmpty) {
            expenseDate.setError("Expense Date cannot be empty");
        } else {
            expenseDate.setError(null); // Clear the error
        }

        if (isExpenseDateInvalid) {
            expenseDate.setError("Invalid date format. Please use dd-mm-yyyy");
        } else {
            expenseDate.setError(null); // Clear the error
        }

        if (isExpenseNameInvalid) {
            expenseName.setError("Name cannot exceed 15 characters");
        } else {
            expenseName.setError(null); // Clear the error
        }

        if (isExpenseCommentInvalid) {
            expenseComment.setError("Comment cannot exceed 20 characters");
        } else {
            expenseComment.setError(null); // Clear the error
        }

        if (isExpenseSerialNUmberEmpty) {
            expenseSerialNumber.setError("Serial number cannot be empty");
        } else {
            expenseSerialNumber.setError(null); // Clear the error
        }

        if (isExpenseMakeEmpty) {
            expenseMake.setError("Expense make cannot be empty");
        } else {
            expenseMake.setError(null); // Clear the error
        }

        if (isExpenseModelEmpty) {
            expenseModel.setError("Expense model cannot be empty");
        } else {
            expenseModel.setError(null); // Clear the error
        }

        boolean isButtonEnabled = !isExpenseNameEmpty && !isExpenseChargeEmpty && !isExpenseDateEmpty && !isExpenseDateInvalid && !isExpenseNameInvalid && !isExpenseCommentInvalid
                && !isExpenseSerialNUmberEmpty && !isExpenseMakeEmpty && !isExpenseModelEmpty;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Button addEditCheckButton = findViewById(R.id.add_edit_check);
        Button back = findViewById(R.id.add_edit_back_button);

        expenseName = findViewById(R.id.add_edit_description);
        expenseDate = findViewById(R.id.add_edit_date);
        expenseCharge = findViewById(R.id.add_edit_price);
        expenseComment = findViewById(R.id.add_edit_comment);
        expenseSerialNumber = findViewById(R.id.add_edit_serial);
        expenseMake = findViewById(R.id.add_edit_make);
        expenseModel = findViewById(R.id.add_edit_model);

        // To disable the button
        addEditCheckButton.setEnabled(false);

        //set up watcher
        setupTextWatcher(expenseName, addEditCheckButton);
        setupTextWatcher(expenseCharge, addEditCheckButton);
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

        back.setOnClickListener(v -> finish());
    }
}