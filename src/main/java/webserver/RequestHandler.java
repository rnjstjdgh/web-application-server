package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            showRequest(in);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = bufferedReader.readLine();
            if("".equals(startLine) || startLine == null ){
                response400(dos);
                return;
            }
            String reqUrl = HttpRequestUtils.getReqUrl(startLine);
            String viewPath = null;
            int querySplitIdx = reqUrl.indexOf("?");
            if(querySplitIdx == -1){
                viewPath = reqUrl;
            }else{
                String reqPath = HttpRequestUtils.getReqPath(reqUrl,querySplitIdx);
                String queryString = HttpRequestUtils.getReqQueryString(reqUrl,querySplitIdx);
                Map<String, String> queryMap = HttpRequestUtils.parseQueryString(queryString);

                if(reqPath.equals("/user/create")){ //회원가입 요청
                    User newUser = User.builder()
                            .userId(queryMap.get("userId"))
                            .password(queryMap.get("password"))
                            .name(queryMap.get("name"))
                            .email(queryMap.get("email")).build();
                    DataBase.addUser(newUser);
                }
                viewPath = reqPath;
            }

            responseView(dos,viewPath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseView(DataOutputStream dos, String viewPath) throws IOException {
        if(viewPath.equals("/"))
            viewPath = "/index.html";
        byte[] body = Files.readAllBytes(new File("./webapp" + viewPath).toPath());
        response200(dos, body);
    }

    private void response200(DataOutputStream dos, byte[] body){
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void response400(DataOutputStream dos) {
        byte[] body = "404 Bad Request!".getBytes();
        try {
            dos.writeBytes("HTTP/1.1 400 Bad Request \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void showRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while(true){
            line = bufferedReader.readLine();
            if("".equals(line) || line == null )
                break;

            log.info(line);
        }
    }
}
