package com.example.wongtonsoup;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.wongtonsoup.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements com.example.wongtonsoup.ItemList.ItemListListener {
    private static final int ADD_EDIT_REQUEST_CODE = 1;
    private static final int VIEW_REQUEST_CODE = 2;
    private int itemSelected;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Boolean expanded = false;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private CollectionReference tagsRef;
    private CollectionReference usersRef;

    ListView ItemList;
    ArrayList<Item> ItemDataList;
    com.example.wongtonsoup.ItemList itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("items");
        tagsRef = db.collection("tags");
        usersRef = db.collection("users");

//        ItemList = findViewById(R.id.listView);

        ItemList = binding.listView;

        ItemDataList = new ArrayList<>();
        itemList = new ItemList(this, ItemDataList);
        ItemList.setAdapter(itemList);
        com.example.wongtonsoup.ItemList.setListener(this);

        // sample data for testing
        Item sampleItem1 = new Item("09-11-2023", "Laptop", "Dell", "XPS 15", 1200.00f, "Work laptop with touch screen", "ABC123XYZ");
        Item sampleItem2 = new Item("16-04-2001", "Smartphone", "Apple", "iPhone X", 999.99f, "Personal phone, space gray color", "XYZ789ABC");
        Item sampleItem3 = new Item("30-10-2017", "Camera", "Canon", "EOS 5D", 2500.50f, "Professional DSLR camera", "123456DEF");

        ItemDataList.add(sampleItem1);
        ItemDataList.add(sampleItem2);
        ItemDataList.add(sampleItem3);

        itemList.updateData(ItemDataList);
        itemList.notifyDataSetChanged();


        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
        });

        // Expand Search
        Button expand = findViewById(R.id.expand_search_button);
        View expandedSearch = findViewById(R.id.expanded);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded){
                    expandedSearch.setVisibility(View.GONE);
                    expanded = false;
                }
                else {
                    expandedSearch.setVisibility(View.VISIBLE);
                    expanded = true;
                }
            }
        });
        initSearchWidgets();
        initSortWidgets();

    }

    /**
     * Specifies the behaviour that should be performed when the displayed itemList changes.
     */
    @Override
    public void onItemListChanged() {
        updateTotalAmount();
    }

    /**
     * Updates the estimated value with the sum of all items currently displayed
     */
    private void updateTotalAmount() {
        TextView totalAmount = findViewById(R.id.estimated_value);
        totalAmount.setText(itemList.getTotalDisplayed());
    }

    /**
     * Initialize the sorting buttons and their onClickListeners.
     */
    private void initSortWidgets(){
        AppCompatButton dateSortButton = findViewById(R.id.sort_date);
        dateSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> sorted_list = new ArrayList<>(itemList.sortByDate());
                itemList.updateData(sorted_list);
            }
        });

        AppCompatButton descSortButton = findViewById(R.id.sort_description);
        descSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> sorted_list = new ArrayList<>(itemList.sortByDescription());
                itemList.updateData(sorted_list);
            }
        });

        AppCompatButton makeSortButton = findViewById(R.id.sort_make);
        makeSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> sorted_list = new ArrayList<>(itemList.sortByMake());
                itemList.updateData(sorted_list);
            }
        });

        AppCompatButton valueSortButton = findViewById(R.id.sort_value);
        valueSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Item> sorted_list = new ArrayList<>(itemList.sortByValue());
                itemList.updateData(sorted_list);
            }
        });
    }

    /**
     * Initializes the listeners for any changes in the search boxes.
     */
    private void initSearchWidgets() {
        SearchView descrptionSearchView = (SearchView) findViewById(R.id.search_view);
        descrptionSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // when user presses enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                itemList.updateData(getFilteredItems());
                return false;
            }
        });

        SearchView makeSearchView = (SearchView) findViewById(R.id.search_view_make);
        makeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // when user presses enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                itemList.updateData(getFilteredItems());
                return false;
            }
        });

        EditText startDateEditText = (EditText) findViewById(R.id.edit_text_start_date);
        startDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidDate(startDateEditText.getText().toString())){
                    startDateEditText.setError(null);
                    itemList.updateData(getFilteredItems());
                }
                else {
                    startDateEditText.setError("Invalid date format. Please use dd-mm-yyyy");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText endDateEditText = (EditText) findViewById(R.id.edit_text_end_date);
        endDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isValidDate(endDateEditText.getText().toString())){
                    endDateEditText.setError(null);
                    itemList.updateData(getFilteredItems());
                }
                else {
                    endDateEditText.setError("Invalid date format. Please use dd-mm-yyyy");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Get the list of items adhering to ALL filters currently displayed.
     * @return an ArrayList of Items that all match the filters.
     */
    private ArrayList<Item> getFilteredItems(){
        ArrayList<Item> filteredItems = new ArrayList<Item>(ItemDataList); // copy item data list

        // get search and text views to filter from
        SearchView descrptionSearchView = (SearchView) findViewById(R.id.search_view);
        SearchView makeSearchView = (SearchView) findViewById(R.id.search_view_make);
        EditText startDateEditText = (EditText) findViewById(R.id.edit_text_start_date);
        EditText endDateEditText = (EditText) findViewById(R.id.edit_text_end_date);

        // get the current values in all the text boxes
        String current_desc = descrptionSearchView.getQuery().toString();
        String current_make = makeSearchView.getQuery().toString();
        String current_start_date = startDateEditText.getText().toString();
        String current_end_date = endDateEditText.getText().toString();

        // if the start date is valid, set the current start day month and year appropriately
        int startdate_day = 0;
        int startdate_month = 0;
        int startdate_year = 0;
        if(isValidDate(current_start_date)) {
            String[] start_date_parts = current_start_date.split("-");
            startdate_day = Integer.parseInt(start_date_parts[0]);
            startdate_month = Integer.parseInt(start_date_parts[1]);
            startdate_year = Integer.parseInt(start_date_parts[2]);
        }

        // if the end date is valid, set the current end day month and year appropriately
        int enddate_day = 0;
        int enddate_month = 0;
        int enddate_year = 0;
        if(isValidDate(current_end_date)) {
            String[] end_date_parts = current_end_date.split("-");
            enddate_day = Integer.parseInt(end_date_parts[0]);
            enddate_month = Integer.parseInt(end_date_parts[1]);
            enddate_year = Integer.parseInt(end_date_parts[2]);
        }


        int size = filteredItems.size();
        int index = 0;
        // loop through every item, either removing it because it does not match one of our search criteria or leaving it and looking at the next item.
        while (index < size){
            Item current_item = filteredItems.get(index);

            // get the day month and year for the item
            String current_item_date = current_item.getPurchaseDate();
            String[] parts = current_item_date.split("-");
            int current_item_day = Integer.parseInt(parts[0]);
            int current_item_month = Integer.parseInt(parts[1]);
            int current_item_year = Integer.parseInt(parts[2]);

            // if the current item does not match even one of our search criteria it will be removed
            if (!current_desc.isEmpty() && !current_item.getDescription().toLowerCase().contains(current_desc.toLowerCase())){
                // the current item should not appear since it doesn't contain the description search string
                filteredItems.remove(index);
                size--;
            }
            else if (!current_make.isEmpty() && !current_item.getMake().toLowerCase().contains(current_make.toLowerCase())){
                // the current item should not appear because it doesn't contain the make search string
                filteredItems.remove(index);
                size--;
            }
            else if (isValidDate(current_start_date) && (current_item_year < startdate_year || (current_item_year == startdate_year && current_item_month < startdate_month) || (current_item_year == startdate_year && current_item_month == startdate_month && current_item_day < startdate_day))){
               // the current item should not appear because it's date is before the specified start date
               filteredItems.remove(index);
               size--;
            }
            else if (isValidDate(current_end_date) && (current_item_year > enddate_year || (current_item_year == enddate_year && current_item_month > enddate_month) || (current_item_year == enddate_year && current_item_month == enddate_month && current_item_day > enddate_day))){
                // the current item should not appear because it's date is after the specified end date
                filteredItems.remove(index);
                size--;
            }
            else{
                index++;
            }
        }

        return filteredItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.profile) {
            return true;
        }
        else if (id == R.id.scan) {
            return true;
        }
        else if (id == R.id.sign_out) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the request code matches and the result is OK
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");

                ItemDataList.add(resultItem);
                itemList.updateData(ItemDataList);
                itemList.notifyDataSetChanged();

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
        else if (requestCode == VIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the request code matches and the result is OK
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");

                ItemDataList.set(itemSelected, resultItem);
                itemAdapter.updateData(ItemDataList);
                itemAdapter.notifyDataSetChanged();

                // Return to view item
                Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
                intent.putExtra("Description", itemAdapter.getItem(itemSelected).getDescription());
                intent.putExtra("Make", itemAdapter.getItem(itemSelected).getMake());
                intent.putExtra("Model", itemAdapter.getItem(itemSelected).getModel());
                intent.putExtra("Comment", itemAdapter.getItem(itemSelected).getComment());
                intent.putExtra("Date", itemAdapter.getItem(itemSelected).getPurchaseDate());
                intent.putExtra("Price", itemAdapter.getItem(itemSelected).getValueAsString());
                intent.putExtra("Serial", itemAdapter.getItem(itemSelected).getSerialNumber());
                startActivityForResult(intent,VIEW_REQUEST_CODE);

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
        else if (requestCode == VIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the request code matches and the result is OK
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");

                ItemDataList.set(itemSelected, resultItem);
                itemAdapter.updateData(ItemDataList);
                itemAdapter.notifyDataSetChanged();

                // Return to view item
                Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
                intent.putExtra("Description", itemAdapter.getItem(itemSelected).getDescription());
                intent.putExtra("Make", itemAdapter.getItem(itemSelected).getMake());
                intent.putExtra("Model", itemAdapter.getItem(itemSelected).getModel());
                intent.putExtra("Comment", itemAdapter.getItem(itemSelected).getComment());
                intent.putExtra("Date", itemAdapter.getItem(itemSelected).getPurchaseDate());
                intent.putExtra("Price", itemAdapter.getItem(itemSelected).getValueAsString());
                intent.putExtra("Serial", itemAdapter.getItem(itemSelected).getSerialNumber());
                startActivityForResult(intent,VIEW_REQUEST_CODE);

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
    }

    /**
     * Validates whether a string date follows dd-mm-yyyy format.
     * @param date
     * @return True if the string date is valid, false otherwise.
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

    public void viewItem(View v) {
        // get view position
        View parentRow = (View) v.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        // go to ViewItemActivity
        Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
        itemSelected = position;
        intent.putExtra("Description", itemAdapter.getItem(position).getDescription());
        intent.putExtra("Make", itemAdapter.getItem(position).getMake());
        intent.putExtra("Model", itemAdapter.getItem(position).getModel());
        intent.putExtra("Comment", itemAdapter.getItem(position).getComment());
        intent.putExtra("Date", itemAdapter.getItem(position).getPurchaseDate());
        intent.putExtra("Price", itemAdapter.getItem(position).getValueAsString());
        intent.putExtra("Serial", itemAdapter.getItem(position).getSerialNumber());
        startActivityForResult(intent,VIEW_REQUEST_CODE);
    }
}