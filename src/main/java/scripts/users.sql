delete from STUDYPOINT where id > -1;
delete from TASK where id >-1;
delete from SEMESTER_PERIOD where id >-1;


delete from SP_CLASS_STUDYPOINT_USER where users_id > -1;
delete from STUDYPOINTUSER_ROLES where roleName != 'xxx';
delete from USER_ROLE where roleName != "xx";
delete from STUDYPOINT_USER where id > -1;
delete from SP_CLASS where id != "xxx";

insert into USER_ROLE values('Admin');
insert into USER_ROLE values('User');
insert into USER_ROLE values('StudentAdmin');


-- insert into STUDYPOINT_USER(id,username,firstname,lastname,email,phone,password,passwordinitial) values (NULL,'lam','Lars','Mortensen','lam@cphbusiness.dk','12345678','larsx1234','larsx12234');
-- insert into STUDYPOINT_USER(id,username,firstname,lastname,email,phone,password,passwordinitial) values (NULL,'tha','Thomas','Hartmann','tha@cphbusiness.dk','----','thomasx1234','thomasx12234');
-- 
-- insert into STUDYPOINTUSER_ROLES(roleName, userName) values('Admin','lam');
-- insert into STUDYPOINTUSER_ROLES(roleName, userName) values('Admin','tha');