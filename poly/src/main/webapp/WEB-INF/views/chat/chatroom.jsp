<!doctype html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
%>
<%@ page import="com.kopo.poly.util.CmmUtil" %>
<%
    // 채팅방 명
    String roomname = CmmUtil.nvl(request.getParameter("roomname"));

    // 채팅방 입장 전 입력한 별명
    String nickname = CmmUtil.nvl(request.getParameter("nickname"));
        %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title><%=roomname%> 채팅방 입장 </title>
    <script src="/js/jquery-3.6.1.min.js" type="text/javascript"></script>
    <script>

        let data = {}; //전송 데이터 JSON
        let ws; // 웹 소켓 객체
        const roomname = "<%=roomname%>"; //채팅룸 이름
        const nickname = "<%=nickname%>"; //채팅 유저 이름

        $(document).ready(function () {
            let btnSend = document.getElementById("btnSend");
            btnSend.onclick = function () {
                send(); //Send 버튼 누르면 Send 함수 호출하기
            }

            // 웹소켓 객체를 생성하는 중 , 웹 소켓 생성 되었는지 체크
            console.log("openSocket");
            if (ws !== undefined && ws.readyState !== WebSocket.CLOSED) {
                console.log("Websocket is already opened.");
                return;
            }

            // 웹 소켓 생성 접속 : ws://localhost
            ws = new WebSocket("ws://" + location.host + "/ws/" + roomname + "/" + nickname);

            //웹 소켓 열기
            ws.onopen = function (event) {
                if (event.data === undefined)
                    return;
                console.log(event.data)
            };

            // 웹 소켓으로부터 메시지를 받을 때 마다 실행
            ws.onmessage = function (msg) {

                // 웹 소켓으로부터 받은 데이터를 JSON 구조로 변환
                let data = JSON.parse(msg.data);

                if (data.name === nickname) { //내가 발송한 채팅 메시지는 파란색 글씨
                    $("#chat").append("<div>");
                    $("#chat").append("<span style='color: blue'><b>[보낸 사람] : <b></span>");
                    $("#chat").append("<span style='color: blue'> 나 </span>");
                    $("#chat").append("<span style='color: blue'><b>[발송 메시지] : <b></span>");
                    $("#chat").append("<span style='color: blue'>" + data.msg + "</span>");
                    $("#chat").append("<span style='color: blue'><b>[발송시간] : <b></span>");
                    $("#chat").append("<span style='color: blue'>" + data.date + "</span>");
                    $("#chat").append("</div>");
                } else if (data.name === "관리자") { //관리자가 발송한 채팅 메시지는 빨간색 글씨
                    $("#chat").append("<div>");
                    $("#chat").append("<span style='color: red'><b>[보낸 사람] : <b></span>");
                    $("#chat").append("<span style='color: red'> 나 </span>");
                    $("#chat").append("<span style='color: red'><b>[발송 메시지] : <b></span>");
                    $("#chat").append("<span style='color: red'>" + data.msg + "</span>");
                    $("#chat").append("<span style='color: red'><b>[발송시간] : <b></span>");
                    $("#chat").append("<span style='color: red'>" + data.date + "</span>");
                    $("#chat").append("</div>");
                } else { //다른 사람이 발송한 메시지는 검정 글씨
                    $("#chat").append("<div>");
                    $("#chat").append("<span><b>[보낸 사람] : <b></span>");
                    $("#chat").append("<span> 나 </span>");
                    $("#chat").append("<span><b>[발송 메시지] : <b></span>");
                    $("#chat").append("<span>" + data.msg + "</span>");
                    $("#chat").append("<span><b>[발송시간] : <b></span>");
                    $("#chat").append("<span>" + data.date + "</span>");
                    $("#chat").append("</div>");
                }

            }
        });

        //채팅 메시지 보내기
        function send() {
            let msgObj = $("#msg"); //Object

            if (msgObj.value !== "") {
                data.name = nickname; //별명
                data.msg = msgObj.val(); //입력한 메시지

                //데이터 구조를 JSON 형태로 변경
                let temp = JSON.stringify(data);

                //채팅 메시지 전송
                ws.send(temp);
            }

            // 채팅 메시지 전송 후 입력한 채팅 내용 지우기
            msgObj.val("");
        }

    </script>
</head>
<body>
<h2><%=nickname%> 님! <%=roomname%> 채팅방 입장하셨습니다.</h2><br/><br/>
<div><b>채팅내용</b></div>
<hr/>
<div id="chat"></div>
<div>
    <label for="msg">전달할 메시지 : </label><input type="text" id="msg">
    <button id="btnSend">메시지 전송</button>
</div>
</body>
</html>