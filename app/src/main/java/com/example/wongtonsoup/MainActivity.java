package com.example.wongtonsoup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.ui.AppBarConfiguration;
import com.example.wongtonsoup.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import android.animation.ObjectAnimator;
import android.transition.TransitionManager;

public class MainActivity extends AppCompatActivity implements com.example.wongtonsoup.ItemList.ItemListListener {
    public static final int CAMERA_PERMISSION_CODE = 301;
    private static final int ADD_EDIT_REQUEST_CODE = 1;
    private static final int OPEN_CAMERA_REQUEST = 102;
    private static final int VIEW_REQUEST_CODE = 2;
    private int itemSelected;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Boolean expanded = false;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    //private CollectionReference tagsRef; this happens in TagList
    private TagList tags;
    private CollectionReference usersRef;
    //delete button
    private FloatingActionButton fabDelete;
    private String defaultUserPfp;
    private ItemListDB itemListDB;
//
    ListView ItemList;
    private Uri currentPhotoUri;
    int TotalPhotoCounter = 0;
    FirebaseVision dbvision;
    ArrayList<Item> ItemDataList;
    com.example.wongtonsoup.ItemList itemList;
    private boolean isEditVisible = true;
    private Button expand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ItemList = findViewById(R.id.listView);

        @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + device_id);

        tags = new TagList();

        db = FirebaseFirestore.getInstance();

        itemListDB = new ItemListDB(this, new ArrayList<Item>());
        ItemDataList = new ArrayList<>();
        itemList = new ItemList(this, ItemDataList);
        ItemList.setAdapter(itemList);

        fetchItemsFromDatabase(device_id); // Fetch items from database


        itemListDB = new ItemListDB(this, new ArrayList<Item>());

        itemsRef = db.collection("items");
        usersRef = db.collection("users");

        usersRef.document(device_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {  // if user id exists in db
                    Log.d("MainActivity", "User exists");
                } else {
                    Log.d("MainActivity", "User does not exist. Creating new user...");

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String currentDate = sdf.format(new Date());

                    String defaultName = "User";

                    User newUser = new User(device_id, currentDate, defaultName);

                    usersRef.document(device_id).set(newUser.toMap()).addOnSuccessListener(aVoid -> {
                        Log.d("MainActivity", "User successfully created!");
                    }).addOnFailureListener(e -> {
                        Log.d("MainActivity", "Failed with: ", e);
                    });

                }
            } else {
                Log.d("MainActivity", "Failed with: ", task.getException());
            }
        });

//        ItemList = findViewById(R.id.listView);
        ItemList = binding.listView;

        ItemDataList = new ArrayList<>();
        itemList = new ItemList(this, ItemDataList);
        ItemList.setAdapter(itemList);
        com.example.wongtonsoup.ItemList.setListener(this);


