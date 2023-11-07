package com.example.wongtonsoup;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.wongtonsoup.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
    ArrayAdapter<Item> ItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();

        itemsRef = db.collection("items");
        tagsRef = db.collection("tags");
        usersRef = db.collection("users");

        ItemList = findViewById(R.id.listView);

        ItemDataList = new ArrayList<>();
        ItemAdapter = new ItemList(this, ItemDataList);
        ItemList.setAdapter(ItemAdapter);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

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
                ItemAdapter.notifyDataSetChanged();

                // Log the size of ItemDataList
                Log.d("ItemDataList", "Size: " + ItemDataList.size());
            }
        }
    }
}