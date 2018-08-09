<div class="grid-container">
    <div class="item1">
        <p>Begin Date:</p>
        <div class="inline-dp" name="begin" id="beginDate"></div>
    </div>
    <div class="item2">
        <p>End Date:</p>
        <div class="inline-dp" name="end" id="endDate"></div>
    </div>
    <div class="item3">
        <button class="schedule-btn" id="btn-al">Load</button>
        <p>Load Activity Log</p>
    </div>
    <div class="item4">
        <button class="schedule-btn" id="btn-bs">Build</button>
        <p>Build All Tech Schedules</p>
    </div>
    <div class="item5">
        <button class="schedule-btn" id="btn-del">Delete</button>
        <p>Delete All Schedules Between Time Range</p>
    </div>
    <div class="item6"></div>
</div>

<script>
    $(function () {
        $('#beginDate').datepicker({inline: true});
        $('#endDate').datepicker({inline: true});
        $('.test').datepicker({inline: true});
    });


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
        return false;
    }

    $(document).ready(function () {
        $("#btn-al").click(function () {

            if (isCalEmpty())
                return;

            var beginDate = $('#beginDate').datepicker("getDate");
            var endDate = $('#endDate').datepicker("getDate");
            $.get("functions.jsp?activity-log=true&beginDate=" + beginDate.getTime()
                  + "&endDate=" + endDate.getTime());
        });

        $("#btn-bs").click(function () {
            if (isCalEmpty())
                return;

            if (confirm("Are you sure you want to Build and Insert ALL Schedules?"))
                txt = "Continue!";
            else
                return;

            var beginDate = $('#beginDate').datepicker("getDate");
            var endDate = $('#endDate').datepicker("getDate");
            $.get("functions.jsp?build-schedule=true&beginDate=" + beginDate.getTime()
                  + "&endDate=" + endDate.getTime());
        });
        $("#btn-del").click(function () {
            if (isCalEmpty())
                return;

            if (confirm("Are you sure you want to DELETE ALL Schedules!!??"))
                txt = "Continue!";
            else
                return;

            var beginDate = $('#beginDate').datepicker("getDate");
            var endDate = $('#endDate').datepicker("getDate");
            $.get("functions.jsp?delete-schedule=true&beginDate=" + beginDate.getTime()
                  + "&endDate=" + endDate.getTime());
        });

    });
</script>
