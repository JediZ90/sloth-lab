package sloth.lab.aio;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    public static void main(String[] args) {
        Map<String,String> test = new HashMap<>();
        System.out.println(test.put("name", "zhangsan"));
        System.out.println(test.put("name", "lisi"));
        System.out.println(test.putIfAbsent("name", "wangwu"));
        System.out.println(test.get("name"));
    }
}
