
var eventList = null;
var cities = new Set(["2950159", "4954380"]);//Berlin and Waltham


var eventListOnMessage = function(event) {
    var parsedData = JSON.parse(event.data);
    var jsonEvent = new Map(Object.entries(parsedData));
    var forecastNode = document.getElementById("mainForecast");

    jsonEvent.forEach(function(value, key) {
        if (!cities.has(key)) {
            return;
        }
        var cityNode = document.getElementById(key);
        if (cityNode == null){
            cityNode = document.createElement("LI");
            cityNode.id = key;
            forecastNode.appendChild(cityNode);
        }
        var removeLink = "<a href='#' title='Remove' onclick='deleteCity(this.parentNode.id);return false;'>[X]</a>";
        cityNode.innerHTML = value.name + "[" + value.sys.country + "]" + "&nbsp;" + value.main.temp + removeLink;
    });
};


var getCityId = function(cityAndCountry, callback, errorCallback) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/weather/" + cityAndCountry);
    xhr.send();
    xhr.onload = function() {
        var responseObject = JSON.parse(xhr.response);
        if (xhr.status == 200) {
            callback(responseObject.id);
        } else {
            errorCallback(responseObject);
        }
    };
};


var unsubscribe = function() {
    if (eventList != null) {
        eventList.close();
    }
};


var reSubscribe = function() {
    unsubscribe();

    if (cities.size == 0) {
        return;
    }

    var citiesJoined = Array.from(cities).join(",");
    var eventList = new EventSource("weatherGroup/" + citiesJoined + "/metric/1");
    eventList.onmessage = eventListOnMessage;
    eventList.onerror = function(err){
        console.log(err);
    };
};


var appendCity = function() {
    var inputCity = document.getElementById("inputCity");
    var cityAndCountry = inputCity.value;
    getCityId(cityAndCountry, function(cityId) {
        inputCity.value = "";
        cities.add(String(cityId));
        reSubscribe();
    }, function(error) {
        alert(error.message);
    });
};


var deleteCity = function(cityId) {
    cities.delete(cityId);
    reSubscribe();
    var element = document.getElementById(cityId);
    element.parentNode.removeChild(element);
};


window.onload = function() {
    var addButton = document.getElementById("addCity");
    addButton.addEventListener("click", appendCity);
    var inputCity = document.getElementById("inputCity");
    inputCity.addEventListener("keyup", function(event) {
        event.preventDefault();
        if (event.keyCode === 13) {
            addButton.click();
        }
    });
    reSubscribe();
};
window.onunload = function() {
    unsubscribe();
};