/*        // sample data for testing
        Item sampleItem1 = new Item("09-11-2023", "Laptop", "Dell", "XPS 15", 1200.00f, "Work laptop with touch screen", "ABC123XYZ");
        Item sampleItem2 = new Item("16-04-2001", "Smartphone", "Apple", "iPhone X", 999.99f, "Personal phone, space gray color", "XYZ789ABC");
        Item sampleItem3 = new Item("30-10-2017", "Camera", "Canon", "EOS 5D", 2500.50f, "Professional DSLR camera", "123456DEF");

        ItemDataList.add(sampleItem1);
        ItemDataList.add(sampleItem2);
        ItemDataList.add(sampleItem3);

        itemList.updateData(ItemDataList);
        itemList.notifyDataSetChanged();*/


        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);
        });

        // Expand Search
        expand = findViewById(R.id.expand_search_button);
        View expandedSearch = findViewById(R.id.expanded);
        expand.setOnClickListener(v -> {
            if (expanded){
                flipArrow();
                expandedSearch.setVisibility(View.GONE);
                expanded = false;
            }
            else {
                flipArrow();
                expandedSearch.setVisibility(View.VISIBLE);
                expanded = true;
            }
        });
        initSearchWidgets();
        initSortWidgets();
        fabDelete = findViewById(R.id.fab_delete);
        fabDelete.setOnClickListener(v -> {
            // delete all selected items
            deleteSelectedItems();
        });

        Button top_back_button = findViewById(R.id.top_back_button);
        top_back_button.setOnClickListener(v -> {
            itemList.setVisible(0);
            itemList.notifyDataSetChanged();

            View edit_bar = findViewById(R.id.edit_list);
            edit_bar.setVisibility(View.GONE);


            FloatingActionButton add = findViewById(R.id.fab);
            add.setVisibility(View.VISIBLE);

            isEditVisible = !isEditVisible;
            invalidateOptionsMenu();

            View top = findViewById(R.id.top);
            top.setVisibility(View.VISIBLE);

            View top_back = findViewById(R.id.top_back);
            top_back.setVisibility(View.GONE);
        });

    }

    /**
     * Specifies the behaviour that should be performed when the displayed itemList changes.
     */
    @Override
    public void onItemListChanged() {
        // Update the total amount after deletion
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
        dateSortButton.setOnClickListener(v -> {
            List<Item> sorted_list = new ArrayList<>(itemList.sortByDate());
            itemList.updateData(sorted_list);
        });

        AppCompatButton descSortButton = findViewById(R.id.sort_description);
        descSortButton.setOnClickListener(v -> {
            List<Item> sorted_list = new ArrayList<>(itemList.sortByDescription());
            itemList.updateData(sorted_list);
        });

        AppCompatButton makeSortButton = findViewById(R.id.sort_make);
        makeSortButton.setOnClickListener(v -> {
            List<Item> sorted_list = new ArrayList<>(itemList.sortByMake());
            itemList.updateData(sorted_list);
        });

        AppCompatButton valueSortButton = findViewById(R.id.sort_value);
        valueSortButton.setOnClickListener(v -> {
            List<Item> sorted_list = new ArrayList<>(itemList.sortByValue());
            itemList.updateData(sorted_list);
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

            Intent intent = new Intent(this, ProfileActivity.class);

            String device_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            usersRef.document(device_id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String joinDate = task.getResult().getString("joined");
                    String name = task.getResult().getString("name");
                    intent.putExtra("USER_ID", device_id);
                    intent.putExtra("JOIN_DATE", joinDate);
                    intent.putExtra("USER_NAME", name);

                    startActivity(intent);
                } else {
                    Log.d("MainActivity", "Failed with: ", task.getException());
                }
            });
            return true;
        }
        else if (id == R.id.scan) {
            scanCode();
        }
        else if (id == R.id.sign_out) {
            return true;
        }
        else if (id == R.id.edit) {
            // Edit the list of items. Show check boxes and delete buttons. Have back button and tags
            if (ItemDataList.size() > 0){
                itemList.setVisible(1);
                itemList.notifyDataSetChanged();

                View edit_bar = findViewById(R.id.edit_list);
                edit_bar.setVisibility(View.VISIBLE);

                FloatingActionButton add = findViewById(R.id.fab);
                add.setVisibility(View.GONE);

                isEditVisible = !isEditVisible;
                invalidateOptionsMenu();

                View top = findViewById(R.id.top);
                top.setVisibility(View.GONE);

                View top_back = findViewById(R.id.top_back);
                top_back.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(this, "No Items to Edit", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.edit).setVisible(isEditVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    private void onScanning() {
        askCameraPermissions();
        String photoUri = currentPhotoUri.toString();
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(MainActivity.this, Uri.fromFile(new File(photoUri)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(barcodes -> {
                    for (FirebaseVisionBarcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        Item resultItem;
                        db.collection("items")
                                .whereEqualTo("barcodes", rawValue)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            try {
                                                String description = document.getString("description");
                                                String make = document.getString("make");
                                                String model = document.getString("model");
                                                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                                                startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);

                                            } catch (Exception e) {
                                                Log.e("MainActivity", "Error scanning barcode: " + e.getMessage());
                                            }
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    throw new IllegalArgumentException();
                                });
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Check if the request code matches and the result is OK
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");
                ItemDataList.add(resultItem);

                // use new ItemListDB here
                //itemListDB.addItem(resultItem);
                itemList.updateData(ItemDataList);
                itemList.notifyDataSetChanged();

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
        else if (requestCode == VIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("resultItem")) {
                Item resultItem = (Item) data.getSerializableExtra("resultItem");

                // Use the getID() method from the resultItem
                String itemId = resultItem.getID();
                Log.d("MainActivity ITEM ID", "Item ID: " + itemId);

                itemListDB.updateItem(resultItem);

                ItemDataList.set(itemSelected, resultItem);
                itemList.updateData(ItemDataList);
                itemList.notifyDataSetChanged();

                // Return to view item
                Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
                intent.putExtra("Description", itemList.getItem(itemSelected).getDescription());
                intent.putExtra("Make", itemList.getItem(itemSelected).getMake());
                intent.putExtra("Model", itemList.getItem(itemSelected).getModel());
                intent.putExtra("Comment", itemList.getItem(itemSelected).getComment());
                intent.putExtra("Date", itemList.getItem(itemSelected).getPurchaseDate());
                intent.putExtra("Price", itemList.getItem(itemSelected).getValueAsString());
                intent.putExtra("Serial", itemList.getItem(itemSelected).getSerialNumber());
                intent.putExtra("ID", itemList.getItem(itemSelected).getID());
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
                itemList.updateData(ItemDataList);
                itemList.notifyDataSetChanged();


                // Return to view item
                Intent intent = new Intent(MainActivity.this, ViewItemActivity.class);
                intent.putExtra("Description", itemList.getItem(itemSelected).getDescription());
                intent.putExtra("Make", itemList.getItem(itemSelected).getMake());
                intent.putExtra("Model", itemList.getItem(itemSelected).getModel());
                intent.putExtra("Comment", itemList.getItem(itemSelected).getComment());
                intent.putExtra("Date", itemList.getItem(itemSelected).getPurchaseDate());
                intent.putExtra("Price", itemList.getItem(itemSelected).getValueAsString());
                intent.putExtra("Serial", itemList.getItem(itemSelected).getSerialNumber());
                intent.putExtra("ID", itemList.getItem(itemSelected).getID());
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
        intent.putExtra("Description", itemList.getItem(position).getDescription());
        intent.putExtra("Make", itemList.getItem(position).getMake());
        intent.putExtra("Model", itemList.getItem(position).getModel());
        intent.putExtra("Comment", itemList.getItem(position).getComment());
        intent.putExtra("Date", itemList.getItem(position).getPurchaseDate());
        intent.putExtra("Price", itemList.getItem(position).getValueAsString());
        intent.putExtra("Serial", itemList.getItem(position).getSerialNumber());
        intent.putExtra("ID", itemList.getItem(position).getID());

        // Add the image paths list extra
        Queue<String> imagePathsQueue = itemList.getItem(itemSelected).getImagePathsCopy();
        if(imagePathsQueue != null && !imagePathsQueue.isEmpty()) {
            List<String> imagePathsList = new ArrayList<>(imagePathsQueue);
            intent.putStringArrayListExtra("ImagePaths", new ArrayList<>(imagePathsList));
        }

        startActivityForResult(intent,VIEW_REQUEST_CODE);
    }
    /**
     * Delete selected items when the delete button is clicked
     */
    private void deleteSelectedItems() {
        Log.d("MainActivity", "deleteSelectedItems: ");
        List<Item> ItemToDelete = itemList.deleteSelectedItems();

        for (Item item : ItemToDelete) {
            itemListDB.deleteItem(item);
        }

        ItemDataList.removeAll(ItemToDelete);
        updateTotalAmount(); // Update the total amount after deletion
        Log.d("ItemDataList", "delete size: " + ItemDataList.size());
    }

    private void fetchItemsFromDatabase(String device_id) {

        db.collection("items")
                .whereEqualTo("owner", device_id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                String id = document.getId();
                                String purchaseDate = document.getString("purchaseDate");
                                String description = document.getString("description");
                                String make = document.getString("make");
                                String model = document.getString("model");
                                Float value = Objects.requireNonNull(document.getDouble("value")).floatValue();
                                String comment = document.getString("comment");
                                String serialNumber = document.getString("serial");
                                String owner = document.getString("owner");

                                List<?> rawImageUrls = (List<?>) document.get("imagePathsCopy");

                                // Create an Item object
                                Item item = new Item(id, purchaseDate, description, make, model, value, comment, serialNumber, owner, new TagList());

                                if (rawImageUrls != null) {
                                    for (Object rawImageUrl : rawImageUrls) {
                                        if (rawImageUrl instanceof String) {
                                            item.setDisplayImage((String) rawImageUrl);
                                        }
                                    }
                                }
                                ItemDataList.add(item);

                            } catch (Exception e) {
                                Log.e("MainActivity", "Error parsing item: " + e.getMessage());
                            }
                        }
                        itemList.updateData(ItemDataList);
                        itemList.notifyDataSetChanged();
                    } else {
                        Log.d("MainActivity", "Error getting documents: ", task.getException());
                    }

                    Log.d("MainActivity", "Logging item IDs from DB:");
                    for (Item item : ItemDataList) {
                        Log.d("MainActivity", "Item ID: " + item.getID() + " | Desc: " + item.getDescription());
                    }
                });
        itemListDB = new ItemListDB(this, ItemDataList);
        ItemList.setAdapter(itemListDB);
    }

    private void askCameraPermissions() {
        Uri uri = null;
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            // request permissions from user
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
        else {
            // already have permissions
            openCamera();
        }
    }

    private void openCamera() {

        // create a file
        String fileName = "image" + TotalPhotoCounter;
        ++TotalPhotoCounter;
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri imageUri = null;

        try {
            File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory); // this throws exceptions
            String currentPhotoPath = imageFile.getAbsolutePath();

            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.wongtonsoup.fileprovider", imageFile);
            currentPhotoUri = imageUri;

            // start an image capture intent
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, OPEN_CAMERA_REQUEST);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void scanCode(){
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);

    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //db.collection("barcodes")
                    //.whereEqualTo("barcodes", result.getContents())
                    db.collection("barcodes").document(result.getContents())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                        try {
                                            String description = task.getResult().getString("description");
                                            String make = task.getResult().getString("make");
                                            String model = task.getResult().getString("model");
                                            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                                            Log.d("MainActivity", "Barcode values" + description + " " + make + " " + model);
                                            intent.putExtra("Description", description);
                                            intent.putExtra("Make", make);
                                            intent.putExtra("Model", model);
                                            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE);

                                        } catch (Exception e) {
                                            Log.e("MainActivity", "Error scanning barcode: " + e.getMessage());
                                        }
                                    }
                                }
                            )
                            .addOnFailureListener(e -> {
                                throw new IllegalArgumentException();
                            });
                }
            }).show();
        }
    });
    private void flipArrow() {
        TransitionManager.beginDelayedTransition((ViewGroup) expand.getParent());
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(expand, "rotation", expand.getRotation(), expand.getRotation() + 180);
        rotateAnimator.setDuration(500);
        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.start();
    }
}