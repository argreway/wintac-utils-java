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

function displayMultipleMarkers(addresses)
{
    var bounds = new google.maps.LatLngBounds();
    var map = new google.maps.Map(document.getElementById('map'), {
        zoom: 10,
        center: {lat: 39.739, lng: -104.990},
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    for (var i = 0; i < addresses.length; i++)
    {
        var infoWindow = new google.maps.InfoWindow();
        displayMarker(addresses[i], bounds, infoWindow, map);
    }

    // var boundsListener = google.maps.event.addListener((map), 'bounds_changed', function (event) {
    //     this.setZoom(14);
    //     google.maps.event.removeListener(boundsListener);
    // });
}

function displayMarker(address,
                       bounds,
                       infoWindow,
                       resultsMap)
{
    var markerContent = "Job: " + address.jobName + ", Address: " + address.fullAddress;

    var marker = new google.maps.Marker(
            {
                map: resultsMap,
                position: new google.maps.LatLng(address.lat, address.lng),
                title: markerContent
            });
    resultsMap.setCenter(marker.getPosition());
    bounds.extend(marker.getPosition());

    resultsMap.fitBounds(bounds);
    google.maps.event.addListener(marker, 'click', (function (marker,
                                                              markerContent) {
        return function () {
            infoWindow.setContent(markerContent);
            infoWindow.open(map, marker);
        }
    })(marker, markerContent));
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


    var marker, i;
    var request = {
        travelMode: google.maps.TravelMode.DRIVING
    };

    for (i = 0; i < locations.length; i++)
    {
        var infowindow = new google.maps.InfoWindow();
        var markerContent = "Stop [" + (i + 1) + "] -> " + " " + locations[i].address;
        console.log(markerContent);
        marker = new google.maps.Marker(
                {
                    position: new google.maps.LatLng(locations[i].lat, locations[i].lng),
                    title: markerContent
                });

        google.maps.event.addListener(marker, 'click', (function (marker,
                                                                  markerContent) {
            return function () {
                infowindow.setContent(markerContent);
                infowindow.open(map, marker);
            }
        })(marker, i));

        if (i == 0)
        {

            console.log("Origin " + locations[i].address)
            request.origin = marker.getPosition();
        }
        else if (i == locations.length - 1)
        {
            console.log("Dest " + locations[i].address)
            request.destination = marker.getPosition();
        }
        else
        {
            console.log("Waypoint " + locations[i].address)
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

    let monthVal = $('#month-picker').val();
    techVal = $('#select-tech').val();
    // $.post("${pageContext.request.contextPath}/maps", {tech: techVal}).done(function (data) {
    $.post("/maps", {tech: techVal, month: monthVal}).done(function (data) {
        $('#select-date').html(data);
    });
});

$('.monthpicker').MonthPicker();
// $('.selector').MonthPicker({
// 	OnBeforeMenuClose: function(event){
$('#month-picker').MonthPicker('option', 'OnAfterChooseMonth', function () {
// $("#month-picker").on('change', function () {

    // $.post("${pageContext.request.contextPath}/maps", {tech: techVal}).done(function (data) {
    let monthVal = $('#month-picker').val();
    $.post("/maps", {month: monthVal}).done(function (data) {
        $('#select-tech').html(data);
    });
});


$("#loadTechRoute").on('click', function () {

    // $(this).button("option", {
    //     disabled: true,
    //     label: "Loading Map..."
    // });
    //dialog.dialog("open");

    let monthVal = $('#month-picker').val();
    techVal = $('#select-tech').val();
    dateVal = $('#select-date').val();
    // $.post("${pageContext.request.contextPath}/maps", {tech: techVal, date: dateVal}).done(function (data) {
    $.post("/maps", {month: monthVal, tech: techVal, date: dateVal}).done(function (data) {

        $('#map').empty();
        $('#right-panel').empty();
        var addrs = JSON.parse(data);
        if (addrs.length < 2 || dateVal == "ALL")
            displayMultipleMarkers(addrs);
        else
            routeWaypoints(addrs);
    });
});

// $(document).ready(function () {
//     Default functionality.
// });