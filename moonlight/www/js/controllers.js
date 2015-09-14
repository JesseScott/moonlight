
angular.module('moonlight.controllers', []);

angular.module('moonlight.controllers')
.controller('LightController', function($scope, $http, $location, $routeParams) {

  $scope.changeView = function(view) {
    console.log('/light --> ' + view);
    $location.url(view);
  }

})
.controller('DataController', function($scope, $http, $location, $routeParams) {

  $scope.changeView = function(view) {
    console.log('/data --> ' + view);
    $location.url(view);
  }

})
.controller('AboutController', function($scope, $http, $location, $routeParams) {

  $scope.changeView = function(view) {
    console.log('about --> ' + view);
    $location.url(view);
  }

});
