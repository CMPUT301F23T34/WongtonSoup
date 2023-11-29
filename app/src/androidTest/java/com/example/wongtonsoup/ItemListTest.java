package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Ordering;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Tests item class
 * @author linaaman
 * @version 1.0
 * @since 11/02/2023
 */
@RunWith(AndroidJUnit4.class)
public class ItemListTest{
    private Context appContext;

    @Before
    public void setup() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    /**
     * Tests creating item and getting/setting values
     * @since 11/02/2023
     */
    @Test
    public void testUpdateData() {
        // Create an atomic boolean to track if the listener was notified
        AtomicBoolean notified = new AtomicBoolean(false);

        // Create a listener for item list changes
        ItemList.ItemListListener listener = new ItemList.ItemListListener() {
            @Override
            public void onItemListChanged() {
                // This is called when the item list changes
                notified.set(true);
            }
        };

        List<Item> initialItems = new ArrayList<>();
        ItemList itemList = new ItemList(appContext, initialItems);
        itemList.setListener(listener);

        // Create a new list of items
        List<Item> updatedItems = new ArrayList<>();
        float f = new Float(100);
        float f2 = new Float(200);
        TagList tagList = new TagList();
        tagList.addTag("car");
        tagList.addTag("vehicle");
        updatedItems.add(new Item("01-01-2000", "Honda car", "Honda", "2016 CRV", f, "I like this car", "1234",tagList, tagList));
        updatedItems.add(new Item("01-02-2000", "Chevy car", "chevrolet ", "Corvette", f2, "I really like this car", "1234",tagList, tagList));

        // Update the data in the adapter
        itemList.updateData(updatedItems);

        // Check if the listener was notified
        assertTrue(notified.get());

    }

    /**
     * Tests sorting items by date
     */
    @Test
    public void testSortByDate() {
        // Create an item list and add items with different dates
        List<Item> items = new ArrayList<>();
        float f = new Float(100);
        float f2 = new Float(200);
        TagList tagList = new TagList();
        tagList.addTag("car");
        tagList.addTag("vehicle");
        items.add(new Item("01-01-2000", "Honda car", "Honda", "2016 CRV", f, "I like this car", "1234",tagList, tagList));
        items.add(new Item("01-02-2000", "Chevy car", "Chevrolet ", "Corvette", f2, "I really like this car", "1234",tagList, tagList));

        ItemList itemList = new ItemList(appContext, items);

        // Sort the items by date
        List<Item> sortedItems = itemList.sortByDate();

        // Check if the items are sorted correctly based on the date
        assertEquals(items.get(0), sortedItems.get(0));

        // You can add more assertions based on your sorting criteria
    }

    /**
     * Tests deleting items
     */
    @Test
    public void testDeleteSelectedItems() {
        // Create an item list and mark some items as selected
        List<Item> items = new ArrayList<>();
        float f = new Float(100);
        float f2 = new Float(200);
        TagList tagList = new TagList();
        tagList.addTag("car");
        tagList.addTag("vehicle");
        items.add(new Item("01-01-2000", "Honda car", "Honda", "2016 CRV", f, "I like this car","1234", tagList, tagList));
        items.add(new Item("01-02-2000", "Chevy car", "Chevrolet ", "Corvette", f2, "I really like this car", "1235", tagList, tagList));

        ItemList itemList = new ItemList(appContext, items);

        // Mark the first item as selected
        items.get(0).setSelected(true);

        // Call deleteSelectedItems
        itemList.deleteSelectedItems();

        // Check if the selected item is removed from the list
        assertFalse(itemList.getItem(0).isSelected());
    }

    /**
     * Tests clearing selection
     */
    @Test
    public void testClearSelection() {
        // Create an item list and mark some items as selected
        List<Item> items = new ArrayList<>();
        float f = new Float(100);
        float f2 = new Float(200);
        TagList tagList = new TagList();
        tagList.addTag("car");
        tagList.addTag("vehicle");
        items.add(new Item("01-01-2000", "Honda car", "Honda", "2016 CRV", f, "I like this car","1234", tagList, tagList));
        items.add(new Item("01-02-2000", "Chevy car", "Chevrolet ", "Corvette", f2, "I really like this car","1235", tagList, tagList));

        ItemList itemList = new ItemList(appContext, items);

        // Mark both items as selected
        items.get(0).setSelected(true);
        items.get(1).setSelected(true);

        // Call clearSelection
        //itemList.clearSelection();

        // Check if the selection is cleared for all items
        assertFalse(itemList.getItem(0).isSelected());
        assertFalse(itemList.getItem(1).isSelected());

        // You can add more assertions based on your use case
    }

    // Add more tests for other methods in the ItemList class

}