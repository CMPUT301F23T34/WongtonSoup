package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests item class
 * @author tyhu and lhouston
 * @version 1.0
 * @since 11/20/2023
 */
public class TagListTest {

    private FirebaseFirestore db;

    /**
     * Sets up the database instance variable
     */
    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Tests the initialization of tag lists
     */
    @Test
    public void init_isCorrect() {
        // test single tag constructor
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);
        assertEquals(new_taglist.getTags().get(0).getName(), "new tag");
    }

    /**
     * Tests find function
     */
    @Test
    public void find_isCorrect() {
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);

        assertEquals(new_taglist.find(new_tag), 0);
        assertEquals(new_taglist.find("nEw tag"), 0); // should find even with different case

        assertEquals(new_taglist.find("NEW tag-"), -1); // should not find this
    }

    /**
     * Tests add item function
     */
    @Test
    public void add_isCorrect() {
        TagList new_taglist = new TagList();
        Tag new_tag = new Tag("new tag");
        new_taglist.addTag(new_tag);
        assertEquals(new_taglist.getTags().get(0).getName(), "new tag");
    }

    /**
     * Tests remove item function
     */
    @Test
    public void remove_isCorrect() {
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);

        assertEquals(new_taglist.find("new TAG"), 0);
        new_taglist.removeTag("new TAG"); // tags are case sensitive

        assertEquals(new_taglist.getTags().size(), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new_taglist.removeTag(new_tag);
        });
    }

    /**
     * Tests converting item list to string
     */
    @Test
    public void toStringTest() {
        TagList new_taglist = new TagList();
        Tag new_tag = new Tag("new tag");
        new_taglist.addTag(new_tag);
        Tag new2_tag = new Tag("new new tag");
        new_taglist.addTag(new2_tag);
        String s = "new tag" + "\n" + "new new tag" + "\n";
        assertEquals(new_taglist.toString(), s);
    }

    /**
     * Tests updating tags in item list
     */
    /*@Test
    public void updateTagsInItemTest() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        TagList new_taglist = new TagList();
        Tag new_tag = new Tag("new tag");
        new_taglist.addTag(new_tag);
        // Create a new item
        HashMap<String, Object> newItem = new HashMap<>();
        newItem.put("DOP", "2023-11-01");
        newItem.put("comment", "Test Comment");
        newItem.put("make", "Test Make");
        newItem.put("model", "Test Model");
        newItem.put("name", "Test Name");
        newItem.put("serial", "1234567890");
        newItem.put("value", 1000.11);
        newItem.put("tags", new_taglist);

        // This will be a random value in our project
        String documentId = "TestItem1";

        Float f = new Float(10);
        Item i = new Item("xoxoxo", "01-01-2000", "This is a car", "Honda", "2016 CRV", f, "I like this car", "1234567890", "test_owner",  new TagList());

        Tag new2_tag = new Tag("new new tag");
        new_taglist.addTag(new2_tag);
        new_taglist.updateTagsInItem(i, new_taglist);

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
                                    assertEquals(new_taglist, document.get("tags").getTags());

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
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }*/

}
