package com.goviya.service;

import com.goviya.dto.CreateListingRequest;
import com.goviya.dto.ListingDto;
import com.goviya.model.Listing;
import com.goviya.model.User;
import com.goviya.repository.ListingRepository;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public List<ListingDto> getListings(String district, String crop) {
        List<Listing> listings;
        if (district != null && !district.isEmpty()) {
            if (crop != null && !crop.isEmpty()) {
                listings = listingRepository.findByStatusAndDistrictAndCropNameContainingIgnoreCaseOrderByCreatedAtDesc("ACTIVE", district, crop);
            } else {
                listings = listingRepository.findByStatusAndDistrictOrderByCreatedAtDesc("ACTIVE", district);
            }
        } else {
            if (crop != null && !crop.isEmpty()) {
                listings = listingRepository.findByStatusAndCropNameContainingIgnoreCaseOrderByCreatedAtDesc("ACTIVE", crop);
            } else {
                listings = listingRepository.findByStatusOrderByCreatedAtDesc("ACTIVE");
            }
        }
        return listings.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ListingDto getListing(String id) {
        return mapToDto(listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found")));
    }

    public ListingDto createListing(String farmerId, CreateListingRequest request) {
        User farmer = userRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));
        
        Listing listing = new Listing();
        listing.setFarmerId(farmer.getId());
        listing.setFarmerName(farmer.getName());
        listing.setFarmerRating(farmer.getRating());
        listing.setCropName(request.getCropName());
        listing.setQuantityKg(request.getQuantityKg());
        listing.setPricePerKg(request.getPricePerKg());
        listing.setDistrict(request.getDistrict());
        listing.setDescription(request.getDescription());
        listing.setImageUrl(request.getImageUrl());
        listing.setStatus("ACTIVE");
        
        if (request.getExpiresAt() != null) {
            listing.setExpiresAt(request.getExpiresAt());
        } else {
            listing.setExpiresAt(LocalDateTime.now().plusDays(7));
        }
        
        return mapToDto(listingRepository.save(listing));
    }

    public List<ListingDto> getMyListings(String farmerId) {
        return listingRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ListingDto updateListing(String listingId, String farmerId, CreateListingRequest request) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!listing.getFarmerId().equals(farmerId)) {
            throw new RuntimeException("Not authorized");
        }
        
        listing.setCropName(request.getCropName());
        listing.setQuantityKg(request.getQuantityKg());
        listing.setPricePerKg(request.getPricePerKg());
        listing.setDistrict(request.getDistrict());
        listing.setDescription(request.getDescription());
        if (request.getImageUrl() != null) listing.setImageUrl(request.getImageUrl());
        if (request.getExpiresAt() != null) listing.setExpiresAt(request.getExpiresAt());
        
        return mapToDto(listingRepository.save(listing));
    }

    public void deleteListing(String listingId, String farmerId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (!listing.getFarmerId().equals(farmerId)) {
            throw new RuntimeException("Not authorized");
        }
        listingRepository.delete(listing);
    }

    public void markAsSold(String listingId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() -> new RuntimeException("Listing not found"));
        listing.setStatus("SOLD");
        listingRepository.save(listing);
    }

    private ListingDto mapToDto(Listing listing) {
        ListingDto dto = new ListingDto();
        dto.setId(listing.getId());
        dto.setFarmerId(listing.getFarmerId());
        dto.setFarmerName(listing.getFarmerName());
        dto.setFarmerRating(listing.getFarmerRating() != null ? listing.getFarmerRating().doubleValue() : null);
        dto.setCropName(listing.getCropName());
        dto.setQuantityKg(listing.getQuantityKg());
        dto.setPricePerKg(listing.getPricePerKg());
        dto.setDistrict(listing.getDistrict());
        dto.setImageUrl(listing.getImageUrl());
        dto.setStatus(listing.getStatus());
        dto.setExpiresAt(listing.getExpiresAt());
        dto.setCreatedAt(listing.getCreatedAt());
        return dto;
    }
}
