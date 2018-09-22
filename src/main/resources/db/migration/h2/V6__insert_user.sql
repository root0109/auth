INSERT INTO ZAPRIT_USER (ID, EMAIL, USER_NAME, FIRST_NAME, LAST_NAME, PENDING_EMAIL, PASSWORD, ENABLED, ACCOUNT_NON_EXPIRED, ACCOUNT_NON_LOCKED, CREDENTIALS_NON_EXPIRED, CONFIRMATION_TOKEN,SIGNUP_TYPE,COMPANY_ID) VALUES
	('1', 'zadmin@zapritlabs.io', 'admin' , 'vaibhav', 'Singh', null, /*admin1234*/'$2a$08$qvrzQZ7jJ7oy2p/msL4M0.l83Cd0jNsX6AJUitbgRXGzge4j035ha', true, true, true, true, null, 1, 'TestCompanyId'),
	('2', 'root@zapritlabs.io', 'reader','Groot' , 'Best' ,null, /*reader1234*/'$2a$08$dwYz8O.qtUXboGosJFsS4u19LHKW7aCQ0LXXuNlRfjjGKwj5NfKSe',  true, true, true, true, null, 1, 'TestCompanyId'),
	('3', 'cyby@zapritlabs.io','modifier','Galvin','Belson', null, /*modifier1234*/'$2a$08$kPjzxewXRGNRiIuL4FtQH.mhMn7ZAFBYKB3ROz.J24IX8vDAcThsG', true, true, true, true, null, 1, 'TestCompanyId'),
	('4', 'torpedo@zapritlabs.io','reader2','Galvin','Belson', null, /*reader1234*/'$2a$08$vVXqh6S8TqfHMs1SlNTu/.J25iUCrpGBpyGExA.9yI.IlDRadR6Ea', true, true, true, true, null, 1, 'TestCompanyId');
	

INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','1');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','2');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','3');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','4');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','5');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','6');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','7');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','8');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('1','9');
                                                                 
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('2','2');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('2','6');
                                                                 
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('3','3');
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('3','7');
                                                             
INSERT INTO USERS_AUTHORITIES(USER_ID, AUTHORITY_ID) VALUES ('4','9');