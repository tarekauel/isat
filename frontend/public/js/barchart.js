function barchart(requestUrl, chartIdentifier) {

  var margin = {top: 20, right: 20, bottom: 30, left: 40},
      width = 1200 - margin.left - margin.right,
      height = 500 - margin.top - margin.bottom;

  var x = d3.scale.ordinal()
      .rangeRoundBands([0, width], .1);

  var y = d3.scale.linear()
      .range([height, 0]);

  var xAxis = d3.svg.axis()
      .scale(x)
      .orient("bottom");

  var yAxis = d3.svg.axis()
      .scale(y)
      .orient("left")
      .ticks(10, "");

  var svg = d3.select(chartIdentifier).append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
    .append("g")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  d3.json(requestUrl + window.location.search, function(error, data) {

    if (error != undefined) {
      $("#error").append("<div class=\"alert alert-danger\" role=\"alert\"><span> (" + error.status + ") " +
          error.statusText + " Url: <a href=\"" + error.responseURL + "\">" + error.responseURL + "</a></span></div>");
    } else {
      if (data.length != 0) {
        data = data.sort(function(a, b) { return a.frequency < b.frequency; })

        x.domain(data.map(function(d) { return d.text; }));
        y.domain([0, d3.max(data, function(d) { return d.frequency; })]);

        svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)

        svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
          .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text("Frequency");

        svg.selectAll(".bar")
            .data(data)
          .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) { return x(d.text); })
            .attr("width", x.rangeBand())
            .attr("y", function(d) { return y(d.frequency); })
            .attr("height", function(d) { return height - y(d.frequency); })
          .on("click", function(d) { window.location.href = d.url + window.location.search });
      } else {
        $(chartIdentifier).append("span").text("No data received");
    }
    }

  });

  function type(d) {
    d.frequency = +d.frequency;
    return d;
  }
}
