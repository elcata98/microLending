package org.microlending.app.loan;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.microlending.app.loan.controller.LoanControllerTests;
import org.microlending.app.loan.repository.LoanApplicationRepositoryTests;
import org.microlending.app.loan.repository.LoanRepositoryTests;
import org.microlending.app.loan.service.RiskAnalysisServiceTests;


/**
 * 	Convenience test class that can be used to run all
 * 	application tests as JUnit test from preferred IDE
 * 
 * 	@author f.martinez.posadas
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
	LoanControllerTests.class,
	LoanApplicationRepositoryTests.class,
	LoanRepositoryTests.class,
	RiskAnalysisServiceTests.class
})
public class MicroLendingAppApplicationTests {

}
