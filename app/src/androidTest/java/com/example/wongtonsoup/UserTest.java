package com.example.wongtonsoup;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Map;

/**
 * Tests creating user profiles
 * @since 11/02/2023
 */
public class UserTest {

    /**
     * Tests creating a user profile
     */
    @Test
    public void createUserTest(){
        User user = new User("abcdefghuj", "12-10-2023", "Tony");
        assertEquals(user.getUID(), "abcdefghuj");
        assertEquals(user.getJoined(), "12-10-2023");
        assertEquals(user.getName(), "Tony");
    }

    /**
     * Tests toMap function for user profiles
     */
    @Test
    public void toMapTest(){
        User user = new User("abcdefghuj", "12-10-2023", "Tony");
        Map<String, Object> map = user.toMap();
        for (Map.Entry<String, Object> pair : map.entrySet()) {
            if (pair.getKey() == "name") {
                assertEquals(pair.getValue(), "Tony");
            }
            else {
                assertEquals(pair.getValue(), "12-10-2023");
            }
        }

    }
}
