package orangecode;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author Tarek Auel
 * @since June 01, 2015.
 */
public class ApiConnector {

    public static Twitter connect() throws Exception {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("jqh1tnLqducbgOYcoGIpxwnDV")
                .setOAuthConsumerSecret("vJV3ewWWmLTE29yNyAeDde9gLKiJHznjkd8KKsAofqN9sYUWcg")
                .setOAuthAccessToken("188024808-uThlSF56Ey9SJuPhYvQGixJsfaTMjw2q6mqzRmSG")
                .setOAuthAccessTokenSecret("mxOYsZGKBTxNv0dFvpkIO0wPUObq1wXwQCQz18P9rP4Mw");
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }
}
