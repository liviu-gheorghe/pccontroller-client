package com.liviugheorghe.pcc_client;

import androidx.test.rule.ActivityTestRule;

import com.liviugheorghe.pcc_client.ui.MainActivity;
import com.pccontroller.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void userCanEnterIpAddress() {
        String ipAddress = "192.168.1.120";
        onView(withId(R.id.computer_ip_text_input)).perform(typeText(ipAddress));
        onView(withId(R.id.computer_ip_text_input)).check(matches(withText(ipAddress)));
    }

    @Test
    public void whenUserEntersInvalidIpAddress_toastWithWarningMessageIsShown() {
        String invalidIpAddress = "100.123.12.34";
        onView(withId(R.id.computer_ip_text_input)).perform(typeText(invalidIpAddress));
        onView(withId(R.id.connect_button)).perform(click());
        onView(withText(R.string.invalid_ip_address_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    @Test
    public void whenUserLeavesIpAddressBlank_toastWithWarningMessageIsShown() {
        onView(withId(R.id.connect_button)).perform(click());
        onView(withText(R.string.invalid_ip_address_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }
}
