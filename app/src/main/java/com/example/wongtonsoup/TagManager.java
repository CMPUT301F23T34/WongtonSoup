package com.example.wongtonsoup;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TagManager {
    private static TagManager instance;
    private TagList existingTags;
    private static CollectionReference tagsRef = FirebaseFirestore.getInstance().collection("tags");

    private TagManager() {
        // Initialize existing tags asynchronously
        fetchExistingTagsFromFirestore();
    }

    public static synchronized TagManager getInstance() {
        if (instance == null) {
            instance = new TagManager();
        }
        return instance;
    }

    private void fetchExistingTagsFromFirestore() {
        existingTags = new TagList(); // Initialize the tag list

        // Fetch existing tags from Firestore
        tagsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Assuming 'name' is a field in your 'tags' collection
                String tagName = documentSnapshot.getString("name");
                existingTags.addTag(tagName);
            }
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.e("TagManager", "Failed to fetch existing tags", e);
        });
    }


    public TagList getExistingTags() {
        return existingTags;
    }
}

