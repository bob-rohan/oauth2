// Controller
oauth2App.controller('homeController', [ '$scope', '$log', '$http', '$location',
		function($scope, $log, $http, $location) {

			$http.get('http://localhost:8086/uaa/oauth/token', {
				transformResponse: undefined
			}).then(function(tokenResponse){
				
				$scope.token = tokenResponse.data;
				
				$http({
					url: 'http://localhost:8082/resource',
					transformResponse: undefined,
					method: 'GET',
					headers: {
						'X-Auth-Token': tokenResponse.data
					}
				}).then(function(response){
					console.log(response);
					$scope.message = response.data;
				});
				
			})
	
			
			
			$scope.logout = function() {
				  $http.post('logout', {}).finally(function() {
				    $location.path("/");
				  });
			};

		} ]);

oauth2App.controller('loginController', [ '$scope', '$log', '$http', '$location', function($scope, $log, $http, $location) {
	
	$scope.credentials = {
			username: "",
			password: ""
	}
	
	$scope.login = function(){
		
		var headers = $scope.credentials ? {authorization : "Basic "
	        + btoa($scope.credentials.username + ":" + $scope.credentials.password)
	    } : {};
		
		$http.get('user', {headers: headers}).then(function(response){
			if(response.data.name){
				console.log("authenticated");
				$location.path("/home");
			} else{
				console.log("not authenticated");
				$location.path("/login");
			}
		})
		
	}
}]);
