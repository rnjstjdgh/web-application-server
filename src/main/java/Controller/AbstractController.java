package Controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller{

    @Override
    public void service(HttpRequest request, HttpResponse response){
        HttpMethod method = request.getMethod();
        if(method.isGet())
            doGet(request,response);
        else if(method.isPost())
            doPost(request,response);
    }

    void doGet(HttpRequest request, HttpResponse response) {

    }

    void doPost(HttpRequest request, HttpResponse response) {

    }
}
