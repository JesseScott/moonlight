'use strict';


	var moonlight = angular.module('moonlight', ['ngRoute','moonlight.controllers']);
	//var moonlight = angular.module('moonlight', ['ngRoute']);

	moonlight.config(['$routeProvider', function($routeProvider) {
	  $routeProvider.
	  when('/light', {
	    templateUrl: 'partials/light.html',
	    controller: 'LightController'
	  }).
	  when('/data', {
	    templateUrl: 'partials/data.html',
	    controller: 'DataController'
	  }).
	  when('/about', {
	    templateUrl: 'partials/about.html',
	    controller: 'AboutController'
	  }).
	  otherwise({
	    redirectTo: '/light'
	  });
	}]);
