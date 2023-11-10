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

public class ItemList extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> itemList; // The adapter's own list for display
    private static ItemListListener listener; // Listener for adapter changes

    // Interface for a class that wants to implement a listener on this class
    public interface ItemListListener {
        void onItemListChanged();
    }

    // Constructor for the ItemAdapter
    public ItemList(@NonNull Context context, @NonNull List<Item> objects) {
        super(context, 0, new ArrayList<Item>()); // Initialize ArrayAdapter with an empty list
        mContext = context;
        itemList = new ArrayList<>(objects); // Create a separate list for the adapter
        addAll(itemList); // Add all items to the ArrayAdapter for display

    }

    public static void setListener(ItemListListener customlistener) {
        listener = customlistener;
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

    /**
     * Updates the list of items currently being displayed
     * @param items new list to be displayed
     */
    public void updateData(List<Item> items) {
        itemList.clear(); // Clear the adapter's own list
        itemList.addAll(items); // Add new items to the adapter's list
        clear(); // Clear the ArrayAdapter's internal list
        addAll(itemList); // Add the adapter's list to the ArrayAdapter's internal list
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
        if (listener != null){
            listener.onItemListChanged();
        }
    }

    public String getTotalDisplayed(){
        double total = 0;
        for (int i = 0 ; i < itemList.size() ; i++){
            total += itemList.get(i).getValue();
        }
        String s = String.format("%.2f", total);
        return s;
    }

    /**
     * Searchs itemList by date
     * @return sorted list.
     */
    public List<Item> sortByDate(){
        itemList.sort(Item.byDate); // use the comparator in the Item class
        return itemList;
    }
    /**
     * Searchs itemList by description
     * @return sorted list.
     */
    public List<Item> sortByDescription(){
        itemList.sort(Item.byDescription); // use the comparator in the Item class
        return itemList;
    }
    /**
     * Searchs itemList by make
     * @return sorted list.
     */
    public List<Item> sortByMake(){
        itemList.sort(Item.byMake); // use the comparator in the Item class
        return itemList;
    }
    /**
     * Searchs itemList by value
     * @return sorted list.
     */
    public List<Item> sortByValue(){
        itemList.sort(Item.byValue); // use the comparator in the Item class
        return itemList;
    }

}

