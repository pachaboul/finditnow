# Introduction #

getBuildings

This is the description of the getBuildings.php interface.

# Details #

This php web service may be accessed with an http GET or POST request.
Any parameters sent will be ignored.

URL: cubist.cs.washington.edu/~johnsj8/getBuildings.php

This web service will respond to the request by sending a JSONArray
of JSONOjects representing a list of all buildings in the FIN database
with their building id, latitude, longitude, name, list of floor ids, and list of floor names.

Array format:
```
[{"bid":int,"lat":int,"long":int,"name":"String","fid":[int,int],"floor_names":
	["String","String"]}, {"bid":int,"lat":int,"long":int,"name":"String","fid":
	[int,int],"floor_names":["String","String"]}]
```


Latitude and Longitude may be removed from response array pending approval.