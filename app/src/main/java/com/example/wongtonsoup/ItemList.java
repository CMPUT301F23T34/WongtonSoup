package com.example.wongtonsoup;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

/**
 * Class for the list of item
 * @author yihui
 * @version 1.0
 * @since 10/28/2023
 */
public class ItemList extends ArrayAdapter<Item> {

    private final Context context;
    private final ArrayList<Item> items;
    public ItemList(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent){

        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        // Get the item at the current position
        Item currentItem = items.get(position);

        // Log position and other relevant information
        Log.d("ItemAdapter", "getView - position: " + position);

        // Set tags in the layout
        ChipGroup chipGroup = convertView.findViewById(R.id.content_chip_group);
        chipGroup.removeAllViews();
        for (Tag tag : currentItem.getTags().getTags()) {
            Chip chip = new Chip(context);
            chip.setText(tag.getName());
            chipGroup.addView(chip);
        }


        // Set the image, content, and tags in the layout
        assert convertView != null;
//        ImageView imageView = view.findViewById(R.id.photo);
//        imageView.setImageResource(currentItem.getImageResource());

        TextView textViewName = view.findViewById(R.id.content_description);
        textViewName.setText(currentItem.getDescription());

        TextView textViewDate = view.findViewById(R.id.content_date);
        textViewDate.setText(currentItem.getPurchaseDate());

        TextView textViewMake = view.findViewById(R.id.content_make);
        textViewMake.setText(currentItem.getMake());

        TextView testViewPrice = view.findViewById(R.id.content_price);
        testViewPrice.setText(currentItem.getValueAsString());

//        TextView textViewTag1 = convertView.findViewById(R.id.textViewTag1);
//        textViewTag1.setText(currentItem.getTag1());
//
//        TextView textViewTag2 = convertView.findViewById(R.id.textViewTag2);
//        textViewTag2.setText(currentItem.getTag2());

        return view;
    }


}
