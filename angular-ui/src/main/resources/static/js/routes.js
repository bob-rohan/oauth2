// Config
oauth2App.config(function($routeProvider, $httpProvider){
   
    $routeProvider.when('/', {
    	templateUrl: '../html/login.html',
        controller: 'loginController'
    }).when('/home', {
    	templateUrl: '../html/home.html',
        controller: 'homeController'
    }).otherwise('/');
    
});