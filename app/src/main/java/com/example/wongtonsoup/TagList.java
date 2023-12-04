package com.example.wongtonsoup;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Class for the current list of valid tags.
 * @author liamhouston
 * @version 1.0
 * @since 11/01/2023
 */
public class TagList implements Iterable<Tag>, Serializable {
    private ArrayList<Tag> current_tags;

    public TagList(Tag current_tag) {
        this.current_tags = new ArrayList<Tag>();
    }

    public TagList() {
        this.current_tags = new ArrayList<Tag>();
    }

    /**
     * Add a tag to the list of valid tags. Throw an Illegal Argument if that tag already exists.
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        // tag should not be currently present in the list
        if (find(tag) == -1) {
            current_tags.add(tag);
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Add a tag to the list of valid tags. Throw an Illegal Argument if that tag already exists.
     *
     * @param tag_name
     */
    public void addTag(String tag_name) {
        // tag should not be currently present in the list
        if (find(tag_name) == -1) {
            current_tags.add(new Tag(tag_name));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove a tag from the list of tags. Throw an Illegal Argument if that tag does not exist.
     *
     * @param tag
     */
    public void removeTag(Tag tag) {
        if (find(tag) != -1) {
            current_tags.remove(find(tag));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove a tag with tag_name from the list of tags. Throw an Illegal Argument if that tag does not exist.
     *
     * @param tag_name
     */
    public void removeTag(String tag_name) {
        if (find(tag_name) != -1) {
            current_tags.remove(find(tag_name));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Find a tag in list of tags. Return the index if it exists, -1 otherwise.
     *
     * @param tag
     * @return the index of the tag in the list. -1 if it does not exist.
     */
    public int find(Tag tag) {
        for (int i = 0; i < current_tags.size(); i++) {
            if (current_tags.get(i).compareTo(tag) == 0) {
                // tags are equivalent (names are the same)
                return i;
            }
        }
        return -1;
    }

    /**
     * Find a tag with a tag_name in list of tags. Return the index if it exists, -1 otherwise.
     *
     * @param tag_name
     * @return the index of the tag in the list. -1 if it does not exist.
     */
    public int find(String tag_name) {
        for (int i = 0; i < current_tags.size(); i++) {
            if (current_tags.get(i).getName().toLowerCase().compareTo(tag_name.toLowerCase()) == 0) {
                // tags are equivalent (names are the same)
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the list of tags.
     *
     * @return list of tags.
     */
    public ArrayList<Tag> getTags() {
        return current_tags;
    }

    /**
     * iterator for the tag list
     *
     * @return iterator
     */
    @Override
    public java.util.Iterator<Tag> iterator() {
        return current_tags.iterator();
    }

    /**
     * print the list of tags
     *
     * @return string representation of the list of tags
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < current_tags.size(); i++) {
            s += current_tags.get(i).getName() + "\n";
        }
        return s;
    }
    /**
     * Add a tag to the list of tags, db version
     * @param tag
     * @since 10/25/2023
     */
    public static void addTagDB(Tag tag) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tagsRef = db.collection("tags");

        String tagName = tag.getName();
        String id = tag.getUuid();
        String owner = tag.getOwner();

        tagsRef.whereEqualTo("name", tagName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    Map<String, Object> db_tag = new HashMap<>();
                    db_tag.put("name", tagName);
                    db_tag.put("id", id);
                    db_tag.put("owner", owner);
                    tagsRef.add(db_tag);
                }
            }
        });
    }
    /**
     * update tags in an item, db version
     * @param item, tags
     * @since 10/25/2023
     */
    public void updateTagsInItem(Item item, TagList new_selected_tags) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .document(item.getSerialNumberAsString())
                .update("tags", new_selected_tags)
                .addOnSuccessListener(aVoid -> Log.d("Item", "Item successfully updated!"))
                .addOnFailureListener(e -> Log.w("Item", "Error updating item", e));
    }
    /**
     * Delete tag from an item, db version
     * @param item, tag
     * @since 10/25/2023
     */
    public void deleteTagFromItem(Item item, Tag tag) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items")
                .document(item.getSerialNumberAsString())
                .update("tags", tag)
                .addOnSuccessListener(aVoid -> Log.d("Item", "Item successfully updated!"))
                .addOnFailureListener(e -> Log.w("Item", "Error updating item", e));
    }

    /**
     * delete a tag from the list of tags, db version
     * @param tagName
     * @since 10/25/2023
     */
    public void deleteTagDB(String tagName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tagsRef = db.collection("tags");
        tagsRef.whereEqualTo("name", tagName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    tagsRef.document(task.getResult().getDocuments().get(0).getId()).delete();
                }
            }
        });
    }



}


