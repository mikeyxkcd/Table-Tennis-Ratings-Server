package wei.mark.tabletennisratingsserver.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import wei.mark.tabletennisratingsserver.model.FriendModel;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONObject;

public class FacebookParser {
	private static FacebookParser mParser;

	public static final String GRAPH_PATH_BASE = "https://graph.facebook.com/";
	public static final String GRAPH_PATH_PART_FRIENDS = "/friends?access_token=";

	private FacebookParser() {
	}

	public static synchronized FacebookParser getParser() {
		if (mParser == null)
			mParser = new FacebookParser();
		return mParser;
	}

	public ArrayList<FriendModel> getFriends(String facebookId,
			String accessToken) {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(GRAPH_PATH_BASE + facebookId
					+ GRAPH_PATH_PART_FRIENDS + accessToken);

			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();

			ArrayList<FriendModel> friendsArray = new ArrayList<FriendModel>();

			JSONObject data = new JSONObject(sb.toString());
			JSONArray friends = data.getJSONArray("data");
			for (int i = 0; i < friends.length(); i++) {
				JSONObject friend = friends.getJSONObject(i);
				friendsArray.add(new FriendModel(friend.getString("id"), friend
						.getString("name")));
			}

			return friendsArray;
		} catch (Exception e) {
			return null;
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
}