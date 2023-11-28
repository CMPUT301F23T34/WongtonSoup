package com.example.wongtonsoup;

import android.annotation.SuppressLint;
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

    private EditText expenseDescription;
    private EditText expenseDate;
    private EditText expenseValue;
    private EditText expenseComment;
    private EditText expenseSerialNumber;
    private EditText expenseMake;
    private EditText expenseModel;

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
        TagList tags = new TagList();

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
     * @param str_value
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
        }
        else if (isExpenseDescriptionEmpty){
            expenseDescription.setError("Expense name cannot be empty");
        }
        else if (isExpenseDescriptionInvalid){
            expenseDescription.setError("Name cannot exceed 15 characters");
        }

        boolean isExpenseValueInvalid = !isValidValue(expenseValue.getText().toString());
        boolean isExpenseValueEmpty = TextUtils.isEmpty(expenseValue.getText().toString());

        if (!isExpenseValueEmpty && !isExpenseValueInvalid){
            expenseValue.setError(null); // clear error
        }
        else if (isExpenseValueEmpty){
            expenseValue.setError("Expense value cannot be empty");
        }
        else if (isExpenseValueInvalid) {
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
        else if (isExpenseDateInvalid){
            expenseDate.setError("Invalid date format. Please use dd-mm-yyyy");
        }

       boolean isExpenseCommentInvalid = !(expenseComment.getText().length() <= 40);

       if (!isExpenseCommentInvalid){
           expenseComment.setError(null);
       }
       else if (isExpenseCommentInvalid){
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
       else if (isExpenseMakeInvalid){
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
       else if (isExpenseModelInvalid){
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
     * @param item
     */

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
        Button addTagButton = findViewById(R.id.add_tag_button);

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

        // set up click listener for add tag button
        addTagButton.setOnClickListener(view -> {
            // Check if the button is enabled before performing actions
            if (addTagButton.isEnabled()) {
                Tags<String> existingTags = /* Get existing tags from your item */;
                TagDialog tagDialog = new TagDialog(AddEditActivity.this, Tags);
                tagDialog.show();
            }
        });


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