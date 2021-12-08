package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseUtils.class);
    private static final String methodAndVersion = "HTTP/1.1";

    public static void setRedirectResponse(DataOutputStream dos,
                                           Map<String, String> headerMap,
                                           String redirectLocation ){

        headerMap.put("Location", redirectLocation);
        setResponseStartLineAndHeader(dos,Status.Found,headerMap);
    }

    public static void setViewResponse(DataOutputStream dos,
                                       Status status, Map<String, String> headerMap,
                                       String viewPath) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + viewPath).toPath());
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));

        setResponseStartLineAndHeader(dos,status, headerMap);
        setResponseBody(dos,body);
    }

    public static void setTextResponse(DataOutputStream dos,
                                       Status status, Map<String, String> headerMap,
                                       String bodyText){
        byte[] body = bodyText.getBytes();
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));
        setResponseStartLineAndHeader(dos,status, headerMap);
        setResponseBody(dos,body);
    }

    public static void setResponseStartLineAndHeader(DataOutputStream dos,
                                   Status status, Map<String, String> headerMap){
        setResponseStartLine(dos,status);
        setResponseHeader(dos,headerMap);
    }

    public static void setResponseStartLine(DataOutputStream dos, Status status){
        String startLine = methodAndVersion + " " + status.getCode() + " " + status.getName();
        setResponseStartLine(dos,startLine);
    }

    public static void setResponseStartLine(DataOutputStream dos, String startLine){
        try {
            dos.writeBytes(startLine + " \r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void setResponseHeader(DataOutputStream dos, Map<String, String> map){
        try {
            for(String key: map.keySet()){
                String value = map.get(key);
                dos.writeBytes(key +": " + value + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void setResponseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
