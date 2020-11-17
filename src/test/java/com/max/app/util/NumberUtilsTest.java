package com.max.app.util;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NumberUtilsTest {

    @ParameterizedTest
    @ValueSource(ints = {12321, 22, 54745, 3, 0, 2147447412, 9})
    public void isPalindromeTrue(int value) {
        assertThat(NumberUtils.isPalindrome1(value)).as("isPalindrome1").isTrue();
        assertThat(NumberUtils.isPalindrome2(value)).as("isPalindrome2").isTrue();
        assertThat(NumberUtils.isPalindrome3(value)).as("isPalindrome3").isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {234532, 23, 10, -10, -22, -232, -2233, Integer.MIN_VALUE, Integer.MAX_VALUE})
    public void isPalindromeFalse(int value) {
        assertThat(NumberUtils.isPalindrome1(value)).as("isPalindrome1").isFalse();
        assertThat(NumberUtils.isPalindrome2(value)).as("isPalindrome2").isFalse();
        assertThat(NumberUtils.isPalindrome3(value)).as("isPalindrome3").isFalse();
    }

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @RepeatedTest(100)
    public void checkAllMethodConsistent() {
        int value = RAND.nextInt();

        boolean res1 = NumberUtils.isPalindrome1(value);
        boolean res2 = NumberUtils.isPalindrome2(value);
        boolean res3 = NumberUtils.isPalindrome3(value);

        assertThat(res1).as("isPalindrome1 != isPalindrome2 for value: %s", value).isEqualTo(res2);
        assertThat(res2).as("isPalindrome2 != isPalindrome3 for value: %s", value).isEqualTo(res3);

    }
}
