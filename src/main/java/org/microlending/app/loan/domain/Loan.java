package org.microlending.app.loan.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Loan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Column
	private Double interest = 10.0;
	
	@NotNull
	@Column
	private Date startDate;

	@Column
	private Date returnedDate;

	@OneToOne
	private LoanApplication loanApplication;

	@OneToOne
	private LoanExtension currentLoanExtension;

	@OneToMany
	private List<LoanExtension> loanExtensions;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(Date returnedDate) {
		this.returnedDate = returnedDate;
	}

	public LoanApplication getLoanApplication() {
		return loanApplication;
	}

	public void setLoanApplication(LoanApplication loanApplication) {
		this.loanApplication = loanApplication;
	}

	public LoanExtension getCurrentLoanExtension() {
		return currentLoanExtension;
	}

	public void setCurrentLoanExtension(LoanExtension currentLoanExtension) {
		this.currentLoanExtension = currentLoanExtension;
	}

	public List<LoanExtension> getLoanExtensions() {
		return loanExtensions;
	}

	public void setLoanExtensions(List<LoanExtension> loanExtensions) {
		this.loanExtensions = loanExtensions;
	}
	
}
