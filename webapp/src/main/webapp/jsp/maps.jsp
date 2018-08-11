<%@ page import="com.sentryfire.config.AppConfiguartion" %>
<%@ page import="com.sentryfire.config.ExternalConfiguartion" %><%--
  Created by IntelliJ IDEA.
  User: argreway
  Date: 8/10/18
  Time: 9:57 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script src="../js/maps.js"></script>

<h3>My Google Maps Demo</h3>
<!--The div element for the map -->
<div id="map"></div>

<%
    String apiKey = ExternalConfiguartion.getInstance().getGoogleMapApiKey();
%>
<script async defer
        src="https://maps.googleapis.com/maps/api/js?key=<%=apiKey%>&callback=initMap">
</script>