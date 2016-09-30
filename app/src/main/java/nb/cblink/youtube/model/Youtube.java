package nb.cblink.youtube.model;

import java.util.ArrayList;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class Youtube {
    private String name;
    private String thumb;
    private ArrayList<String> format;
    private ArrayList[] listsUrl;

    public Youtube(String name, String thumb, ArrayList<String> format, ArrayList[] listsUrl) {
        this.name = name;
        this.thumb = thumb;
        this.format = format;
        this.listsUrl = listsUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public ArrayList<String> getFormat() {
        return format;
    }

    public void setFormat(ArrayList<String> format) {
        this.format = format;
    }

    public ArrayList<String>[] getListsUrl() {
        return listsUrl;
    }

    public void setListsUrl(ArrayList<String>[] listsUrl) {
        this.listsUrl = listsUrl;
    }
}
