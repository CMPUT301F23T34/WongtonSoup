package com.example.wongtonsoup;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    private FirebaseFirestore db;

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
    }

    @Test
    public void test1FirestoreItemCreation() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        // Create a new item
        HashMap<String, Object> newItem = new HashMap<>();
        newItem.put("DOP", "2023-11-01");
        newItem.put("comment", "Test Comment");
        newItem.put("make", "Test Make");
        newItem.put("model", "Test Model");
        newItem.put("name", "Test Name");
        newItem.put("serial", "1234567890");
        newItem.put("value", 1000.11);

        // This will be a random value in our project
        String documentId = "TestItem1";

        db.collection("items").document(documentId).set(newItem)
                .addOnSuccessListener(aVoid -> {
                    // Document added, now try to fetch it
                    db.collection("items").document(documentId).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assertNotNull(document);
                                    assertTrue(document.exists());

                                    // Assertions to confirm data integrity
                                    assertEquals("2023-11-01", document.getString("DOP"));
                                    assertEquals("Test Comment", document.getString("comment"));
                                    assertEquals("Test Make", document.getString("make"));
                                    assertEquals("Test Model", document.getString("model"));
                                    assertEquals("Test Name", document.getString("name"));
                                    assertEquals("1234567890", document.getString("serial"));
                                    assertEquals(1000.11, document.getDouble("value"), 0.0);

                                    latch.countDown();
                                } else {
                                    fail("Failed to fetch document");
                                    latch.countDown();
                                }
                            })
                            .addOnFailureListener(e -> {
                                fail("Exception on fetching document: " + e.getMessage());
                                latch.countDown();
                            });
                })
                .addOnFailureListener(e -> {
                    fail("Failed to write document: " + e.getMessage());
                    latch.countDown();
                });

        // Wait for the async operations
        if (!latch.await(10, TimeUnit.SECONDS)) {
            fail("Latch timeout");
        }
    }

    @Test
    public void test2FirestoreItemEdit() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        String documentId = "TestItem1";

        HashMap<String, Object> updatedItem = new HashMap<>();
        updatedItem.put("value", 0.0);

        db.collection("items").document(documentId).update(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    // After update, fetch document to verify update
                    db.collection("items").document(documentId).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assertNotNull(document);
                                    assertTrue(document.exists());

                                    // Check if the "value" field is updated
                                    assertEquals(0.0, document.getDouble("value"), 0.0);

                                    latch.countDown();
                                } else {
                                    fail("Failed to fetch document after update");
                                    latch.countDown();
                                }
                            })
                            .addOnFailureListener(e -> {
                                fail("Exception on fetching document after update: " + e.getMessage());
                                latch.countDown();
                            });
                })
                .addOnFailureListener(e -> {
                    fail("Failed to update document: " + e.getMessage());
                    latch.countDown();
                });

        // Wait for async operation
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void test3FirestoreItemDeletion() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        String documentId = "TestItem1";

        db.collection("items").document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("items").document(documentId).get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    assertFalse(document.exists());
                                } else {
                                    fail("Failed during deletion validation");
                                }
                                latch.countDown();
                            })
                            .addOnFailureListener(e -> {
                                fail("Exception on validation after deletion: " + e.getMessage());
                                latch.countDown();
                            });
                })
                .addOnFailureListener(e -> {
                    fail("Failed to delete document: " + e.getMessage());
                    latch.countDown();
                });

        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

}
