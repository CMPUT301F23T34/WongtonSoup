package com.example.wongtonsoup;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for the current list of valid tags.
 * @author liamhouston
 * @version 1.0
 * @since 11/01/2023
 */
public class TagList implements Serializable {
    private ArrayList<Tag> current_tags;

    public TagList(Tag current_tag){
        this.current_tags = new ArrayList<Tag>();
        this.current_tags.add(current_tag);
    }

    public TagList(){
        this.current_tags = new ArrayList<Tag>();
    }

    /**
     * Add a tag to the list of valid tags. Throw an Illegal Argument if that tag already exists.
     * @param tag
     */
    public void addTag(Tag tag){
        // tag should not be currently present in the list
        if (find(tag) == -1) {
            current_tags.add(tag);
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Add a tag to the list of valid tags. Throw an Illegal Argument if that tag already exists.
     * @param tag_name
     */
    public void addTag(String tag_name){
        // tag should not be currently present in the list
        if (find(tag_name) == -1) {
            current_tags.add(new Tag(tag_name));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove a tag from the list of tags. Throw an Illegal Argument if that tag does not exist.
     * @param tag
     */
    public void removeTag(Tag tag){
        if (find(tag) != -1){
            current_tags.remove(find(tag));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Remove a tag with tag_name from the list of tags. Throw an Illegal Argument if that tag does not exist.
     * @param tag_name
     */
    public void removeTag(String tag_name){
        if (find(tag_name) != -1){
            current_tags.remove(find(tag_name));
            return;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Find a tag in list of tags. Return the index if it exists, -1 otherwise.
     * @param tag
     * @return the index of the tag in the list. -1 if it does not exist.
     */
    public int find(Tag tag){
        for (int i = 0 ; i < current_tags.size() ; i++){
            if(current_tags.get(i).compareTo(tag) == 0){
                // tags are equivalent (names are the same)
                return i;
            }
        }
        return -1;
    }

    /**
     * Find a tag with a tag_name in list of tags. Return the index if it exists, -1 otherwise.
     * @param tag_name
     * @return the index of the tag in the list. -1 if it does not exist.
     */
    public int find(String tag_name){
        for (int i = 0 ; i < current_tags.size() ; i++){
            if(current_tags.get(i).getName().toLowerCase().compareTo(tag_name.toLowerCase()) == 0){
                // tags are equivalent (names are the same)
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the list of tags.
     * @return list of tags.
     */
    public ArrayList<Tag> getTags(){
        return current_tags;
    }
}
