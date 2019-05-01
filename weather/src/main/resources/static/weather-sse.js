var eventList = new EventSource("stream/weather");

eventList.onmessage = function(event) {
  var weather = JSON.parse(event.data);
  console.log(weather.name)
  document.getElementById("forecast").innerHTML += weather.name + "&nbsp;"+weather.main.temp+"<br>";
};

eventList.onerror = function(err){
    console.log(err);
}