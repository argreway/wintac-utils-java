function sleep(ms)
{
    return new Promise(
            resolve => setTimeout(resolve, ms));
}

function initialize()
{
    // var bounds = new google.maps.LatLngBounds();

    // Display a map on the page
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        mapTypeId: 'roadmap',
        center: {lat: 39.739, lng: -104.990}
    });
}

async function geoCodeAndRoute(addresses)
{
    var geocoder = new google.maps.Geocoder();
    var markerArray = [];
    for (var i = 0; i < addresses.length; i++)
    {
        if (i != 0 && (i % 5) == 0)
        {
            await sleep(2000);
        }
        console.log("Geocoding " + i)

        var currAddress = addresses[i];
        geocoder.geocode({'address': currAddress}, routeCallBack(addresses, i, markerArray));
    }
}

function routeCallBack(addresses,
                       i,
                       markerArray)
{
    var geoCallBack = function (results,
                                status) {
        console.log("Status [ " + i + "] " + status)
        markerArray[i] = results[0];
        progress(addresses.length, i);
        if (markerArray.length == addresses.length)
        {
            routeWaypoints(markerArray);
        }

    };
    return geoCallBack;
}

function geoCodeAddresses(addresses)
{
    var infoWindow = new google.maps.InfoWindow();
    var geocoder = new google.maps.Geocoder();
    var bounds = new google.maps.LatLngBounds();
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 10,
        center: {lat: 39.739, lng: -104.990},
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    for (var i = 0; i < addresses.length; i++)
    {
        var currAddress = addresses[i];
        geocoder.geocode({'address': currAddress}, makeCallback(i, map, bounds, infoWindow));
    }

    var boundsListener = google.maps.event.addListener((map), 'bounds_changed', function (event) {
        this.setZoom(14);
        google.maps.event.removeListener(boundsListener);
    });
}

function routeWaypoints(locations)
{
    var directionsDisplay = new google.maps.DirectionsRenderer();
    var directionsService = new google.maps.DirectionsService();

    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 10,
        center: {lat: 39.739, lng: -104.990},
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    directionsDisplay.setMap(map);
    directionsDisplay.setPanel(document.getElementById('right-panel'));

    var infowindow = new google.maps.InfoWindow();

    var marker, i;
    var request = {
        travelMode: google.maps.TravelMode.DRIVING
    };

    for (i = 0; i < locations.length; i++)
    {
        var markerContent = "Stop [" + (i + 1) + "] -> " + " " + locations[i].formatted_address;
        console.log(markerContent);
        marker = new google.maps.Marker(
                {
                    position: locations[i].geometry.location,
                    title: markerContent
                });

        google.maps.event.addListener(marker, 'click', (function (marker,
                                                                  i) {
            return function () {
                var markerContent = "Stop [" + (i + 1) + "] -> " + " " + locations[i].formatted_address;
                infowindow.setContent(markerContent);
                infowindow.open(map, marker);
            }
        })(marker, i));

        if (i == 0)
        {

            console.log("Origin " + locations[i].formatted_address)
            request.origin = marker.getPosition();
        }
        else if (i == locations.length - 1)
        {
            console.log("Dest " + locations[i].formatted_address)
            request.destination = marker.getPosition();
        }
        else
        {
            console.log("Waypoint " + locations[i].formatted_address)
            if (!request.waypoints) request.waypoints = [];
            request.waypoints.push(
                    {
                        location: marker.getPosition(),
                        stopover: true
                    });
        }

    }
    directionsService.route(request, function (result,
                                               status) {
        if (status == google.maps.DirectionsStatus.OK)
        {
            directionsDisplay.setDirections(result);
        }
    });
}

function makeCallback(addressIndex,
                      resultsMap,
                      bounds,
                      infoWindow)
{
    var geocodeCallBack = function (results,
                                    status) {

        if (status == 'OK')
        {
            resultsMap.setCenter(results[0].geometry.location);
            bounds.extend(results[0].geometry.location);
            // console.log("cords " + results[0].geometry.location);
            var markerContent = "Stop [" + (addressIndex + 1) + "] -> " + " " + results[0].formatted_address;
            var marker = new google.maps.Marker(
                    {
                        map: resultsMap,
                        position: results[0].geometry.location,
                        title: markerContent
                    });

            resultsMap.fitBounds(bounds);
            google.maps.event.addListener(marker, 'click', (function (marker,
                                                                      markerContent) {
                return function () {
                    infoWindow.setContent(markerContent);
                    infoWindow.open(map, marker);
                }
            })(marker, markerContent));
        }
        else
        {
            throw('No results found: ' + status);
        }
    }
    return geocodeCallBack;
}


////// Progress Bar
var progressTimer,
        loadButton = $("#loadTechRoute"),
        progressbar = $("#progressbar"),
        progressLabel = $(".progress-label"),
        dialogButtons = [{
            text: "Cancel Load",
            click: closeDownload
        }],
        dialog = $("#dialog").dialog({
                                         autoOpen: false,
                                         closeOnEscape: false,
                                         resizable: false,
                                         open: function () {
                                             progressTimer = setTimeout(progress, 10000);
                                         },
                                         beforeClose: function () {
                                             loadButton.button("option", {
                                                 disabled: false,
                                                 label: "Start Download"
                                             });
                                         }
                                     });

progressbar.progressbar({
                            value: false,
                            change: function () {
                                progressLabel.text("Current Progress: " + progressbar.progressbar("value") + "%");
                            },
                            complete: function () {
                                progressLabel.text("Complete!");
                                dialog.dialog("option", "buttons", [{
                                    text: "Close",
                                    click: closeDownload
                                }]);
                                $(".ui-dialog button").last().trigger("focus");
                            }
                        });

var progressCount = 0;

function progress(total,
                  current)
{
    var val = progressbar.progressbar("value") || 0;

    if (current != null)
        progressCount++;
    progressbar.progressbar("value", progressCount);

    if (val <= total)
    {
        progressTimer = setTimeout(progress, 50);
    }
}

function closeDownload()
{
    clearTimeout(progressTimer);
    // dialog .dialog("option", "buttons", dialogButtons) .dialog("close");
    progressbar.progressbar("value", false);
    progressLabel
            .text("Starting download...");
    loadButton.trigger("focus");
}


////// AJAX

// Button Updates AJAX
$("#select-tech").on('change', function () {

    techVal = $('#select-tech').val();
    // $.post("${pageContext.request.contextPath}/maps", {tech: techVal}).done(function (data) {
    $.post("/maps", {tech: techVal}).done(function (data) {
        $('#select-date').html(data);
    });
});


$("#loadTechRoute").on('click', function () {

    // $(this).button("option", {
    //     disabled: true,
    //     label: "Loading Map..."
    // });
    dialog.dialog("open");

    techVal = $('#select-tech').val();
    dateVal = $('#select-date').val();
    // $.post("${pageContext.request.contextPath}/maps", {tech: techVal, date: dateVal}).done(function (data) {
    $.post("/maps", {tech: techVal, date: dateVal}).done(function (data) {

        $('#map').empty();
        $('#right-panel').empty();
        var addrs = JSON.parse(data);
        if (addrs.length < 2)
            geoCodeAddresses(addrs);
        else
            geoCodeAndRoute(addrs);
    });
});