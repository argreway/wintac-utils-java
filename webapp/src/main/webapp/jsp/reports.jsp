<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.sentryfire.config.ExternalConfiguartion" %>
<%@ page import="com.sentryfire.WebUtilities" %>

<link rel="stylesheet" href="../css/reports.css">
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<%--<script type="text/javascript" src="https://canvasjs.com/assets/script/jquery-1.11.1.min.js"></script>--%>
<%--<script type="text/javascript" src="https://canvasjs.com/assets/script/jquery.canvasjs.min.js"></script>--%>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>


<h3 style="text-align: center; color: darkred;">Tech Scheduler Status</h3>

<%--<div id="chartContainer" style="height: 300px; width: 100%;"></div>--%>
<div id="columnchart_values" style="width: 900px; height: 300px;"></div>

<button id="test-report">Test Me</button>

<script src="../js/reports.js"></script>


