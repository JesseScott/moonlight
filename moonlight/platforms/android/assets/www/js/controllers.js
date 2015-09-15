
angular.module('moonlight.controllers', []);

angular.module('moonlight.controllers')
.controller('LightController', function($scope, $http, $location, $routeParams, $rootScope) {

  $scope.changeView = function(view) {
    console.log('/light --> ' + view);
    $location.url(view);
  }

  // GPS
  navigator.geolocation.getCurrentPosition(
    function(position) {
      console.log('Retrieved Position: ' + position.coords.latitude + ' ' + position.coords.longitude);
      $rootScope.position = position;
      $scope.$apply();
    },
    function(e) {
      console.log("Error Retrieving Position " + e.code + " " + e.message)
    }
  );

})
.controller('DataController', function($scope, $http, $location, $routeParams, $rootScope) {
  $scope.position = $rootScope.position;
  console.log('Pulling Position: ' + $scope.position.coords.latitude + ' ' + $scope.position.coords.longitude);

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
