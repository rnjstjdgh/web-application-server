package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

public class HttpRequestUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);

    /***
     * InputStream으로 들어온 요청 정보 파싱
     * @param in
     * @return
     * @throws IOException
     */
    public static Map<String, Object> parseRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        Map<String, Object> resultMap = new HashMap<>();

        String startLine = bufferedReader.readLine();
        if("".equals(startLine) || startLine == null )
            return resultMap;

        resultMap.put("method", getMethod(startLine));
        resultMap.put("httpVersion", getHTTPVersion(startLine));
        String reqUrl = getReqUrl(startLine);
        int querySplitIdx = reqUrl.indexOf("?");
        if(querySplitIdx == -1)
            resultMap.put("path", reqUrl);
        else{
            resultMap.put("path",getReqPath(reqUrl,querySplitIdx));
            String queryString = getReqQueryString(reqUrl,querySplitIdx);
            resultMap.put("queryStringMap",parseQueryString(queryString));
        }

        String line = null;
        Map<String,Object> headerMap = new HashMap<>();
        while(true){
            line = bufferedReader.readLine();
            if("".equals(line) || line == null )
                break;
            log.info(line);
            Pair pair = parseHeader(line);
            headerMap.put(pair.key,pair.value);
        }
        resultMap.put("headerMap",headerMap);
        return resultMap;
    }

    public static String getMethod(String startLine){
        String[] token = startLine.split(" ");
        return token[0];
    }

    public static String getReqUrl(String startLine){
        String[] token = startLine.split(" ");
        return token[1];
    }

    public static String getHTTPVersion(String startLine){
        String[] token = startLine.split(" ");
        return token[2];
    }

    public static String getReqPath(String reqUrl, int idx){
        return reqUrl.substring(0,idx);
    }

    public static String getReqQueryString(String reqUrl, int idx){
        return reqUrl.substring(idx + 1);
    }

    /**
     * @param queryString은
     *            URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param 쿠키
     *            값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }

    public static void showRequest(InputStream in) throws IOException {
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
