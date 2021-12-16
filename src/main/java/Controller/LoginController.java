package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
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
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                response.addHeader("Set-Cookie","logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.forward("/user/login_failed.html");
            }
        } else {
            response.forward("/user/login_failed.html");
        }
    }
}
