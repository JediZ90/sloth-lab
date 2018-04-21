package sloth.lab.aio;

import java.nio.ByteBuffer;

public class ByteBufferTest {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(6);
        System.out.println(byteBuffer);
        byteBuffer.put((byte) 1);
        System.out.println(byteBuffer);
        
        byteBuffer.position(0);
        byteBuffer.limit(1);
        System.out.println(byteBuffer);
        
        byte bs = byteBuffer.get();
        System.out.println(byteBuffer);
    }
}
