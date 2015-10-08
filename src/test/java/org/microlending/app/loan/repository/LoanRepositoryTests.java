package org.microlending.app.loan.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.microlending.app.loan.MicroLendingAppApplication;
import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.Loan;
import org.microlending.app.loan.domain.LoanApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Sql("classpath:testingData.sql")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MicroLendingAppApplication.class)
public class LoanRepositoryTests {

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private LoanApplicationRepository loanApplicationRepository;

	@Autowired
	private ClientRepository clientRepository;
	
	@Test
	public void testFindByLoanApplications(){
		Client client = clientRepository.findAll().iterator().next();
		
		Iterable<LoanApplication> loanApplications = loanApplicationRepository.findByClient(client);
		Loan fromApplication = loanApplications.iterator().next().getLoan();
		
		Iterable<Loan> loans = loanRepository.findByLoanApplications(loanApplications);
		Loan fromRepository = loans.iterator().next();
		assertTrue(fromApplication.getId().equals(fromRepository.getId()));
	}

	@Test
	public void testFindByLoanApplicationsNoLoans(){
		Iterator<Client> clients = clientRepository.findAll().iterator();
		Client client = clients.next();
		client = clients.next();

		Iterable<LoanApplication> loanApplications = loanApplicationRepository.findByClient(client);
		assertFalse(loanApplications.iterator().hasNext());
		
		Iterable<Loan> loans = loanRepository.findByLoanApplications(loanApplications);
		assertFalse(loans.iterator().hasNext());
	}

	@Test
	public void testFindActiveLoanByClient(){
		Client client = clientRepository.findAll().iterator().next();
		Loan loan = loanRepository.findActiveLoanByClient(client);
		assertNotNull(loan);
	}

	@Test
	public void testFindActiveLoanByClientNoLoan(){
		Iterator<Client> clients = clientRepository.findAll().iterator();
		Client client = clients.next();
		client = clients.next();
		Loan loan = loanRepository.findActiveLoanByClient(client);
		assertNull(loan);
	}

	@Test
	public void testFindActiveLoanByClientNoActiveLoan(){
		Client client = clientRepository.findAll().iterator().next();
		Loan loan = loanRepository.findActiveLoanByClient(client);
		assertNotNull(loan);
		
		loan.setReturnedDate(new Date());
		loanRepository.save(loan);
		
		loan = loanRepository.findActiveLoanByClient(client);
		assertNull(loan);
	}

}
