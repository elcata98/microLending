package org.microlending.app.loan.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.microlending.app.loan.MicroLendingAppApplication;
import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Sql("classpath:testingData.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = MicroLendingAppApplication.class)
public class LoanControllerTests {

	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private ClientRepository clientRepository;

	private MockMvc mockMvc;
	
	@Value("${result.error}")
	private String resultError;
	
	@Value("${result.error.loan}")
	private String resultErrorLoan;
	
	@Value("${apply.result.ko.activeLoanFound}")
	private String activeLoanFound;
	
	@Value("${apply.result.ko.validation}")
	private String validationError;
	
	@Value("${apply.result.ko.highRisk}")
	private String highRisk;

	@Value("${apply.result.ko.highRisk.maxAmount}")
	private String maxAmount;

	@Value("${apply.result.ko.highRisk.maxApplications}")
	private String maxApplications;

	@Value("${result.error.client}")
	private String resultErrorClient;

	private static final String PARAM_CLIENT_ID = "clientId";
	private static final String PARAM_AMOUNT = "amount";
	private static final String PARAM_TERM = "term";

	private static final Integer INVALID_CLIENT = Integer.MAX_VALUE;
	
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
	
	/**
	 * 	Tests for the get loan history method: 
	 * 	{@link org.microlending.app.loan.controller.LoanController#getLoanHistory(Long)}} 	
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetLoanHistory() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		mockMvc.perform(get("/api/loans/search?"+PARAM_CLIENT_ID+"="+client.getId()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$",hasSize(1)));
	}

	@Test
	public void testGetLoanHistoryNoLoans() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		Long id = client.getId();
		++id;	
		mockMvc.perform(get("/api/loans/search?"+PARAM_CLIENT_ID+"="+id))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$",hasSize(0)));
	}

	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testGetLoanHistoryInvalidClient() throws Exception{
		mockMvc.perform(get("/api/loans/search?"+PARAM_CLIENT_ID+"="+INVALID_CLIENT))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$",hasSize(0)));
	}

	
	/**
	 * 	Tests for the get loan history method: 
	 * 	{@link org.microlending.app.loan.controller.LoanController#applyForLoan(Long, Integer, Integer, javax.servlet.http.HttpServletRequest)}} 	
	 * 
	 * @throws Exception
	 */
	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testApplyForLoan() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		mockMvc.perform(post("/api/loans/apply")
				.param(PARAM_CLIENT_ID,client.getId().toString())
				.param(PARAM_AMOUNT,"100")
				.param(PARAM_TERM,"2"))
			.andExpect(status().isOk());
	}

	@Test
	public void testApplyForLoanActiveLoanError() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		String error = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_CLIENT_ID,client.getId().toString())
								.param(PARAM_AMOUNT,"100")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn()
							.getResponse()
							.getHeader(resultError);
		assertTrue(activeLoanFound.equals(error));
	}

	@Test
	public void testApplyForLoanInvalidClient() throws Exception{
		String error = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_CLIENT_ID,INVALID_CLIENT.toString())
								.param(PARAM_AMOUNT,"100")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn()
							.getResponse()
							.getHeader(resultError);
		assertTrue(resultErrorClient.equals(error));
	}

	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testApplyForLoanValidationError() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		String error = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_CLIENT_ID,client.getId().toString())
								.param(PARAM_AMOUNT,"1")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn()
							.getResponse()
							.getHeader(resultError);
		assertTrue(validationError.equals(error));
	}

	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testApplyForLoanMaxAmountRisk() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		MvcResult result = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_CLIENT_ID,client.getId().toString())
								.param(PARAM_AMOUNT,"10000")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn();
		String error = result.getResponse().getHeader(resultError);
		assertTrue(highRisk.equals(error));
		assertFalse(resultErrorClient.equals(error));
		assertTrue(result.getResponse().getContentAsString().contains(maxAmount));
	}

	@Test
	@SqlGroup({
		@Sql("classpath:testingDataOnlyClient.sql"),
		@Sql("classpath:testingDataMaxApplications.sql")}
	)
	public void testApplyForLoanMaxApplicationsRisk() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		MvcResult result = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_CLIENT_ID,client.getId().toString())
								.param(PARAM_AMOUNT,"100")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn();
		String error = result.getResponse().getHeader(resultError);
		assertTrue(highRisk.equals(error));
		assertTrue(result.getResponse().getContentAsString().contains(maxApplications));
	}

//	Only one of them as per decision. No need to test all required parameters
	@Test
	public void testApplyForLoanMissingClientParameter() throws Exception{
		MvcResult result = mockMvc.perform(post("/api/loans/apply")
								.param(PARAM_AMOUNT,"100")
								.param(PARAM_TERM,"2"))
							.andExpect(status().isBadRequest())
							.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(PARAM_CLIENT_ID));
	}

	
	/**
	 * 	Tests for the get loan history method: 
	 * 	{@link org.microlending.app.loan.controller.LoanController#extendLoan(Long)}} 	
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExtendLoan() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		mockMvc.perform(get("/api/loans/extend?"+PARAM_CLIENT_ID+"="+client.getId()))
				.andExpect(status().isOk());
	}
	
	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testExtendLoanInvalidClient() throws Exception{
		String error = mockMvc.perform(get("/api/loans/extend?"+PARAM_CLIENT_ID+"="+INVALID_CLIENT))
								.andExpect(status().isBadRequest())
								.andReturn()
								.getResponse()
								.getHeader(resultError);
		assertTrue(resultErrorClient.equals(error));
	}

	@Test
	@Sql("classpath:testingDataOnlyClient.sql")
	public void testExtendLoanNoActiveLoan() throws Exception{
		Client client = clientRepository.findAll().iterator().next();
		String error = mockMvc.perform(get("/api/loans/extend?"+PARAM_CLIENT_ID+"="+client.getId()))
								.andExpect(status().isBadRequest())
								.andReturn()
								.getResponse()
								.getHeader(resultError);
		assertTrue(resultErrorLoan.equals(error));
	}

}
