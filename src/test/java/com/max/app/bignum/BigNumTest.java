package com.max.app.bignum;

import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigNumTest {

    private static final int NUMBERS_RANGE = '9' - '0' + 1;

    private static final Random RAND = new Random();

    @Test
    public void test1(){
        // 1152921509975556099
        BigNum value = new BigNum("1152921509975556099");

        int x =133;
    }

    @Test
    public void addRandomPositiveValues() {

        for (int it = 0; it < 1000; ++it) {
            BigNum first = new BigNum(createRandomNumberString(5 + RAND.nextInt(100)));
            BigNum second = new BigNum(createRandomNumberString(3 + RAND.nextInt(100)));

            BigNum sum = first.add(second);

            assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
        }
    }

    @Test
    public void addRandomNegativeValues() {

        for (int it = 0; it < 1000; ++it) {
            BigNum first = new BigNum("-" + createRandomNumberString(5 + RAND.nextInt(100)));
            BigNum second = new BigNum("-" + createRandomNumberString(3 + RAND.nextInt(100)));

            BigNum sum = first.add(second);

            assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
        }
    }

    @Test
    public void addZeroToRandomValue() {

        for (int it = 0; it < 1000; ++it) {

            BigNum first;
            BigNum second;

            if (RAND.nextBoolean()) {
                first = new BigNum("0");
                second = new BigNum(createRandomNumberString(3 + RAND.nextInt(100)));
            }
            else {
                first = new BigNum(createRandomNumberString(3 + RAND.nextInt(100)));
                second = new BigNum("0");
            }

            BigNum sum = first.add(second);

            assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
        }
    }

    private static BigInteger addBigIntegers(BigInteger first, BigInteger second) {
        return first.add(second);
    }

    @Test
    public void createZero() {
        BigNum zero = new BigNum("0");
        assertThat(zero.toBigInt()).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void createPositiveZero() {
        BigNum zero = new BigNum("+0");
        assertThat(zero.toBigInt()).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void createNegativeZero() {
        BigNum zero = new BigNum("-0");
        assertThat(zero.toBigInt()).isEqualTo(BigInteger.ZERO);
    }

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