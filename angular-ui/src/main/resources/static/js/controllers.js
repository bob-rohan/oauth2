// Controller
oauth2App.controller('homeController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', 'oauth2Context',
		function($scope, $log, $http, $location, $httpParamSerializer, oauth2Context) {
			
		$scope.getMessage = function(){
			$http({
				method: 'GET',
				url: 'http://localhost:8087/resource',
				headers: oauth2Context.getBearerAuthHeaders(),
				transformResponse: undefined
			}).then(function(response){
				console.log(response);
				$scope.message = response.data;
			});
		}
		
		$scope.refresh = function(){
			$http({
		    	method: 'POST',
		    	url: 'http://localhost:8086/uaa/oauth/token?grant_type=refresh_token&refresh_token=' + oauth2Context.refreshToken,
		    	headers: oauth2Context.getBasicAuthHeaders()
		    }).then(function(response){
		    	console.log(response);
		    	
		    	// success response
		    	if(response.data.access_token){
		    		oauth2Context.accessToken = response.data.access_token;
		    		
		    		$scope.getMessage();
		    	}
		    });	
		}
		
		$scope.logout = function() {
		  $http({
			  method: 'POST',
			  url: 'http://localhost:8086/uaa/ssologout', 
			  headers: oauth2Context.getBearerAuthHeaders() 
		  }).then(function(response){
			  $location.path("/login");  
		  });
		}
		
		// if oauth2Context.accessToken isEmpty return to login
		if(!oauth2Context.accessToken){
			$location.path("/login");	
		} else{
			$scope.getMessage();
		}

		}]);

oauth2App.controller('loginController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', 'oauth2Context', function($scope, $log, $http, $location, $httpParamSerializer, oauth2Context) {
	    
    // user credentials
    $scope.credentials = {
    		username: "",
    		password: ""
    }
	
	$scope.login = function(){
		
	    $http({
	    	method: 'POST',
	    	url: 'http://localhost:8086/uaa/oauth/token?grant_type=password',
	    	headers: oauth2Context.getBasicAuthHeaders(),
	    	data: $httpParamSerializer($scope.credentials)
	    }).then(function(response){
	    	
	    	console.log(response);
	    	
	    	// success response
	    	if(response.data.access_token){
	    		oauth2Context.accessToken = response.data.access_token;
	    		oauth2Context.refreshToken = response.data.refresh_token;
	    		$location.path("/home");
	    	}
	    });
	}
}]);
