package org.microlending.app.loan.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.Loan;
import org.microlending.app.loan.domain.LoanApplication;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.repository.ClientRepository;
import org.microlending.app.loan.repository.LoanApplicationRepository;
import org.microlending.app.loan.repository.LoanRepository;
import org.microlending.app.loan.service.RiskAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
	
	@Autowired
	private RiskAnalysisService riskAnalysisService;
	
	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private LoanApplicationRepository loanApplicationRepository;
	
	@Value("${result.error}")
	private String resultError;

	@Value("${result.error.unknown}")
	private String resultErrorUnknown;

	@Value("${apply.result.ok}")
	private String applyResultOk;
	
	@Value("${apply.result.ko.highrisk}")
	private String highRisk;
	

	@RequestMapping(value="/apply", method=RequestMethod.POST)
	public ResponseEntity<String> applyForLoan(Long clientId, Integer amount, Integer term, HttpServletRequest request){
		Client client = clientRepository.findOne(clientId);
		
//		Prepare the loan application
		LoanApplication loanApplication = new LoanApplication();
		loanApplication.setAmount(amount);
		loanApplication.setApplicationDate(new Date());
		loanApplication.setClient(client);
		loanApplication.setTerm(term);
		loanApplication.setApplicationIP(request.getRemoteAddr());
		
//		Check for possible risks on that loan
		RiskType risk = riskAnalysisService.riskAnalysis(client, amount);
		loanApplication.setRiskType(risk.toString());

		loanApplication = loanApplicationRepository.save(loanApplication);
		
		ResponseEntity<String> result;
		switch(risk){
			case NO_RISK:
//				Loan can be created
				Loan loan = new Loan();
				loan.setLoanApplication(loanApplication);
				loan.setStartDate(new Date());
				loan = loanRepository.save(loan);
				result = ResponseEntity.ok().body(applyResultOk);
				break;
			case MAX_AMOUNT:
//				Loan application higher than the max amount  
				result = ResponseEntity.badRequest().header(resultError,highRisk).body(highRisk);
				break;
			case MAX_APPLICATIONS:
//				Max applications in 1 day per user and IP reached
				result = ResponseEntity.badRequest().header(resultError,highRisk).body(highRisk);
				break;
			default:
//				Default should only hit if new value is added to enum and this code is not update accordingly
				result = ResponseEntity.badRequest().header(resultError,resultErrorUnknown).body(resultErrorUnknown);
		}
		
		return result;
	}
	
	@RequestMapping(value="extend", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String extendLoan(Long clientId){
		return "";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Loan>> getLoanHistory(@Param("clientId") Long clientId){
		Client client = clientRepository.findOne(clientId);
		Iterable<LoanApplication> loanApplications = loanApplicationRepository.findByClient(client); 
		Iterable<Loan> result = loanRepository.findByLoanApplications(loanApplications);
		return new ResponseEntity<Iterable<Loan>>(result,HttpStatus.OK);
	}
	
}
