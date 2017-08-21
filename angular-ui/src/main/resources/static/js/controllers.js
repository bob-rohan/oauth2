// Controller
oauth2App.controller('homeController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', '$cookies', 'oauth2Context',
		function($scope, $log, $http, $location, $httpParamSerializer, $cookies, oauth2Context) {
		
		oauth2Context.accessToken = JSON.parse(window.localStorage.getItem("oauth")).oauth.access_token; 
		
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
				$log.debug('unable to receive message from resource sever, token may have expired');
				$log.debug(response);
				oauth2Context.accessToken = undefined;
				window.localStorage.removeItem("oauth");
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
				  window.localStorage.removeItem("oauth");
				  
				  $location.path("/login");
			  });
		}
		
		$scope.getMessage();
		
		}]);

oauth2App.controller('loginController', [ '$scope', '$log', '$http', '$location', '$httpParamSerializer', '$cookies', 'oauth2Context', function($scope, $log, $http, $location, $httpParamSerializer, $cookies, oauth2Context) {
	    
	window.location.href = "http://localhost:8086/uaa/oauth/authorize?client_id=angularui&response_type=token&redirect_uri=http://localhost:8089/html/oauth_callback.html";
    
}]);
