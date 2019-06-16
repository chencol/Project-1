<%@include file="protect.jsp" %>
<%@page import="java.util.*"%>


display request parameter (year, gender, school) and the time they want to search
if values are not year/gender/school: display error message "invalid order"
note: the order must be a comma-separated example: order=year,gender
follow by end date
if time values are not in (YYYY-MM-DDTHH:MM:SS) format: display error message "invalid date"

this can be done by giving 3 options by their order:
1) year/gender/school
2) year/gender/school/nothing (maybe if first option is selected as year, then in option 2, year is grey out)
3) year/gender/school/nothing

after an option to select date/time they wish to view

case 1: if option 2 and 3 is blank
retrieve the request attribute
example: 
if option 1 is year, retrieve students from year 2013-2017(whatever the year limits are) at the time requested(-15mins) that are in SIS building (through location.csv, get the mac address, then reference to demographic.csv to get number of people and their gender)
sort  by year in ascending order and add all the students up in each year and display their count  (include a case to exclude duplicates)



case 2: if option 3 is blank
example:
if option 1 is year and option 2 is gender, retrieve students from year 2013-2017 (whatever the year limits are) at the time requested(-15mins) that are in SIS building
sort  by year in ascending order and add all the students up in each year and display their count, if student is M, increase M by 1, if F, increase by 1. display end result. (include a case to exclude duplicates)

case 3: if all options is filled up
example:
if option 1 is gender, option 2 is year, option 3 is school
retrieve students from year 2013-2017 (whatever the year limits are) at the time requested(-15mins) that are in SIS building
retrieve all males student from each year, display count. Sort by year in ascending order and retrieve and display number of male students in the year. if school == (whatever school is there), add count by 1.
repeat process through all years. (include a case to exclude duplicates)
Repeat process for female. (include a case to exclude duplicates)




eg 1: only selected year : prints year then number of people(gender combined) http://<host>/json/basic-loc-report?order=year&date=2017-01-01T12:00:00&token=[tokenValue]
eg 2: year then gender : prints year then total number of people then breakdown of gender in terms of m and f http://<host>/json/basic-loc-report?order=year,gender&date=2017-01-01T12:00:00&token=[tokenValue]
eg 3: gender then year then school : prints total number of M first then how many M in the year then in each year break it down by their school. Followed by F. http://<host>/json/basic-loc-report?order=gender,year,school&date=2017-01-01T12:00:00token=[tokenValue]


the output is sorted
1) according to the order specified in the request parameter
2) year in ascending order (2013, 2014, ...)
3) gender (M followed by F)
4) school in alphabetical order (a first, z last)

