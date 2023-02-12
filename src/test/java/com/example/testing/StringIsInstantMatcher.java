package com.example.testing;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.time.Instant;

public final class StringIsInstantMatcher extends TypeSafeMatcher<String> {

    @Override
    public boolean matchesSafely(String string) {
        if (string == null) {
            return false;
        }

        try {
            Instant.parse(string);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Is a String that can be parsed to java.util.Instant");
    }
}
