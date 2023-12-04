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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemIntentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testAddItem(){
        onView(withId(R.id.fab)).perform(click());
        // ensure correct view
        onView(withId(R.id.activity_add_edit)).check(matches(isDisplayed()));
        // test add
        // try with no input
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_add_edit)).check(matches(isDisplayed()));
        // edit values
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("Laptop"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_make)).perform(ViewActions.typeText("Dell"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_model)).perform(ViewActions.typeText("XPS 15"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.typeText("Work laptop with touch screen. Very large display and used by staff."));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_date)).perform(ViewActions.typeText("80-11-2023"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.typeText("1234567"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("1200"));
        closeSoftKeyboard();
        // try with invalid input
        onView(withId(R.id.add_edit_check)).perform(click());
        onView(withId(R.id.activity_add_edit)).check(matches(isDisplayed()));
        // re edit values
        onView(withId(R.id.add_edit_description)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_make)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_model)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_price)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_comment)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_serial)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_date)).perform(ViewActions.clearText());

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
        // try with valid input
        onView(withId(R.id.add_edit_check)).perform(click());
        closeSoftKeyboard();
        // ensure we are back in main
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        // ensure item added
        onView(withId(R.id.content_description)).check(matches(withText("Laptop")));
        onView(withId(R.id.content_make)).check(matches(withText("Dell")));
        onView(withId(R.id.content_date)).check(matches(withText("09-11-2023")));
        onView(withId(R.id.content_price)).check(matches(withText("1200.00")));
    }

    @Test
    public void testViewItem(){
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
        // go to view item
        onView(withId(R.id.get_item_info_button)).perform(click());
        // ensure correct view
        onView(withId(R.id.activity_view_item)).check(matches(isDisplayed()));
        // ensure data matches
        onView(withId(R.id.item_view_description)).check(matches(withText("Laptop")));
        onView(withId(R.id.view_serial)).check(matches(withText("1234567")));
        onView(withId(R.id.view_date)).check(matches(withText("09-11-2023")));
        onView(withId(R.id.view_comment)).check(matches(withText("Work laptop with touch screen")));
        onView(withId(R.id.view_model)).check(matches(withText("XPS 15")));
        onView(withId(R.id.view_make)).check(matches(withText("Dell")));
        onView(withId(R.id.item_view_price)).check(matches(withText("1200.0")));
        // ensure back button goes back to main
        onView(withId(R.id.view_back_button)).perform(click());
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
    }

    @Test
    public void testEditItem(){
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
        // go to view item
        onView(withId(R.id.get_item_info_button)).perform(click());
        // edit item
        onView((withId(R.id.edit_button))).perform(click());
        onView(withId(R.id.activity_add_edit)).check(matches(isDisplayed()));
        // ensure data is correct
        onView(withId(R.id.add_edit_description)).check(matches(withText("Laptop")));
        onView(withId(R.id.add_edit_serial)).check(matches(withText("1234567")));
        onView(withId(R.id.add_edit_date)).check(matches(withText("09-11-2023")));
        onView(withId(R.id.add_edit_comment)).check(matches(withText("Work laptop with touch screen")));
        onView(withId(R.id.add_edit_model)).check(matches(withText("XPS 15")));
        onView(withId(R.id.add_edit_make)).check(matches(withText("Dell")));
        onView(withId(R.id.add_edit_price)).check(matches(withText("1200.0")));
        // edit data
        onView(withId(R.id.add_edit_description)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_description)).perform(ViewActions.typeText("MyLaptop"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_price)).perform(ViewActions.clearText());
        onView(withId(R.id.add_edit_price)).perform(ViewActions.typeText("1450.45"));
        closeSoftKeyboard();
        onView(withId(R.id.add_edit_check)).perform(click());
        // ensure correct view
        onView(withId(R.id.activity_view_item)).check(matches(isDisplayed()));
        // ensure data is correct
        onView(withId(R.id.item_view_description)).check(matches(withText("MyLaptop")));
        onView(withId(R.id.view_serial)).check(matches(withText("1234567")));
        onView(withId(R.id.view_date)).check(matches(withText("09-11-2023")));
        onView(withId(R.id.view_comment)).check(matches(withText("Work laptop with touch screen")));
        onView(withId(R.id.view_model)).check(matches(withText("XPS 15")));
        onView(withId(R.id.view_make)).check(matches(withText("Dell")));
        onView(withId(R.id.item_view_price)).check(matches(withText("1450.45")));
    }
    @Test
    public void testDeleteItem(){
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
        // ensure correct view
        onView(withId(R.id.activity_view_item)).check(matches(isDisplayed()));
        // select item
        onView(withId(R.id.select)).perform(click());
        // delete item
        onView(withId(R.id.fab_delete)).perform(click());
        // ensure correct view
        onView(withId(R.id.activity_main)).check(matches(isDisplayed()));
        // ensure item is deleted
        onView(withId(R.id.content_description)).check(matches(withText("")));
        onView(withId(R.id.content_make)).check(matches(withText("")));
        onView(withId(R.id.content_date)).check(matches(withText("")));
    }



}
