# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* tomcat과 spring이 다 만들어 주는 환경에서 개발하다가 직접 socket 수준에서 웹 서버를 개발
    * http 프로토콜에 의해 전송되는 문자열 값을 직접 파싱 해야함
    * 이때, 클라이언트가 보내는 문자열이 http 프로토콜에 부합한지 확인은 해야할까 안해야할까?
        * 기본적으로 브라우저는 http 클라이언트니까 프로토콜에 맞게 잘 보낼 것이다.
    * 예외 처리를 어느 수준으로 해야하는지 약간 모호하다..
        * 예외 처리에 대한 요구사항이 없다.
        * http client는 요청 메시지를 잘 보낸다고 가정하고 코드를 작성해도 될까??
        * 아니면 이런것까지 하나하나 다 서버에서 예외처리하고 검증해서 적절한 응답을 내려줘야 할까?
    * 응답을 줄 때에도, http 프로토콜에 맞는 적절한 헤더와 바디를 보내주는 코드를 내가 직접 관리해야 한다.
    


### 요구사항 2 - get 방식으로 회원가입
1.  테스트 코드
    * 기존 프로젝트에 존재하던 테스트 코드를 읽어보는 것을 통해, 기존 코드의 동작을 확실히 이해하기 편했다.
2.  앞으로의 구현을 위해 http 요청정보 파싱 -> 비즈니스 로직 처리 -> 응답 반환 3가지 모듈을 미리 만들기로 했다.
    * 책에서 나오는 흐름과는 조금 다를수도 있겠다 싶다..
    * http 요청 정보를 파싱하고 응답을 적절하게 반환하기 위해 http 스펙을 참고했다.
        * https://www.w3.org/Protocols/rfc2616/rfc2616-sec5.html

    * 우선, 요청정보는 필요 요소를 파싱해 Map으로 리턴하도록 구현하자
        1.  Request-Line => Method / Request-URL / HTTP-Version
        2.  request-header => key-value값 형식
        3.  message Body
    * http 요청 메시지를 직접 파싱하려고 하니 socket io에서 주의해야 할 점과 http 요청 메시지 스펙을 더 공부할 수 있는 기회가 되었다.
        * socket io에서 주의해야 할 점
            * (너무 당연한 말이지만)data를 쓰는 쪽과 읽는 쪽의 프로토콜에 맞게 움직이는게 중요
            * 구현 중, 웹 클라이언트는 이제 그만 보내는데 서버에서 다음 바이트가 도착하기를 기다리는 코드를 작성했었음
            * 위와 같은 코드는 서버가 block되는 결과를 만들었음

### 요구사항 3 - post 방식으로 회원가입
* `요구사항 2`에서 이미 요청 메시지 파싱 모듈작업을 끝내 놓아서 수월하게 해결 가능했음

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
