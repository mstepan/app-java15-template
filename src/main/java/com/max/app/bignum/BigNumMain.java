package com.max.app.bignum;

public final class BigNumMain {


    public static void main(String[] args) throws Exception {

        String initialValue = "51090942171709440000";
        BigNum value = new BigNum("51090942171709440000");

        System.out.println(initialValue);
        System.out.println(value.toBigInt().toString());

        System.out.printf("BigNumMain completed..., java version:%s%n", System.getProperty("java.version"));
    }


}

