package nb.cblink.youtube.data;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by nguyenbinh on 12/09/2016.
 */

public interface YoutubeServerFactory {
    @POST("/")
    Call<String> loadResource(@Header("content") String content);
}
