package org.microlending.app.loan.controller;

import org.microlending.app.loan.service.RiskAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
	
	@Autowired
	private RiskAnalysisService riskAnalysisService;

	@RequestMapping("/test")
	public String testLoan(){
		return "A Robar Carteras!!!";
	}
	
	@RequestMapping("/apply")
	public String applyForLoan(Long userId, Integer amount, Integer term){
		return "";
	}
	
	@RequestMapping("/extend")
	public String extendLoan(Long userId){
		return "";
	}
	
	@RequestMapping("/getAll")
	public String getLoanHistory(Long userId){
		return "";
	}
	
}
