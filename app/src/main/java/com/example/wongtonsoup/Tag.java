package com.example.wongtonsoup;

// tags can be compared by their names
public class Tag implements Comparable<Tag> {
    private String name;

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
     * Tags are compared by their names.
     * @param t the tag to be compared.
     * @return
     */
    @Override
    public int compareTo(Tag t) {
        return this.name.compareTo(t.name);
    }
}
