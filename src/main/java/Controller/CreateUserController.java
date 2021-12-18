package Controller;

import db.UserDataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

public class CreateUserController extends AbstractController{

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    void doGet(HttpRequest request, HttpResponse response) {

    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        User user = new User(request.getParameter("userId"), request.getParameter("password"), request.getParameter("name"),
                request.getParameter("email"));
        log.debug("user : {}", user);
        UserDataBase.addUser(user);
        response.sendRedirect("/index.html");
    }
}
