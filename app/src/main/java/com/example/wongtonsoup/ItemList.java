package com.example.wongtonsoup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
        Item currentItem = this.items.get(position);

        // Set the image, content, and tags in the layout
        assert convertView != null;
        ImageView imageView = convertView.findViewById(R.id.imageView);
        imageView.setImageResource(currentItem.getImageResource());

        TextView textViewName = convertView.findViewById(R.id.ItemName);
        textViewName.setText(currentItem.getName());

        TextView textViewDate = convertView.findViewById(R.id.ItemDate);
        textViewDate.setText(currentItem.getPurchaseDate());

        TextView textViewMake = convertView.findViewById(R.id.ItemMake);
        textViewMake.setText(currentItem.getPurchaseDate());

//        TextView textViewTag1 = convertView.findViewById(R.id.textViewTag1);
//        textViewTag1.setText(currentItem.getTag1());
//
//        TextView textViewTag2 = convertView.findViewById(R.id.textViewTag2);
//        textViewTag2.setText(currentItem.getTag2());

        return view;
    }


}
