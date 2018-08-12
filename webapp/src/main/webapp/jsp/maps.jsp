<%@ page import="com.sentryfire.model.WO" %>
<%@ page import="com.sentryfire.WebUtilities" %>
<%@ page import="com.sentryfire.business.schedule.SchedulerBuilder" %>
<%@ page import="com.sentryfire.business.schedule.googlecalendar.CalenderUtils" %>
<%@ page import="com.sentryfire.config.TechProfileConfiguration" %>
<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="org.joda.time.DateTime" %>
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

        var geocoder = new google.maps.Geocoder();
        var infoWindow = new google.maps.InfoWindow(), marker, i;

        <%--<% List<WO> woList = SchedulerBuilder.getWorkOrderList(new DateTime(),false); %>--%>
        <%--<% String addressArray = WebUtilities.jsonArrayList(woList); %>--%>
        <%--addresses = <%=addressArray%>;--%>
        // console.log("ddata " + addresses);
        // geoCodeAddresses(geocoder, addresses, map, infoWindow);

    }

    function geoCodeAddresses(geocoder,
                              addresses,
                              resultsMap,
                              infoWindow)
    {
        for (var i = 0; i < addresses.length; i++)
        {
            var currAddress = addresses[i];
            console.log("current " + currAddress);

            geocoder.geocode(
                    {'address': currAddress},
                    function (results,
                              status) {

                        if (status == 'OK')
                        {
                            resultsMap.setCenter(results[0].geometry.location);
                            console.log("cords " + results[0].geometry.location);
                            var marker = new google.maps.Marker(
                                    {
                                        map: resultsMap,
                                        position: results[0].geometry.location
                                        // title: '"' + currAddress + "'"
                                    });
                            // google.maps.event.addListener(marker, 'click', (function (marker,
                            //                                                           i) {
                            //     return function () {
                            <%--infoWindow.setContent(`<%=CalenderUtils.getShortCustDesc(wo)%>`);--%>
                            // infoWindow.open(map, marker);
                            // }
                            // })(marker, i));
                        }
                        else
                        {
                            throw('No results found: ' + status);
                        }
                    });
        }
    }

    // Button Updates AJAX
    $("#select-tech").on('change', function () {

        techVal = $('#select-tech').val();
        $.post("${pageContext.request.contextPath}/maps", {tech: techVal}).done(function (data) {
            $('#select-date').html(data);
        });
    });
    $("#loadTechRoute").on('click', function () {

        techVal = $('#select-tech').val();
        dateVal = $('#select-date').val();
        $.post("${pageContext.request.contextPath}/maps", {tech: techVal, date: dateVal}).done(function (data) {
            var addrs = JSON.parse(data);
            console.log("data " + data);
            console.log("addrs " + addrs);
            var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 8,
                mapTypeId: 'roadmap',
                center: {lat: 39.739, lng: -104.990}
            });

            var geocoder = new google.maps.Geocoder();
            geoCodeAddresses(geocoder, addrs, map, null)
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
            <%="<option>"
               +
               tech
               +
               "</option>"%>
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

<script async defer
        <%
            String
                    apiKey
                    =
                    ExternalConfiguartion
                            .
                                    getInstance
                                            (
                                            )
                            .
                                    getGoogleMapApiKey
                                            (
                                            );
        %>
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initialize">
</script>

