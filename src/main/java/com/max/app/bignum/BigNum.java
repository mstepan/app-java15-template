package com.max.app.bignum;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class BigNum {

    // use 2**30 as a base
    private static final int BASE = 1 << 20;

//    private static final int BASE = 1024;

    private final int[] value;

    public BigNum(String decimalValue) {
        value = convertToBase(toIntArray(decimalValue), BASE);
    }

    public BigNum(int[] digits) {
        this.value = digits;
    }

    private int[] convertToBase(int[] decimalDigits, int base) {

        Deque<Integer> resDigits = new ArrayDeque<>();

        int[] cur = decimalDigits;

        while (cur.length != 0) {

            DivResult res = divideDecimalForBase(cur, base);
            resDigits.push(res.remain());

            cur = res.result();
        }

        int[] convertedValue = new int[resDigits.size()];

        for (int i = 0; i < convertedValue.length && !resDigits.isEmpty(); ++i) {
            convertedValue[i] = resDigits.pop();
        }

        return convertedValue;
    }

    /**
     * @param value - decimal value passed using big endian order (most significant digit first).
     * @param base  - base to convert value
     * @return DivResult record with:
     * 'result' - big endian(MSD first) array of 'value' representation in 'base'
     * 'remain' - remainder after division
     */
    public static DivResult divideDecimalForBase(int[] value, int base) {

        assert value != null : "null 'value' detected";
        assert base > 0 : "negative of zero 'base' detected: " + base;

        Queue<Integer> result = new ArrayDeque<>();

        int index = 0;
        int cur = 0;

        while (cur < base && index < value.length) {
            cur = cur * 10 + value[index];
            ++index;
        }

        if (index == value.length) {

            if (cur >= base) {
                result.add(cur / base);
                cur %= base;
            }

            return new DivResult(toIntArray(result), cur);
        }

        result.add(cur / base);
        cur %= base;

        for (int i = index; i < value.length; ++i) {

            cur = cur * 10 + value[i];

            // do division
            if (cur >= base) {
                result.add(cur / base);
                cur %= base;
            }
            else {
                result.add(0);
            }
        }

        return new DivResult(toIntArray(result), cur);
    }

    private static int[] toIntArray(Queue<Integer> queue) {
        int[] res = new int[queue.size()];

        int i = 0;
        for (int singleValue : queue) {
            res[i] = singleValue;
            ++i;
        }
        return res;
    }

    public static int[] toIntArray(String decimalValue) {
        assert decimalValue != null;
        int[] res = new int[decimalValue.length()];

        for (int i = 0; i < res.length; ++i) {

            char digitCh = decimalValue.charAt(i);

            assert digitCh >= '0' && digitCh <= '9' : "incorrect decimal digit detected: " + digitCh;

            res[i] = (digitCh - '0');
        }

        return res;
    }

    public BigInteger toBigInt() {
        BigInteger res = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(BASE);

        for (int singleDigit : value) {
            res = res.multiply(base).add(BigInteger.valueOf(singleDigit));
        }

        return res;
    }

    @Override
    public String toString() {
        return toBigInt().toString();
    }

}
