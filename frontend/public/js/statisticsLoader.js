function loadStatistics(identifier) {

  function parse(x) {
    return "<span>HashTags: " + x.HashTags + "</span><br />" +
    "<span>Tweets: " + x.Tweets + "</span><br />" +
    "<span>User: " + x.User + "</span><br />"
  }

  $.getJSON("/api/mgmt/stats", function(data) {
    $(identifier).append(parse(data))
  })
}