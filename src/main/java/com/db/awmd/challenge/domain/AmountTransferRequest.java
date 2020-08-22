package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AmountTransferRequest {
	@NotNull(message = "Sender account no. cannot be null")
	private String accountFrom;

	@NotNull(message = "Receiver account no. cannot be null")
	private String accountTo;

	@NotNull
	@Min(value = 0, message = "Amount to transfer must be positive.")
	private BigDecimal transferAmount;

	@JsonCreator
	public AmountTransferRequest(@JsonProperty("accountFrom") String accountFrom,
			@JsonProperty("accountTo") String accountTo, @JsonProperty("transferAmount") BigDecimal transferAmount) {
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.transferAmount = transferAmount;
	}
}
