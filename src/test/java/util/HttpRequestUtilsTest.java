package util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import util.HttpRequestUtils.Pair;

public class HttpRequestUtilsTest {

    ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void parseRequestTest() throws IOException {
        String reqMsgSample1 = "POST /cgi-bin/process.cgi HTTP/1.1\n" +
                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n" +
                "Host: www.tutorialspoint.com\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Content-Length: 49\n" +
                "Accept-Language: en-us\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Connection: Keep-Alive\n" +
                "\n" +
                "licenseID=string&content=string&/paramsXML=string";

        String reqMsgSample2 = "POST /cgi-bin/process.cgi HTTP/1.1\n" +
                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n" +
                "Host: www.tutorialspoint.com\n" +
                "Content-Type: text/xml; charset=utf-8\n" +
                "Content-Length: 23\n" +
                "Accept-Language: en-us\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Connection: Keep-Alive\n" +
                "\n" +
                "dsssdsds\n" +
                "dsssdsdsfsdfsd";

        String reqMsgSample3 = "GET /hello.htm HTTP/1.1\n" +
                "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n" +
                "Host: www.tutorialspoint.com\n" +
                "Accept-Language: en-us\n" +
                "Accept-Encoding: gzip, deflate\n" +
                "Connection: Keep-Alive";

        HttpRequestUtils.parseRequest(new ByteArrayInputStream(reqMsgSample1.getBytes()));
        Map<String, Object> resultMap1 = Context.reqMap.get();
        assertThat("/cgi-bin/process.cgi",is(resultMap1.get(HttpRequestUtils.PATH)));
        assertThat("POST",is(resultMap1.get(HttpRequestUtils.METHOD)));
        assertThat(null,is(resultMap1.get(HttpRequestUtils.QUERYSTRINGMAP)));
        Object headerMapObj = resultMap1.get(HttpRequestUtils.HEADERMAP);
        Map<String, String> headerMap = objectMapper.convertValue(headerMapObj, Map.class);
        assertThat("www.tutorialspoint.com",is(headerMap.get("Host")));
        assertThat("application/x-www-form-urlencoded",is(headerMap.get("Content-Type")));
        assertThat("licenseID=string&content=string&/paramsXML=string",is(resultMap1.get(HttpRequestUtils.BODY).toString()));

        HttpRequestUtils.parseRequest(new ByteArrayInputStream(reqMsgSample2.getBytes()));
        Map<String, Object> resultMap2 = Context.reqMap.get();
        assertThat("dsssdsds\n" + "dsssdsdsfsdfsd",is(resultMap2.get(HttpRequestUtils.BODY).toString()));

        HttpRequestUtils.parseRequest(new ByteArrayInputStream(reqMsgSample3.getBytes()));
        Map<String, Object> resultMap3 = Context.reqMap.get();
        assertThat(null,is(resultMap3.get(HttpRequestUtils.BODY)));
    }

    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }

    @Test
    public void getReqUrl(){
        String startLine = "GET /index.html HTTP/1.1";
        String reqUrl = HttpRequestUtils.getReqUrl(startLine);
        assertThat(reqUrl, is("/index.html"));
    }

    @Test
    public void getReqPath(){
        String reqUrl = "/user/create?userId=fdsf&password=fsd&name=fsd&email=fds%40fds";
        int idx = reqUrl.indexOf("?");
        String reqPath = HttpRequestUtils.getReqPath(reqUrl,idx);
        assertThat(reqPath,is("/user/create"));
    }

    @Test
    public void getReqQueryString(){
        String reqUrl = "/user/create?userId=fdsf&password=fsd&name=fsd&email=fds%40fds";
        int idx = reqUrl.indexOf("?");
        String queryString = HttpRequestUtils.getReqQueryString(reqUrl,idx);
        assertThat(queryString,is("userId=fdsf&password=fsd&name=fsd&email=fds%40fds"));
    }
}
