-- SQL that only loads 2 clients
delete from client;

insert into client (user_name,first_name,last_name,bank_account) values ('elcata98','Francisco','Martinez Posadas','123456789');
insert into client (user_name,first_name,last_name,bank_account) values ('other','Francisco','Martinez Posadas','123456789');