package sloth.lab.perf.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class StreamUtil {

    public static String toString(Throwable e) {
        if (e == null) {
            return null;
        }
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(b);
        e.printStackTrace(p);
        p.flush();
        p.close();
        return new String(b.toByteArray());
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        copyStream(in, out, 2048);
    }

    public static void copyStream(InputStream in, OutputStream out, int bufSize) throws IOException {
        byte[] buf = new byte[bufSize];
        int c = 0;
        do {
            c = in.read(buf);
            if (c > 0) {
                out.write(buf, 0, c);
            }

        } while (c != -1);
        out.flush();
    }

    public static String readAsString(InputStream fin, String encode) {
        if (encode == null) {
            encode = "utf-8";
        }
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            copyStream(fin, bout);
            bout.close();
            return new String(bout.toByteArray(), encode);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] readAsBytes(InputStream fin) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            copyStream(fin, bout);
            bout.close();
            return bout.toByteArray();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
