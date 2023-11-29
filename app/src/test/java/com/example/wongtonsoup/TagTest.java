package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TagTest {
    @Test
    public void init_isCorrect() {
        Tag new_tag = new Tag("Tag-1A");
        assertEquals(new_tag.getName(), "Tag-1A");
    }

    @Test
    public void sorting_isCorrect() {
        Tag main_tag = new Tag("tag-1A");

        Tag lexically_before = new Tag("Alphabetically before Tag-1A");
        assertTrue(main_tag.compareTo(lexically_before) > 0);

        Tag lexically_same = new Tag("Tag-1A");
        assertTrue(main_tag.compareTo(lexically_same) == 0);

        Tag lexically_after = new Tag("ZZZ- Alphabetically after Tag-1A");
        assertTrue(main_tag.compareTo(lexically_after) < 0);


    }
}
