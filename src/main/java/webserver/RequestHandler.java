package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.HttpResponseUtils;

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
            Map<String, Object> requestMap = HttpRequestUtils.parseRequest(in);

            if(requestMap.get(HttpRequestUtils.PATH).equals("/index.html")
                    ||requestMap.get(HttpRequestUtils.PATH).equals("/") ){
                HttpResponseUtils.response200View(out,"/index.html");
            }
            else if(requestMap.get(HttpRequestUtils.PATH).equals("/user/create")){
                //요구사항 2 - get 방식으로 회원가입
                Object queryStringMapObj = requestMap.get(HttpRequestUtils.QUERYSTRINGMAP);
                Map<String, String> queryStringMap = objectMapper.convertValue(queryStringMapObj, Map.class);

                User user = User.builder()
                        .userId(queryStringMap.get("userId"))
                        .password(queryStringMap.get("password"))
                        .name(queryStringMap.get("name")).build();
                DataBase.addUser(user);

                HttpResponseUtils.response200View(out,"/index.html");
            }
            else if(requestMap.get(HttpRequestUtils.PATH).equals("/user/form.html")){
                HttpResponseUtils.response200View(out,"/user/form.html");
            }


        } catch (IOException e) {
            log.error("[]", e);
        }
    }


}
