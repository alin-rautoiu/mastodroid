package eu.theinvaded.mastondroid.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ItemTootBinding;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import eu.theinvaded.mastondroid.viewmodel.ItemTootViewModel;
import eu.theinvaded.mastondroid.viewmodel.TootViewModelContract;
import retrofit2.http.Path;

/**
 * Created by alin on 10.12.2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TootViewHolder> {
    private List<Toot> timeline;
    private String credentials;

    public TimelineAdapter(String credentials) {
        this.timeline = Collections.emptyList();
        this.credentials = credentials;
    }

    @Override
    public TootViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemTootBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_toot, parent, false);
        return new TootViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(TootViewHolder holder, int position) {
        holder.bindToot(timeline.get(position));
    }

    @Override
    public int getItemCount() {
        return timeline.size();
    }

    public void setTimeline(List<Toot> timeline) {
        if (this.timeline == null || this.timeline.size() == 0) {
            this.timeline = timeline;
        } else {
            this.timeline.addAll(timeline);
        }

        notifyDataSetChanged();
    }

    public long getLatestId() {
        return timeline.get(0).id;
    }

    public long getLastId() {
        return timeline.get(timeline.size() - 1).id;
    }

    public class TootViewHolder extends RecyclerView.ViewHolder implements TootViewModelContract.TootView {
        ItemTootBinding dataBinding;

        public TootViewHolder(ItemTootBinding binding) {
            super(binding.itemToot);
            this.dataBinding = binding;
        }

        void bindToot(Toot toot) {
            if (dataBinding.getTootViewModel() == null) {
                dataBinding.setTootViewModel(new ItemTootViewModel(toot, itemView.getContext(), this));
            } else {
                dataBinding.getTootViewModel().setToot(toot);
            }

            if (toot == null)
                return;

            String avatarUri = "";
            if (toot.statusType != null
                    && toot.statusType == StatusType.Boost
                    && toot.reblog.account != null) {
                avatarUri = toot.reblog.account.avatar;
            } else if (toot.account != null) {
                avatarUri = toot.account.avatar;
            }

            Picasso.with(getContext())
                    .load(avatarUri)
                    .placeholder(R.drawable.ic_person)
                    .into(dataBinding.avatarIv);
        }

        @Override
        public Context getContext() {
            return itemView.getContext();
        }

        @Override
        public void setStatusFavorite(Toot toot) {
            dataBinding.getTootViewModel().isFavorited.set(true);
        }

        @Override
        public void setStatusUnfavorite(Toot favoritedStatus) {
            dataBinding.getTootViewModel().isFavorited.set(false);
        }

        @Override
        public void reply(Toot toot) {
            getContext().startActivity(ReplyActivity.getStartIntent(getContext(), toot));
        }

        @Override
        public String getCredentials() {
            return credentials;
        }
    }
}
