package com.max.app.bignum;

import java.util.ArrayDeque;
import java.util.Queue;

public class BigNum {

    private static final int BASE = 16;
    private final int[] value;

    public BigNum(String decimalValue) {
        value = convertToBase(toIntArray(decimalValue), BASE);
    }

    public BigNum(int[] digits) {
        this.value = digits;
    }


    private int[] convertToBase(int[] decimalDigits, int base) {


        DivResult res = divideDecimalForBase(decimalDigits, base);


        return null;

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
        int cur = 0;

        for (int i = 0; i < value.length; ++i) {

            cur = cur * 10 + value[i];

            // do division
            if (cur >= base) {
                result.add(cur / base);
                cur %= base;
            }
            // handle corner-case when we added last digit, but cur < base
            else if (i == value.length - 1) {
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

    /**
     * @param digits - array of digits using big endian order (MSD first) in 'base'.
     * @param base   - the base that is used
     * @return decimal value representation
     */
    private static int toDecimalValue(int[] digits, int base) {
        //TODO: do not handle overflow yet, just convert to base 10.

        int res = 0;

        for (int singleDigit : digits) {
            res = res * base + singleDigit;
        }

        return res;
    }

    @Override
    public String toString() {
        return "value: " + toDecimalValue(value, BASE);
    }

}
