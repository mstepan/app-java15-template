package com.max.app.interview;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RunLengthEncoderTest {

    @Test
    public void encode(){
        assertEquals("a1b3c2d1e5F3f2", RunLengthEncoder.encode("abbbccdeeeeeFFFff"));
        assertEquals("a3", RunLengthEncoder.encode("aaa"));
    }

    @Test
    public void encodeWithSpaces(){
        assertEquals("a1    b3c7   ", RunLengthEncoder.encode("a    bbbccccccc   "));
    }

    @Test
    public void encodeWithEncodedLengthGreaterShouldReturnInitialString(){
        assertEquals("a", RunLengthEncoder.encode("a"));
        assertEquals("bb", RunLengthEncoder.encode("bb"));
        assertEquals("bc", RunLengthEncoder.encode("bc"));
        assertEquals(" bb c  ", RunLengthEncoder.encode(" bb c  "));
    }
}
