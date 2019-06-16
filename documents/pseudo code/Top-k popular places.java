<%@include file="protect.jsp" %>
<%@page import="java.util.*"%>

Top-k popular places
A user can see the top-k popular places in the whole SIS building at a specified time.
1) Popularity of a place is determined by the number of people inside the place.
Use the 15-minute processing window as in other queries.
If there is more than one update from a device in the processing window for the same semantic place, count the device only once.
If there are more than one update from a device in the processing window for different semantic place, count the device for the last semantic place.
2) Note that the requirement is to find popular places, not popular location_ids.
3) The output should list the rank, semantic place, and the number of people at that semantic place.
4) k should be configurable by the user in a range from 1 to 10 with a default value of 3 (i.e., if not specified, k should be 3). This applies to all other queries listed below as well.

option to select 1-10 with default value of 3
required to enter date with format of (YYYY-MM-DDTHH:MM:SS)


for each location in the SIS , retrieve number of people inside and increase count by 1. 
use location.csv to count which locationID appears the most in the timestamp of -15 mins
convert locationID to sementic place through retrieving it by location_lookup.csv
the top k and number of people is displayed 
include a case to exclude duplicate macaddress. i.e. if macaddress appears more than once, take the locationID of the macaddress at the latest timestamp
