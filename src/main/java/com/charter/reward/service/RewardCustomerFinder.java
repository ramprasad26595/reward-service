package com.charter.reward.service;

import com.charter.reward.entity.Customer;
import com.charter.reward.exception.ResourceNotFoundException;
import com.charter.reward.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Resolves customer data for reward calculations.
 */
@Component
@RequiredArgsConstructor
public class RewardCustomerFinder {

	private final CustomerRepository customerRepository;

	/**
	 * Finds a customer by identifier.
	 *
	 * @param customerId customer identifier
	 * @return matching customer
	 */
	public Customer findById(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer " + customerId + " was not found"));
	}

	/**
	 * Returns all customers.
	 *
	 * @return all customers
	 */
	public List<Customer> findAll() {
		return customerRepository.findAll();
	}
}
