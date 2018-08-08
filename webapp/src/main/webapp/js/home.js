var acc = document.getElementsByClassName("accordion");
var i;

for (i = 0; i < acc.length; i++)
{
    acc[i].addEventListener("click", function () {
        this.classList.toggle("active-button");
        var panel = this.nextElementSibling;
        if (panel.style.maxHeight)
        {
            panel.style.maxHeight = null;
        }
        else
        {
            panel.style.maxHeight = panel.scrollHeight + "px";
        }
    });
}

$(function () {
    $("#menu").menu();
});

$(document).ready(function () {
    $("#btn-al").click(function () {
        $.get("functions.jsp?activity-log=true");
    });
});