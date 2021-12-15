package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.HttpResponseUtils;
import util.HttpStatus;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    ObjectMapper objectMapper = new ObjectMapper();
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            
            //요청 정보 파싱
            DataOutputStream dos = new DataOutputStream(out);
            Map<String, Object> requestMap = HttpRequestUtils.parseRequest(in);

            String reqPath = requestMap.get(HttpRequestUtils.PATH).toString();
            String reqMethod = requestMap.get(HttpRequestUtils.METHOD).toString();
            Map<String ,Object> reqHeaderMap
                    = objectMapper.convertValue(requestMap.get(HttpRequestUtils.HEADERMAP),Map.class);
            String reqBody = null;
            if(requestMap.get(HttpRequestUtils.BODY) != null)
                reqBody = requestMap.get(HttpRequestUtils.BODY).toString();

            String acceptStr = reqHeaderMap.get("Accept").toString();
            for(String type: acceptStr.split(",")){
                if(type.equals("text/css")){
                    HttpResponseUtils.setCSSResponse(dos,HttpStatus.OK,reqPath);
                }
            }
            
            //파싱된 요청 정보 기반 처리 로직 수행 + 리소스 메핑 + 응답 반환
            if(reqPath.equals("/index.html") ||reqPath.equals("/") ){
                HttpResponseUtils.setViewResponse(dos,HttpStatus.OK,"/index.html");
                return;
            }
            else if(reqPath.equals("/user/create") && (reqMethod.equals("POST") || reqMethod.equals("post"))){
                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(reqBody.toString());

                User user = User.builder()
                        .userId(queryStringMap.get("userId"))
                        .password(queryStringMap.get("password"))
                        .name(queryStringMap.get("name")).build();
                DataBase.addUser(user);
                HttpResponseUtils.setRedirectResponse(dos,"/index.html");
                return;
            }
            else if(reqPath.equals("/user/login") && (reqMethod.equals("POST")||reqMethod.equals("post"))){
                Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(reqBody.toString());
                User user = DataBase.findUserById(queryStringMap.get("userId"));
                if(user != null && user.getPassword().equals(queryStringMap.get("password"))){
                    //로그인 성공
                    Map<String,String> headerMap = new HashMap<>();
                    headerMap.put("Set-Cookie","logined=true; Path=/; Secure; SameSite=None;");
                    HttpResponseUtils.setRedirectResponse(dos,headerMap,"/index.html");
                    return;
                }
                else{
                    //로그인 실패
                    Map<String,String> headerMap = new HashMap<>();
                    headerMap.put("Set-Cookie","logined=false; Path=/; Secure; SameSite=None;");
                    HttpResponseUtils.setRedirectResponse(dos,headerMap,"/user/login_failed.html");
                    return;
                }
            }
            else if(reqPath.equals("/user/list") && (reqMethod.equals("GET")||reqMethod.equals("get"))){
                String cookieStr = reqHeaderMap.get("Cookie").toString();
                if(cookieStr == null) {
                    HttpResponseUtils.setRedirectResponse(dos,"/index.html");
                    return;
                }
                Map<String, String> cookies
                        = HttpRequestUtils.parseCookies(cookieStr);
                if(cookies.get("logined") != null && cookies.get("logined").equals("true")){
                    //TODO 사용자 정보 보여주기
                    StringBuilder stringBuilder = new StringBuilder();
                    Collection<User> userList = DataBase.findAll();

                    for(User user: userList){
                        stringBuilder.append(user.toString());
                        stringBuilder.append('\n');
                    }
                    HttpResponseUtils.setTextResponse(dos, HttpStatus.OK,stringBuilder.toString());
                }
                else{
                    HttpResponseUtils.setRedirectResponse(dos,"/user/login.html");
                    return;
                }
            }
            else{
                HttpResponseUtils.setViewResponse(dos,HttpStatus.OK,reqPath);
            }


        } catch (IOException e) {
            log.error("[]", e);
        }
    }


}
