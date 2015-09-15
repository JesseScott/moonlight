
angular.module('moonlight.controllers', []);

angular.module('moonlight.controllers')
.controller('LightController', function($scope, $http, $location, $routeParams, $rootScope) {

  // SCOPE
  $rootScope.position  = {};
  $rootScope.moon      = {};

  // CANVAS
  var canvas = document.getElementById('canvas');
  var context = canvas.getContext('2d');
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  // NAV
  $scope.changeView = function(view) {
    console.log('/light --> ' + view);
    $location.url(view);
  }

  // GPS
  navigator.geolocation.getCurrentPosition(
    function(position) {
      $rootScope.position = position;
      $scope.$apply();
      getMoon(position.coords.latitude, position.coords.longitude);
    },
    function(e) {
      console.log("Error Retrieving Position " + e.code + " " + e.message)
    }
  );

  // SUNCALC
  function getMoon(lat, lon) {
    console.log("-- getMoonTime --");

    var now = new Date();
    console.log("Now is " + now);

    var times = SunCalc.getMoonTimes(now, lat, lon);
    console.log("Rise is " + times.rise);
    console.log("Set is " + times.set);

    var illumination = SunCalc.getMoonIllumination(now);
    console.log("Phase is " + illumination.phase);
    console.log("Fraction is " + illumination.fraction);

    $rootScope.moon.rise      = times.rise.toLocaleTimeString();
    $rootScope.moon.set       = times.set.toLocaleTimeString();
    $rootScope.moon.phase     = illumination.phase;
    $rootScope.moon.fraction  = illumination.fraction;


    setCanvas(illumination.fraction);

    //if( (now > rise)  && (now < set) ) {
    if(now > times.rise) {
      console.log("YES - the moon rose at " + times.rise + " and will set at " + times.set);
      // Pass
      //setCanvas(illumination.fraction);
    }
    else {
      var remaining =  times.rise.getHours() - now.getHours();
      console.log("SORRY:" + '\n' + "The Moon Is Not In View Yet... It Will Rise In " + remaining + " Hours...");
    }
  }

  // CANVAS
  function setCanvas(fraction) {
    console.log("-- setCanvas --");

    console.log("Fraction is " + fraction);

    context.rect(0, 0, canvas.width, canvas.height);
    var grad = context.createLinearGradient(0, 0, canvas.width, canvas.height);

    grad.addColorStop(0.000, 'rgba(0, 0, 0, ' + fraction + ')');
    grad.addColorStop(0.050, 'rgba(25, 25, 25, ' + fraction + ')');
    grad.addColorStop(0.100, 'rgba(50, 50, 50, ' + fraction + ')');
    grad.addColorStop(0.150, 'rgba(75, 75, 75, ' + fraction + ')');
    grad.addColorStop(0.200, 'rgba(100, 100, 100, ' + fraction + ')');
    grad.addColorStop(0.250, 'rgba(125, 125, 125, ' + fraction + ')');
    grad.addColorStop(0.300, 'rgba(150, 150, 150, ' + fraction + ')');
    grad.addColorStop(0.350, 'rgba(175, 175, 175, ' + fraction + ')');
    grad.addColorStop(0.400, 'rgba(200, 200, 200, ' + fraction + ')');
    grad.addColorStop(0.450, 'rgba(225, 225, 225, ' + fraction + ')');
    grad.addColorStop(0.500, 'rgba(250, 250, 250, ' + fraction + ')');
    grad.addColorStop(0.550, 'rgba(225, 225, 225, ' + fraction + ')');
    grad.addColorStop(0.600, 'rgba(200, 200, 200, ' + fraction + ')');
    grad.addColorStop(0.650, 'rgba(175, 175, 175, ' + fraction + ')');
    grad.addColorStop(0.700, 'rgba(150, 150, 150, ' + fraction + ')');
    grad.addColorStop(0.750, 'rgba(125, 125, 125, ' + fraction + ')');
    grad.addColorStop(0.800, 'rgba(100, 100, 100, ' + fraction + ')');
    grad.addColorStop(0.850, 'rgba(75, 75, 75, ' + fraction + ')');
    grad.addColorStop(0.900, 'rgba(50, 50, 50, ' + fraction + ')');
    grad.addColorStop(0.950, 'rgba(25, 25, 25, ' + fraction + ')');
    grad.addColorStop(1.000, 'rgba(0, 0, 0, ' + fraction + ')');

    context.shadowBlur = 500;
    context.shadowColor = "white";

    context.fillStyle = grad;
    context.fill();

  }

})
.controller('DataController', function($scope, $http, $location, $routeParams, $rootScope) {

  // DATA
  $scope.position = $rootScope.position;
  $scope.moon = $rootScope.moon;

  // NAV
  $scope.changeView = function(view) {
    console.log('/data --> ' + view);
    $location.url(view);
  }

})
.controller('AboutController', function($scope, $http, $location, $routeParams) {

  // NAV
  $scope.changeView = function(view) {
    console.log('about --> ' + view);
    $location.url(view);
  }

});
