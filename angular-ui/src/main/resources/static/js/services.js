// Services
oauth2App.service('oauth2Context', [ '$log', function($log) {

	this.getBearerAuthHeaders = function() {
		return (this.accessToken) ? {
			authorization : "Bearer " + this.accessToken,
			"Content-type" : "application/x-www-form-urlencoded; charset=utf-8"
		} : {};
	}
	
	this.getBasicAuthHeaders = function(){
		// application aka "client" credentials
    	return {authorization : "Basic " + btoa("acme" + ":" + ""), "Content-type": "application/x-www-form-urlencoded; charset=utf-8"};
	}

} ]);