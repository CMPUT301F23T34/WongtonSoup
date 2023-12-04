package com.example.wongtonsoup;

// tags can be compared by their names

import java.io.Serializable;
import java.util.UUID;

/**
 * Class for a tag.
 * @author liamhouston
 * @version 1.0
 * @since 10/30/2023
 */
public class Tag implements Comparable<Tag>, Serializable {
    private String name;
    private String uuid;
    private String owner;

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tags are compared by their names. Comparison is not case-sensitive ("aab" == "AaB").
     * @param t the tag to be compared.
     * @return
     */
    @Override
    public int compareTo(Tag t) {
        // assume we have tag objects tag1, tag2. tag1.name = 'first', tag2.name = 'last'.
        // -- tag1.compareTo(tag2) will return positive
        // -- tag1.compareTo(tag1) will return zero
        // -- tag2.compareTo(tag1) will return negative
        return this.name.toLowerCase().compareTo(t.name.toLowerCase());
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
