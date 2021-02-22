package com.max.app.bignum;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.Queue;

/**
 * Represents arbitrary precision numbers using array of integer in big-endian
 * order (most significant digit first). As a base we use 2**30.
 */
public class BigNum {

    // use 2**30 as a base
    private static final int BASE_SHIFT = 30;
    private static final int BASE = 1 << BASE_SHIFT;

    // BASE_MASK or max digit value in BASE
    private static final int BASE_MASK = BASE - 1;

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
                result.add((int) (cur >>> BASE_SHIFT));
                cur &= BASE_MASK;
            }

            return new DivResult(toIntArray(result), (int) cur);
        }

        result.add((int) (cur >>> BASE_SHIFT));
        cur &= BASE_MASK;

        for (int i = index; i < value.length; ++i) {

            cur = cur * 10 + value[i];

            // do division
            if (cur >= BASE) {
                result.add((int) (cur >>> BASE_SHIFT));
                cur &= BASE_MASK;
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

            int cmp = cmpAbsValues(this.digits, other.digits);

            // |x| >= |y|
            if (cmp >= 0) {
                return new BigNum(1, subAbs(this.digits, other.digits));
            }
            // |x| < |y|, swap two number and change sign
            else {
                return new BigNum(-1, subAbs(other.digits, this.digits));
            }
        }
        // case-3: (-x) + (+y) => |y| - |x|
        else if (isNegative() && other.isPositive()) {

            int cmp = cmpAbsValues(other.digits, this.digits);

            // |y| >= |x|
            if (cmp >= 0) {
                return new BigNum(1, subAbs(other.digits, this.digits));
            }
            // |y| < |x|, so swap two number and change sign
            else {
                return new BigNum(-1, subAbs(this.digits, other.digits));
            }
        }

        // case-4: (-x) + (-y) => -(|x| + |y|)
        return new BigNum(-1, addAbs(this.digits, other.digits));
    }

    /**
     * Use grade school subtraction algorithm with borrowing if needed.
     * <p>
     * Important invariant that always holds: |first| >= |second|
     */
    private static int[] subAbs(int[] initialFirst, int[] second) {

        // taking into account that we can modify 'initialFirst' array during
        // subtraction procedure, we need make defensive copy here
        final int[] first = Arrays.copyOf(initialFirst, initialFirst.length);

        int i = first.length - 1;
        int j = second.length - 1;

        Deque<Integer> res = new ArrayDeque<>();

        while (j >= 0) {

            int d1 = first[i];
            int d2 = second[j];

            if (d1 >= d2) {
                res.push(d1 - d2);
            }
            else {
                int index = i - 1;

                while (first[index] == 0) {
                    --index;
                }

                first[index] -= 1;
                index += 1;

                while (index < i) {
                    first[index] = BASE_MASK;
                    ++index;
                }

                // overflow is not possible here, because d2 > d1
                int cur = BASE - d2 + d1;
                res.push(cur);
            }

            --i;
            --j;
        }

        while (i >= 0) {
            res.push(first[i]);
            --i;
        }

        return toIntArray(res);
    }

    public int cmp(BigNum other) {

        // this.isZero() case
        if (isZero()) {
            return (other.isZero() ? 0 : (other.isPositive() ? -1 : 1));
        }

        // other.isZero() case
        if (other.isZero()) {
            return isPositive() ? 1 : -1;
        }

        if (isPositive()) {
            return other.isPositive() ? cmpAbsValues(this.digits, other.digits) : 1;
        }
        else {
            return other.isNegative() ? -cmpAbsValues(this.digits, other.digits) : -1;
        }
    }

    /**
     * Compare absolute values of arrays.
     */
    private static int cmpAbsValues(int[] first, int[] second) {

        // first bigger, more digits
        if (first.length > second.length) {
            return 1;
        }

        // second bigger
        if (second.length > first.length) {
            return -1;
        }

        // first.length == second.length, compare digit by digit
        return Arrays.compare(first, second);
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

            result.push((int) (digitsSum & BASE_MASK));
            carry = digitsSum >>> BASE_SHIFT;
        }

        if (carry > 0L) {
            result.push((int) (carry & BASE_MASK));
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
