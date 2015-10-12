package org.microlending.app.loan.repository;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.Loan;
import org.microlending.app.loan.domain.LoanApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LoanRepository extends CrudRepository<Loan, Long> {

	@Query("select l from Loan l where l.loanApplication in ?1")
	Iterable<Loan> findByLoanApplications(Iterable<LoanApplication> loanApplications); 
	
	@Query("select l from Loan l where l.loanApplication.client = ?1 and l.returnedDate is null")
	Loan findActiveLoanByClient(Client client);

}
