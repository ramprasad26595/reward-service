package com.charter.reward.repository;

import com.charter.reward.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Customer persistence access.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
