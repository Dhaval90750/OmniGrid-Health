package com.medcore.his.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Map;

@Controller
@CrossOrigin(originPatterns = "*")
public class SignalingController {

    private static final Logger logger = LoggerFactory.getLogger(SignalingController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public SignalingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * WebRTC peers send their SDP offers/answers and ICE candidates here.
     * The message is routed to all subscribers of /topic/room/{roomId}.
     */
    @MessageMapping("/telemedicine/signal/{roomId}")
    public void processSignal(@DestinationVariable String roomId, @Payload Map<String, Object> payload) {
        logger.info("Received WebRTC signal for room {}: {}", roomId, payload.get("type"));
        messagingTemplate.convertAndSend("/topic/room/" + roomId, payload);
    }
}
