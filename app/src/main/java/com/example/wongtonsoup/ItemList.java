package com.example.wongtonsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemList extends ArrayAdapter<Item> {

    private Context mContext;
    private List<Item> itemList; // The adapter's own list for display
    private static ItemListListener listener; // Listener for adapter changes
    private int visible; // Variable for determining if checkBox is visible


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
        ImageView imageView = convertView.findViewById(R.id.photo);

        descriptionTextView.setText(currentItem.getDescription());
        dateTextView.setText(currentItem.getPurchaseDate());
        makeTextView.setText(currentItem.getMake());
        priceTextView.setText(String.format(Locale.getDefault(), "%.2f", currentItem.getValue()));
        if (currentItem.GetDisplayImage() != null){
            String imageURL = currentItem.GetDisplayImage();
            Picasso.get().load(imageURL).into(imageView);
            Log.d("ItemList URI", "getView: " + imageURL);
        }

        // Display tags
        View view = convertView;
        Item item = itemList.get(position);

        RecyclerView recyclerViewItem = view.findViewById(R.id.recyclerViewItem);
        LinearLayoutManager layoutManagerItem = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewItem.setLayoutManager(layoutManagerItem);
        TagListAdapter tagAdapterItem = new TagListAdapter(mContext, item.getTags());
        recyclerViewItem.setAdapter(tagAdapterItem);

        CheckBox checkBox = convertView.findViewById(R.id.select);
        checkBox.setChecked(currentItem.isSelected());
        // Set an OnCheckedChangeListener to update the selected state when the CheckBox is clicked
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentItem.setSelected(isChecked);
            //Set delete button visibility


        });
        View listItemView = convertView;
        if (visible == 1) {
            checkBox.setVisibility(View.VISIBLE);
        }
        else {
            checkBox.setVisibility(View.GONE);
        }


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
        // Notify the listener
        if (listener != null) {
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
     * Searchs itemList by make
     * @return sorted list.
     */
    public List<Item> sortByTag(){
        itemList.sort(Item.byTag); // use the comparator in the Item class
        return itemList;
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
     * @return sorted lista.
     */
    public List<Item> sortByValue(){
        itemList.sort(Item.byValue); // use the comparator in the Item class
        return itemList;
    }
    public List<Item> deleteSelectedItems() {
        Log.d("ItemList", "deleteSelectedItems: " + itemList.size());
        List<Item> itemsToDelete = new ArrayList<>();

        // Collect selected items
        for (Item item : itemList) {
            if (item.isSelected()) {
                Log.d("ItemList", "isSelected called: " + item.getDescription());
                itemsToDelete.add(item);
            }
        }

        // Remove selected items from the main list
        itemList.removeAll(itemsToDelete);

        // Clear the adapter's list and add the updated list
        clear();
        addAll(itemList);

        // Notify the adapter that the data set has changed
        notifyDataSetChanged();

        // Clear the selection
        clearSelection();

        return itemsToDelete;
    }

    /**
     * Clear the selection of all items
     */
    private void clearSelection() {
        for (Item item : itemList) {
            item.setSelected(false);
        }
    }

    void setVisible(int i) {
        visible = i;
    }

    public void addTagToItem (Item item, Tag tag) {
        int position = itemList.indexOf(item);
        TagList tags = item.getTags();
        if (tags.find(tag) == -1) {
            tags.addTag(tag);
        }
        item.setTags(tags);
        itemList.set(position, item);
    }

}

