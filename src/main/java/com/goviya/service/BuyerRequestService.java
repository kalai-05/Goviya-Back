package com.goviya.service;

import com.goviya.dto.BuyerRequestDto;
import com.goviya.dto.CreateRequestRequest;
import com.goviya.dto.OrderDto;
import com.goviya.dto.RespondToRequestRequest;
import com.goviya.model.BuyerRequest;
import com.goviya.model.RequestResponse;
import com.goviya.model.User;
import com.goviya.repository.BuyerRequestRepository;
import com.goviya.repository.RequestResponseRepository;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerRequestService {

    private final BuyerRequestRepository buyerRequestRepository;
    private final RequestResponseRepository requestResponseRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final OrderService orderService;

    public List<BuyerRequestDto> getOpenRequests(String district) {
        return buyerRequestRepository.findByStatusAndDistrictOrderByCreatedAtDesc("OPEN", district)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BuyerRequestDto> getMyRequests(String userId, String role) {
        return buyerRequestRepository.findByBuyerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BuyerRequestDto createRequest(String buyerId, CreateRequestRequest request) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        BuyerRequest br = new BuyerRequest();
        br.setBuyerId(buyer.getId());
        br.setBuyerName(buyer.getName());
        br.setCropName(request.getCropName());
        br.setQuantityKg(request.getQuantityKg());
        br.setMaxPricePerKg(request.getMaxPricePerKg());
        br.setDistrict(request.getDistrict());
        br.setDescription(request.getDescription());
        br.setStatus("OPEN");
        br.setExpiresAt(LocalDateTime.now().plusDays(7));
        
        br = buyerRequestRepository.save(br);
        notificationService.notifyFarmersInDistrict(br.getDistrict(), br.getCropName());
        
        return mapToDto(br);
    }

    public RequestResponse respondToRequest(String requestId, String farmerId, RespondToRequestRequest request) {
        BuyerRequest br = buyerRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        if (!"OPEN".equals(br.getStatus())) {
            throw new RuntimeException("Request is no longer OPEN");
        }
        
        boolean alreadyResponded = requestResponseRepository.findByRequestIdAndFarmerId(requestId, farmerId).isPresent();
        if (alreadyResponded) {
            throw new RuntimeException("Farmer already responded to this request");
        }
        
        User farmer = userRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        RequestResponse response = new RequestResponse();
        response.setRequestId(br.getId());
        response.setFarmerId(farmer.getId());
        response.setFarmerName(farmer.getName());
        response.setOfferedPricePerKg(request.getOfferedPricePerKg());
        response.setQuantityKg(request.getQuantityKg());
        response.setMessage(request.getMessage());
        response.setStatus("PENDING");
        
        response = requestResponseRepository.save(response);
        
        notificationService.sendToUser(br.getBuyerId(), "New Offer!", 
            "A farmer has offered " + br.getCropName() + " at Rs." + request.getOfferedPricePerKg());
            
        return response;
    }

    public OrderDto acceptResponse(String responseId, String buyerId) {
        RequestResponse response = requestResponseRepository.findById(responseId).orElseThrow(() -> new RuntimeException("Response not found"));
        BuyerRequest br = buyerRequestRepository.findById(response.getRequestId()).orElseThrow(() -> new RuntimeException("Request not found"));
        
        if (!br.getBuyerId().equals(buyerId)) {
            throw new RuntimeException("Not authorized to accept this response");
        }
        
        response.setStatus("ACCEPTED");
        requestResponseRepository.save(response);
        
        List<RequestResponse> otherResponses = requestResponseRepository.findByRequestId(br.getId());
        for (RequestResponse r : otherResponses) {
            if (!r.getId().equals(responseId)) {
                r.setStatus("DECLINED");
                requestResponseRepository.save(r);
            }
        }
        
        br.setStatus("FILLED");
        buyerRequestRepository.save(br);
        
        return orderService.createOrderFromResponse(response);
    }

    private BuyerRequestDto mapToDto(BuyerRequest br) {
        BuyerRequestDto dto = new BuyerRequestDto();
        dto.setId(br.getId());
        dto.setBuyerId(br.getBuyerId());
        dto.setBuyerName(br.getBuyerName());
        dto.setCropName(br.getCropName());
        dto.setQuantityKg(br.getQuantityKg());
        dto.setMaxPricePerKg(br.getMaxPricePerKg());
        dto.setDistrict(br.getDistrict());
        dto.setDescription(br.getDescription());
        dto.setStatus(br.getStatus());
        dto.setExpiresAt(br.getExpiresAt());
        dto.setCreatedAt(br.getCreatedAt());
        
        int count = (int) requestResponseRepository.countByRequestId(br.getId());
        dto.setResponseCount(count);
        return dto;
    }
}
