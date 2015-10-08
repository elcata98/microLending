package org.microlending.app.loan.service;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.RiskType;

public interface RiskAnalysisService {

	public RiskType riskAnalysis(Client client, Integer amount, String ipAddress);
	
}
