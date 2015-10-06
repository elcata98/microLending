package org.microlending.app.loan.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class LoanExtension {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(nullable=false)
//	@Min(1)
	private Integer oldTerm;

	@Column(nullable=false)
//	@Min(1)
	private Integer newTerm;

	@Column(nullable=false)
	private Double oldInterest;

	@Column(nullable=false)
	private Double newInterest;

	@Column(nullable=false)
	private Boolean active;

	@ManyToOne
	@JsonIgnore
	private Loan loan;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOldTerm() {
		return oldTerm;
	}

	public void setOldTerm(Integer oldTerm) {
		this.oldTerm = oldTerm;
	}

	public Integer getNewTerm() {
		return newTerm;
	}

	public void setNewTerm(Integer newTerm) {
		this.newTerm = newTerm;
	}

	public Double getOldInterest() {
		return oldInterest;
	}

	public void setOldInterest(Double oldInterest) {
		this.oldInterest = oldInterest;
	}

	public Double getNewInterest() {
		return newInterest;
	}

	public void setNewInterest(Double newInterest) {
		this.newInterest = newInterest;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}
			
}
