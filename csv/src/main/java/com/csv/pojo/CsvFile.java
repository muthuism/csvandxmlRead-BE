package com.csv.pojo;

public class CsvFile {

	private Integer reference;
	private String accountNumber;
	private String description;
	private Double startBalance;
	private Double mutation;
	private Double endBalance;

	public CsvFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CsvFile(Integer reference, String accountNumber, String description, Double startBalance, Double mutation,
			Double endBalance) {
		super();
		this.reference = reference;
		this.accountNumber = accountNumber;
		this.description = description;
		this.startBalance = startBalance;
		this.mutation = mutation;
		this.endBalance = endBalance;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getStartBalance() {
		return startBalance;
	}

	public void setStartBalance(Double startBalance) {
		this.startBalance = startBalance;
	}

	public Double getMutation() {
		return mutation;
	}

	public void setMutation(Double mutation) {
		this.mutation = mutation;
	}

	public Double getEndBalance() {
		return endBalance;
	}

	public void setEndBalance(Double endBalance) {
		this.endBalance = endBalance;
	}

}
