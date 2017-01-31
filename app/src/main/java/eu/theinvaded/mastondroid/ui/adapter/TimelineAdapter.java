package eu.theinvaded.mastondroid.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ViewUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import eu.theinvaded.mastondroid.R;
import eu.theinvaded.mastondroid.databinding.ItemTootBinding;
import eu.theinvaded.mastondroid.model.MastodonAccount;
import eu.theinvaded.mastondroid.model.MediaAttachments;
import eu.theinvaded.mastondroid.model.StatusType;
import eu.theinvaded.mastondroid.model.Toot;
import eu.theinvaded.mastondroid.ui.activity.MainActivity;
import eu.theinvaded.mastondroid.ui.activity.ReplyActivity;
import eu.theinvaded.mastondroid.ui.activity.ThreadActivity;
import eu.theinvaded.mastondroid.ui.fragment.FragmentUser;
import eu.theinvaded.mastondroid.ui.fragment.FullscreenImageFragment;
import eu.theinvaded.mastondroid.utils.ViewsUtils;
import eu.theinvaded.mastondroid.viewmodel.ItemTootViewModel;
import eu.theinvaded.mastondroid.viewmodel.TootViewModelContract;

/**
 * Created by alin on 10.12.2016.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TootViewHolder> {
    private List<Toot> timeline;
    private String credentials;
    private String username;
    private FragmentManager fragmentManager;

    public TimelineAdapter(FragmentManager fragmentManager) {
        this.timeline = Collections.emptyList();
        this.fragmentManager = fragmentManager;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCredentials(String credentials) {
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

    public void setTimeline(List<Toot> timeline, boolean inFront, boolean isNotifications) {
        if (this.timeline == null || this.timeline.size() == 0) {
            this.timeline = timeline;
        } else {
            if(inFront && !isNotifications) {
                this.timeline.addAll(0, timeline);
            } else if (!isNotifications){
                this.timeline.addAll(timeline);
            } else {
                this.timeline = timeline;
            }
        }

        notifyDataSetChanged();
    }

    public long getLatestId() {
        return timeline.get(0).id;
    }

    public long getLastId() {
        return timeline.get(timeline.size() - 1).id;
    }

    public long getFirstId() {
        return timeline.get(0).id;
    }

    public class TootViewHolder extends RecyclerView.ViewHolder implements TootViewModelContract.TootView {
        ItemTootBinding itemTootBinding;

        public TootViewHolder(ItemTootBinding binding) {
            super(binding.itemToot);
            this.itemTootBinding = binding;
        }

        void bindToot(Toot toot) {

            if (itemTootBinding.getTootViewModel() == null) {
                itemTootBinding.setTootViewModel(new ItemTootViewModel(toot, itemView.getContext(), this));
            } else {
                itemTootBinding.getTootViewModel().setToot(toot);
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
                    .into(itemTootBinding.avatarIv);

            this.itemTootBinding.contentTv.setMovementMethod(new LinkMovementMethod());

            this.itemTootBinding.mediaLayout.removeAllViews();

            if(toot.statusType == StatusType.Boost) {
                if (toot.reblog.mediaAttachments != null) {
                    final int imageHeight = (int) ViewsUtils.convertDpToPixel(120, getContext());
                    Picasso picasso = Picasso.with(getContext());

                    for (final MediaAttachments attachment : toot.reblog.mediaAttachments) {
                        ImageView attachmentView = new ImageView(getContext());
                        attachmentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        LinearLayout.LayoutParams imageLayoutParams =
                                new LinearLayout.LayoutParams(0, imageHeight);
                        final int sideMargin = (int) ViewsUtils.convertDpToPixel(2, getContext());
                        imageLayoutParams.setMargins(sideMargin, 0, sideMargin, 0);
                        imageLayoutParams.weight = 1;
                        itemTootBinding.mediaLayout.addView(attachmentView, imageLayoutParams);
                        picasso.load(attachment.previewUrl).into(attachmentView);
                        attachmentView.setTag(attachment.url);
                        attachmentView.setOnClickListener(imageClickListener);
                    }
                }
            } else {
                if (toot.mediaAttachments != null) {
                    final int imageHeight = (int) ViewsUtils.convertDpToPixel(120, getContext());
                    Picasso picasso = Picasso.with(getContext());

                    for (final MediaAttachments attachment : toot.mediaAttachments) {
                        ImageView attachmentView = new ImageView(getContext());
                        attachmentView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        LinearLayout.LayoutParams imageLayoutParams =
                                new LinearLayout.LayoutParams(0, imageHeight);
                        final int sideMargin = (int) ViewsUtils.convertDpToPixel(2, getContext());
                        imageLayoutParams.setMargins(sideMargin, 0, sideMargin, 0);
                        imageLayoutParams.weight = 1;
                        itemTootBinding.mediaLayout.addView(attachmentView, imageLayoutParams);
                        picasso.load(attachment.previewUrl).into(attachmentView);
                        attachmentView.setTag(attachment.url);
                        attachmentView.setOnClickListener(imageClickListener);
                    }
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
            toot.isHiglighted = true;
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

        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = (String) v.getTag();
                FullscreenImageFragment imageFragment =
                        FullscreenImageFragment.getInstance(imageUrl);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, imageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        };
    }
}
