package org.microlending.app.loan.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.repository.LoanApplicationRepository;
import org.microlending.app.loan.service.RiskAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RiskAnalysisServiceImpl implements RiskAnalysisService {

	@Autowired
	private LoanApplicationRepository loanApplicationRepository;

	@Value("${risk.maxAmount}")
	private Integer maxAmount;

	@Value("${risk.maxApplications}")
	private Integer maxApplications;

	
	public RiskType riskAnalysis(Client client, Integer amount, String ipAddress) {
		RiskType result = RiskType.NO_RISK;
		
//		Prepare the date range to Check
//		From current day at 00:00 till current time
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		Date rangeStart = cal.getTime();
		Date rangeEnd = new Date();
		
		Integer applicationCount = loanApplicationRepository.getApplicationsCountByDateRange(client,ipAddress,rangeStart,rangeEnd);
		if(applicationCount>=maxApplications){
			result = RiskType.MAX_APPLICATIONS;
		}else if(amount>maxAmount){
			result = RiskType.MAX_AMOUNT;
		}
		return result;
	}

}
