package nb.cblink.youtube.viewmodel;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import nb.cblink.youtube.R;
import nb.cblink.youtube.databinding.TitleContentBinding;
import nb.cblink.youtube.databinding.UrlContentBinding;
import nb.cblink.youtube.model.Youtube;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class ListUrlRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private Youtube data;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_TITLE = 1;
    private final int VIEW_TYPE_URL = 2;
    private ArrayList<String> mDocs;
    private GetYTContentModelView modelView;

    public ListUrlRecycleViewAdapter(Context context, Youtube data, GetYTContentModelView modelView){
        this.context = context;
        this.data = data;
        this.modelView = modelView;
        mDocs = new ArrayList<>();
        for(int i = 0; i < data.getFormat().size(); i++){
            if(data.getListsUrl()[i].size() > 0) {
                mDocs.add("titleformat" + data.getFormat().get(i));
                for (int j = 0; j < data.getListsUrl()[i].size(); j += 2) {
                    mDocs.add("quality" + data.getListsUrl()[i].get(j));
                    mDocs.add(data.getListsUrl()[i].get(j + 1));
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            UrlContentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_content_layout, parent, false);
            return new MyViewHolder.UrlRecycleViewHolder(binding);
        } else if (viewType == VIEW_TYPE_TITLE) {
            TitleContentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_line_layout, parent, false);
            return new MyViewHolder.TitleRecycleViewHolder(binding);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.empty_layout, parent, false);
            return new MyViewHolder.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder.UrlRecycleViewHolder) {
            UrlContentBinding binding = ((MyViewHolder.UrlRecycleViewHolder) holder).getUrlContentBinding();
            binding.setYtMV(modelView);
            binding.setType(mDocs.get(position).substring("quality".length()));
            binding.setUrlDownload(mDocs.get(position+1));
        } else if (holder instanceof MyViewHolder.TitleRecycleViewHolder) {
            TitleContentBinding binding = ((MyViewHolder.TitleRecycleViewHolder) holder).getTitleContentBinding();
            binding.setTitle(mDocs.get(position).substring("titleformat".length()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        String current = mDocs.get(position);
        if(current.startsWith("titleformat")) return VIEW_TYPE_TITLE;
        if(current.startsWith("quality")) return VIEW_TYPE_ITEM;
        return VIEW_TYPE_URL;
    }

    @Override
    public int getItemCount() {
        return mDocs == null ? 0 : mDocs.size();
    }

}
