
angular.module('moonlight.controllers', []);

angular.module('moonlight.controllers')
.controller('LightController', function($scope, $http, $location) {

  $scope.changeView = function(view) {
    console.log('CHANGING VIEW TO ' + view);
    $location.url(view);
  }

})
.controller('DataController', ['$scope', '$http','$routeParams', function($scope, $http, $routeParams) {


}])
.controller('AboutController', ['$scope', '$http','$routeParams', function($scope, $http, $routeParams) {



}]);
