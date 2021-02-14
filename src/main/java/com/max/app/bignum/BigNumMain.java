package com.max.app.bignum;

public final class BigNumMain {


    public static void main(String[] args) throws Exception {

        BigNum value = new BigNum(new int[]{2, 6, 12, 13, 15});

        System.out.println(value);

        System.out.printf("BigNumMain completed..., java version:%s%n", System.getProperty("java.version"));
    }


}

