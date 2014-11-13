CREATE OR REPLACE FUNCTION get_fund_price(f_symbol in varchar2) return float
IS
curr_price float;
currdate date;
yesterdate date;
BEGIN
	-- get date of last closing price
	select MAX(c_date) into currdate from mutualdate;
	if (to_char(currdate, 'DY') = 'MON') then
		yesterdate := currdate - 3;
	elsif (to_char(currdate, 'DY') = 'SUN') then
		yesterdate := currdate - 2;
	else
		yesterdate := currdate - 1;
	end if;

	-- get price of fund on last closing date
	select nvl(price, 0) into curr_price 
		from closingprice 
		where (symbol = f_symbol AND  p_date = yesterdate);
	return (curr_price);
END;
/
commit;