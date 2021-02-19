package com.max.app.bignum;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigNumTest {

    private static final int NUMBERS_RANGE = '9' - '0' + 1;

    private static final Random RAND = new Random();

    @Test
    public void createFromInt() {
        for (int it = 0; it < 1_000; ++it) {
            int randValue = RAND.nextInt();
            BigNum bigNum = new BigNum(randValue);

            assertThat(bigNum.toBigInt()).isEqualTo(BigInteger.valueOf(randValue));
        }
    }

    @Test
    public void createFromLong() {
        for (int it = 0; it < 1_000; ++it) {
            long randValue = RAND.nextLong();
            BigNum bigNum = new BigNum(randValue);

            assertThat(bigNum.toBigInt()).isEqualTo(BigInteger.valueOf(randValue));
        }
    }

    @Test
    public void createBigNumberFromString() {

        String initialValue = "51090942171709440000";
        BigNum value = new BigNum("51090942171709440000");

        assertThat(value.toBigInt()).isEqualTo(new BigInteger(initialValue));
    }

    @Test
    public void createRandomVeryBigNumbers() {
        for (int it = 0; it < 1000; ++it) {
            String numberStr = createRandomNumberString(10 + RAND.nextInt(1000));

            BigNum num = new BigNum(numberStr);

            assertThat(num.toBigInt()).isEqualTo(new BigInteger(numberStr));

        }
    }


    private static String createRandomNumberString(int length) {
        char[] arr = new char[length];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = (char) ('0' + RAND.nextInt(NUMBERS_RANGE));
        }
        return new String(arr);
    }


}
