// We use an "Immediate Function" to initialize the application to avoid leaving anything behind in the global scope
(function () {

    /* ---------------------------------- Local Variables ---------------------------------- */

    var service = new ConferenceService();
    service.initialize().done(function () {
        console.log("Service initialized");

        renderHomeView();

        //includeJs("js/suncalc/suncalc.js");
        //$.getScript("js/suncalc/suncalc.js");
        //var SunCalc = require('suncalc');

        //getCurrentTime();
    });

    /* --------------------------------- Event Registration -------------------------------- */

    //$('.search-key').on('keyup', findByName);
    //$('.help-btn').on('click', function() {
    //    alert("PhoneGap Day v1.0");
    //});

    document.addEventListener('deviceready', function () {
      console.log("ready");
  	  if (navigator.notification) { // Override default HTML alert with native dialog
  		  window.alert = function (message) {
  			  navigator.notification.alert(
  				  message,    // message
  				  null,       // callback
  				  "Workshop", // title
  				  'OK'        // buttonName
  			  );
  		  };
  	  }
	   }, false);

    /* ---------------------------------- Local Functions ---------------------------------- */

    function findByName() {
        service.findByName($('.search-key').val()).done(function (sessions) {
            var l = sessions.length;
            var e;
            $('.session-list').empty();
            for (var i = 0; i < l; i++) {
                e = sessions[i];
                $('.session-list').append('<li><a href="#sessions/' + e.id + '">' + e.firstName + ' ' + e.lastName + '</a></li>');
            }
        });
    }

    function renderHomeView() {
		var html =
		  "<h1>moonlight</h1>";
		  $('body').html(html);
	   }


     /* ---------------------------------- Remote Functions ---------------------------------- */

     function includeJs(jsFilePath) {
       var js = document.createElement("script");

       js.type = "text/javascript";
       js.src = jsFilePath;

       document.body.appendChild(js);
     }


  	function getCurrentTime() {
  		var d = new Date();
  		var t = d.getTime();
  		console.log("Time Is " + t);

  	}

  	function getMoonTime() {
      var times = SunCalc.getMoonTimes(new Date(), 51.5, -0.1);
      console.log(times);
  	}



}());
