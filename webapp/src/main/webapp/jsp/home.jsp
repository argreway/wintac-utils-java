<%@ page import="com.sentryfire.WebUtilities" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>Sentry Fire & Safety - Scheduler</title>

    <link rel="stylesheet" href="../css/home.css">
    <link rel="stylesheet" href="../css/header.css">

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
    <div id="div-mainmenu">
        <ul id="menu">
            <li class="ui-state-disabled">
                <div>Toys (n/a)</div>
            </li>
            <li>
                <div>Electronics</div>
                <ul>
                    <li class="ui-state-disabled">
                        <div>Home Entertainment</div>
                    </li>
                </ul>
            </li>
            <li>
                <div>Movies</div>
            </li>
            <li>
                <div>Music</div>
                <ul>
                    <li>
                        <div>Rock</div>
                        <ul>
                            <li>
                                <div>Alternative</div>
                            </li>
                            <li>
                                <div>Classic</div>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div>Jazz</div>
                        <ul>
                            <li>
                                <div>Big Band</div>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <div>Pop</div>
                    </li>
                </ul>
            </li>
            <li class="ui-state-disabled">
                <div>Specials (n/a)</div>
            </li>
        </ul>
    </div>

    <div id="div-collapse">
        <button class="accordion">Section 1</button>
        <div class="panel">
            <p>test1</p>
        </div>

        <button class="accordion">Section 2</button>
        <div class="panel">
            <p>test2</p>
        </div>

        <button class="accordion">Section 3</button>
        <div class="panel">
            <p>test3</p>
        </div>
        </div>
    </div>

<p>
    Some simple stuff here
    <% WebUtilities webUtilities = new WebUtilities();
        String output = webUtilities.outputSimple(); %>
    <%= output%>
</p>

    <script src="../js/home.js"></script>


</body>
</html>
