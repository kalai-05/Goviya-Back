package com.goviya.controller;

import com.goviya.dto.*;
import com.goviya.model.ChatMessage;
import com.goviya.model.Order;
import com.goviya.model.User;
import com.goviya.repository.*;
import com.goviya.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    // ── Unique conversation ID helper ─────────────────────
    private String getConversationId(String id1, String id2) {
        return id1.compareTo(id2) < 0
            ? id1 + "_" + id2
            : id2 + "_" + id1;
    }

    // ── Send a message ────────────────────────────────────
    @MessageMapping("/chat.send")
    public void sendMessage(
        @Payload ChatMessagePayload payload,
        Principal principal
    ) {
        String senderId = principal.getName();

        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));

        String conversationId = getConversationId(senderId, payload.getReceiverId());

        ChatMessage saved = chatMessageRepository.save(
            ChatMessage.builder()
                .senderId(senderId)
                .senderName(sender.getName())
                .senderRole(sender.getRole())
                .receiverId(payload.getReceiverId())
                .message(payload.getMessage())
                .type(payload.getType() != null ? payload.getType() : "TEXT")
                .orderId(payload.getOrderId())
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build()
        );

        ChatMessageResponse response = ChatMessageResponse.builder()
            .id(saved.getId())
            .senderId(senderId)
            .senderName(sender.getName())
            .senderRole(sender.getRole())
            .receiverId(payload.getReceiverId())
            .message(payload.getMessage())
            .type(saved.getType())
            .orderId(payload.getOrderId())
            .dealDetails(payload.getDealDetails())
            .isRead(false)
            .sentAt(saved.getSentAt())
            .conversationId(conversationId)
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + payload.getReceiverId(), response);
        messagingTemplate.convertAndSend("/topic/messages/" + senderId, response);

        notificationService.sendToUser(
            payload.getReceiverId(),
            sender.getName() + " sent you a message",
            payload.getMessage().length() > 50 ? payload.getMessage().substring(0, 50) + "..." : payload.getMessage()
        );

        log.info("Message sent: {} → {}", senderId, payload.getReceiverId());
    }

    // ── Propose a Deal ────────────────────────────────────
    @MessageMapping("/chat.deal.propose")
    public void proposeDeal(
        @Payload ChatMessagePayload payload,
        Principal principal
    ) {
        String senderId = principal.getName();
        User sender = userRepository.findById(senderId).orElseThrow();

        DealDetails deal = payload.getDealDetails();
        String conversationId = getConversationId(senderId, payload.getReceiverId());

        String dealText = String.format(
            "Deal Proposed: %s · %.1fkg · LKR %.1f/kg · Total: LKR %.1f",
            deal.getCropName(),
            deal.getQuantityKg(),
            deal.getPricePerKg(),
            deal.getTotalPrice()
        );

        ChatMessage saved = chatMessageRepository.save(
            ChatMessage.builder()
                .senderId(senderId)
                .senderName(sender.getName())
                .senderRole(sender.getRole())
                .receiverId(payload.getReceiverId())
                .message(dealText)
                .type("DEAL_PROPOSE")
                .isRead(false)
                .sentAt(LocalDateTime.now())
                .build()
        );

        ChatMessageResponse response = ChatMessageResponse.builder()
            .id(saved.getId())
            .senderId(senderId)
            .senderName(sender.getName())
            .senderRole(sender.getRole())
            .receiverId(payload.getReceiverId())
            .message(dealText)
            .type("DEAL_PROPOSE")
            .dealDetails(deal)
            .isRead(false)
            .sentAt(saved.getSentAt())
            .conversationId(conversationId)
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + payload.getReceiverId(), response);
        messagingTemplate.convertAndSend("/topic/messages/" + senderId, response);

        notificationService.sendToUser(
            payload.getReceiverId(),
            "💰 Deal Proposed!",
            sender.getName() + " offered " + deal.getCropName() + " at LKR " + deal.getPricePerKg() + "/kg"
        );
    }

    // ── Accept a Deal ─────────────────────────────────────
    @MessageMapping("/chat.deal.accept")
    public void acceptDeal(
        @Payload DealActionPayload payload,
        Principal principal
    ) {
        String buyerId = principal.getName();

        ChatMessage dealMsg = chatMessageRepository.findById(payload.getMessageId())
            .orElseThrow(() -> new RuntimeException("Deal message not found"));

        String farmerId = dealMsg.getSenderId();
        String conversationId = getConversationId(buyerId, farmerId);

        // Basic Order creation logic
        Order order = Order.builder()
            .buyerId(buyerId)
            .farmerId(farmerId)
            .cropName("Negotiated Crop") // Can be refined to pull from structured deal storage
            .status("PENDING")
            .paymentStatus("UNPAID")
            .build();

        userRepository.findById(buyerId).ifPresent(buyer -> {
            order.setBuyerName(buyer.getName());
            order.setBuyerPhone(buyer.getPhone());
        });
        userRepository.findById(farmerId).ifPresent(farmer -> {
            order.setFarmerName(farmer.getName());
            order.setFarmerPhone(farmer.getPhone());
        });

        Order savedOrder = orderRepository.save(order);

        String acceptText = "✅ Deal accepted! Order #" 
            + savedOrder.getId().substring(0, 8).toUpperCase()
            + " created. Please complete payment.";

        chatMessageRepository.save(ChatMessage.builder()
            .senderId(buyerId)
            .receiverId(farmerId)
            .message(acceptText)
            .type("DEAL_ACCEPT")
            .orderId(savedOrder.getId())
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .build());

        ChatMessageResponse response = ChatMessageResponse.builder()
            .senderId(buyerId)
            .receiverId(farmerId)
            .message(acceptText)
            .type("DEAL_ACCEPT")
            .orderId(savedOrder.getId())
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .conversationId(conversationId)
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + farmerId, response);
        messagingTemplate.convertAndSend("/topic/messages/" + buyerId, response);

        notificationService.sendToUser(
            farmerId,
            "🎉 Deal Accepted!",
            "Buyer accepted your deal. Waiting for payment."
        );
    }

    // ── Reject a Deal ─────────────────────────────────────
    @MessageMapping("/chat.deal.reject")
    public void rejectDeal(
        @Payload DealActionPayload payload,
        Principal principal
    ) {
        String rejectorId = principal.getName();

        ChatMessage dealMsg = chatMessageRepository.findById(payload.getMessageId()).orElseThrow();

        String otherId = dealMsg.getSenderId().equals(rejectorId) ? dealMsg.getReceiverId() : dealMsg.getSenderId();
        String conversationId = getConversationId(rejectorId, otherId);

        String rejectText = "❌ Deal rejected. Feel free to propose a new price.";

        chatMessageRepository.save(ChatMessage.builder()
            .senderId(rejectorId)
            .receiverId(otherId)
            .message(rejectText)
            .type("DEAL_REJECT")
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .build());

        ChatMessageResponse response = ChatMessageResponse.builder()
            .senderId(rejectorId)
            .receiverId(otherId)
            .message(rejectText)
            .type("DEAL_REJECT")
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .conversationId(conversationId)
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + otherId, response);
        messagingTemplate.convertAndSend("/topic/messages/" + rejectorId, response);

        notificationService.sendToUser(otherId, "Deal Update", "The deal was rejected. Try a new price.");
    }

    // ── Mark messages as Read ─────────────────────────────
    @MessageMapping("/chat.read")
    public void markAsRead(
        @Payload Map<String, String> payload,
        Principal principal
    ) {
        String readerId = principal.getName();
        String senderId = payload.get("senderId");

        List<ChatMessage> unread = chatMessageRepository.findByReceiverIdAndSenderIdAndIsReadFalse(readerId, senderId);
        unread.forEach(msg -> msg.setIsRead(true));
        chatMessageRepository.saveAll(unread);

        ChatMessageResponse readReceipt = ChatMessageResponse.builder()
            .type("READ_RECEIPT")
            .senderId(readerId)
            .receiverId(senderId)
            .message("seen")
            .sentAt(LocalDateTime.now())
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + senderId, readReceipt);
    }

    // ── Payment Done notification ──────────────────────────
    @MessageMapping("/chat.payment.done")
    public void paymentDone(
        @Payload Map<String, String> payload,
        Principal principal
    ) {
        String buyerId = principal.getName();
        String orderId = payload.get("orderId");
        String farmerId = payload.get("farmerId");

        String conversationId = getConversationId(buyerId, farmerId);

        orderRepository.findById(orderId).ifPresent(order -> {
            order.setPaymentStatus("PAID");
            order.setStatus("CONFIRMED");
            orderRepository.save(order);
        });

        String paymentText = "💳 Payment completed! Order confirmed. Farmer will contact you for pickup details.";

        chatMessageRepository.save(ChatMessage.builder()
            .senderId(buyerId)
            .receiverId(farmerId)
            .message(paymentText)
            .type("PAYMENT_DONE")
            .orderId(orderId)
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .build());

        ChatMessageResponse response = ChatMessageResponse.builder()
            .senderId(buyerId)
            .receiverId(farmerId)
            .message(paymentText)
            .type("PAYMENT_DONE")
            .orderId(orderId)
            .isRead(false)
            .sentAt(LocalDateTime.now())
            .conversationId(conversationId)
            .build();

        messagingTemplate.convertAndSend("/topic/messages/" + farmerId, response);
        messagingTemplate.convertAndSend("/topic/messages/" + buyerId, response);

        notificationService.sendToUser(
            farmerId,
            "💳 Payment Received!",
            "Buyer completed payment. Prepare for pickup!"
        );
    }
}
