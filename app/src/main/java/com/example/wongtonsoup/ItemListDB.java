package com.example.wongtonsoup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListDB extends ArrayAdapter<Item> {
    private Context mContext;
    private List<Item> itemList; // The adapter's own list for display
    private FirebaseFirestore db;

    public ItemListDB(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, 0, new ArrayList<Item>()); // Initialize ArrayAdapter with an empty list
        mContext = context;
        itemList = new ArrayList<>(objects); // Create a separate list for the adapter
        addAll(itemList); // Add all items to the ArrayAdapter for display
        db = FirebaseFirestore.getInstance();
    }
    public void addItem(Item item) {
        itemList.add(item);
        add(item);
        notifyDataSetChanged();

        // add item to database
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("description", item.getDescription());
        itemData.put("value", item.getValue());
        itemData.put("serial", item.getSerialNumber());
        itemData.put("DOP", item.getPurchaseDate());
        itemData.put("make", item.getMake());
        itemData.put("model", item.getModel());
        itemData.put("comment", item.getComment());
        itemData.put("owner", item.getOwner());

        ArrayList<String> tags_ref = new ArrayList<String>();
        if (item.getTags() != null && item.getTags().getTags() != null) {
            for (int tag_index = 0; tag_index < item.getTags().getTags().size(); tag_index++) {
                tags_ref.add(item.getTags().getTags().get(tag_index).getUuid().toString());
            }
        }
        itemData.put("tags", tags_ref);

        String id = item.getID();

        db.collection("items").document(id).set(itemData)
                .addOnSuccessListener(aVoid -> Log.d("Item", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("ItemList", "Error writing document", e));

    }

    public void deleteItem(Item item) {
        itemList.remove(item);
        remove(item);
        notifyDataSetChanged();

        db.collection("items").document(item.getID())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("ItemList", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w("ItemList", "Error deleting document", e));
    }

    public void updateItem(Item item) {
        // Update the item in itemList and notifyDataSetChanged if needed
        notifyDataSetChanged();

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("description", item.getDescription());
        itemData.put("value", item.getValue());
        itemData.put("serial", item.getSerialNumber());
        itemData.put("DOP", item.getPurchaseDate());
        itemData.put("make", item.getMake());
        itemData.put("model", item.getModel());
        itemData.put("comment", item.getComment());
        itemData.put("owner", item.getOwner());

        String id = item.getID(); // This should be the same ID used when the item was first created
        Log.d("ItemListDB UPDATE", "ID: " + id);
        // Update the existing document
        db.collection("items").document(id).update(itemData)
                .addOnSuccessListener(aVoid -> Log.d("Item", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("ItemList", "Error updating document", e));
    }

}