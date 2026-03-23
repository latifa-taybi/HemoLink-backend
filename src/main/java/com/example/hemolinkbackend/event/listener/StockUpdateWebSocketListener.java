package com.example.hemolinkbackend.event.listener;

import com.example.hemolinkbackend.event.StockChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockUpdateWebSocketListener {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onStockChange(StockChangeEvent event) {
        log.info("🔔 Événement StockChangeEvent reçu - Groupe: {}, Type: {}, Poche ID: {}",
                event.getGroupeSanguin(), event.getChangeType(), event.getPocheSangId());

        // ✅ Envoyer notification temps réel aux clients WebSocket
        String destination = "/topic/stock/" + event.getGroupeSanguin();

        StockUpdateMessage message = new StockUpdateMessage(
                event.getGroupeSanguin().name(),
                event.getChangeType(),
                event.getPocheSangId(),
                System.currentTimeMillis()
        );

        try {
            messagingTemplate.convertAndSend(destination, message);
            log.info("✅ Notification WebSocket envoyée à {}", destination);
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi WebSocket", e);
        }
    }

    // DTO pour le message WebSocket
    public static class StockUpdateMessage {
        public String groupeSanguin;
        public String changeType;
        public Long pocheSangId;
        public long timestamp;

        public StockUpdateMessage(String groupeSanguin, String changeType, Long pocheSangId, long timestamp) {
            this.groupeSanguin = groupeSanguin;
            this.changeType = changeType;
            this.pocheSangId = pocheSangId;
            this.timestamp = timestamp;
        }

        public String getGroupeSanguin() {
            return groupeSanguin;
        }

        public String getChangeType() {
            return changeType;
        }

        public Long getPocheSangId() {
            return pocheSangId;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}

