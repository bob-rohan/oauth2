// Config
oauth2App.config(function($routeProvider, $httpProvider){
   
    $routeProvider.when('/', {
    	templateUrl: '../html/home.html',
        controller: 'homeController'
    }).otherwise('/');
    
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    
});