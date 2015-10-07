package org.microlending.app.loan.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@Entity
public class LoanApplication {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
//	@Min(100)
//	@Max(5000)
	private Integer amount;
	
	@Column(nullable=false)
//	@Min(1)
	private Integer term;
	
	@Column(nullable=false)
	@JsonFormat(shape=Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
	private Date applicationDate;
	
	@Column(nullable=false, name="ip")
	private String applicationIP;
	
	@Column(nullable=false)
	private String riskType;
	
	@OneToOne(mappedBy="loanApplication")
	@JsonIgnore
	private Loan loan;
	
	@ManyToOne
	@JoinColumn(name="CLIENT_ID")
	private Client client;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public String getApplicationIP() {
		return applicationIP;
	}

	public void setApplicationIP(String applicationIP) {
		this.applicationIP = applicationIP;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
