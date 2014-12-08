--set transaction read write name "deposit";
--UPDATE customer set balance = 1000.00 where login = 'mike';
--commit;



--set transaction read write name "sell valid";
--ALTER TRIGGER InvestDeposit DISABLE;
--INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
--			 values(8, 'mike', 'MM', TO_DATE('01-APR-14', 'DD-MON-YY'), 'sell', 20, 0, 0);
--ALTER TRGGER InvestDeposit ENABLE;
--commit;



--set transaction read write name "buy valid";
--SET CONSTRAINT valid_balance DEFERRED;
--INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
--			 values(9, 'mike', 'RE', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'buy', -1, 0, 100);
--commit;



--set transaction read write name 'changeAlloc';
--UPDATE mutualdate set c_date = TO_DATE('01-DEC-14', 'DD-MON-YY');
--INSERT INTO allocation values(3, 'mike', TO_DATE('01-DEC-14', 'DD-MON-YY'));
--INSERT INTO prefers values(3, 'RE', .6);
--INSERT INTO prefers values(3, 'MM', .4);
--commit;



set transaction read write name 'fundsByCategory';
SELECT f.symbol, f.name, f.description, f.category, c.price, c.p_date
	FROM mutualfund f join closingprice c on f.symbol = c.symbol
	WHERE f.category = 'stocks' AND c.p_date = get_last_trade_date
	ORDER BY c.price DESC;
commit;



set transaction read only name 'getAllCategories';
SELECT DISTINCT f.category
	FROM mutualfund f join closingprice c on f.symbol = c.symbol
	WHERE c.p_date = get_last_trade_date;
commit;



--Mutualfunds that match the keywords ins the description
set transaction read only name 'getFundsFromSearch';
SELECT f.symbol, f.description
	FROM mutualfund f join closingprice c on f.symbol = c.symbol
	WHERE (c.p_date = get_last_trade_date AND (REGEXP_LIKE(f.description, '^.*bond.*term.*$') OR REGEXP_LIKE(f.description, '^.*term.*bond.*$')));
commit;


























