## Server IP:
120.25.232.98:9999

### Register

* API: /user/register.php
* HTTP Method: POST
* Parameters:
	* name: username (String)
	* password: password (String)

* 400 Bad Request: Happen when the request is from GET.

* 422 Duplicate Name

* 200 OK: Return JSON, including:
  	* _id: need sending everytime
  	* name: need sending everytime 
  	* token: need sending everytime

### Login

* API: /user/login.php
* HTTP Method: POST
* Parameters:
	* name: username (String)
	* password: password (String)

* 400 Bad Request: Happen when the request is from GET.

* 422 Wrong Name or Password

* 200 OK: Return JSON, including:
  	* _id: need sending everytime
  	* name: need sending everytime 
  	* token: need sending everytime


### Update Info

* API: /user/info.php
* HTTP Method: POST
* Parameters:
	* _id: Integer
	* name: String
	* old_password: String
	* new_password: String
	* token: String

* 401 Not Login: Happen when 'token' is tampered.

* 422 Wrong Password: Happen when 'old_password' is wrong.

* 200 OK: Return None. '_id' and token are still available.