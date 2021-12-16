package http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class RequestLineTest {

    @Test
    public void create_method_get() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        assertTrue(line.getMethod().isGet());
        assertEquals("/index.html", line.getPath());
    }
    
    @Test
    public void create_method_post() {
        RequestLine line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=pass HTTP/1.1");
        assertTrue(line.getMethod().isGet());
        assertEquals("/user/create", line.getPath());
        Map<String, String> params = line.getParams();
        assertEquals(2, params.size());
    }
}
