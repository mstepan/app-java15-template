package com.max.app.bignum;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Queue;

/**
 * Represents arbitrary precision numbers using array of integer in big-endian
 * order (most significant digit first). As a base we use 2**30.
 */
public class BigNum {

    // use 2**30 as a base
    private static final int BASE = 1 << 30;

    private static final int BASE_SHIFT = 30;
    private static final int BASE_MASK = (1 << 30 - 1);

    private final int[] digits;

    // for positive values, sign = 1,
    // for negative, sign = -1,
    // for '0', sign = 0
    private final int sign;

    public BigNum(String decimalValue) {

        String normalized = Objects.requireNonNull(decimalValue).trim();

        if (isZeroStringRepresentation(normalized)) {
            this.sign = 0;
            this.digits = new int[0];
        }
        else if (beginsWith(normalized, "-")) {
            this.sign = -1;
            this.digits = convertToBase(toIntArray(normalized.substring(1)));
        }
        else if (beginsWith(normalized, "+")) {
            this.sign = 1;
            this.digits = convertToBase(toIntArray(normalized.substring(1)));
        }
        else {
            this.sign = 1;
            this.digits = convertToBase(toIntArray(normalized));
        }
    }

    private static boolean beginsWith(String str, String prefix) {
        return str.startsWith(prefix);
    }

    private static boolean isZeroStringRepresentation(String str) {
        return "0".equals(str) || "-0".equals(str) || "+0".equals(str);
    }

    public BigNum(int intValue) {
        this(String.valueOf(intValue));
    }

    public BigNum(long longValue) {
        this(String.valueOf(longValue));
    }

    private BigNum(int sign, int[] digits) {
        this.sign = sign;
        this.digits = digits;
    }

    private int[] convertToBase(int[] decimalDigits) {

        Deque<Integer> resDigits = new ArrayDeque<>();

        int[] cur = decimalDigits;

        while (cur.length != 0) {

            DivResult res = divideDecimalForBase(cur);

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
    public static DivResult divideDecimalForBase(int[] value) {

        assert value != null : "null 'value' detected";

        Queue<Integer> result = new ArrayDeque<>();

        int index = 0;

        // very important: cur should be 'long' here otherwise overflow will happen
        long cur = 0L;

        while (cur < BASE && index < value.length) {
            cur = cur * 10L + value[index];
            ++index;
        }

        if (index == value.length) {

            if (cur >= BASE) {
                result.add((int) (cur / BASE));
                cur %= BASE;
            }

            return new DivResult(toIntArray(result), (int) cur);
        }

        result.add((int) (cur / BASE));
        cur %= BASE;

        for (int i = index; i < value.length; ++i) {

            cur = cur * 10 + value[i];

            // do division
            if (cur >= BASE) {
                result.add((int) (cur / BASE));
                cur %= BASE;
            }
            else {
                result.add(0);
            }
        }

        return new DivResult(toIntArray(result), (int) cur);
    }

    /**
     * Converts Queue<Integer> in big endian order to array of int[]
     * with the same order (most significant digit first).
     */
    private static int[] toIntArray(Queue<Integer> queue) {
        int[] res = new int[queue.size()];

        int i = 0;
        for (int singleValue : queue) {
            res[i] = singleValue;
            ++i;
        }
        return res;
    }

    /**
     * Converts stack of integers in big endian order (Deque<Integer>) to an array of int[]
     * with the same order (most significant digit first).
     */
    private static int[] toIntArray(Deque<Integer> stack) {
        int[] res = new int[stack.size()];

        int i = 0;

        while (!stack.isEmpty()) {
            res[i] = stack.pop();
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

    public BigNum add(BigNum other) {

        // handle zero as a corner case
        if (isZero() || other.isZero()) {
            return isZero() ? other : this;
        }

        // case-1: (+x) + (+y) => |x| + |y|
        if (isPositive() && other.isPositive()) {
            return new BigNum(1, addAbs(this.digits, other.digits));
        }
        // case-2: (+x) + (-y) => |x| - |y|
        else if (isPositive() && other.isNegative()) {
            //TODO:
            return null;
        }
        // case-3: (-x) + (+y) => |y| - |x|
        else if (isNegative() && other.isPositive()) {
            //TODO:
            return null;
        }

        // case-4: (-x) + (-y) => -(|x| + |y|)
        return new BigNum(-1, addAbs(this.digits, other.digits));
    }

    /**
     * Add absolute values.
     */
    private int[] addAbs(int[] first, int[] second) {

        int i = first.length - 1;
        int j = second.length - 1;

        Deque<Integer> result = new ArrayDeque<>();
        long carry = 0L;

        while (i >= 0 || j >= 0) {
            long d1 = (i >= 0) ? first[i] : 0L;
            long d2 = (j >= 0) ? second[j] : 0L;
            --i;
            --j;

            long digitsSum = d1 + d2 + carry;

            result.push((int) (digitsSum % BASE));
            carry = digitsSum / BASE;
        }

        if (carry > 0L) {
            result.push((int) (carry % BASE));
        }

        return toIntArray(result);
    }

    public boolean isPositive() {
        return sign == 1;
    }

    public boolean isNegative() {
        return sign == -1;
    }

    public boolean isZero() {
        return sign == 0;
    }

    /**
     * Use Horner's rule to convert number from base=2**30 to base = 10.
     */
    public BigInteger toBigInt() {

        if (sign == 0) {
            return BigInteger.ZERO;
        }

        BigInteger res = BigInteger.ZERO;
        BigInteger base = BigInteger.valueOf(BASE);

        for (int singleDigit : digits) {
            res = res.multiply(base).add(BigInteger.valueOf(singleDigit));
        }

        return res.multiply(BigInteger.valueOf(sign));
    }

    @Override
    public String toString() {
        return toBigInt().toString();
    }

}
