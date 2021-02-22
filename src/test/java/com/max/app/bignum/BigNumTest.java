package com.max.app.bignum;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BigNumTest {

    private static final int NUMBERS_RANGE = '9' - '0' + 1;

    private static final Random RAND = new Random();

//    @Test
//    public void test123() {
//        BigNum first =
//                new BigNum
//               ("125379294290042010653340352469359972299657416718126393366087281868076370408568694633010512382615");
//        BigNum second =
//                new BigNum("-5380619368869818099777344962590138365660376222643867468635475812304222376482591177868");
//
//        BigNum sum = first.add(second);
//
//        assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
//
//        // actual   => 125379294284661391284472017060426043764665417571192802166930546501644694449420103347099101974027
//        // expected => 125379294284661391284470534369582627337067278352466017143443414399440894596264472256527921204747
//    }

    @Test
    public void subRandomValues() {

        for (int it = 0; it < 10_000; ++it) {

            BigNum first = new BigNum((RAND.nextBoolean() ? "" : "-") + createRandomNumberString(5 + RAND.nextInt(100)));
            BigNum second = new BigNum((RAND.nextBoolean() ? "" : "-") + createRandomNumberString(5 + RAND.nextInt(100)));

            BigNum result = first.sub(second);

            assertThat(result.toBigInt()).isEqualTo(subBigIntegers(first.toBigInt(), second.toBigInt()));
        }
    }


    @Test
    public void addPositiveWithNegative() {

        BigNum first = new BigNum("7646");
        BigNum second = new BigNum("-49946");

        BigNum sum = first.add(second);

        assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));

        first = new BigNum("5552275433");
        second = new BigNum("-31371");

        sum = first.add(second);

        assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
    }

    @Test
    public void addNegativeWithPositive() {

        BigNum first = new BigNum("-7646");
        BigNum second = new BigNum("49946");

        BigNum sum = first.add(second);

        assertThat(sum.toBigInt()).isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
    }

    @Test
    public void addRandomPositiveAndNegativeValues() {

        for (int it = 0; it < 10_000; ++it) {

            BigNum first = new BigNum(createRandomNumberString(5 + RAND.nextInt(100)));

            BigNum second = new BigNum("-" + createRandomNumberString(3 + RAND.nextInt(100)));

            BigNum sum = first.add(second);

            assertThat(sum.toBigInt()).
                    as(String.format("first: %s, second: %s", first.toString(), second.toString())).
                    isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
        }
    }

    @Test
    public void addRandomNegativeAndPositiveValues() {

        for (int it = 0; it < 10_000; ++it) {

            BigNum first = new BigNum("-" + createRandomNumberString(5 + RAND.nextInt(100)));

            BigNum second = new BigNum(createRandomNumberString(3 + RAND.nextInt(100)));

            BigNum sum = first.add(second);

            assertThat(sum.toBigInt()).
                    as(String.format("first: %s, second: %s", first.toString(), second.toString())).
                    isEqualTo(addBigIntegers(first.toBigInt(), second.toBigInt()));
        }
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

    @Test
    public void checkComparison() {

        BigNum x = new BigNum("2152921509975556099");
        BigNum y = new BigNum("1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(1);

        x = new BigNum("2921509975556099");
        y = new BigNum("1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(-1);

        x = new BigNum("34556152921509975556099");
        y = new BigNum("1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(1);

        x = new BigNum("-2152921509975556099");
        y = new BigNum("1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(-1);

        x = new BigNum("2921509975556099");
        y = new BigNum("-1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(1);

        x = new BigNum("-5152921509975556099");
        y = new BigNum("-1152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(-1);

        x = new BigNum("-5152921509975556099");
        y = new BigNum("-9152921509975556099");
        assertThat(x.cmp(y)).isEqualTo(1);

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

    private static BigInteger addBigIntegers(BigInteger first, BigInteger second) {
        return first.add(second);
    }

    private static BigInteger subBigIntegers(BigInteger first, BigInteger second) {
        return first.subtract(second);
    }

}
