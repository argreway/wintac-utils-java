<%@ page import="com.sentryfire.config.TechProfileConfiguration" %>
<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%--
  Created by IntelliJ IDEA.
  User: argreway
  Date: 8/10/18
  Time: 9:57 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script>
    //@ sourceURL=maps.jsp


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

    function geoCodeAndRoute(addresses)
    {
        var geocoder = new google.maps.Geocoder();
        var markerArray = [];
        for (var i = 0; i < addresses.length; i++)
        {
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
            markerArray[i] = results[0];
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
                                                                          i) {
                    return function () {
                        infoWindow.setContent(markerContent);
                        infoWindow.open(map, marker);
                    }
                })(marker, i));
            }
            else
            {
                throw('No results found: ' + status);
            }
        }
        return geocodeCallBack;
    }


    // Button Updates AJAX
    $("#select-tech").on('change', function () {

        techVal = $('#select-tech').val();
        $.post("${pageContext.request.contextPath}/maps", {tech: techVal}).done(function (data) {
            $('#select-date').html(data);
        });
    });
    // $("#loadTechRoute").on('click', function () {
    //
    //     techVal = $('#select-tech').val();
    //     dateVal = $('#select-date').val();
    <%--$.post("${pageContext.request.contextPath}/maps", {tech: techVal, date: dateVal}).done(function (data) {--%>
    //     var addrs = JSON.parse(data);
    //     var infoWindow = new google.maps.InfoWindow(), marker, i;
    //     var map = new google.maps.Map(document.getElementById('map'), {
    //         zoom: 8,
    //         mapTypeId: 'roadmap',
    //         center: {lat: 39.739, lng: -104.990}
    //     });
    //
    //     geoCodeAddresses(geocoder, addrs, map, infoWindow)
    // });
    // });

    $("#loadTechRoute").on('click', function () {

        techVal = $('#select-tech').val();
        dateVal = $('#select-date').val();
        $.post("${pageContext.request.contextPath}/maps", {tech: techVal, date: dateVal}).done(function (data) {

            $('#map').empty();
            $('#right-panel').empty();
            var addrs = JSON.parse(data);
            if (addrs.length < 2)
                geoCodeAddresses(addrs);
            else
                geoCodeAndRoute(addrs);
        });
    });
</script>

<h3 style="text-align: center; color: darkred;">Tech Route Map</h3>

<br/>

<fieldset>
    <legend>Technician</legend>
    <div class="controlgroup">
        <select id="select-tech">
            <option>-Select Tech-</option>
            <% for (String tech : TechProfileConfiguration.getInstance().getDenTechToProfiles().keySet())
            { %>
            <%="<option>" + tech + "</option>"%>
            <%}%>
        </select>
        <select id="select-date">
            <option>-Select Calendar Date-</option>
        </select>
        <button id="loadTechRoute">Load Tech Route</button>
    </div>
</fieldset>

<!--The div element for the map -->
<div id="map"></div>
<div id="right-panel"></div>

<script async defer
        <% String apiKey = ExternalConfiguartion.getInstance().getGoogleMapApiKey(); %>
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initialize">
</script>

