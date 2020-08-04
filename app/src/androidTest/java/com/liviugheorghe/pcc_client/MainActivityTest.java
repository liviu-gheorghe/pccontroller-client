package com.liviugheorghe.pcc_client;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.liviugheorghe.pcc_client.ui.MainActivity;
import com.liviugheorghe.pcc_client.ui.QrCodeScannerActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class MainActivityTest {

    private static final int LONG_DELAY = 3500;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void userCanEnterIpAddress() {
        String ipAddress = "192.168.1.120";
        onView(withId(R.id.computer_ip_text_input)).perform(typeText(ipAddress));
        onView(withId(R.id.computer_ip_text_input)).check(matches(withText(ipAddress)));
    }

    @Test
    public void whenUserEntersInvalidIpAddress_toastWithWarningMessageIsShown() throws InterruptedException {
        String invalidIpAddress = "100.123.12.34";
        onView(withId(R.id.computer_ip_text_input)).perform(typeText(invalidIpAddress));
        onView(withId(R.id.connect_button)).perform(click());
        onView(withText(R.string.invalid_ip_address_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        Thread.sleep(LONG_DELAY);
    }

    @Test
    public void whenUserLeavesIpAddressBlank_toastWithWarningMessageIsShown() throws InterruptedException {
        onView(withId(R.id.connect_button)).perform(click());
        onView(withText(R.string.invalid_ip_address_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        Thread.sleep(LONG_DELAY);
    }


    @Test
    public void whenUserTriesToClickConnectButtonWhileAnotherConnectionIsEstablished_toastWithWarningMessageIsShown() throws InterruptedException {
        App.CONNECTION_ALIVE = true;
        onView(withId(R.id.connect_button)).perform(click());
        onView(withText(R.string.connection_already_established_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        App.CONNECTION_ALIVE = false;
        Thread.sleep(LONG_DELAY);
    }

    @Test
    public void whenUserTriesToClickScanQrCodeButtonWhileAnotherConnectionIsEstablished_toastWithWarningMessageIsShown() throws InterruptedException {
        App.CONNECTION_ALIVE = true;
        onView(withId(R.id.start_scan_button)).perform(click());
        onView(withText(R.string.connection_already_established_toast_text)).inRoot(withDecorView(not(rule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        App.CONNECTION_ALIVE = false;
        Thread.sleep(LONG_DELAY);
    }

    @Test
    public void whenUserTriesToClickScanQrCodeButtonWhileNoConnectionIsEstablished_QrCodeScannerActivityShouldBeLaunched() {
        Intents.init();
        onView(withId(R.id.start_scan_button)).perform(click());
        intended(hasComponent(QrCodeScannerActivity.class.getName()));
    }

}
