package org.microlending.app.loan.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Loan {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
	private Double interest = 10.0;
	
	@Column(nullable=false)
	private Date startDate;

	@Column
	private Date returnedDate;

	@OneToOne
	@JoinColumn(name="LOAN_APPLICATION_ID")
	private LoanApplication loanApplication;

	@OneToOne
	@JoinColumn(name="ACTIVE_LOAN_EXTENSION_ID")
	private LoanExtension activeLoanExtension;

	@OneToMany
	@JoinTable(name="LOAN_LOAN_EXTENSION", 
		joinColumns=
            @JoinColumn(name="LOAN_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="LOAN_EXTENSION_ID", referencedColumnName="ID")
	)
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

	public LoanExtension getActiveLoanExtension() {
		return activeLoanExtension;
	}

	public void setCurrentLoanExtension(LoanExtension activeLoanExtension) {
		this.activeLoanExtension = activeLoanExtension;
	}

	public List<LoanExtension> getLoanExtensions() {
		return loanExtensions;
	}

	public void setLoanExtensions(List<LoanExtension> loanExtensions) {
		this.loanExtensions = loanExtensions;
	}

}
