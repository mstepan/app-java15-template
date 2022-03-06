package com.max.app.interview;

import static com.google.common.base.Preconditions.checkNotNull;

public final class RunLengthEncoder {

    private RunLengthEncoder() {
        throw new AssertionError("Can't instantiate utility-only class.");
    }

    /**
     * time: O(N)
     * space: O(2N)
     */
    public static String encode(String str) {
        checkNotNull(str);

        if (str.length() < 3) {
            return str;
        }

        final StringBuilder buf = new StringBuilder(2 * str.length());

        char prev = str.charAt(0);
        int cnt = 1;

        for (int i = 1; i < str.length(); ++i) {
            char ch = str.charAt(i);

            if (ch == prev) {
                ++cnt;
            }
            else {
                encodeSingleChar(buf, prev, cnt);
                prev = ch;
                cnt = 1;
            }
        }

        encodeSingleChar(buf, prev, cnt);

        if (str.length() <= buf.length()) {
            return str;
        }

        return buf.toString();
    }

    private static void encodeSingleChar(StringBuilder buf, char ch, int cnt) {
        if (Character.isWhitespace(ch)) {
            for (int i = 0; i < cnt; ++i) {
                buf.append(ch);
            }
        }
        else {
            buf.append(ch);
            buf.append(cnt);
        }
    }

}
