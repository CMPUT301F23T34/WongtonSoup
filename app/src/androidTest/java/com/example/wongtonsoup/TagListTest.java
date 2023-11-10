package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TagListTest {
    @Test
    public void init_isCorrect(){
        // test single tag constructor
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);
        assertEquals(new_taglist.getTags().get(0).getName(), "new tag");
    }

    @Test
    public void find_isCorrect(){
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);

        assertEquals(new_taglist.find(new_tag), 0);
        assertEquals(new_taglist.find("nEw tag"), 0); // should find even with different case

        assertEquals(new_taglist.find("NEW tag-"), -1); // should not find this
    }

    @Test
    public void add_isCorrect(){
        TagList new_taglist = new TagList();
        Tag new_tag = new Tag("new tag");
        new_taglist.addTag(new_tag);
        assertEquals(new_taglist.getTags().get(0).getName(), "new tag");
    }

    @Test
    public void remove_isCorrect(){
        Tag new_tag = new Tag("new tag");
        TagList new_taglist = new TagList(new_tag);

        assertEquals(new_taglist.find("new TAG"), 0);
        new_taglist.removeTag("new TAG"); // tags are case sensitive

        assertEquals(new_taglist.getTags().size(), 0);

        assertThrows(IllegalArgumentException.class, () -> {
            new_taglist.removeTag(new_tag);
        });
    }

}
