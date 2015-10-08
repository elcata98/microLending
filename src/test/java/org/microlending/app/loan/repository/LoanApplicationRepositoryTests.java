package org.microlending.app.loan.repository;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.microlending.app.loan.MicroLendingAppApplication;
import org.microlending.app.loan.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Sql("classpath:testingData.sql")
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MicroLendingAppApplication.class)
public class LoanApplicationRepositoryTests {

	@Autowired
	private LoanApplicationRepository loanApplicationRepository;

	@Autowired
	private ClientRepository clientRepository;
	
	private static final String IP_ADDRESS = "127.0.0.1";

	
	@Test
	public void testGetApplicationsCountByDateRange(){
		Client client = clientRepository.findAll().iterator().next();

//		Prepare the date range to Check
//		From current day at 00:00 till current time
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date rangeStart = cal.getTime();
		Date rangeEnd = new Date();

		Integer count = loanApplicationRepository.getApplicationsCountByDateRange(client,IP_ADDRESS,rangeStart,rangeEnd);

		assertTrue(count.intValue()==1);
	}

	@Test
	public void testGetApplicationsCountByDateRangeNoApplications(){
		Iterator<Client> clients = clientRepository.findAll().iterator();
		Client client = clients.next();
		client = clients.next();

//		Prepare the date range to Check
//		From current day at 00:00 till current time
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date rangeStart = cal.getTime();
		Date rangeEnd = new Date();

		Integer count = loanApplicationRepository.getApplicationsCountByDateRange(client,IP_ADDRESS,rangeStart,rangeEnd);

		assertTrue(count.intValue()==0);
	}

}
