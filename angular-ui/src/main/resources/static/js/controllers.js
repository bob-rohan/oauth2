// Controller
oauth2App.controller('homeController', [ '$scope', '$log', '$http', '$location', 'oauth2Context',
		function($scope, $log, $http, $location, oauth2Context) {
				
		// if oauth2Context.accessToken isEmpty return to login
		if(oauth2Context.accessToken){
			$location.path("/login");	
		}
		
		$scope.logout = function() {
		  $http.post('logout', {}).finally(function() {
		    $location.path("/");
		  });
		};

		}]);

oauth2App.controller('loginController', [ '$scope', '$log', '$http', '$location', function($scope, $log, $http, $location) {
	
	$scope.credentials = {
			username: "",
			password: ""
	}
	
	$scope.login = function(){
		
		var headers = $scope.credentials ? {authorization : "Basic "
	        + btoa($scope.credentials.username + ":" + $scope.credentials.password)
	    } : {};
	    
	    var data = {
	    		client_id: "acme"
	    }
		
	    $http({
	    	method: 'GET',
	    	url: 'http://localhost:8086/uaa/oauth/token?client_id=acme&grant_type=authorization_code',
	    	headers: headers,
	    	data: data
	    }).then(function(response){
	    	console.log(response);
	    });
	    
	    /*
		$http({
			url: 'http://localhost:8087/resource?access_token=' + response.data,
			transformResponse: undefined,
			method: 'GET'
		}).then(function(response){
			console.log(response);
			$scope.message = response.data;
		});*/
	    
	    
		/*$http.get('user', {headers: headers}).then(function(response){
			if(response.data.name){
				console.log("authenticated");
				$location.path("/home");
			} else{
				console.log("not authenticated");
				$location.path("/login");
			}
		})*/
		
	}
}]);
