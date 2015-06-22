function parse(user) {

  function parseDate(date) {
    if (date == 0) return ""
    else return new Date(date)
  }

  var out =  "<tr><td>" + user.name + "</td><td>" + '<a href="/users/' + user.screenName + '">@' +
    user.screenName + "</a></td><td>" + user.userId + "</td>"

  if (user.watching) {
    out += '<td><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></td>'
  } else {
    out += '<td><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></td>'
  }

  out += "<td>" + parseDate(user.lastUpdate) + "</td>"
  out += '<td><a href="/api/user/' + user.screenName + '/tweets/update"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></a></td>'

  return out + "</tr>";
}

function userLoader(identifier) {

  $.getJSON("/api/user", function(data) {

    data = data.sort(function(a, b) {return a.screenName.localeCompare(b.screenName)})

    data.forEach(function(t) {
      $(identifier).append(parse(t))
    })

  })
}