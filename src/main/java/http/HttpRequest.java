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

    private String method = null;
    private String version = null;
    private String path = null;
    private Map<String,String> headerMap = null;
    private Map<String,String> paramMap = null;
    private String body = null;

    /***
     * in으로 들어오는 값은 http 스펙을 잘 지키고 있다고 가정하고 작성하였습니다.
     * @param in
     * @throws IOException
     */
    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        String startLine = bufferedReader.readLine();
        method = HttpRequestUtils.getMethod(startLine);
        version = HttpRequestUtils.getHTTPVersion(startLine);
        String reqUrl = HttpRequestUtils.getReqUrl(startLine);
        int querySplitIdx = reqUrl.indexOf("?");
        if(querySplitIdx == -1){
            path = reqUrl;
        }
        else{
            path = HttpRequestUtils.getReqPath(reqUrl,querySplitIdx)
            String queryString = HttpRequestUtils.getReqQueryString(reqUrl,querySplitIdx);
            paramMap = HttpRequestUtils.parseQueryString(queryString);
        }

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
        headerMap = map;

        String msgBody = null;
        if(headerMap.get("Content-Length") != null){    //body가 있다는 의미
            StringBuffer buffer = new StringBuffer();
            int bodyLen = Integer.parseInt(headerMap.get("Content-Length").toString());
            while(buffer.length() < bodyLen) {
                char chBuf[] = new char[1024];
                bufferedReader.read(chBuf);
                buffer.append(chBuf);
            }
            body = buffer.toString().substring(0,bodyLen);

            if("application/x-www-form-urlencoded".equals(headerMap.get("Content-Type"))){
                paramMap = HttpRequestUtils.parseQueryString(body);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headerMap.get(name);
    }

    public String getParameter(String name) {
        return paramMap.get(name);
    }
}
