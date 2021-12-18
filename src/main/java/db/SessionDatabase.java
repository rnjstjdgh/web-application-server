package db;

import http.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class SessionDatabase {

    private static Map<String, HttpSession> sessions = new HashMap<>();    //key가 sessionID

    public static void addSession(String id, HttpSession session){
        sessions.put(id,session);
    }

    public static HttpSession findSessionById(String id){
        return sessions.get(id);
    }
}
