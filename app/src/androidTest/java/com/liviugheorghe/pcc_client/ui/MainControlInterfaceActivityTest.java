package com.liviugheorghe.pcc_client.ui;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.liviugheorghe.pcc_client.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


public class MainControlInterfaceActivityTest {

    @Rule
    public ActivityTestRule<MainControlInterfaceActivity> rule = new ActivityTestRule<>(MainControlInterfaceActivity.class);

    @Test
    public void whenUserClicksTouchpadActivityButton_TouchpadActivityShouldBeLaunched() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.open_touchpad)).perform(scrollTo(), click());
        intended(hasComponent(TouchpadActivity.class.getName()));
        Intents.release();
    }


    @Test
    public void whenUserClicksDisconnectButton_MainActivityShouldBeLaunched() throws InterruptedException {
        Intents.init();
        onView(withId(R.id.action_disconnect)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }
}