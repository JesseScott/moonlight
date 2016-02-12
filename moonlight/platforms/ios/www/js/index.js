
var app = {

    // Application Constructor
    initialize: function() {
        console.log('initialize');
        this.bindEvents();
    },

    // Bind Event Listeners
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },

    // DeviceReady Event Handler
    onDeviceReady: function() {
      console.log('onDeviceReady');
      app.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        console.log('event: ' + id);
        var parentElement = document.getElementById(id);
    }


};

/*

<!-- JAVASCRIPT -->
    <script type="text/javascript">

      // GLOBALS

      var light = document.getElementById('light');
      var data = document.getElementById('data');
      var about = document.getElementById('about');

      var canvas = document.getElementById('canvas');
      var context = canvas.getContext('2d');

      var rise, set;
      var lat, lon;
      var lumens;
      var index = 0;

      // INIT
      function init() {
        console.log("-- init --");

        // Resize Canvas
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;

        // Hide Screens
        light.style.display = 'block';
        data.style.display = 'none';
        about.style.display = 'none';

        // CHEAT
        //setCanvas(0.80);
        //getMoonTime(49.246292, -123.116226);
      }

      // PHONEGAP

      function onDeviceReady() {
          console.log("-- onDeviceReady --");

          // INIT
          init();

          // Get Current GPS
          var options = {
            maximumAge: 3000,
            timeout: 5000,
            enableHighAccuracy: true
          };
          if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(onSuccess, onError);
          }
          else {
            console.log("ERROR: \ncouldn't find geolocation ability on this device - check permissions and wifi");
          }
      }

      document.addEventListener("deviceready", onDeviceReady, false);

      function onSuccess(position) {
          console.log("-- onSuccess --");

          // Get
          lat = position.coords.latitude;
          lon = position.coords.longitude;
          console.log("lat is " + lat + " and lon is " + lon);

          // Set
          if((lat !=== undefined) && (lon !=== undefined)) {
            var table = document.getElementById('dataTable');
            table.rows[0].cells[1].innerHTML = lat.toFixed(5);
            table.rows[1].cells[1].innerHTML = lon.toFixed(5);
          }

          // Pass
          getMoonTime(lat, lon);
      }

      function onError(error) {
        console.log("-- onError --");
        console.log('error: '    + error.code    + '\n' + 'message: ' + error.message + '\n');
      }

      // SUNCALC

      function getMoonTime(lat, lon) {
        console.log("-- getMoonTime --");
        var times = SunCalc.getMoonTimes(Date.now(), lat, lon);
        console.log(times);

        // Get
        var now = new Date();
        console.log("Now is " + now + '\n');
        rise = times.rise;
        console.log("Rise is " + rise + '\n');
        set = times.set;
        console.log("Set is " + set + '\n');

        // Set
        var table = document.getElementById('dataTable');
        table.rows[2].cells[1].innerHTML = rise.toLocaleTimeString();
        table.rows[3].cells[1].innerHTML = set.toLocaleTimeString();

        // Pass
        //if( (now > rise)  && (now < set) ) {
        if(now > rise) {
          console.log("true - the moon rose at " + rise + " and will set at " + set);
          getMoonIllumination();
        }
        else {
          var remaining =  rise.getHours() - now.getHours();
          console.log("SORRY:" + '\n' + "The Moon Is Not In View Yet..." + '\n' + "It Will Rise In " + remaining + " Hours...");
        }
      }

      function getMoonIllumination() {
        console.log("-- getMoonIllumination --");

        // Get
        var illumination = SunCalc.getMoonIllumination(Date.now());
        console.log("LUM is " + illumination);

        // Set
        var table = document.getElementById('dataTable');
        table.rows[4].cells[1].innerHTML = illumination.fraction;

        // Pass
        setCanvas(illumination.fraction);
      }

      // CANVAS

      function setCanvas(fraction) {
        console.log("-- setCanvas --");

        //fraction = fraction.toFixed(3);
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

      // JQUERY

      // Tap
      $("body").bind('tap', function(event) {
        event.stopImmediatePropagation();
        switchView();
      });

      function switchView() {
        console.log("-- switchView --");

        index++;
        if(index % 3 == 0) {
          light.style.display = 'block';
          data.style.display = 'none';
          about.style.display = 'none';
        }
        else if(index % 3 == 1) {
          light.style.display = 'none';
          data.style.display = 'block';
          about.style.display = 'none';
        }
        else if(index % 3 == 2) {
          light.style.display = 'none';
          data.style.display = 'none';
          about.style.display = 'block';
        }
      }

    </script>



*/
