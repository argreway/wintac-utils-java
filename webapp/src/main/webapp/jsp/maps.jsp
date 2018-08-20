<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%@ page import="com.sentryfire.WebUtilities" %>

<link rel="stylesheet" href="../css/maps.css">
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>

<h3 style="text-align: center; color: darkred;">Tech Route Map</h3>

<br/>

<fieldset>
    <legend>Technician</legend>
    <div class="controlgroup">
        <select id="select-tech">
            <option>-Select Tech-</option>
            <%
                for (String tech : WebUtilities.getTechs())
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
<div id="dialog" title="Loading Data">
    <div class="progress-label">Loading Map Data ...</div>
    <div id="progressbar"></div>
</div>

<div id="map"></div>
<div id="right-panel"></div>

<script src="../js/maps.js"></script>

<script async defer
        <% String apiKey = ExternalConfiguartion.getInstance().getGoogleMapApiKey(); %>
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initialize">
</script>

