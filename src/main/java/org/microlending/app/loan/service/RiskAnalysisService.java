package org.microlending.app.loan.service;

import org.microlending.app.loan.domain.Client;

public interface RiskAnalysisService {

	public String riskAnalysis(Client user, Integer Amount);
	
}
