CREATE OR REPLACE FUNCTION get_last_trade_date return date
IS
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

	return (yesterdate);
END;
/
commit;