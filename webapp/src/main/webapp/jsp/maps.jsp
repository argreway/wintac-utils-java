<%@ page import="com.sentryfire.model.WO" %>
<%@ page import="com.sentryfire.WebUtilities" %>
<%@ page import="com.sentryfire.business.schedule.SchedulerBuilder" %>
<%@ page import="com.sentryfire.business.schedule.googlecalendar.CalenderUtils" %>
<%@ page import="com.sentryfire.config.TechProfileConfiguration" %>
<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%@ page import="java.util.List" %>
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
        geoCodeAddresses(geocoder, map, infoWindow);

    }

    function geoCodeAddresses(geocoder,
                              resultsMap,
                              infoWindow)
    {
        <% List<WO> woList = WebUtilities.getAddressListWO(); %>
        <% for(int i =0; i < woList.size(); i++) {
            WO wo = woList.get(i);
            String currAddress = wo.getADR1() + " " + wo.getCITY() + " " + wo.getZIP(); %>
        var currAddress = "<%=currAddress%>";

        if (geocoder)
        {
            geocoder.geocode(
                    {'address': currAddress},
                    function (results,
                              status) {

                        if (status == 'OK')
                        {
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
                        }
                        else
                        {
                            throw('No results found: ' + status);
                        }
                    });
        }
        <% } %>
    }

    $(function () {
        $(".controlgroup").controlgroup()
        $(".controlgroup-vertical").controlgroup(
                {
                    "direction": "vertical"
                });
    });

    $(document).ready(function () {
        $("#select-tech").on('click', function () {

            if ($("#select-tech").val().indexOf("Select") == -1)
            {
                var html = '<option value="val1">Val-1</option><option value="val12">Val-12</option>';
                $("#select-date").append(html);
            }
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
            {
            %>
            <%="<option>" + tech + "</option>"%>
            <%}%>
        </select>
        <select id="select-date">
            <option>-Select Calendar Date-</option>
        </select>
        <button>Load Tech Route</button>
    </div>
</fieldset>

<!--The div element for the map -->
<div id="map"></div>


<script async defer
        <%
            String apiKey = ExternalConfiguartion.getInstance().getGoogleMapApiKey();
        %>
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initialize">
</script>
