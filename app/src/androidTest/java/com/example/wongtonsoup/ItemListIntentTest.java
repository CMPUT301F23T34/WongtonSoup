package com.example.wongtonsoup;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

/**
 * Intent and UI testing for item list
 */
public class ItemListIntentTest {

    /**
     * Sets up testing functionality
     */
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Test to see if the estimated value displayed is correct
     */
    @Test
    public void estimatedValueTest(){
        // add item
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("Laptop"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_make)).perform(ViewActions.typeText("Dell"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_model)).perform(ViewActions.typeText("XPS 15"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.typeText("Work laptop with touch screen"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_date)).perform(ViewActions.typeText("09-11-2023"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.typeText("1234567"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("1200"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("Phone"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_make)).perform(ViewActions.typeText("Apple"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_model)).perform(ViewActions.typeText("12"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.typeText("Work phone"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_date)).perform(ViewActions.typeText("10-11-2023"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.typeText("7654321"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("300"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        onView(withId(R.id.estimated_value)).check(matches(withText("1500.00")));
    }

    /**
     * Test sorting items in item list
     */
    @Test
    public void sortingItemsTest(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("Laptop"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_make)).perform(ViewActions.typeText("Dell"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_model)).perform(ViewActions.typeText("XPS 15"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.typeText("Work laptop with touch screen"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_date)).perform(ViewActions.typeText("09-11-2023"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.typeText("1234567"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("1200"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("Phone"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_make)).perform(ViewActions.typeText("Apple"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_model)).perform(ViewActions.typeText("12"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.typeText("Work phone"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_date)).perform(ViewActions.typeText("10-11-2023"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.typeText("7654321"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("300"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        onView(withId(R.id.expand_search_button)).perform(click());
        onView(withId(R.id.sort_date)).perform(click());
        onView(withId(R.id.expand_search_button)).perform(click());
        onView(withId(R.id.content_description)).check(matches(withText("Laptop")));
    }
}
