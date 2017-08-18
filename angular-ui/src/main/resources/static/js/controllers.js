// Controller
oauth2App.controller('homeController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', '$cookies', 'oauth2Context',
		function($scope, $log, $http, $location, $httpParamSerializer, $cookies, oauth2Context) {
			
		$scope.getMessage = function(){
			$log.debug('getting message from resource server');
			
			$http({
				method: 'GET',
				url: 'http://localhost:8087/resource',
				headers: oauth2Context.getBearerAuthHeaders(),
				transformResponse: undefined
			}).then(function(response){
				$log.debug('successfully retrieved message from resource server');
				$log.debug(response);
				$scope.message = response.data;
			}).catch(function(response){
				$log.debug('unable to receive message from resource sever, try refreshing the access token');
				$log.debug(response);
			});
		}
		
		$scope.refreshTokenThenMessage = function(){
			$log.debug('refreshing access token');
			$http({
		    	method: 'POST',
		    	url: 'http://localhost:8086/uaa/oauth/token?grant_type=refresh_token&refresh_token=' + oauth2Context.refreshToken,
		    	headers: oauth2Context.getBasicAuthHeaders()
		    }).then(function(response){
		    	$log.debug('successfully refreshed access token');
		    	$log.debug(response);
		    	
		    	oauth2Context.accessToken = response.data.access_token;
		    	
		    	$cookies.put("access_token", oauth2Context.accessToken);
		    	
		    	$scope.getMessage();
		    }).catch(function(response){
		    	$log.debug("unable to refresh token, logging out");
		    	$log.debug(response);
		    	
		    	// init context
		    	oauth2Context.accessToken === undefined;
		    	oauth2Context.refreshToken === undefined;
		    	$cookies.put("access_token", null);
		    	$cookies.put("refresh_token", null);
		    	
		    	$location.path("/login");
		    });	
		}
		
		$scope.logout = function() {
		  $log.debug('logging out');
		  $http({
			  method: 'POST',
			  url: 'http://localhost:8086/uaa/ssologout', 
			  headers: oauth2Context.getBearerAuthHeaders() 
		  }).then(function(response){
			  $log.debug('log out successful');
			  $log.debug(response);
			  $location.path("/login");  
		  }).catch(function(response){
			  $log.debug('log out unsuccessful');
			  $log.debug(response);
			  
			  // init context
			  oauth2Context.accessToken === undefined;
			  oauth2Context.refreshToken === undefined;
			  $cookies.put("access_token", null);
			  $cookies.put("refresh_token", null);
			  
			  $location.path("/login");
		  });
		}
		
		// if oauth2Context.accessToken isEmpty return to login
		if(!oauth2Context.accessToken){
			$log.debug('access token isEmpty, try getting token from cookie');
			
			oauth2Context.accessToken = $cookies.get("access_token");
			oauth2Context.refreshToken = $cookies.get("refresh_token");
			
			if(!oauth2Context.accessToken){
				$log.debug('access token isEmpty, routing to the login page');
				$location.path("/login");	
			}
		}
		
		$log.debug('log message on controller load');
		$scope.getMessage();

		}]);

oauth2App.controller('loginController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', '$cookies', 'oauth2Context', function($scope, $log, $http, $location, $httpParamSerializer, $cookies, oauth2Context) {
	    
    // user credentials
    $scope.credentials = {
    		username: "",
    		password: ""
    }
	
	$scope.login = function(){
		$log.debug('logging in');
    	
	    $http({
	    	method: 'POST',
	    	url: 'http://localhost:8086/uaa/oauth/token?grant_type=password',
	    	headers: oauth2Context.getBasicAuthHeaders(),
	    	data: $httpParamSerializer($scope.credentials)
	    }).then(function(response){
	    	$log.debug('login successful');
	    	console.log(response);
	    	
	    	// success response
	    	if(response.data.access_token){
	    		$cookies.put("access_token", response.data.access_token);
	    		$cookies.put("refresh_token", response.data.refresh_token);
	    		
	    		oauth2Context.accessToken = response.data.access_token;
	    		oauth2Context.refreshToken = response.data.refresh_token;
	    		
	    		$location.path("/home");
	    	}
	    }).catch(function(response){
	    	$log.debug('unsuccessful login');
	    	$log.debug('response');
	    	// TODO set warning for 401 etc..
	    });
	}
}]);
