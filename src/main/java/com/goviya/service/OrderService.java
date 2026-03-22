package com.goviya.service;

import com.goviya.dto.OrderDto;
import com.goviya.model.Listing;
import com.goviya.model.Order;
import com.goviya.model.RequestResponse;
import com.goviya.model.User;
import com.goviya.repository.ListingRepository;
import com.goviya.repository.OrderRepository;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final com.goviya.repository.BuyerRequestRepository buyerRequestRepository;

    public OrderDto createOrder(String buyerId, String listingId, Double quantityKg) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!"ACTIVE".equals(listing.getStatus())) {
            throw new RuntimeException("Listing is not available");
        }
        
        User buyer = userRepository.findById(buyerId).orElseThrow();
        
        Order order = new Order();
        order.setBuyerId(buyer.getId());
        order.setBuyerName(buyer.getName());
        order.setBuyerPhone(buyer.getPhone());
        
        User farmer = userRepository.findById(listing.getFarmerId()).orElseThrow();
        order.setFarmerId(farmer.getId());
        order.setFarmerName(farmer.getName());
        order.setFarmerPhone(farmer.getPhone());
        
        order.setListingId(listing.getId());
        order.setCropName(listing.getCropName());
        order.setQuantityKg(quantityKg);
        
        Double totalPrice = quantityKg * listing.getPricePerKg();
        order.setTotalPrice(totalPrice);
        order.setCommission(totalPrice * 0.025);
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        
        return mapToDto(orderRepository.save(order));
    }
    
    public OrderDto createOrderFromResponse(RequestResponse response) {
        Order order = new Order();
        com.goviya.model.BuyerRequest req = buyerRequestRepository.findById(response.getRequestId()).orElseThrow();
        User buyer = userRepository.findById(req.getBuyerId()).orElseThrow();
        order.setBuyerId(buyer.getId());
        order.setBuyerName(buyer.getName());
        order.setBuyerPhone(buyer.getPhone());
        
        User farmer = userRepository.findById(response.getFarmerId()).orElseThrow();
        order.setFarmerId(farmer.getId());
        order.setFarmerName(farmer.getName());
        order.setFarmerPhone(farmer.getPhone());
        
        order.setCropName(req.getCropName());
        order.setQuantityKg(response.getQuantityKg());
        
        Double totalPrice = response.getQuantityKg() * response.getOfferedPricePerKg();
        order.setTotalPrice(totalPrice);
        order.setCommission(totalPrice * 0.025);
        order.setStatus("PENDING");
        order.setPaymentStatus("UNPAID");
        
        return mapToDto(orderRepository.save(order));
    }

    public List<OrderDto> getMyOrders(String userId, String role) {
        List<Order> orders;
        if ("FARMER".equalsIgnoreCase(role)) {
            orders = orderRepository.findByFarmerIdOrderByCreatedAtDesc(userId);
        } else {
            orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
        }
        return orders.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public OrderDto confirmOrder(String orderId, String farmerId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getFarmerId().equals(farmerId)) {
            throw new RuntimeException("Not authorized");
        }
        order.setStatus("CONFIRMED");
        order = orderRepository.save(order);
        
        notificationService.sendToUser(order.getBuyerId(), "Order Confirmed", "Your order for " + order.getCropName() + " was confirmed.");
        return mapToDto(order);
    }

    public OrderDto completeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("DONE");
        order = orderRepository.save(order);
        
        if (order.getListingId() != null) {
            Listing listing = listingRepository.findById(order.getListingId()).orElse(null);
            if (listing != null) {
                listing.setStatus("SOLD");
                listingRepository.save(listing);
            }
        }
        return mapToDto(order);
    }

    private OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setBuyerId(order.getBuyerId());
        dto.setBuyerName(order.getBuyerName());
        dto.setFarmerId(order.getFarmerId());
        dto.setFarmerName(order.getFarmerName());
        if (order.getListingId() != null) {
            dto.setListingId(order.getListingId());
        }
        dto.setCropName(order.getCropName());
        dto.setQuantityKg(order.getQuantityKg());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCommission(order.getCommission());
        dto.setStatus(order.getStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPickupDate(order.getPickupDate());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
