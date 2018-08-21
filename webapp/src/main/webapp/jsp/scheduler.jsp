<%@ page import="com.sentryfire.WebUtilities" %>
<head>
    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="../css/header.css">
    <link rel="stylesheet" href="../css/console.css">
    <link rel="stylesheet" href="../css/scheduler.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <link rel="shortcut icon" href="../images/logo_transparent.png"/>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>

<div class="container">
    <div class="cal-row">
        <div class="col-date">
            <h3> 1) Select Dates </h3>
            <div class="col-begin">
                <div class="inline-dp" name="begin" id="beginDate"></div>
                <p>Begin Date</p>
            </div>
            <div class="col-end">
                <div class="inline-dp" name="end" id="endDate"></div>
                <p>End Date</p>
            </div>
        </div>

        <div class="col-tech">
            <h3> 2) Select A Tech </h3>
            <ul id="select-tech" class="checkbox-grid">
                <li><input id="cb-ALL" type="checkbox" name="tech" value="ALL"/><label>ALL</label></li>
                <%
                    for (String tech : WebUtilities.getTechs())
                    { %>
                <li><input type="checkbox" name="tech" value="<%=tech%>"/><label><%=tech%>
                </label></li>
                <%}%>
            </ul>
        </div>

        <div class="col-action">
            <h3> 3) Select An Action </h3>
            <div class="">
                <button class="schedule-btn" id="btn-al">Load
                    <span class="tooltiptext"> Load Activity Log</span>
                </button>
            </div>
            <div class="">
                <button class="schedule-btn" id="btn-bs">Build
                    <span class="tooltiptext"> Build All Tech Schedules </span>
                </button>
            </div>
            <div class="">
                <button class="schedule-btn" id="btn-del">Delete
                    <span class="tooltiptext"> Delete All Schedules Between Time Range </span>
                </button>
            </div>
            <div class="">
                <button class="schedule-btn" id="btn-clear">Clear Cache
                    <span class="tooltiptext"> Clear All Cache Files</span>
                </button>
            </div>
        </div>
    </div>
</div>


<script src="../js/scheduler.js"></script>

