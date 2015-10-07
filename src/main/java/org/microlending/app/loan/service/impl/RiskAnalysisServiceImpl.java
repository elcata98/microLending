package org.microlending.app.loan.service.impl;

import org.microlending.app.loan.domain.Client;
import org.microlending.app.loan.domain.RiskType;
import org.microlending.app.loan.service.RiskAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class RiskAnalysisServiceImpl implements RiskAnalysisService {

	public RiskType riskAnalysis(Client client, Integer amount) {
		return RiskType.NO_RISK;
	}

}
