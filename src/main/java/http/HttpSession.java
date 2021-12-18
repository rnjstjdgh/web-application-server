package http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession {

    private String id;
    private Map<String, Object> sessionData;

    public HttpSession(){
        id = UUID.randomUUID().toString();
        sessionData = new HashMap<>();
    };

    /***
     * 현재 세션에 할당되어 있는 고유한 세션 아이디를 반환
     * @return
     */
    public String getId(){
        return id;
    }

    /***
     * 현재 세션에 value 인자로 전달되는 객체를 key 인자 이름으로 저장
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value){
        sessionData.put(key,value);
    }

    /***
     * 현재 세션에 key 인자로 저장되어 있는 객체 값을 찾아 반환
     * @param key
     * @return
     */
    public Object getAttribute(String key){
        return sessionData.get(key);
    }

    /***
     * 현재 세션에 key 인자로 저장되어 있는 객체 값을 삭제
     * @param key
     */
    public void removeAttribute(String key){
        sessionData.remove(key);
    }

    /***
     * 현재 세션에 저장되어 있는 모든 값을 삭제
     */
    public void invalidate(){
        sessionData.clear();
    }
}
