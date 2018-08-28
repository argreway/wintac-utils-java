google.charts.load("current", {packages: ['corechart']});
google.charts.setOnLoadCallback(drawWOChart);


var graphData = [
    ['Genre', 'Fantasy & Sci Fi', 'Romance', 'Mystery/Crime', 'General',
     'Western', 'Literature', {role: 'annotation'}],
    ['2010', 10, 24, 20, 32, 18, 5, ''],
    ['2020', 16, 22, 23, 30, 16, 9, ''],
    ['2030', 28, 19, 29, 30, 12, 13, '']
];


// function drawWOChart(graphData)
function drawWOChart()
{
    var array = [];
    array.push(graphData);
    var data = google.visualization.arrayToDataTable([
                                                         ['Genre', 'Fantasy & Sci Fi', 'Romance', 'Mystery/Crime', 'General',
                                                          'Western', 'Literature', {role: 'annotation'}],
                                                         ['2010', 10, 24, 20, 32, 18, 5, ''],
                                                         ['2020', 16, 22, 23, 30, 16, 9, ''],
                                                         ['2030', 28, 19, 29, 30, 12, 13, '']
                                                     ]);

    var view = new google.visualization.DataView(data);
    view.setColumns([0, 1, 2, 3, 4, 5]);

    var options = {
        title: "Work Order Distribution Per Tech",
        width: 600,
        height: 400,
        legend: {position: 'top', maxLines: 3},
        isStacked: true
    };
    var chart = new google.visualization.ColumnChart(document.getElementById("columnchart_values"));
    chart.draw(view, options);
}


$("#test-report").on('click', function () {
    $.post("/reports", {tech: "test", date: "test2"}).done(function (data) {

        console.log("Did something")
        alert(data);

        var options = {
            animationEnabled: true,
            theme: "light2",
            title: {
                text: "Work Order Distribution"
            },
            axisY2: {
                lineThickness: 0
            },
            toolTip: {
                shared: true
            },
            legend: {
                verticalAlign: "top",
                horizontalAlign: "center"
            },
            data: []
        }

        obj = JSON.parse(data);

        options.data.push(obj);

        $("#chartContainer").CanvasJSChart(options);
    });
});

