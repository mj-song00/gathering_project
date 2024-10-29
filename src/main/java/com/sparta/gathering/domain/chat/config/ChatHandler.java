package com.sparta.gathering.domain.chat.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ChatHandler extends TextWebSocketHandler {

    private static List<WebSocketSession> list = new ArrayList<>();

    // 연결(클라이언트 접속)
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        list.add(session);
        log.info("[{}] 클라이언트 접속", session);
    }

    // 연결 종료(클라이언트 접속 해제)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {
        log.info("[{}] 클라이언트 접속 해제", session);
        list.remove(session);
    }

    // 메시지 수신
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);

        for (WebSocketSession webSocketSession : list) {
            webSocketSession.sendMessage(message);
        }
    }

    // 오류 처리
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        log.error(exception.getMessage());
    }

}