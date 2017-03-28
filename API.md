## Server IP:
120.25.232.98:9999

### Register

* API: /user/register.php
* HTTP Method: POST
* Parameters:
	name: username (String)
	password: password (String)

* 400 Bad Request:
	Happen when the request is from GET.

* 422 Duplicate Name

* 200 OK:
	Return JSON, including:
  		_id: need sending everytime
  		name: need sending everytime 
  		token: need sending everytime

