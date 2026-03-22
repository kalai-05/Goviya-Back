package com.goviya.scheduler;

import com.goviya.model.Listing;
import com.goviya.repository.ListingRepository;
import com.goviya.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ListingExpiryScheduler {

    private final ListingRepository listingRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 1 * * *")
    public void expireOldListings() {
        log.info("Running listing expiry scheduler...");
        
        List<Listing> expiredListings = listingRepository.findByStatusAndExpiresAtBefore("ACTIVE", LocalDateTime.now());
        
        int count = 0;
        for (Listing listing : expiredListings) {
            listing.setStatus("EXPIRED");
            listingRepository.save(listing);
            
            notificationService.sendToUser(
                    listing.getFarmerId(),
                    "Listing expired",
                    "Your " + listing.getCropName() + " listing has expired. Create a new listing to sell your produce."
            );
            count++;
        }
        
        log.info("Total expired listings updated: {}", count);
    }
}
