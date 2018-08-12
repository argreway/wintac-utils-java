<%@ page import="com.sentryfire.WebUtilities" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Sentry Fire & Safety - Scheduler</title>

    <link rel="stylesheet" href="../css/main.css">
    <link rel="stylesheet" href="../css/header.css">
    <link rel="stylesheet" href="../css/console.css">
    <link rel="stylesheet" href="../css/scheduler.css">
    <link rel="stylesheet" href="../css/maps.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

    <link rel="shortcut icon" href="../images/logo_transparent.png"/>

    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
</head>

<body>

<div class="header">
    <a href="#default" class="logo"> <img id="img-logo" href="#default" src="../images/logo_transparent.png" class="logo"/></a>
    <div class="header-center">
        <h1>Sentry Fire Scheduler</h1>
    </div>
</div>

<div id="div-home">
    <div id="div-collapse">
        <button id="acc1" class="accordion">Build Tech Schedule</button>
        <div id="panel1" class="panel">
            <div id="p1content"></div>
        </div>
    </div>

    <button class="accordion">Calendar</button>
    <div id="panel2" class="panel">
        <div style="float: left;">
            <iframe name="g-calendar"
                    src="https://calendar.google.com/calendar/b/0/embed?height=600&amp;wkst=1&amp;bgcolor=%23ff0000&amp;src=sentryfire.com_ri3ghqmhsdrrg016qqqjbeoq4g%40group.calendar.google.com&amp;color=%23B1440E&amp;src=sentryfire.com_khlhn3om7c3crq7d6ckust0q4g%40group.calendar.google.com&amp;color=%2323164E&amp;src=sentryfire.com_c8sv357pr1fsn6liliiugujvlo%40group.calendar.google.com&amp;color=%23711616&amp;src=sentryfire.com_d7jigmhl15akvurdv7jrrrb220%40group.calendar.google.com&amp;color=%230F4B38&amp;src=sentryfire.com_3e1rtirsiuak66eeas6uh3jltk%40group.calendar.google.com&amp;color=%23B1365F&amp;src=sentryfire.com_s3f24tb8j0680ife93lgknmm5o%40group.calendar.google.com&amp;color=%23B1440E&amp;src=sentryfire.com_3r39384dt12ubo2daln5sodnrk%40group.calendar.google.com&amp;color=%23333333&amp;src=sentryfire.com_och0gp14dgq0f53k10hrk6vglo%40group.calendar.google.com&amp;color=%232F6309&amp;src=sentryfire.com_oupcvqe324p0gcfe52klnrcqgg%40group.calendar.google.com&amp;color=%2342104A&amp;src=sentryfire.com_98m1rumousgd1opgb31tjeno40%40group.calendar.google.com&amp;color=%23B1440E&amp;ctz=America%2FDenver"
                    style="border:solid 1px #777" width="1000" height="600" frameborder="0" scrolling="no"></iframe>
        </div>
        <div style="float: right; overflow: auto">
            <button class="fa fa-refresh"
                    style="font-size:100px;color:darkgreen;text-shadow:2px 2px 4px #000000;"
                    id="refreshBtn" onclick="refreshIframe();"></button>
        </div>
    </div>

    <button class="accordion">Maps</button>
    <div id="panel3" class="panel">
        <div id="panel-maps-content"></div>
    </div>

    <button class="accordion">Console Logger</button>
    <div id="panel4" class="panel">
        <div id="div-console" style="overflow:scroll; height:400px;"></div>
    </div>

    <button class="accordion">Notes/Help</button>
    <div id="panel5" class="panel">
    </div>
</div>

<!-- Run after building components-->
<script src="../js/common.js"></script>
<script src="../js/main.js"></script>
<script src="../js/console.js"></script>


</body>
</html>
