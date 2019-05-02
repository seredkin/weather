function ready(){
    var forecast = document.getElementById("mainForecast");
    var citySelect = document.getElementById("citySelect");


    for(var i=0; i<citySelect.length; i++){
        var cityTag = citySelect[i];
        var cityNode = document.createElement("LI");
        cityNode.id = cityTag.value;
        cityNode.innerHTML=cityTag.value
        cityNode.appendChild = document.createTextNode(cityTag.value);

        forecast.appendChild(cityNode);
    }
}
document.addEventListener("DOMContentLoaded", ready);

var eventList = new EventSource("stream/weather/"+getCities()+"/5");

function getCities(){
  var citySelect = document.getElementById("citySelect");
  var cities = ""
  for(var i=0; i<citySelect.length; i++){
        cities += citySelect[i].value;
  }
  return cities;
}

eventList.onmessage = function(event) {
  var weather = JSON.parse(event.data);
  var itemId = weather.name+"."+weather.sys.country;
  var cityNode = document.getElementById(itemId);
  cityNode.innerHTML = weather.name+"["+weather.sys.country+"]" + "&nbsp;"+weather.main.temp;
};

eventList.onerror = function(err){
    console.log(err);
}
