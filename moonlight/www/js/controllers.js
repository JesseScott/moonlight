
var moonlightControllers = angular.module('moonlightControllers', []);

  moonlightControllers.controller('LightCtrl', ['$scope','$routeParams', '$location',
    function($scope,  $routeParams, $location) {
      alert('LIGHT');
      $scope.message = 'LIGHT';
      $scope.changeView = function(view){
        $location.path(view);
      }
    }
  ]);

  moonlightControllers.controller('AboutCtrl', ['$scope', '$routeParams',
    function($scope, $routeParams) {
      $scope.message = 'ABOUT';
    }
  ]);

  moonlightControllers.controller('DataCtrl', ['$scope', '$routeParams',
    function($scope, $routeParams) {
      $scope.message = 'DATA';
    }
  ]);
