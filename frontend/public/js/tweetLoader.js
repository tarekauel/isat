function parse(tweet) {

  function transform(tweet) {
      tweet = tweet.replace(/(http[^ ]*)/g, "<a href=\"$1\">$1</a>")
      tweet = tweet.replace(/#([a-zA-Z0-9]*)/g, "<a href=\"/hashtags/$1\">#$1</a>")
      //tweet = tweet.replace(/@([a-zA-Z0-9]*)/g, "<a href=\"/users/$1\">@$1</a>")
      return tweet
    }

  return "<tr><td>" + transform(tweet.text) + "</td><td>" +
    tweet.user.screenName + "</td><td>" + tweet.createdAt + "</td></tr>"
}

function loadTweets(requestUrl, identifier) {

  $.getJSON(requestUrl, function(data) {

    data.forEach(function(t) {
      $(identifier).append(parse(t))
    })

  })
}