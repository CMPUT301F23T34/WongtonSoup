package com.example.wongtonsoup;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.SearchView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.wongtonsoup.databinding.ActivityMainBinding;
import com.example.wongtonsoup.ItemAdapter;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ItemAdapter.ItemAdapterListener {
    private static final int ADD_EDIT_REQUEST_CODE = 1;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    Boolean expanded = false;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private CollectionReference tagsRef;
    private CollectionReference usersRef;

    ListView ItemList;
    ArrayList<Item> ItemDataList;
    ArrayList<Item> DisplayedItemDataList; // what's currently on the screen
    ItemAdapter itemAdapter;

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
        itemAdapter = new ItemAdapter(this, ItemDataList);
        ItemList.setAdapter(itemAdapter);
        ItemAdapter.setListener(this);

        // sample data for testing
        Item sampleItem1 = new Item("09-11-2023", "Laptop", "Dell", "XPS 15", 1200.00f, "Work laptop with touch screen", "ABC123XYZ");
        Item sampleItem2 = new Item("16-04-2001", "Smartphone", "Apple", "iPhone X", 999.99f, "Personal phone, space gray color", "XYZ789ABC");
        Item sampleItem3 = new Item("30-10-2017", "Camera", "Canon", "EOS 5D", 2500.50f, "Professional DSLR camera", "123456DEF");

        ItemDataList.add(sampleItem1);
        ItemDataList.add(sampleItem2);
        ItemDataList.add(sampleItem3);

        itemAdapter.updateData(ItemDataList);
        itemAdapter.notifyDataSetChanged();


        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            //intent.putExtra();
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


    }

    @Override
    public void onItemAdapterChanged() {
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        TextView totalAmount = findViewById(R.id.estimated_value);
        Float updated_total = itemAdapter.getTotalDisplayed();
        totalAmount.setText(updated_total.toString());
    }

    private void initSearchWidgets() {
        SearchView descrptionSearchView = (SearchView) findViewById(R.id.search_view);
        descrptionSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // when user presses enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.updateData(getFilteredItems());
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
                itemAdapter.updateData(getFilteredItems());
                return false;
            }
        });

        SearchView startDateSearchView = (SearchView) findViewById(R.id.search_view_make);
        makeSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // when user presses enter
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.updateData(getFilteredItems());
                return false;
            }
        });
    }

    private ArrayList<Item> getFilteredItems(){
        ArrayList<Item> filteredItems = new ArrayList<Item>(ItemDataList); // copy item data list
        SearchView descrptionSearchView = (SearchView) findViewById(R.id.search_view);
        SearchView makeSearchView = (SearchView) findViewById(R.id.search_view_make);

        String current_desc = descrptionSearchView.getQuery().toString();
        String current_make = makeSearchView.getQuery().toString();

        int size = filteredItems.size();
        int index = 0;
        // loop through every item, either removing it because it does not match one of our search criteria or leaving it and looking at the next item.
        while (index < size){
            Item current_item = filteredItems.get(index);
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
                itemAdapter.updateData(ItemDataList);
                itemAdapter.notifyDataSetChanged();

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
    }
}