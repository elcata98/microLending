package org.microlending.app.loan.repository;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.LoanApplication;
import org.springframework.data.repository.CrudRepository;

public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Long> {

	Iterable<LoanApplication> findByClient(Client client); 
	
}
