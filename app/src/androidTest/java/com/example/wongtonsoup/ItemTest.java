package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;

/**
 * Tests item class
 * @author tyhu
 * @version 1.0
 * @since 11/02/2023
 */
public class ItemTest {
    private Item i;

    /**
     * Tests creating item and getting/setting values
     * @since 11/02/2023
     */
    @Test
    public void testAdd(){
        Float f = new Float(10);
        i = new Item("xoxoxo", "01-01-2000", "This is a car", "Honda", "2016 CRV", f, "I like this car", "test_owner",  new TagList());
        assertThrows(IllegalArgumentException.class, () -> {
            i.getSerialNumber();
        });
        i.setSerialNumber("21348193824y9824-");
        assertEquals("21348193824y9824-", i.getSerialNumber());
        Float f2 = new Float(-1);
        assertThrows(IllegalArgumentException.class, () -> {
            i.setValue(f2);
        });
    }

    /**
     * Tests checking if a given date for an item is valid or not
     * @since 12/02/2023
     */
    @Test
    public void isValidDateTest(){
        Float f = new Float(10);
        i = new Item("xoxoxo", "01-01-2000", "This is a car", "Honda", "2016 CRV", f, "I like this car", "test_owner",  new TagList());
        String s = "10-12-2023";
        assertTrue(i.isValidDate(s));
        s = "10-13-2023";
        assertFalse(i.isValidDate(s));
    }
}
