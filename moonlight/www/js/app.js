

	var moonlight = angular.module('moonlight', [
		'ngRoute',
		'moonlightControllers'
	]);

	moonlight.config(['$routeProvider',
		function($routeProvider) {
			  $routeProvider.
				  when('/light', {
				    templateUrl: 'partials/light.html',
				    controller: 'LightCtrl'
				  }).
				  when('/data', {
				    templateUrl: 'partials/data.html',
				    controller: 'DataCtrl'
				  }).
				  when('/about', {
				    templateUrl: 'partials/about.html',
				    controller: 'AboutCtrl'
				  }).
				  otherwise({
				    redirectTo: '/light'
		  });
	}]);
