package com.max.app.util;

import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class ListUtilsTest {

    @Test
    public void flattenNormalCase() {
        List<String> list4 = new ArrayList<>();
        list4.add("ab");
        list4.add("ac");
        list4.add("ad");

        List<Object> list3 = new ArrayList<>();
        list3.add("hello");
        list3.add("wonderful");
        list3.add("world");
        list3.add(list4);

        List<Object> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add(list3);
        list2.add("c");

        List<Object> list1 = new ArrayList<>();
        list1.add(null);
        list1.add(1);
        list1.add(2);
        list1.add(list2);
        list1.add(3);
        list1.add(4);

        List<Object> flatList = ListUtils.flatten(list1);

        assertThat(flatList)
            .isNotEmpty()
            .hasSize(14)
            .doesNotHaveDuplicates()
            .containsExactly(null, 1, 2, "a", "b", "hello", "wonderful", "world", "ab", "ac", "ad", "c", 3, 4);

    }


}
