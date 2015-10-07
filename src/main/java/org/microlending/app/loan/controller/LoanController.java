package org.microlending.app.loan.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.Loan;
import org.microlending.app.loan.domain.LoanApplication;
import org.microlending.app.loan.domain.LoanExtension;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.repository.ClientRepository;
import org.microlending.app.loan.repository.LoanApplicationRepository;
import org.microlending.app.loan.repository.LoanExtensionRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
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

	@Autowired
	private LoanExtensionRepository loanExtensionRepository;

	@Value("${result.error}")
	private String resultError;

	@Value("${result.error.client}")
	private String resultErrorClient;

	@Value("${result.error.loan}")
	private String resultErrorLoan;

	@Value("${result.error.unknown}")
	private String resultErrorUnknown;

	@Value("${apply.result.ok}")
	private String applyResultOk;
	
	@Value("${apply.result.ko.highRisk}")
	private String highRisk;

	@Value("${apply.result.ko.activeLoanFound}")
	private String activeLoanFound;

	@Value("${extend.result.ok}")
	private String extendResultOk;

	@Value("${extend.term}")
	private Integer extendTerm;
	
	@Value("${extend.interest}")
	private Double extendInterest;


	@RequestMapping(value="/apply", method=RequestMethod.POST)
	public ResponseEntity<String> applyForLoan(@RequestParam(required=true) Long clientId, @RequestParam(required=true) Integer amount, @RequestParam(required=true) Integer term, HttpServletRequest request){
		ResponseEntity<String> result;

		Client client = clientRepository.findOne(clientId);
		
		if(client!=null){
	//		Check if the user already has an active loan in the system
			Loan loan = loanRepository.findActiveLoanByClient(client);
	
			if(loan!=null){
				result = ResponseEntity.badRequest().header(resultError,activeLoanFound).body(activeLoanFound);
			}else{
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
		
				loanApplicationRepository.save(loanApplication);
				
				switch(risk){
					case NO_RISK:
		//				Loan can be created
						loan = new Loan();
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
			}
		}else{
			result = ResponseEntity.badRequest().header(resultError,resultErrorClient).body(resultErrorClient);
		}
		return result;
	}
	
	@RequestMapping(value="/extend", method=RequestMethod.GET)
	public ResponseEntity<String> extendLoan(@RequestParam(value="clientId",required=true) Long clientId){
		ResponseEntity<String> result;

		Client client = clientRepository.findOne(clientId);
		if(client!=null){
			Loan loan = loanRepository.findActiveLoanByClient(client);
			
			if(loan!=null){
				LoanExtension activeExtension = loan.getActiveLoanExtension();
				LoanExtension newExtension = new LoanExtension();
				newExtension.setActive(Boolean.TRUE);
				newExtension.setLoan(loan);
				
				if(activeExtension==null){
		//			We take the reference values from the loan itself
					newExtension.setOldInterest(loan.getInterest());
					newExtension.setOldTerm(loan.getLoanApplication().getTerm());
				}else{
		//			We take the reference values from the active loan extension
					newExtension.setOldInterest(activeExtension.getNewInterest());
					newExtension.setOldTerm(activeExtension.getNewTerm());
					activeExtension.setActive(Boolean.FALSE);
					loanExtensionRepository.save(activeExtension);
				}
				
				newExtension.setNewInterest(newExtension.getOldInterest()+extendInterest);
				newExtension.setNewTerm(newExtension.getOldTerm()+extendTerm);
				loanExtensionRepository.save(newExtension);

				loan.setCurrentLoanExtension(newExtension);
				List<LoanExtension> extensions = loan.getLoanExtensions();
				extensions.add(newExtension);
				loan.setLoanExtensions(extensions);
				loanRepository.save(loan);
					
				result = ResponseEntity.ok().body(extendResultOk);
			}else{
				result = ResponseEntity.badRequest().header(resultError,resultErrorLoan).body(resultErrorLoan);
			}
		}else{
			result = ResponseEntity.badRequest().header(resultError,resultErrorClient).body(resultErrorClient);
		}
		return result;
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Loan>> getLoanHistory(@Param("clientId") Long clientId){
		Client client = clientRepository.findOne(clientId);
		Iterable<LoanApplication> loanApplications = loanApplicationRepository.findByClient(client); 
		Iterable<Loan> result = loanRepository.findByLoanApplications(loanApplications);
		return new ResponseEntity<Iterable<Loan>>(result,HttpStatus.OK);
	}
	
}
