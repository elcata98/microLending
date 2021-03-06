package org.microlending.app.loan.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

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
	
	/*
	 * 	Text messages stored in messages.properties
	 */
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

	@Value("${apply.result.ko.highRisk.maxAmount}")
	private String maxAmount;

	@Value("${apply.result.ko.highRisk.maxApplications}")
	private String maxApplications;

	@Value("${apply.result.ko.activeLoanFound}")
	private String activeLoanFound;

	@Value("${apply.result.ko.validation}")
	private String validationError;

	@Value("${extend.result.ok}")
	private String extendResultOk;

	@Value("${extend.term}")
	private Integer extendTerm;
	
	@Value("${extend.interest}")
	private Double extendInterest;


	/**
	 * 	Method to apply for a loan.
	 * 	Loan will be created if is requested by a valid client 
	 * 	who has no other active loans and no associated risks are found. 
	 * 
	 * @param clientId
	 * @param amount
	 * @param term
	 * @param request
	 * @return HTTP response, that can be either OK (200) or 
	 * 			Bad Request (400) with the corresponding error message
	 */
	@RequestMapping(value="/apply", method=RequestMethod.POST)
	public ResponseEntity<String> applyForLoan(@RequestParam(required=true) Long clientId, @RequestParam(required=true) Integer amount, @RequestParam(required=true) Integer term, HttpServletRequest request){
		ResponseEntity<String> result;

		Client client = clientRepository.findById(clientId).get();
		
//		First check if the user already exists. 
//		Otherwise response will be "Bad Request (400)"
		if(client!=null){
//			Check if the user already has an active loan in the system.
//			In that case response will be "Bad Request (400)"
			
			Loan loan = loanRepository.findActiveLoanByClient(client);
	
			if(loan!=null){
				result = ResponseEntity.badRequest().header(resultError,activeLoanFound).body(activeLoanFound);
			}else{
		//		Prepare the loan application
				String ipAddress = request.getRemoteAddr();
				LoanApplication loanApplication = new LoanApplication();
				loanApplication.setAmount(amount);
				loanApplication.setApplicationDate(new Date());
				loanApplication.setClient(client);
				loanApplication.setTerm(term);
				loanApplication.setApplicationIp(ipAddress);
				
		//		Check for possible risks on that loan
				RiskType risk = riskAnalysisService.riskAnalysis(client,amount,ipAddress);
				loanApplication.setRiskType(risk.toString());
		
				try{
//					We want to keep track of non accepted applications as well
					loanApplicationRepository.save(loanApplication);
					
//					Decide from the risk analysis service result if loan is granted
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
						result = ResponseEntity.badRequest().header(resultError,highRisk).body(highRisk+maxAmount);
						break;
					case MAX_APPLICATIONS:
		//				Max applications in 1 day per user and IP reached
						result = ResponseEntity.badRequest().header(resultError,highRisk).body(highRisk+maxApplications);
						break;
					default:
		//				Default should only hit if new value is added to enum and this code is not update accordingly
						result = ResponseEntity.badRequest().header(resultError,resultErrorUnknown).body(resultErrorUnknown);
					}
				}catch(ValidationException ve){
//					In case that Loan and LoanApplication validation failure
					result = ResponseEntity.badRequest().header(resultError,validationError).body(validationError);
				}
			}
		}else{
			result = ResponseEntity.badRequest().header(resultError,resultErrorClient).body(resultErrorClient);
		}
		return result;
	}
	
	/**
	 * 	Method to extend a loan.
	 * 	Loan will be extended if is requested by a valid client 
	 * 	who already has an active loan (which ill be extended). 
	 * 
	 * @param clientId
	 * @return HTTP response, that can be either OK (200) or 
	 * 			Bad Request (400) with the corresponding error message
	 */
	@RequestMapping(value="/extend", method=RequestMethod.GET)
	public ResponseEntity<String> extendLoan(@RequestParam(value="clientId",required=true) Long clientId){
		ResponseEntity<String> result;

		Client client = clientRepository.findById(clientId).get();

//		First check if the user already exists. 
//		Otherwise response will be "Bad Request (400)"
		if(client!=null){
			Loan loan = loanRepository.findActiveLoanByClient(client);
			
//			Check if the user already has an active loan in the system.
//			Otherwise response will be "Bad Request (400)"
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
	
	/**
	 * 	Method to get all the loans for a given client.
	 * 	It gets first the loan applications for the client and then 
	 * 	queries for the related loans.
	 * 	In case of invalid client, method will return empty result.
	 * 
	 * @param clientId
	 * @return Iterable in JSON format with all the loans for the requested client
	 */
	@RequestMapping(value="/search", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Iterable<Loan>> getLoanHistory(@RequestParam(value="clientId", required=true) Long clientId){
		Client client = clientRepository.findById(clientId).get();
		Iterable<LoanApplication> loanApplications = loanApplicationRepository.findByClient(client); 
		Iterable<Loan> result = loanRepository.findByLoanApplications(loanApplications);
		return new ResponseEntity<Iterable<Loan>>(result,HttpStatus.OK);
	}
	
}
