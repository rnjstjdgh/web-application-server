package util;

import java.util.HashMap;
import java.util.Map;

public class Context {

    public static ThreadLocal<Map<String, Object>> reqMap = new ThreadLocal<>();

}
