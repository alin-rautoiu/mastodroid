package eu.theinvaded.mastondroid.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ItemSelectedTootBinding;
import eu.theinvaded.mastondroid.databinding.ItemTootBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.activity.MainActivity;
import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import eu.theinvaded.mastondroid.ui.activity.ThreadActivity;
import eu.theinvaded.mastondroid.ui.fragment.FragmentUser;
import eu.theinvaded.mastondroid.viewmodel.ItemTootViewModel;
import eu.theinvaded.mastondroid.viewmodel.TootViewModelContract;

/**
 * Created by alin on 10.12.2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TootViewHolder> {
    private List<Toot> timeline;
    private String credentials;
    private String username;

    private final static int NORMAL = 1;
    private final static int HIGHLIGHTED = 2;

    public TimelineAdapter() {
        this.timeline = Collections.emptyList();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    @Override
    public int getItemViewType(int position) {
        return timeline.get(position).isHiglighted ? HIGHLIGHTED : NORMAL;
    }

    @Override
    public TootViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case NORMAL:
                ItemTootBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_toot, parent, false);
                return new TootViewHolder(dataBinding);
            case HIGHLIGHTED:
                ItemSelectedTootBinding itemSelectedTootBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_selected_toot, parent, false);
                return new TootViewHolder(itemSelectedTootBinding);
        }

        return null;
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
        ItemTootBinding itemTootBinding;
        ItemSelectedTootBinding itemSelectedTootBinding;
        public TootViewHolder(ItemTootBinding binding) {
            super(binding.itemToot);
            this.itemTootBinding = binding;
        }

        public TootViewHolder(ItemSelectedTootBinding binding) {
            super(binding.itemToot);
            this.itemSelectedTootBinding = binding;
        }

        void bindToot(Toot toot) {
            if (toot.isHiglighted) {
                if (itemSelectedTootBinding.getTootViewModel() == null) {
                    itemSelectedTootBinding.setTootViewModel(new ItemTootViewModel(toot, itemView.getContext(), this));
                } else {
                    itemSelectedTootBinding.getTootViewModel().setToot(toot);
                }

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
                        .into(itemSelectedTootBinding.avatarIv);
            } else {
                if (itemTootBinding.getTootViewModel() == null) {
                    itemTootBinding.setTootViewModel(new ItemTootViewModel(toot, itemView.getContext(), this));
                } else {
                    itemTootBinding.getTootViewModel().setToot(toot);
                }
            }
        }

        @Override
        public Context getContext() {
            return itemView.getContext();
        }

        @Override
        public void reply(Toot toot) {
            getContext().startActivity(ReplyActivity.getStartIntent(getContext(), toot));
        }

        @Override
        public void startThread(Toot toot) {
            getContext().startActivity(ThreadActivity.getStartIntent(getContext(), toot));
        }

        @Override
        public String getCredentials() {
            return credentials;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public void expandUser(MastodonAccount account) {
            ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .addToBackStack("user")
                    .replace(R.id.container, FragmentUser.getInstance(account))
                    .commit();
        }
    }
}
