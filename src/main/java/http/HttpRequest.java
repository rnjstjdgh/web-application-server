package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;


public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private RequestLine requestLine = null;
    private Map<String,String> headerMap = null;
    private RequestParams requestParam = null;
    private String body = null;

    /***
     * in으로 들어오는 값은 http 스펙을 잘 지키고 있다고 가정하고 작성하였습니다.
     * @param in
     * @throws IOException
     */
    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        //1) start line
        String startLine = bufferedReader.readLine();
        requestLine = new RequestLine(startLine);
        requestParam = new RequestParams();
        if(requestLine.getParams() != null){
            requestParam.setParams(requestLine.getParams());
        }

        //2) header
        headerMap = parseHeader(bufferedReader);

        //3) body
        if(headerMap.get("Content-Length") != null){    //body가 있다는 의미
            body = parseBody(bufferedReader);
            if("application/x-www-form-urlencoded".equals(headerMap.get("Content-Type"))){
                requestParam.addBody(body);
            }
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return headerMap.get(name);
    }

    public String getParameter(String name) {
        return requestParam.getParameter(name);
    }

    private Map<String,String> parseHeader(BufferedReader bufferedReader) throws IOException {
        String line = null;
        Map<String,String> map = new HashMap<>();
        while(true){
            line = bufferedReader.readLine();
            if("".equals(line) || line == null )
                break;
            log.info(line);
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            map.put(pair.getKey(),pair.getValue());
        }
        return map;
    }

    private String parseBody(BufferedReader bufferedReader) throws IOException{
        StringBuffer buffer = new StringBuffer();
        int bodyLen = Integer.parseInt(headerMap.get("Content-Length").toString());
        while(buffer.length() < bodyLen) {
            char chBuf[] = new char[1024];
            bufferedReader.read(chBuf);
            buffer.append(chBuf);
        }
        return buffer.toString().substring(0,bodyLen);
    }
}
