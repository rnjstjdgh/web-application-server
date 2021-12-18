package Controller;

import db.SessionDatabase;
import db.UserDataBase;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSession;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class LoginController extends AbstractController{
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    void doGet(HttpRequest request, HttpResponse response) {

    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        User user = UserDataBase.findUserById(request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                //로그인 성공
                HttpSession newSession = new HttpSession();
                newSession.setAttribute("userId", user.getUserId());
                newSession.setAttribute("password", user.getPassword());
                SessionDatabase.addSession(newSession.getId(), newSession);
                response.addHeader("Set-Cookie", "JSESSIONID=" + newSession.getId() + "; Path=/; Secure; SameSite=None;");
                response.sendRedirect("/index.html");
            } else {
                response.forward("/user/login_failed.html");
            }
        } else {
            response.forward("/user/login_failed.html");
        }
    }
}
