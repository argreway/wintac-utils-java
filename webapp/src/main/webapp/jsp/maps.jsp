<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%@ page import="com.sentryfire.model.WO" %>
<%@ page import="com.sentryfire.WebUtilities" %>
<%@ page import="com.sentryfire.business.schedule.SchedulerBuilder" %>
<%@ page import="com.sentryfire.business.schedule.googlecalendar.CalenderUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: argreway
  Date: 8/10/18
  Time: 9:57 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String apiKey = ExternalConfiguartion.getInstance().getGoogleMapApiKey();
%>

<script>
    //@ sourceURL=maps.jsp

    function initialize()
    {
        // var bounds = new google.maps.LatLngBounds();
        var mapOptions = {
            center: {lat: 39.742, lng: -104.991}
        };

        // Display a map on the page
        var map = new google.maps.Map(document.getElementById('map'), {
            zoom: 8,
            mapTypeId: 'roadmap',
            center: {lat: -34.397, lng: 130.644}
        });
        // var map = new google.maps.Map(document.getElementById("map"), mapOptions);

        var geocoder = new google.maps.Geocoder();
        var infoWindow = new google.maps.InfoWindow(), marker, i;
        // geoCodeAddresses(geocoder, map, infoWindow);

    }

    function geoCodeAddresses(geocoder,
                              resultsMap,
                              infoWindow)
    {
        <% for(int i =0; i < WebUtilities.getAddressList().size(); i++) { %>
        <%  WO wo = WebUtilities.getAddressListWO().get(i);
            String currAddress = WebUtilities.getAddressList().get(i); %>
        var currAddress = "<%=currAddress%>"

        if (geocoder)
        {
            geocoder.geocode(
                    {'address': currAddress},
                    function (results,
                              status) {

                        if (status == 'OK')
                        {
                            // coords.push(results[0].geometry.location);
                            resultsMap.setCenter(results[0].geometry.location);
                            var marker = new google.maps.Marker(
                                    {
                                        map: resultsMap,
                                        position: results[0].geometry.location,
                                        title: "<%= SchedulerBuilder.convert(wo.getNAME())%>"
                                    });
                            google.maps.event.addListener(marker, 'click', (function (marker,
                                                                                      i) {
                                return function () {
                                    infoWindow.setContent(`<%=CalenderUtils.getShortCustDesc(wo)%>`);
                                    infoWindow.open(map, marker);
                                }
                            })(marker, i));
                            // alert("got : " + results[0].geometry.location);
                            // alert("marker : " + marker);
                        }
                        else
                        {
                            throw('No results found: ' + status);
                        }
                    });
        }
        <% } %>
    }

    // geoCodeAddresses(geocoder, map, infoWindow);

</script>

<h3 style="text-align: center; color: darkred;">Tech Route Map</h3>

<!--The div element for the map -->
<div id="map"></div>

<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initialize">
</script>
