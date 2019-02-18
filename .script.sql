---xavapro from v5.6 to v6.0.2

-- From v5.6 to v5.7
alter table oxusers add column passwordRecoveringCode varchar(32);
alter table oxusers add column passwordRecoveringDate date;
create index UK_1kr6bnx5w22vlyob1y4p8qqft on OXUSERS (email);
create index UK_4scpsyerabcdjqu5hj2yq3el7 on OXUSERS (passwordRecoveringCode);
alter table oxroles add column description varchar(80);
-- From v5.9 to v5.9.1
alter table oxconfiguration rename column forceLetterAndNumbersInPassword to forceLetterAndNumbersInPasswd;
alter table oxconfiguration rename column inactiveDaysBeforeDisablingUser to inactiveDaysBeforeDisUser;
alter table oxconfiguration rename column guestCanCreateAccountInOrganizations to guestCanCreateAccountInOrgs;
alter table oxconfiguration rename column sharedUsersBetweenOrganizations to sharedUsersBetweenOrgs;
-- From v5.9.1 to v6.0
alter table oxconfiguration add column useEmailAsUserName varchar(1) default 'N' not null;
alter table oxconfiguration add column privacyPolicyOnSignUp varchar(1) default 'N' not null;
alter table oxusers add column privacyPolicyAcceptanceDate date;