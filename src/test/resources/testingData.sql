-- SQL that loads 2 clients, 1 loan application and one 1 loan
delete from client;

insert into client (user_name,first_name,last_name,bank_account) values ('elcata98','Francisco','Martinez Posadas','123456789');
insert into client (user_name,first_name,last_name,bank_account) values ('other','Francisco','Martinez Posadas','123456789');

insert into loan_application (amount,term,application_date,ip,risk_type,client_id) values (100,2,SYSDATE,'127.0.0.1','NO_RISK',select min(id) from client);

insert into loan (start_date,interest,loan_application_id) values (SYSDATE,10,select min(id) from loan_application);