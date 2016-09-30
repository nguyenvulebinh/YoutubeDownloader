package nb.cblink.youtube.viewmodel;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

import nb.cblink.youtube.data.DownloadBroadcastReceiver;
import nb.cblink.youtube.data.YoutubeClient;
import nb.cblink.youtube.data.YoutubeServerFactory;
import nb.cblink.youtube.model.Youtube;
import nb.cblink.youtube.model.YoutubeResource;
import nb.cblink.youtube.view.activity.GetYTContentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class GetYTContentModelView extends BaseObservable {
    private GetYTContentActivity context;
    private YoutubeServerFactory factory;
    public Youtube data;
    @Bindable
    public String thumb;
    @Bindable
    public String title;
    @Bindable
    public int showLine = View.INVISIBLE;

    private HashMap<Long, Long> downloading;

    private ListUrlRecycleViewAdapter adapter;

    private RecyclerView recyclerView;
    private DownloadManager downloadManager;

    public GetYTContentModelView(GetYTContentActivity context, RecyclerView recyclerView) {
        this.context = context;
        factory = YoutubeClient.getClient().create(YoutubeServerFactory.class);
        this.recyclerView = recyclerView;
        downloading = new HashMap<>();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(!MainActivityModelView.isMyServiceRunning(DetectCopyURLService.class, context)){
            MainActivityModelView.startServiceDetectClipboard(context);
        }
    }

    private ProgressDialog progress = null;

    public void handleIntentShare(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        YoutubeResource resource = new YoutubeResource(sharedText);
        if (resource.isMatch()) {
            loadYoutubeResource(resource.getResourceID());
            progress = ProgressDialog.show(context, "Get youtube content",
                    "Processing...", true);
        } else {
            showDialogNotMatch("Url not match!");
        }
    }

    private void loadYoutubeResource(String id) {
        Call<String> call = factory.loadResource(id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    if (response.body().length() < 100) {
                        progress.dismiss();
                        progress = null;
                        showDialogNotMatch("Resource not found!");
                    } else {
                        Document doc = Jsoup.parse(response.body());
                        String thumb = doc.select("img").attr("src");
                        String name = doc.select("h5").text();
                        Elements listUrl = doc.select("li.list-group-item");
                        ArrayList<String> format = new ArrayList<String>();
                        ArrayList[] dataListsUrl = new ArrayList[10];
                        for (int i = 0; i < listUrl.size(); i += 2) {
                            format.add(listUrl.get(i).text());
                            Elements listUrlFormat = listUrl.get(i + 1).select("a");
                            dataListsUrl[i / 2] = new ArrayList();
                            for (Element element : listUrlFormat) {
                                if (element.attr("href") != null && !element.attr("href").equals("")) {
                                    dataListsUrl[i / 2].add(element.text());
                                    dataListsUrl[i / 2].add(element.attr("href"));
                                }
                            }
                        }
                        data = new Youtube(name, thumb, format, dataListsUrl);
                        showContent();
                    }
                } else if (response == null || response.body() == null) {
                    Toast.makeText(context, "Server error!", Toast.LENGTH_SHORT).show();
                }
                if (progress != null) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    private void showContent() {
        this.thumb = data.getThumb();
        this.title = data.getName();
        showLine = View.VISIBLE;
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapter = new ListUrlRecycleViewAdapter(context, data, this);
        recyclerView.setAdapter(adapter);

        notifyChange();
    }


    private void showDialogNotMatch(String content) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(context);
        builder.setTitle("Error")
                .setMessage(content)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                context.finish();
                            }
                        });
        builder.create().show();
    }

    public void clickDownload(String url) {
        if (downloading.get((long) url.hashCode()) == null) {
            long id = new DownloadBroadcastReceiver().startDownload(context, downloadManager, url, data.getName());
            downloading.put((long) url.hashCode(), id);
        } else {
            try {
                String status = DownloadStatus(downloadManager, downloading.get((long) url.hashCode()));
                if (status.equals("STATUS_SUCCESSFUL") || status.equals("STATUS_FAILED")) {
                    downloading.remove((long) url.hashCode());
                    Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String DownloadStatus( DownloadManager downloadManager, long DownloadId) throws Exception{

        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(DownloadId);
        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if(cursor.moveToFirst()) {
            //column for download  status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            //column for reason code if the download failed or paused
            int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);
            //get the download filename
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            String filename = cursor.getString(filenameIndex);

            String statusText = "";
            String reasonText = "";

            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    statusText = "STATUS_FAILED";
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            reasonText = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            reasonText = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            reasonText = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            reasonText = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            reasonText = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            reasonText = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            reasonText = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            reasonText = "ERROR_UNKNOWN";
                            break;
                    }
                    break;
                case DownloadManager.STATUS_PAUSED:
                    statusText = "STATUS_PAUSED";
                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            reasonText = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            reasonText = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            reasonText = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            reasonText = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = "STATUS_PENDING";
                    break;
                case DownloadManager.STATUS_RUNNING:
                    statusText = "STATUS_RUNNING";
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = "STATUS_SUCCESSFUL";
                    reasonText = "Filename:\n" + filename;
                    break;
            }

            return statusText;
        }else {
            return "Downloading";
        }
    }


    public void clickShare(String url) {
        Intent intent2 = new Intent();
        intent2.setAction(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(intent2, "Share link download"));
    }
}
