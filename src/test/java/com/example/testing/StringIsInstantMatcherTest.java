package com.example.testing;

import org.hamcrest.Matcher;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringIsInstantMatcherTest {

    private final Matcher<String> matcher = new StringIsInstantMatcher();

    static Stream<Arguments> matches() {
        return Stream.of(
                Arguments.of("2023-02-12T02:27:10.025+00:00"),
                Arguments.of(Instant.now().toString())
        );
    }

    @ParameterizedTest
    @MethodSource("matches")
    void matchesSafely(final String string) {
        assertThat(matcher.matches(string)).isTrue();
    }

    static Stream<Arguments> doesNotMatches() {
        return Stream.of(Arguments.of("not-a-timestamp"));
    }

    @ParameterizedTest
    @MethodSource("doesNotMatches")
    @NullAndEmptySource
    void doesNotMatchesSafely(final String string) {
        assertThat(matcher.matches(string)).isFalse();
    }
}
