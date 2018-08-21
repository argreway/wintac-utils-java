function initDatePickers()
{
    $("#beginDate").datepicker({
                                   inline: true,
                                   dateFormat: "dd-M-yy",
                                   minDate: null,
                                   onSelect: function (date) {
                                       var val = $(this).datepicker('getDate');
                                       console.log("date " + val);
                                       var dateVal = new Date(date);
                                       var val = $(this).datepicker('setDate', dateVal);
                                       var queryDate = '2009-11-01',
                                               dateParts = queryDate.match(/(\d+)/g)
                                       realDate = new Date(2009, 11, 1);
                                       // months are 0-based!

                                       $('#beginDate').datepicker({dateFormat: 'yy-mm-dd'}); // format to show
                                       $('#endDate').datepicker('setDate', realDate);
                                       var val = $(this).datepicker('getDate');
                                       console.log("date " + val);

                                   }
                               });
    $('#endDate').datepicker({
                                 inline: true,
                                 dateFormat: "dd-M-yy",
                                 minDate: null,
                                 onSelect: function (date) {
                                     var val = $(this).datepicker('getDate');
                                     console.log("date " + val);
                                 }
                             });

}

///////// Function Calls //////////

function isCalEmpty()
{
    var a = $('#beginDate').val();
    var b = $('#endDate').val();

    if (a == null || a == "", b == null || b == "")
    {
        alert("Must populate begin and end dates!")
        return true;
    }

    let tech = getCheckBoxes();
    if (tech == null || tech.length <= 0)
    {
        alert("Must populate a tech!")
        return true;
    }

    return false;
}

function getCheckBoxes()
{
    var result = [];
    var techs = $("input[type=checkbox]")
    for (var i = 0, len = techs.length; i < len; i++)
    {
        box = techs[i];
        if (box.checked)
            result.push(box.value);

    }
    return result;
}

$(document).ready(function () {
    initDatePickers();

    $("#cb-ALL").click(function () {

                           var check = this.checked;

                           var techs = $("input[type=checkbox]")
                           for (var i = 0, len = techs.length; i < len; i++)
                           {
                               techs[i].checked = false;
                               if (check)
                                   techs[i].disabled = true;
                               else
                                   techs[i].disabled = false;

                           }

                           // Re-enable the ALL button
                           if (check)
                               this.checked = true;
                           this.disabled = false;

                       }
    );

    $("#btn-al").click(function () {

        if (isCalEmpty())
            return;

        let techs = getCheckBoxes();
        let beginDateVal = $('#beginDate').datepicker("getDate");
        let endDateVal = $('#endDate').datepicker("getDate");
        $.post(
                "/schedule",
                {beginDate: beginDateVal, endDate: endDateVal, tech: techs, action: "load"}
        ).done(function (data) {
            alert(data);
        });
    });

    $("#btn-bs").click(function () {
        if (isCalEmpty())
            return;

        if (confirm("Are you sure you want to Build and Insert ALL Schedules?"))
            txt = "Continue!";
        else
            return;

        let techs = getCheckBoxes();
        let beginDateVal = $('#beginDate').datepicker("getDate");
        let endDateVal = $('#endDate').datepicker("getDate");
        $.post(
                "/schedule",
                {beginDate: beginDateVal, endDate: endDateVal, tech: techs, action: "build"}
        ).done(function (data) {
            alert(data);
        });

    });
    $("#btn-del").click(function () {
        if (isCalEmpty())
            return;

        if (confirm("Are you sure you want to DELETE ALL Schedules!!??"))
            txt = "Continue!";
        else
            return;

        let techs = getCheckBoxes();
        let beginDateVal = $('#beginDate').datepicker("getDate");
        let endDateVal = $('#endDate').datepicker("getDate");
        $.post(
                "/schedule",
                {beginDate: beginDateVal, endDate: endDateVal, tech: techs, action: "delete"}
        ).done(function (data) {
            alert(data);
        });
    });
    $("#btn-clear").click(function () {
        if (isCalEmpty())
            return;

        if (confirm("Are you sure you want to CLEAR ALL Schedules!!??"))
            txt = "Continue!";
        else
            return;

        let techs = getCheckBoxes();
        let beginDateVal = $('#beginDate').datepicker("getDate");
        let endDateVal = $('#endDate').datepicker("getDate");
        $.post(
                "/schedule",
                {beginDate: beginDateVal, endDate: endDateVal, tech: techs, action: "clear"}
        ).done(function (data) {
            alert(data);
        });
    });


});