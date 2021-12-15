package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpResponseUtils.class);
    private static final String methodAndVersion = "HTTP/1.1";

    /***
     * 리다이렉트 할 때 사용할 메소드
     * @param dos               DataOutputStream 객체
     * @param headerMap         response에 추가적으로 포함시킬 헤더 값(디폴트로 redirectLocation 헤더가 있음)
     * @param redirectLocation  리다이렉트 할 주소
     */
    public static void setRedirectResponse(DataOutputStream dos,
                                           Map<String, String> headerMap,
                                           String redirectLocation){
        headerMap.put("Location", redirectLocation);
        setResponseStartLineAndHeader(dos, HttpStatus.Found,headerMap);
    }

    /***
     * 리다이렉트 할 때 사용할 메소드
     * @param dos               DataOutputStream 객체
     * @param redirectLocation  리다이렉트 할 주소
     */
    public static void setRedirectResponse(DataOutputStream dos,
                                           String redirectLocation){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Location", redirectLocation);
        setResponseStartLineAndHeader(dos, HttpStatus.Found,headerMap);
    }

    public static void setCSSResponse(DataOutputStream dos,
                                       HttpStatus httpStatus,
                                       String viewPath) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + viewPath).toPath());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/css;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));

        setResponseStartLineAndHeader(dos, httpStatus, headerMap);
        setResponseBody(dos,body);
    }

    /***
     *
     * @param dos               DataOutputStream 객체
     * @param httpStatus        응답으로 보낼 상태 값
     * @param headerMap         추가적으로 넣을 헤더 값
     * @param viewPath          뷰 경로
     * @throws IOException
     */
    public static void setViewResponse(DataOutputStream dos,
                                       HttpStatus httpStatus, Map<String, String> headerMap,
                                       String viewPath) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + viewPath).toPath());
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));

        setResponseStartLineAndHeader(dos, httpStatus, headerMap);
        setResponseBody(dos,body);
    }

    /***
     *
     * @param dos               DataOutputStream 객체
     * @param httpStatus        응답으로 보낼 상태 값
     * @param viewPath          뷰 경로
     * @throws IOException
     */
    public static void setViewResponse(DataOutputStream dos,
                                       HttpStatus httpStatus,
                                       String viewPath) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + viewPath).toPath());
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));

        setResponseStartLineAndHeader(dos, httpStatus, headerMap);
        setResponseBody(dos,body);
    }

    /***
     * 
     * @param dos           DataOutputStream 객체
     * @param httpStatus    응답으로 보낼 상태 값
     * @param headerMap     추가적으로 넣을 헤더 값
     * @param bodyText      응답으로 보낼 바디 값
     */
    public static void setTextResponse(DataOutputStream dos,
                                       HttpStatus httpStatus, Map<String, String> headerMap,
                                       String bodyText){
        byte[] body = bodyText.getBytes();
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));
        setResponseStartLineAndHeader(dos, httpStatus, headerMap);
        setResponseBody(dos,body);
    }

    /***
     *
     * @param dos           DataOutputStream 객체
     * @param httpStatus    응답으로 보낼 상태 값
     * @param bodyText      응답으로 보낼 바디 값
     */
    public static void setTextResponse(DataOutputStream dos,
                                       HttpStatus httpStatus,
                                       String bodyText){
        byte[] body = bodyText.getBytes();
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "text/html;charset=utf-8");
        headerMap.put("Content-Length", Integer.toString(body.length));
        setResponseStartLineAndHeader(dos, httpStatus, headerMap);
        setResponseBody(dos,body);
    }

    public static void setResponseStartLineAndHeader(DataOutputStream dos,
                                                     HttpStatus httpStatus, Map<String, String> headerMap){
        setResponseStartLine(dos, httpStatus);
        setResponseHeader(dos,headerMap);
    }

    public static void setResponseStartLine(DataOutputStream dos, HttpStatus httpStatus){
        String startLine = methodAndVersion + " " + httpStatus.getCode() + " " + httpStatus.getName();
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
