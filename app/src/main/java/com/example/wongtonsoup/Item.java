package com.example.wongtonsoup;
import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Class for an item object
 * @author tyhu
 * @version 1.0
 * @since 10/25/2023
 */
public class Item implements Serializable {
    private String purchaseDate; // must be in format dd-mm-yyyy ( 11-9-2023 is invalid, should be 11-09-2023 )
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private Float value;
    private String comment;
    private TagList tags;

    /**
     * Constructs an item containing a serial number
     * @param purchaseDate
     * @param description
     * @param make
     * @param model
     * @param serialNumber
     * @param value
     * @param comment
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public Item(String purchaseDate, String description, String make, String model, Float value, String comment, String serialNumber) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.value = value;
        this.comment = comment;
        this.tags = new TagList();
        if (value < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Constructs an item without a serial number
     * @param purchaseDate
     * @param description
     * @param make
     * @param model
     * @param value
     * @param comment
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public Item(String purchaseDate, String description, String make, String model, Float value, String comment) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.value = value;
        this.comment = comment;
        if (value < 0){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Return comment
     * @return comment
     * @since 10/25/2023
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets comment
     * @param comment
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setComment(String comment) {
        if (comment == null){
            throw new IllegalArgumentException();
        }
        else {
            this.comment = comment;
        }
    }
    /**
     * Return value
     * @return value
     * @since 10/25/2023
     */
    public Float getValue() {
        return value;
    }

    public String getValueAsString() {
        return value.toString();
    }

    /**
     * Sets value
     * @param value
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setValue(Float value) {
        if (value == null){
            throw new IllegalArgumentException();
        }
        else if (value < 0){
            throw new IllegalArgumentException();
        }
        else {
            this.value = value;
        }
    }

    /**
     * Return serial number
     * @return serial number
     * @throws
     *      IllegalArgumentException if serialNumber is not initialized
     * @since 10/25/2023
     */
    public String getSerialNumber() {
        if (serialNumber == null) {
            throw new IllegalArgumentException();
        }
        else{
            return serialNumber;
        }
    }
    /**
     * Sets serial number
     * @param serialNumber
     * @since 10/25/2023
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    /**
     * Return model
     * @return model
     * @since 10/25/2023
     */
    public String getModel() {
        return model;
    }
    /**
     * Sets model
     * @param model
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setModel(String model) {
        if (model == null){
            throw new IllegalArgumentException();
        }
        else {
            this.model = model;
        }
    }
    /**
     * Return make
     * @return make
     * @since 10/25/2023
     */
    public String getMake() {
        return make;
    }
    /**
     * Sets make
     * @param make
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setMake(String make) {
        if (make == ""){
            throw new IllegalArgumentException();
        }
        else {
            this.make = make;
        }
    }
    /**
     * Return description
     * @return description
     * @since 10/25/2023
     */
    public String getDescription() {
        return description;
    }
    /**
     * Sets description
     * @param description
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setDescription(String description) {
        if (description == null){
            throw new IllegalArgumentException();
        }
        else {
            this.description = description;
        }
    }
    /**
     * Return purchase date
     * @return purchase date
     * @since 10/25/2023
     */
    public String getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets purchase date
     * @param purchaseDate
     * @throws IllegalArgumentException
     * @since 10/25/2023
     */
    public void setPurchaseDate(String purchaseDate) {
        if (isValidDate(purchaseDate)){
            this.purchaseDate = purchaseDate;
        }
        else {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Return tags
     * @return tags
     * @since 10/25/2023
     */
    public TagList getTags() {
        return tags;
    }

    /**
     * Validates a date
     * @param date
     * @return
     */
    public boolean isValidDate(String date) {
        // Define a regular expression for the dd_mm_yyyy format
        String datePattern = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$";

        // Check if the entered date matches the pattern
        if (!date.matches(datePattern)) {
            return false;
        }

        // Extract day, month, and year from the entered date
        String[] parts = date.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        // Check if the day is in the valid range (1 to 31) and the month is in the valid range (1 to 12)
        return (day >= 1 && day <= 31) && (month >= 1 && month <= 12);
    }


    public static Comparator<Item> byDate = new Comparator<Item>() {
        @Override
        public int compare(Item item1, Item item2) {
            String[] item1_parts = item1.getPurchaseDate().split("-");
            Integer day1 = Integer.parseInt(item1_parts[0]);
            Integer month1 = Integer.parseInt(item1_parts[1]);
            Integer year1 = Integer.parseInt(item1_parts[2]);
            // get item2 info
            String[] item2_parts = item2.getPurchaseDate().split("-");
            Integer day2 = Integer.parseInt(item2_parts[0]);
            Integer month2 = Integer.parseInt(item2_parts[1]);
            Integer year2 = Integer.parseInt(item2_parts[2]);

            if (year1.compareTo(year2) != 0)  return year1.compareTo(year2);
            if (month1.compareTo(month2) != 0) return month1.compareTo(month2);
            return day1.compareTo(day2);
        }
    };

    public static Comparator<Item> byDescription = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
           return o1.getDescription().toLowerCase().compareTo(o2.getDescription().toLowerCase());
        }
    };

    public static Comparator<Item> byMake = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
           return o1.getMake().toLowerCase().compareTo(o2.getMake().toLowerCase());
        }
    };

    public static Comparator<Item> byValue = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
           return o1.getValue().compareTo(o2.getValue());
        }
    };
}