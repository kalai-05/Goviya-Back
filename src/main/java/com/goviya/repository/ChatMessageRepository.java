package com.goviya.repository;

import com.goviya.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(
        String s1, String r1, String s2, String r2);
    List<ChatMessage> findByReceiverIdAndSenderIdAndIsReadFalse(
        String receiverId, String senderId);
    long countByReceiverIdAndIsReadFalse(String receiverId);
    List<ChatMessage> findByReceiverIdOrSenderId(
        String receiverId, String senderId);
    long countByReceiverIdAndSenderIdAndIsReadFalse(
        String receiverId, String senderId);
}
