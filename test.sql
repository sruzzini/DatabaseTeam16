drop table prefers cascade constraints;
commit;

create table  prefers (
	allocation_no	int not null,
	symbol			varchar2(20) not null,
	percentage		float not null,
	constraint pk_prefers primary key(allocation_no, symbol)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_allocation_number foreign key(allocation_no) references allocation(allocation_no)
		INITIALLY DEFERRED DEFERRABLE,
	constraint fk_fund_prefer foreign key(symbol) references mutualfund(symbol)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint correct_sum CHECK (NOT EXISTS
			(SELECT * FROM prefers P 
				WHERE 1 = (SELECT SUM(percentage)
							FROM prefers X
							WHERE X.allocation_no = P.allocation_no)))
	INITIALLY DEFERRED DEFERRABLE
);
