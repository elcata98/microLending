-- SQL that loads 3 loan applications
insert into loan_application (amount,term,application_date,ip,risk_type,client_id) values (100,2,SYSDATE,'127.0.0.1','NO_RISK',select min(id) from client);
insert into loan_application (amount,term,application_date,ip,risk_type,client_id) values (1000,2,SYSDATE,'127.0.0.1','NO_RISK',select min(id) from client);
insert into loan_application (amount,term,application_date,ip,risk_type,client_id) values (1000,2,SYSDATE,'127.0.0.1','NO_RISK',select min(id) from client);

