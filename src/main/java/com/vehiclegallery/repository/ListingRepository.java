package com.vehiclegallery.repository;

import com.vehiclegallery.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByListingType(String listingType);

    List<Listing> findByIsActiveTrue();

    List<Listing> findByVehicleId(Long vehicleId);

    List<Listing> findByDealerId(Long dealerId);
}
