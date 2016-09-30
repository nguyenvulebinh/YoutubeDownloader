package nb.cblink.youtube.model;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class YoutubeResource {
    private final String URL_REGEX = ".*(?:https?://)?(?:(?:www|m)\\.)?(?:youtu\\.be/|youtube(?:-nocookie)?\\.com/(?:embed/|v/|watch\\?v=|watch\\?.+&v=))((\\w|-){11})(?:\\S+)?$.*";

    private String resourceID = null;
    private boolean isMatch = false;

    public YoutubeResource(String input){
        if(input != null) {
            Pattern pattern = Pattern.compile(URL_REGEX);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                resourceID = matcher.group(1);
                isMatch = true;
            }
        }
    }

    public boolean isMatch() {
        return isMatch;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }
}
