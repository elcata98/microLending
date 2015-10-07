package org.microlending.app.loan.controller;

import java.util.Date;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.Loan;
import org.microlending.app.loan.domain.LoanApplication;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.repository.ClientRepository;
import org.microlending.app.loan.repository.LoanApplicationRepository;
import org.microlending.app.loan.repository.LoanRepository;
import org.microlending.app.loan.service.RiskAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
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

	//POST
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Loan> applyForLoan(Long clientId, Integer amount, Integer term){
		Client client = clientRepository.findOne(clientId);
		
		LoanApplication loanApplication = new LoanApplication();
		loanApplication.setAmount(amount);
		loanApplication.setApplicationDate(new Date());
		loanApplication.setClient(client);
		loanApplication.setTerm(term);
		loanApplication.setApplicationIP("10.1.1.1");
		RiskType risk = riskAnalysisService.riskAnalysis(client, amount);
		loanApplication.setRiskType(risk.toString());
		loanApplication = loanApplicationRepository.save(loanApplication);
		
		ResponseEntity<Loan> result;
		switch(risk){
			case NO_RISK:
				Loan loan = new Loan();
				loan.setLoanApplication(loanApplication);
				loan.setStartDate(new Date());
				loan = loanRepository.save(loan);
				result = ResponseEntity.ok().body(null);
				break;
			default:
				result = ResponseEntity.badRequest().header("Error","Risk associated to the loan is too high").body(null);
		}
		
		return result;
	}
	
	//PUT
//	@RequestMapping("/extend")
	@RequestMapping(method=RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
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
