--set transaction read write name 'deposit';
--UPDATE customer set balance = 1000.00 where login = 'mike';
--commit;

set transaction read write name 'deposit mary';
UPDATE customer set balance = 1000.00 where login = 'mary';
commit;

set transaction read write name 'deposit john';
UPDATE customer set balance = 300.00 where login = 'john';
commit;

set transaction read write name 'sell valid';
ALTER TRIGGER InvestDeposit DISABLE;
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(12, 'mike', 'RE', TO_DATE('01-APR-14', 'DD-MON-YY'), 'sell', 20, 0, 0);
ALTER TRIGGER InvestDeposit ENABLE;
commit;

set transaction read write name 'buy valid';
SET CONSTRAINT valid_balance DEFERRED;
INSERT INTO trxlog(trans_id,login,symbol,t_date,action,num_shares,price,amount)
			 values(13, 'mike', 'RE', TO_DATE('29-MAR-14', 'DD-MON-YY'), 'buy', -1, 0, 100);
commit;

--set transaction read write name 'changeAlloc';	
--UPDATE mutualdate set c_date = TO_DATE('01-DEC-14', 'DD-MON-YY');
--INSERT INTO allocation values(3, 'mike', TO_DATE('01-DEC-14', 'DD-MON-YY'));
--INSERT INTO prefers values(3, 'RE', .6);
--INSERT INTO prefers values(3, 'MM', .4);
--commit;


--Returns the stocks for a given user on the most recent mutual date
set transaction read write name 'current_values';
SELECT owns.login, owns.symbol, owns.shares, closingprice.price, (owns.shares * closingprice.price) as current_values 
FROM owns, mutualdate, closingprice
WHERE closingprice.p_date = get_last_trade_date and closingprice.symbol = owns.symbol and owns.login = 'mike';
commit;


--Returns a table for a given customer with the total paid amount for each stock
set transaction read write name 'cost_values';
SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount) as cost_values
FROM trxlog
WHERE trxlog.action = 'buy' and trxlog.login = 'mike'
GROUP BY  trxlog.login, trxlog.symbol;
commit;


--Returns a table for a given customer with the total sales for each stock
set transaction read write name 'sum_sales';
SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount) as sum_sales
FROM trxlog
WHERE trxlog.action = 'sell' and trxlog.login = 'mike'
GROUP BY  trxlog.login, trxlog.symbol;
commit;


--Return all the table necessary to calculate the yield for each stock for a given customer
set transaction read write name 'stock_yield';
SELECT owns.login, owns.symbol, owns.shares, closingprice.price, (owns.shares * closingprice.price) as current_values 
FROM owns, mutualdate, closingprice
WHERE closingprice.p_date = get_last_trade_date and closingprice.symbol = owns.symbol and owns.login = 'mike';

--Returns a table for a given customer with the total paid amount for each stock
SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount) as cost_values
FROM trxlog
WHERE trxlog.action = 'buy' and trxlog.login = 'mike'
GROUP BY  trxlog.login, trxlog.symbol;

--Returns a table for a given customer with the total sales for each stock
SELECT trxlog.login, trxlog.symbol, sum(trxlog.amount) as sum_sales
FROM trxlog
WHERE trxlog.action = 'sell' and trxlog.login = 'mike'
GROUP BY  trxlog.login, trxlog.symbol;
commit;


--Returns a table with all the necessary values to calculate the total value of a customer portfolio
set transaction read write name 'total value';
SELECT t1.login, t2.current_values as current_values, sum(t1.amount) as cost_values, t2.sum_sales as sum_sales
FROM trxlog t1
	JOIN (SELECT t2.login, sum(t2.amount) as sum_sales, t3.current_values as current_values
	FROM trxlog t2
		JOIN (	SELECT owns.login, sum(owns.shares * closingprice.price) as current_values 
			FROM owns, mutualdate, closingprice
			WHERE closingprice.p_date = get_last_trade_date and closingprice.symbol = owns.symbol and owns.login = 'mike'
			GROUP BY owns.login) t3 ON t3.login = t2.login
	WHERE t2.action = 'sell' and t2.login = 'mike'
	GROUP BY t2.login, t3.current_values) t2 ON t1.login = t2.login
WHERE t1.action = 'buy' and t1.login = 'mike'
GROUP BY t1.login, t2.sum_sales, t2.current_values;
commit;


--the top k highest volume categories (highest count of shares sold)
--k needs to be negative
set transaction read write name 'highestVolume';
SELECT *
FROM
(
	SELECT trxlog.symbol, sum(trxlog.num_shares) as highestVolume
	FROM trxlog, mutualdate
	WHERE trxlog.action = 'sell' and ADD_MONTHS(mutualdate.c_date, -5) <= trxlog.t_date
	GROUP BY  trxlog.symbol
	ORDER BY sum(trxlog.num_shares) DESC
)
WHERE ROWNUM <= 2;
commit;


--the top k most investors (highest invested amount)
--k needs to be negative
set transaction read write name 'highestInvest';
SELECT *
FROM
(
	SELECT trxlog.login, sum(trxlog.amount) as highestInvest
	FROM trxlog, mutualdate
	WHERE trxlog.action = 'buy' and ADD_MONTHS(mutualdate.c_date, -5) <= trxlog.t_date
	GROUP BY  trxlog.login
	ORDER BY sum(trxlog.amount) DESC
)
WHERE ROWNUM <= 2;
commit;