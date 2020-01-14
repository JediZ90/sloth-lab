package sloth.lab.java.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOSercet {

    public static int[] key = new int[] { 2, 3, 5, 8, 3, 1, 4, 5, 9, 4, 3, 7, 0, 7, 7, 4, 1 };

    public static void main(String[] args) throws IOException {

    }

    public static void secret(String filename, String dir) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dir + filename));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dir + System.currentTimeMillis()));
        int n;

        int aIter = 0;

        while ((n = bis.read()) != -1) {

            if (aIter == key.length) {
                aIter = 0;
            }

            bos.write(n + key[aIter]);

            aIter++;
        }

        bis.close();
        bos.close();
    }

    public static void decrypt(String filename, String dir) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(dir + filename));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dir + System.currentTimeMillis()));
        int n;

        int aIter = 0;

        while ((n = bis.read()) != -1) {

            if (aIter == key.length) {
                aIter = 0;
            }

            bos.write(n - key[aIter]);

            aIter++;
        }

        bis.close();
        bos.close();
    }
}
