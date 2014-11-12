-- Team 16
--
-- Stephen Ruzzini, Michael Kudlaty

----------------------------------------------------------------------------------
-- Drop Tables
drop table mutualfund cascade constraints;
drop table closingprice cascade constraints;
drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table allocation cascade constraints;
drop table prefers cascade constraints;
drop table trxlog cascade constraints;
drop table owns cascade constraints;
drop table mutualdate cascade constraints;

----------------------------------------------------------------------------------
-- Create Tables
create table  mutualfund (
	symbol		varchar2(20) not null,
	name		varchar2(30) not null,
	description	varchar2(100) not null,
	category	varchar2(10) not null,
	c_date		date not null,
	constraint pk_mutualfund primary key(symbol)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  closingprice (
	symbol		varchar2(20) not null,
	price		float not null,
	p_date		date not null,
	constraint pk_closingprice primary key(symbol, p_date)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_fund_symbol foreign key(symbol) references mutualfund(symbol)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint valid_price CHECK (price >= 0)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  customer (
	login		varchar2(10) not null,
	name		varchar2(20) not null,
	email		varchar2(20) not null,
	address		varchar2(30) not null,
	password	varchar2(10) not null,
	balance		float not null,
	constraint pk_customer primary key(login)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint valid_balance CHECK (balance >= 0)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  administrator (
	login		varchar2(10) not null,
	name		varchar2(20) not null,
	email		varchar2(20) not null,
	address		varchar2(30) not null,
	password	varchar2(10) not null,
	constraint pk_admin primary key(login)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  allocation (
	allocation_no	int not null,
	login			varchar2(10) not null,
	p_date			date not null,
	constraint pk_allocation primary key(allocation_no)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_customer_login foreign key(login) references customer(login)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  prefers (
	allocation_no	int not null,
	symbol			varchar2(20) not null,
	percentage		float not null,
	constraint pk_prefers primary key(allocation_no, symbol)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_allocation_number foreign key(allocation_no) references allocation(allocation_no)
		INITIALLY DEFERRED DEFERRABLE,
	constraint fk_fund_prefer foreign key(symbol) references mutualfund(symbol)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  trxlog (
	trans_id	int not null,
	login		varchar2(10) not null,
	symbol		varchar2(20),
	t_date		date not null,
	action		varchar2(10) not null,
	num_shares	int,
	price		float,
	amount		float,
	constraint pk_trxlog primary key(trans_id)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_customer_log foreign key(login) references customer(login)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_fund_log foreign key(symbol) references mutualfund(symbol)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  owns (
	login		varchar2(10) not null,
	symbol		varchar2(20) not null,
	shares		int not null,
	constraint pk_owns primary key(login, symbol)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_customer_owns foreign key(login) references customer(login)
		INITIALLY IMMEDIATE DEFERRABLE,
	constraint fk_fund_owns foreign key(symbol) references mutualfund(symbol)
		INITIALLY IMMEDIATE DEFERRABLE
);

create table  mutualdate (
	c_date		date not null,
	constraint pk_date primary key(c_date)
		INITIALLY IMMEDIATE DEFERRABLE
);


--!!  INSERT DATA  !!--
INSERT INTO mutualdate values(TO_DATE('04-APR-14', 'DD-MON-YY'));

INSERT INTO customer values('mike', 'Mike', 'mike@pitt.edu', '1st street', 'pwd', 750);
INSERT INTO customer values('mary', 'Mary', 'mary@pitt.edu', '2nd street', 'pwd', 0);
INSERT INTO administrator values('admin', 'Admin', 'admin@pitt.edu', '5th Ave, Pitt', 'root');

INSERT INTO mutualfund values('MM', 'money-market', 'money market, conservative', 'fixed', TO_DATE('06-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('RE', 'real-estate', 'real estate', 'fixed', TO_DATE('09-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('STB', 'short-term-bonds', 'short term bonds', 'bonds', TO_DATE('10-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('LTB', 'long-term-bonds', 'long term bonds', 'bonds', TO_DATE('11-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('BBS', 'balance-bonds-stocks', 'balance bonds and stocks', 'mixed', TO_DATE('12-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('SRBS', 'socail-respons-bonds-stocks', 'social responsibility bonds and stocks', 'mixed', TO_DATE('12-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('GS', 'general-stocks', 'general stocks', 'stocks', TO_DATE('16-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('AS', 'aggressive-stocks', 'aggressive stocks', 'stocks', TO_DATE('23-JAN-14', 'DD-MON-YY'));
INSERT INTO mutualfund values('IMS', 'international-market-stocks', 'internaitional markets stock, risky', 'stocks', TO_DATE('30-JAN-14', 'DD-MON-YY'));

INSERT INTO owns values('mike', 'RE', 50);

INSERT INTO allocation values(0, 'mike', TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO allocation values(1, 'mary', TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO allocation values(2, 'mike', TO_DATE('30-MAR-14', 'DD-MON-YY'));

INSERT INTO prefers values(0, 'MM', .5);
INSERT INTO prefers values(0, 'RE', .5);
INSERT INTO prefers values(1, 'STB', .2);
INSERT INTO prefers values(1, 'LTB', .4);
INSERT INTO prefers values(1, 'BBS', .4);
INSERT INTO prefers values(2, 'GS', .3);
INSERT INTO prefers values(2, 'AS', .3);
INSERT INTO prefers values(2, 'IMS', .4);

INSERT INTO closingprice values('MM', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 11, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 12, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 15, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 14, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 15, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('MM', 16, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 12, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 15, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 14, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 16, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 17, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('RE', 15, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 9, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 10, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 12, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 14, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 10, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('STB', 12, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 12, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 13, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 15, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 12, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 9, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('LTB', 10, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 11, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 14, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 18, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 13, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 15, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('BBS', 16, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 12, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 12, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 14, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 17, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 20, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('SRBS', 20, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 12, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 13, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 15, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 14, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 15, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('GS', 12, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 15, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 14, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 16, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 14, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 17, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('AS', 18, TO_DATE('03-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 10, TO_DATE('28-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 12, TO_DATE('29-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 12, TO_DATE('30-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 14, TO_DATE('31-MAR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 13, TO_DATE('01-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 12, TO_DATE('02-APR-14', 'DD-MON-YY'));
INSERT INTO closingprice values('IMS', 11, TO_DATE('03-APR-14', 'DD-MON-YY'));

INSERT INTO trxlog(trans_id,login,t_date,action,amount)
			 values(0, 'mike', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'deposit', 1000);
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(1, 'mike', 'MM', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'buy', 50, 10, 500);
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(2, 'mike', 'RE', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'buy', 50, 10, 500);
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(3, 'mike', 'MM', TO_DATE('01-APR-14', 'DD-MON-YY'), 'sell', 50, 15, 750);





----------------------------------------------------------------------------------
-- Create Triggers

--*****************************************************--
--					Deposit Trigger                    --
--*****************************************************--
CREATE OR REPLACE TRIGGER MakeDeposit
AFTER INSERT ON trxlog
FOR EACH ROW
WHEN (new.action = 'deposit')
BEGIN

END;
/

--*****************************************************--
--					  Sell Trigger                     --
--*****************************************************--
CREATE OR REPLACE TRIGGER SellShare
AFTER INSERT ON trxlog
FOR EACH ROW
WHEN (new.action = 'sell')
BEGIN
	sell_share(:new.trans_id, :new.login, :new.symbol, :new.num_shares);
END;
/

--*****************************************************--
--					   Buy Trigger                     --
--*****************************************************--
CREATE OR REPLACE TRIGGER BuyShare
AFTER INSERT ON trxlog
FOR EACH ROW
WHEN (new.action = 'buy')
BEGIN
	if (:new.num_shares > 0) then
		buy_share_num(:new.trans_id, :new.login, :new.symbol, :new.num_shares);
	else
		buy_share_amount(:new.trans_id, :new.login, :new.symbol, :new.amount);
	end if;
END;
/


----------------------------------------------------------------------------------
-- Create Stored Procedures

------------Sell Shares--------------
CREATE OR REPLACE PROCEDURE sell_share(transaction int,
									   c_login varchar2(10), 
									   m_symbol varchar2(20),
									   num int)
AS
initial_shares int;
final_shares int;
share_price float;
total_cost float;
initial_balance float;
final_balance float;
BEGIN
	select nvl(shares, 0) into initial_shares from owns where (c_login = login AND m_symbol = symbol);
	select nvl(balance, 0) into initial_balance from customer where c_login = login;
	if (num > initial_shares AND initial_shares > 0) then
		share_price := get_fund_price(m_symbol);
		total_cost := share_price * num;

		final_shares := initial_shares - num;
		final_balance := (initial_balance + (share_price * num));

		-- Update customer balance
		UPDATE customer SET balance = final_balance WHERE login = c_login;

		-- Update owns shares
		if (final_shares > 0)
			UPDATE owns SET shares = final_shares WHERE (login = c_login AND symbol = m_symbol);
		else
			DELETE FROM owns WHERE (login = c_login AND symbol = m_symbol);
		end if;

		-- Update transaction with price
		UPDATE trxlog SET price = share_price WHERE trans_id = transaction;
		UPDATE trxlog SET amount = total_cost WHERE trans_id = transaction;
	else
		dbms_output.put_line('Can not sell more shares than you own');
		DELETE FROM trxlog WHERE trans_id = transaction;
	end if;
END;
/

-------------Buy Shares (number)--------------
CREATE OR REPLACE PROCEDURE buy_share_num(transaction int,
									   	  c_login varchar2(10), 
									   	  m_symbol varchar2(20),
									   	  num int)
AS
initial_shares int;
final_shares int;
share_price float;
total_cost float;
initial_balance float;
final_balance float;
BEGIN
	select nvl(shares, 0) into initial_shares from owns where (login = c_login AND symbol = m_symbol);
	select nvl(balance, 0) into initial_balance from customer where c_login = login;
	-- Ensure we are buying a positive number of shares
	if (num > 0) then
		share_price := get_fund_price(m_symbol);
		total_cost := share_price * num;
		-- Ensure we have enough money to pay for the shares
		if (total_cost < initial_balance) then
			final_shares := initial_shares + num;
			final_balance := (initial_balance - total_cost);

			-- Update customer balance
			UPDATE customer set balance = final_balance where login = c_login;

			-- Update owns shares
			if (initial_shares > 0)
				UPDATE owns set shares = final_shares where (login = c_login AND symbol = m_symbol);
			else
				INSERT INTO owns VALUES (c_login, m_symbol, num);
			end if;

			-- Update transaction with price
			UPDATE trxlog SET price = share_price WHERE trans_id = transaction;
			UPDATE trxlog SET amount = total_cost WHERE trans_id = transaction;
		else
			dbms_output.put_line('Insufficient funds.');
		end if;
	else
		dbms_output.put_line('Must buy a positive number of shares.');
	end if;
END;
/

-------------Buy Shares (Amount)--------------
CREATE OR REPLACE PROCEDURE buy_share_amount(transaction int,
									   	  	 c_login varchar2(10), 
									   	  	 m_symbol varchar2(20),
									   	  	 m_amount float)
AS
initial_shares int;
final_shares int;
share_price float;
num_shares int;
total_cost float;
initial_balance float;
final_balance float;
BEGIN
	select nvl(shares, 0) into initial_shares from owns where (login = c_login AND symbol = m_symbol);
	select nvl(balance, 0) into initial_balance from customer where c_login = login;
	-- Ensure we have enough money to pay for the shares
	if (m_amount > 0 AND m_amount <= initial_balance) then
		share_price := get_fund_price(m_symbol);
		num_shares := FLOOR(m_amount/share_price);
		total_cost := share_price * num_shares;
		
		final_shares := initial_shares + num_shares;
		final_balance := (initial_balance - total_cost);

		-- Update customer balance
		UPDATE customer set balance = final_balance where login = c_login;
		
		-- Update owns shares
		if (initial_shares > 0)
			UPDATE owns set shares = final_shares where (login = c_login AND symbol = m_symbol);
		else
			INSERT INTO owns VALUES (c_login, m_symbol, num);
		end if;

		-- Update transaction with price
		UPDATE trxlog SET price = share_price WHERE trans_id = transaction;
		UPDATE trxlog SET amount = total_cost WHERE trans_id = transaction;
	else
		dbms_output.put_line('Insufficient funds.');
	end if;
END;
/


----------------------------------------------------------------------------------
-- Create Functions

------------Get Fund Price--------------
CREATE OR REPLACE FUNCTION get_fund_price(f_symbol varchar(20)) return float
IS
curr_price float;
BEGIN
	select nvl(price, 0) into curr_price 
		from closingprice 
		where (symbol = f_symbol AND  p_date = (select MAX(p_date)
												from closingprice P
												where P.symbol = f_symbol));
	return (curr_price);
END;
/

------------Get Allocation Number--------------
CREATE OR REPLACE FUNCTION get_curr_allocation(c_login varchar(10)) return int
IS
allocation_num int;
BEGIN
	select nvl(MAX(allocation_no), -1) into allocation_num 
		from allocation
		where (login = c_login);
	return (allocation_num);
END;
/

------------Get First Allocation Date--------------
CREATE OR REPLACE FUNCTION get_first_allocation(c_login varchar(10)) return date
IS
alloc_date date;
BEGIN
	select nvl(MIN(p_date), TO_DATE('01-JAN-1900', 'DD-MON-YYYY')) into alloc_date 
		from allocation
		where (login = c_login);
	return (allocation_num);
END;
/































