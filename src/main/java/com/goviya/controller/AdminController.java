package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.model.*;
import com.goviya.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ListingRepository listingRepository;
    private final BuyerRequestRepository buyerRequestRepository;
    private final MarketPriceRepository marketPriceRepository;
    private final ShopProductRepository shopProductRepository;

    // ─── Admin Key Check ───────────────────────────────────
    private boolean isValidAdmin(String adminKey) {
        return "goviya-admin-2025".equals(adminKey);
    }

    // ─── STATS: Users ──────────────────────────────────────
    @GetMapping("/stats/users")
    public ResponseEntity<?> getUserStats(
        @RequestHeader("X-Admin-Key") String adminKey) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        long totalUsers = userRepository.count();
        long farmers = userRepository.findByRole("FARMER").size();
        long buyers = userRepository.findByRole("BUYER").size();
        long shops = userRepository.findByRole("SHOP").size();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", totalUsers);
        stats.put("farmers", farmers);
        stats.put("buyers", buyers);
        stats.put("shops", shops);
        stats.put("changePercent", 12);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ─── STATS: Orders + Revenue ───────────────────────────
    @GetMapping("/stats/orders")
    public ResponseEntity<?> getOrderStats(
        @RequestHeader("X-Admin-Key") String adminKey) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();

        double totalRevenue = allOrders.stream()
            .filter(o -> "PAID".equals(o.getPaymentStatus()))
            .mapToDouble(Order::getTotalPrice)
            .sum();

        double totalCommission = totalRevenue * 0.025;

        long pendingOrders = allOrders.stream()
            .filter(o -> "PENDING".equals(o.getStatus()))
            .count();

        long confirmedOrders = allOrders.stream()
            .filter(o -> "CONFIRMED".equals(o.getStatus()))
            .count();

        long doneOrders = allOrders.stream()
            .filter(o -> "DONE".equals(o.getStatus()))
            .count();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", totalOrders);
        stats.put("pending", pendingOrders);
        stats.put("confirmed", confirmedOrders);
        stats.put("done", doneOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("totalCommission", totalCommission);
        stats.put("changePercent", 18);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    // ─── STATS: Revenue Chart (last N days) ────────────────
    @GetMapping("/stats/revenue-chart")
    public ResponseEntity<?> getRevenueChart(
        @RequestHeader("X-Admin-Key") String adminKey,
        @RequestParam(defaultValue = "30") int days) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Order> allOrders = orderRepository.findAll();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.toString();

            double dayRevenue = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null &&
                    o.getCreatedAt().toLocalDate().equals(date))
                .filter(o -> "PAID".equals(o.getPaymentStatus()))
                .mapToDouble(Order::getTotalPrice)
                .sum();

            long dayOrders = allOrders.stream()
                .filter(o -> o.getCreatedAt() != null &&
                    o.getCreatedAt().toLocalDate().equals(date))
                .count();

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("day", date.getMonthValue() 
                + "/" + date.getDayOfMonth());
            point.put("date", dateStr);
            point.put("revenue", Math.round(dayRevenue));
            point.put("commission", 
                Math.round(dayRevenue * 0.025));
            point.put("orders", dayOrders);

            chartData.add(point);
        }

        return ResponseEntity.ok(ApiResponse.success(chartData));
    }

    // ─── STATS: Revenue by District ────────────────────────
    @GetMapping("/stats/revenue-by-district")
    public ResponseEntity<?> getRevenueByDistrict(
        @RequestHeader("X-Admin-Key") String adminKey) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Order> paidOrders = orderRepository
            .findAll().stream()
            .filter(o -> "PAID".equals(o.getPaymentStatus()))
            .collect(Collectors.toList());

        // Group by farmer district
        Map<String, Double> districtRevenue = new HashMap<>();
        for (Order order : paidOrders) {
            userRepository.findById(order.getFarmerId())
                .ifPresent(farmer -> {
                    String district = farmer.getDistrict();
                    districtRevenue.merge(
                        district, 
                        order.getTotalPrice(), 
                        Double::sum
                    );
                });
        }

        List<Map<String, Object>> result = districtRevenue
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue()
                .reversed())
            .limit(10)
            .map(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("district", e.getKey());
                m.put("revenue", Math.round(e.getValue()));
                m.put("commission", 
                    Math.round(e.getValue() * 0.025));
                return m;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ─── ALL ORDERS (with filters) ─────────────────────────
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
        @RequestHeader("X-Admin-Key") String adminKey,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int limit) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Order> orders = orderRepository.findAll();

        // Filter by status
        if (status != null && !status.equals("ALL")) {
            orders = orders.stream()
                .filter(o -> status.equals(o.getStatus()))
                .collect(Collectors.toList());
        }

        // Sort by latest first
        orders.sort((a, b) -> {
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        // Pagination
        int total = orders.size();
        int fromIdx = page * limit;
        int toIdx = Math.min(fromIdx + limit, total);
        List<Order> paged = fromIdx < total 
            ? orders.subList(fromIdx, toIdx) 
            : new ArrayList<>();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orders", paged);
        response.put("total", total);
        response.put("page", page);
        response.put("totalPages", 
            (int) Math.ceil((double) total / limit));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ─── ALL USERS (with role filter) ─────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
        @RequestHeader("X-Admin-Key") String adminKey,
        @RequestParam(required = false) String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int limit) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<User> users = (role != null && !role.equals("ALL"))
            ? userRepository.findByRole(role)
            : userRepository.findAll();

        users.sort((a, b) -> {
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });

        int total = users.size();
        int fromIdx = page * limit;
        int toIdx = Math.min(fromIdx + limit, total);
        List<User> paged = fromIdx < total
            ? users.subList(fromIdx, toIdx)
            : new ArrayList<>();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("users", paged);
        response.put("total", total);
        response.put("page", page);
        response.put("totalPages",
            (int) Math.ceil((double) total / limit));

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ─── TOP FARMERS by revenue ────────────────────────────
    @GetMapping("/stats/top-farmers")
    public ResponseEntity<?> getTopFarmers(
        @RequestHeader("X-Admin-Key") String adminKey) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Order> paidOrders = orderRepository
            .findAll().stream()
            .filter(o -> "PAID".equals(o.getPaymentStatus()))
            .collect(Collectors.toList());

        Map<String, Double> farmerRevenue = new HashMap<>();
        Map<String, Long> farmerOrderCount = new HashMap<>();

        for (Order order : paidOrders) {
            farmerRevenue.merge(
                order.getFarmerId(),
                order.getTotalPrice(),
                Double::sum
            );
            farmerOrderCount.merge(
                order.getFarmerId(), 1L, Long::sum);
        }

        List<Map<String, Object>> result = farmerRevenue
            .entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue()
                .reversed())
            .limit(5)
            .map(e -> {
                Map<String, Object> m = new LinkedHashMap<>();
                userRepository.findById(e.getKey())
                    .ifPresent(u -> {
                        m.put("farmerId", e.getKey());
                        m.put("name", u.getName());
                        m.put("district", u.getDistrict());
                        m.put("rating", u.getRating());
                        m.put("totalRevenue", 
                            Math.round(e.getValue()));
                        m.put("orderCount", 
                            farmerOrderCount.get(e.getKey()));
                    });
                return m;
            })
            .filter(m -> !m.isEmpty())
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ─── ALL LISTINGS ──────────────────────────────────────
    @GetMapping("/listings")
    public ResponseEntity<?> getAllListings(
        @RequestHeader("X-Admin-Key") String adminKey,
        @RequestParam(required = false) String status) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        List<Listing> listings = (status != null)
            ? listingRepository
                .findByStatusOrderByCreatedAtDesc(status)
            : listingRepository.findAll();

        return ResponseEntity.ok(ApiResponse.success(listings));
    }

    // ─── DELETE user (ban) ─────────────────────────────────
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> banUser(
        @RequestHeader("X-Admin-Key") String adminKey,
        @PathVariable String userId) {

        if (!isValidAdmin(adminKey)) return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));

        userRepository.deleteById(userId);
        log.warn("Admin banned user: {}", userId);
        return ResponseEntity.ok(
            ApiResponse.success("User removed", null));
    }

    // ─── SUMMARY STATS (single call for dashboard) ────────
    @GetMapping("/stats/summary")
    public ResponseEntity<?> getSummary(
        @RequestHeader("X-Admin-Key") String adminKey) {

        if (!isValidAdmin(adminKey)) return unauthorized();

        long totalUsers = userRepository.count();
        long farmers = userRepository.findByRole("FARMER").size();
        long buyers = userRepository.findByRole("BUYER").size();
        long shops = userRepository.findByRole("SHOP").size();

        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();

        double totalRevenue = allOrders.stream()
            .filter(o -> "PAID".equals(o.getPaymentStatus()))
            .mapToDouble(Order::getTotalPrice)
            .sum();

        long activeListings = listingRepository
            .findByStatusOrderByCreatedAtDesc("ACTIVE").size();

        long openRequests = buyerRequestRepository
            .findByStatusOrderByCreatedAtDesc("OPEN").size();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalUsers", totalUsers);
        summary.put("farmers", farmers);
        summary.put("buyers", buyers);
        summary.put("shops", shops);
        summary.put("totalOrders", totalOrders);
        summary.put("totalRevenue", Math.round(totalRevenue));
        summary.put("totalCommission", 
            Math.round(totalRevenue * 0.025));
        summary.put("activeListings", activeListings);
        summary.put("openRequests", openRequests);

        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(403).body(ApiResponse.error("Unauthorized"));
    }
}
