package util;

public enum HttpStatus {
    OK(200,"OK"),
    Found(302, "Found"),
    BadRequest(400, "Bad Request");

    private int code;
    private String name;

    private HttpStatus(){

    }

    private HttpStatus(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }
}
