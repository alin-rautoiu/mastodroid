package eu.theinvaded.mastondroid.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ItemFollowerBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.ui.activity.MainActivity;
import eu.theinvaded.mastondroid.ui.fragment.FragmentUser;
import eu.theinvaded.mastondroid.utils.Constants;
import eu.theinvaded.mastondroid.viewmodel.ItemFollowerViewModel;
import eu.theinvaded.mastondroid.viewmodel.ItemFollowerViewModelContract;

/**
 * Created by alin on 09.01.2017.
 */

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder>  {
    private List<MastodonAccount> accountsList = new ArrayList<>();

    @Override
    public FollowersAdapter.FollowerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemFollowerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_follower, parent, false);
        return new FollowerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FollowersAdapter.FollowerViewHolder holder, int position) {
        holder.bindFollower(accountsList.get(position));
    }

    public void setAccountList(List<MastodonAccount> accountsList) {
        if (this.accountsList == null) {
            this.accountsList = accountsList;
        } else {
            this.accountsList.addAll(accountsList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    public long getLastId() {
        return accountsList.get(accountsList.size() - 1).id;
    }

    public class FollowerViewHolder extends RecyclerView.ViewHolder implements ItemFollowerViewModelContract.FollowerView {
        ItemFollowerBinding binding;

        public FollowerViewHolder(ItemFollowerBinding binding) {
            super(binding.itemFollower);
            this.binding = binding;
        }

        @Override
        public Context getContext() {
            return itemView.getContext();
        }

        @Override
        public String getCredentials() {
            return ((MainActivity) getContext())
                    .getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constants.AUTH_KEY, "");
        }

        @Override
        public String getUsername() {
            return ((MainActivity) getContext())
                    .getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
                    .getString(Constants.CURRENT_USERNAME, "");
        }

        @Override
        public void expandUser(MastodonAccount account) {
            ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .addToBackStack("user")
                    .replace(R.id.container, FragmentUser.getInstance(account))
                    .commit();
        }

        public void bindFollower(MastodonAccount account) {
            binding.setViewModel(new ItemFollowerViewModel(account, itemView.getContext(), this));
        }
    }
}
