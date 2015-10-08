package org.microlending.app.loan.repository;

import java.util.Date;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.LoanApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LoanApplicationRepository extends CrudRepository<LoanApplication, Long> {

	Iterable<LoanApplication> findByClient(Client client); 

	@Query("select count(*) from LoanApplication la where la.client=?1 and la.applicationIp=?2 and "
			+ "la.applicationDate between ?3 and ?4")
	Integer getApplicationsCountByDateRange(Client client, String ipAddress, Date startDate, Date endDate);
	
}
