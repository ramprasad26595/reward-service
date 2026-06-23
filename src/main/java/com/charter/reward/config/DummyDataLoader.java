package com.charter.reward.config;

import com.charter.reward.entity.Customer;
import com.charter.reward.entity.PurchaseTransaction;
import com.charter.reward.repository.CustomerRepository;
import com.charter.reward.repository.PurchaseTransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds demo customers and transactions at startup.
 */
@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {

	private final CustomerRepository customerRepository;
	private final PurchaseTransactionRepository transactionRepository;

	@Override
	@Transactional
	public void run(String... args) {
		if (customerRepository.count() > 0) {
			return;
		}

		Customer aarav = customerRepository.save(new Customer("Aarav Sharma", "aarav.sharma@example.com"));
		Customer priya = customerRepository.save(new Customer("Priya Iyer", "priya.iyer@example.com"));
		Customer rohan = customerRepository.save(new Customer("Rohan Mehta", "rohan.mehta@example.com"));

		transactionRepository.saveAll(List.of(
				transaction(aarav, "2026-04-03", "84.25", "Reliance Fresh"),
				transaction(aarav, "2026-04-21", "120.00", "Westside"),
				transaction(aarav, "2026-05-09", "52.40", "Cafe Coffee Day"),
				transaction(aarav, "2026-05-26", "211.75", "Pepperfry"),
				transaction(aarav, "2026-06-12", "149.99", "Croma"),
				transaction(priya, "2026-04-10", "49.99", "Apollo Pharmacy"),
				transaction(priya, "2026-04-18", "75.00", "Decathlon"),
				transaction(priya, "2026-05-05", "101.00", "Bata"),
				transaction(priya, "2026-06-03", "305.40", "Tanishq"),
				transaction(rohan, "2026-04-07", "63.75", "Crossword"),
				transaction(rohan, "2026-05-16", "99.99", "Big Bazaar"),
				transaction(rohan, "2026-06-14", "180.10", "MakeMyTrip")));
	}

	private PurchaseTransaction transaction(Customer customer, String date, String amount, String merchant) {
		return new PurchaseTransaction(customer, LocalDate.parse(date), new BigDecimal(amount), merchant);
	}
}
