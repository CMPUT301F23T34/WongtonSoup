package com.example.wongtonsoup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> itemList; // The adapter's own list for display

    // Constructor for the ItemAdapter
    public ItemAdapter(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, 0, new ArrayList<Item>()); // Initialize ArrayAdapter with an empty list
        mContext = context;
        itemList = new ArrayList<>(objects); // Create a separate list for the adapter
        addAll(itemList); // Add all items to the ArrayAdapter for display
    }


    // Returns the view for an item in the list
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content, parent, false);
        }

        Item currentItem = getItem(position);

        TextView descriptionTextView = convertView.findViewById(R.id.content_description);
        TextView dateTextView = convertView.findViewById(R.id.content_date);
        TextView makeTextView = convertView.findViewById(R.id.content_make);
        TextView priceTextView = convertView.findViewById(R.id.content_price);

        descriptionTextView.setText(currentItem.getDescription());
        dateTextView.setText(currentItem.getPurchaseDate().toString());
        makeTextView.setText(currentItem.getMake());
        priceTextView.setText(String.format(Locale.getDefault(), "%.2f", currentItem.getValue()));

        // Return the completed view to render on screen
        return convertView;
    }

    public void updateData(List<Item> items) {
        itemList.clear(); // Clear the adapter's own list
        itemList.addAll(items); // Add new items to the adapter's list
        clear(); // Clear the ArrayAdapter's internal list
        addAll(itemList); // Add the adapter's list to the ArrayAdapter's internal list
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }
}

