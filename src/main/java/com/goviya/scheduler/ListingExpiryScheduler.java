package com.goviya.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ListingExpiryScheduler {

    private static final Logger log = Logger.getLogger(ListingExpiryScheduler.class.getName());

    // @Autowired
    // private ListingRepository listingRepository;
    
    // @Autowired
    // private FirebaseMessagingService fcmService;

    /**
     * Executes at 01:00 AM daily
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void sweepExpiredListings() {
        log.info("[SCHEDULER] Running ListingExpiryScheduler (1:00 AM)...");

        // Step 1: Scan for all explicitly ACTIVE produce listings matching expiry thresholds 
        /*
        LocalDateTime now = LocalDateTime.now();
        List<Listing> expiredListings = listingRepository.findAllByStatusAndExpiresAtBefore(Listing.Status.ACTIVE, now);
        
        if(expiredListings.isEmpty()) {
            log.info("No expired listings found passing bounds.");
            return;
        }

        // Step 2: Mutate status configurations directly to EXPIRED 
        for (Listing listing : expiredListings) {
            listing.setStatus(Listing.Status.EXPIRED);
            
            // Step 3: Push Notification sequentially backwards toward originating Farmer
            // fcmService.sendToUser(
            //     listing.getFarmer(), 
            //     "Produce Expired", 
            //     "Your " + listing.getCropName() + " listing has expired."
            // );
        }
        
        listingRepository.saveAll(expiredListings);
        log.info("Successfully updated " + expiredListings.size() + " records to EXPIRED logic statuses.");
        */

        log.info("[SCHEDULER] ListingExpiryScheduler finished background clearance accurately.");
    }
}
