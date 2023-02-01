--------Ar. Parminder Singh  Chawla


insert into state.eg_user (id, tenantid, locale, username, password, pwdexpirydate, mobilenumber, createddate, lastmodifieddate, createdby, lastmodifiedby, active, name, gender, type, version) 
select nextval('state.seq_eg_user'), 'state', 'en_IN', 'parrychawla123@gmail.com', '$2a$10$uheIOutTnD33x7CDqac1zOL8DMiuz7mWplToPgcf7oxAI9OzRKxmK', '31-Dec-2099', '9814002027', current_date, current_date, 1, 1, true, 'Parminder Singh  Chawla', 1, 'BUSINESS', 0 where not exists (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state');


insert into state.eg_userrole (roleid, userid) 
select (select id from state.eg_role where upper(name)='BUSINESS'), (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state') where not exists (select roleid,userid from state.eg_userrole where roleid in (select id from state.eg_role where upper(name)='BUSINESS') and userid in (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state'));

INSERT INTO state.eg_address (housenobldgapt, subdistrict, postoffice, landmark, country, userid, type, streetroadline, citytownvillage, arealocalitysector, district, state, pincode, id, version)
 select 'NA170', null, 'Chandigarh', null, 'india', (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state'), 'CORRESPONDENCE', 'Building over the New Bridge', 'Chandigarh', 'Chandigarh', 'Chandigarh', 'Chandigarh', null, nextval('state.seq_eg_address'), 0 where not exists (select id from state.eg_address where housenobldgapt='NA170');

INSERT INTO state.egbpa_mstr_stakeholder (id, stakeholdertype, code, licencenumber, buildinglicenceissuedate, coaenrolmentnumber, coaenrolmentduedate, isenrolwithlocalbody, organizationname, organizationaddress, organizationurl, organizationmobno, isonbehalfoforganization, tinnumber, version, createduser, createdate, lastupdateduser, lastupdateddate, buildinglicenceexpirydate, contactperson, designation, source, comments, status, isaddresssame, nooftimesrejected, nooftimesblocked, demand, cinnumber)
 select (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state'), 1, 'CA/1981/06689', 'CA/1981/06689', '17/OCT/2022', 'CA/1981/06689', '31/DEC/2031', null, null, 'H.No.1427, Phase-3B.2, Sector-60, Mohali,', null, null, false, null, 0, 1, now(), 1, now(), '2030-03-31 13:20:49.463', null, null, 3, '', 'APPROVED', true, null, null, null, null where not exists (select id from state.egbpa_mstr_stakeholder where code='CA/2013/60421');

insert into state.eg_businessuser (id) 
select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state' and not exists (select id from state.eg_businessuser where id = (select id from state.eg_user where username='parrychawla123@gmail.com' and tenantid='state'));


