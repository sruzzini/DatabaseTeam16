CREATE or REPLACE PROCEDURE EnsureAllocation(a_num in int,
											 user in varchar2)
AS
firstAlloc date;
lastAlloc date;
currDate date;
begin
	firstAlloc := get_first_allocation(user);
	-- an allocation has been made for the user
	if (firstAlloc <> TO_DATE('01-JAN-1900', 'DD-MON-YYYY')) then
		lastAlloc := get_last_allocation(user);
		-- the user has changed there allocation at least once
		if (firstAlloc <> lastAlloc) then
			-- ensure that the current month is not the same as lastAlloc month
			SELECT MAX(c_date) into currDate from mutualdate;
			if (LAST_DAY(currDate) = LAST_DAY(lastAlloc)) then
				--months are the same, so remove the tuple
				DELETE FROM allocation WHERE allocation_no = a_num;
			end if;
		end if;
	end if;
end;
/
commit;