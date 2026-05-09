package com.shopcart.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Address;

/**
 * Repository interface for Address entity operations
 * Provides database access methods for address management
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
   
    /**
     * Find an address by its ID and user ID
     * @param id the address ID
     * @param userId the user ID
     * @return optional containing the address if found
     */
    Optional<Address> findByIdAndUserId(UUID id, UUID userId);
    
    /**
     * Find all addresses belonging to a specific user
     * @param userId the user ID
     * @return list of addresses belonging to the user
     */
    List<Address> findByUserId(UUID userId);
    
    /**
     * Check if an address exists by its ID and user ID
     * @param id the address ID
     * @param userId the user ID
     * @return true if address exists, false otherwise
     */
    boolean existsByIdAndUserId(UUID id, UUID userId);
}