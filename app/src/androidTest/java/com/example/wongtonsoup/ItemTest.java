package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.util.Date;

public class ItemTest {
    private Item i;
    @Test
    public void testAdd(){
        Date d = new Date();
        Float f = new Float(10);
        i = new Item(d, "This is a car", "Honda", "2016 CRV", f, "I like this car");
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


}
