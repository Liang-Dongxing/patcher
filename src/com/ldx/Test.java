package com.ldx;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LiangYong-LDX
 * @date 2020/7/29 12:11
 */
public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        String string = new String("73.25".getBytes(), StandardCharsets.UTF_8);
        for (int i = 0; i < 100000000; i++) {
            string = encryptThisString(string);
            System.out.println(i);
        }
        System.out.println("+++++++++" + string);
    }

    public static String encryptThisString(String input) {
        try {
            // getInstance() method is called with algorithm MD2
            MessageDigest md = MessageDigest.getInstance("MD2");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
