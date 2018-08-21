///////// Accordion Stuff //////////
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


///////// Main Layout //////////
$(function () {
    $("#menu").menu();
});

///////// Load Other Files //////////
// $("#p1content").load("scheduler.jsp");
// $("#panel-maps-content").load("maps.jsp");

// $('#acc1').trigger("click");

function refreshIframe()
{
    var ifr = document.getElementsByName('g-calendar')[0];
    ifr.src = ifr.src;
}

$(function () {
    $("#draggable").draggable();
});