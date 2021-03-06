package org.microlending.app.loan.service;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.microlending.app.loan.MicroLendingAppApplication;
import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Sql("classpath:testingDataOnlyClient.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MicroLendingAppApplication.class)
public class RiskAnalysisServiceTests {

	@Autowired
	private RiskAnalysisService riskAnalysisService;

	@Autowired
	private ClientRepository clientRepository;
	
	private static final String IP_ADDRESS = "127.0.0.1";
	
	
	/**
	 * 	Tests for the risk analysis service: 
	 * 	{@link org.microlending.app.loan.service.impl.RiskAnalysisServiceImpl#riskAnalysis(Client, Integer, String)}} 	
	 */
	@Test
	public void testRiskAnalysisNoRisk(){
		Client client = clientRepository.findAll().iterator().next();
		RiskType risk = riskAnalysisService.riskAnalysis(client,100,IP_ADDRESS);
		assertTrue(RiskType.NO_RISK.equals(risk));
	}

	@Test
	public void testRiskAnalysisMaxAmountRisk(){
		Client client = clientRepository.findAll().iterator().next();
		RiskType risk = riskAnalysisService.riskAnalysis(client,10000,IP_ADDRESS);
		assertTrue(RiskType.MAX_AMOUNT.equals(risk));
	}
	
	@Test
	@SqlGroup({
		@Sql("classpath:testingDataOnlyClient.sql"),
		@Sql("classpath:testingDataMaxApplications.sql")}
	)
	public void testRiskAnalysisMaxApplicationsRisk(){
		Client client = clientRepository.findAll().iterator().next();
		RiskType risk = riskAnalysisService.riskAnalysis(client,100,IP_ADDRESS);
		assertTrue(RiskType.MAX_APPLICATIONS.equals(risk));
	}
	
}
