package nb.cblink.youtube.viewmodel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import nb.cblink.youtube.databinding.TitleContentBinding;
import nb.cblink.youtube.databinding.UrlContentBinding;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class MyViewHolder {

    public static class UrlRecycleViewHolder extends RecyclerView.ViewHolder {
        private UrlContentBinding urlContentBinding;

        public UrlRecycleViewHolder(UrlContentBinding urlContentBinding) {
            super(urlContentBinding.getRoot());
            this.urlContentBinding = urlContentBinding;
        }

        public UrlContentBinding getUrlContentBinding() {
            return urlContentBinding;
        }
    }

    public static class TitleRecycleViewHolder extends RecyclerView.ViewHolder {
        private TitleContentBinding titleContentBinding;

        public TitleRecycleViewHolder(TitleContentBinding titleContentBinding) {
            super(titleContentBinding.getRoot());
            this.titleContentBinding = titleContentBinding;
        }

        public TitleContentBinding getTitleContentBinding() {
            return titleContentBinding;
        }
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

}
