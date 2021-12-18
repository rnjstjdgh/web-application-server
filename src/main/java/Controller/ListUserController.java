package Controller;

import db.SessionDatabase;
import db.UserDataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import webserver.RequestHandler;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogin(request.getHeader("Cookie"))) {
            response.forward("/user/login.html");
            return;
        }

        Collection<User> users = UserDataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {

    }

    private boolean isLogin(String cookieValue) {
        if(cookieValue == null)
            return false;
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue.trim());
        String sessionid = cookies.get("JSESSIONID");
        HttpSession session = SessionDatabase.findSessionById(sessionid);
        if(session == null)
            return false;
        return true;
    }
}
