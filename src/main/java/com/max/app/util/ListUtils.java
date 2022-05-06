package com.max.app.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public final class ListUtils {

    private ListUtils() {
        throw new AssertionError("Can't instantiate utility-only class.");
    }

    /**
     * Flatten list.
     * time: O(n)
     * space: O(N)
     */
    @SuppressWarnings("unchecked")
    public static List<Object> flatten(List<Object> list) {

        Deque<Iterator<Object>> stack = new ArrayDeque<>();
        stack.push(list.iterator());

        List<Object> flatLst = new ArrayList<>();

        while (!stack.isEmpty()) {
            Iterator<Object> cur = stack.pop();

            while (cur.hasNext()) {
                Object value = cur.next();
                if (value instanceof List<?> innerList) {
                    stack.push(cur);
                    stack.push((Iterator<Object>) innerList.iterator());
                    break;
                }
                else {
                    flatLst.add(value);
                }
            }
        }

        return flatLst;
    }

}
