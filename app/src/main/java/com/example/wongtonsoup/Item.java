package com.example.wongtonsoup;
import java.util.Date;

/**
 * Class for an item object
 * @author tyhu
 * @version 1.0
 * @since 10/25/2023
 */
public class Item {
    private Date purchaseDate;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private Float value;
    private String comment;

    /**
     * Constructs an item containing a serial number
     * @param purchaseDate
     * @param description
     * @param make
     * @param model
     * @param serialNumber
     * @param value
     * @param comment
     * @since 10/25/2023
     */
    public Item(Date purchaseDate, String description, String make, String model, String serialNumber, Float value, String comment) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.value = value;
        this.comment = comment;
    }

    /**
     * Constructs an item without a serial number
     * @param purchaseDate
     * @param description
     * @param make
     * @param model
     * @param value
     * @param comment
     * @since 10/25/2023
     */
    public Item(Date purchaseDate, String description, String make, String model, Float value, String comment) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.value = value;
        this.comment = comment;
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
     * @since 10/25/2023
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * Return value
     * @return value
     * @since 10/25/2023
     */
    public Float getValue() {
        return value;
    }
    /**
     * Sets value
     * @param value
     * @since 10/25/2023
     */
    public void setValue(Float value) {
        this.value = value;
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
     * @since 10/25/2023
     */
    public void setModel(String model) {
        this.model = model;
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
     * @since 10/25/2023
     */
    public void setMake(String make) {
        this.make = make;
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
     * @since 10/25/2023
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Return purchase date
     * @return purchase date
     * @since 10/25/2023
     */
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    /**
     * Sets purchase date
     * @param purchaseDate
     * @since 10/25/2023
     */
    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

}


