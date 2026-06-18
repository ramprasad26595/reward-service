package com.reward.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_transactions")
public class PurchaseTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(nullable = false)
	private LocalDate transactionDate;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	private String merchantName;

	protected PurchaseTransaction() {
	}

	public PurchaseTransaction(Customer customer, LocalDate transactionDate, BigDecimal amount, String merchantName) {
		this.customer = customer;
		this.transactionDate = transactionDate;
		this.amount = amount;
		this.merchantName = merchantName;
	}

	public Long getId() {
		return id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getMerchantName() {
		return merchantName;
	}
}
