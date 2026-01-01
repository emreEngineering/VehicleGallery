package com.vehiclegallery.service;

import com.vehiclegallery.entity.Listing;
import com.vehiclegallery.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    public List<Listing> findAll() {
        return listingRepository.findAll();
    }

    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    public Listing save(Listing listing) {
        return listingRepository.save(listing);
    }

    public void deleteById(Long id) {
        listingRepository.deleteById(id);
    }

    public List<Listing> findByListingType(String listingType) {
        return listingRepository.findByListingType(listingType);
    }

    public List<Listing> findActiveListings() {
        return listingRepository.findByIsActiveTrue();
    }

    public long count() {
        return listingRepository.count();
    }

    public long countByType(String type) {
        return listingRepository.findByListingType(type).size();
    }

    public List<Listing> findByType(String type) {
        return listingRepository.findByListingType(type);
    }

    public List<Listing> findByDealerId(Long dealerId) {
        return listingRepository.findByDealerId(dealerId);
    }
}
