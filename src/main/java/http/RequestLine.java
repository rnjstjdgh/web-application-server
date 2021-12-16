package http;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private String method = null;

    private String version = null;

    private String path = null;

    private Map<String, String> params = null;

    RequestLine(String startLine) {
        method = HttpRequestUtils.getMethod(startLine);
        version = HttpRequestUtils.getHTTPVersion(startLine);
        String reqUrl = HttpRequestUtils.getReqUrl(startLine);
        int querySplitIdx = reqUrl.indexOf("?");
        if(querySplitIdx == -1){
            path = reqUrl;
        }
        else {
            path = HttpRequestUtils.getReqPath(reqUrl, querySplitIdx);
            String queryString = HttpRequestUtils.getReqQueryString(reqUrl, querySplitIdx);
            params = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
