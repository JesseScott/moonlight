
var moonlightControllers = angular.module('moonlightControllers', []);

  moonlightControllers.controller('LightCtrl', ['$scope',
    function($scope) {
      $scope.message = 'LIGHT';
    }
  ]);
