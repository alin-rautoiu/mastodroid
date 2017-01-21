package eu.theinvaded.mastondroid.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by charlag on 21/01/2017.
 */

public class FullscreenImageFragment extends Fragment {

    private static String IMAGE_URL = "IMAGE_URL";

    public static FullscreenImageFragment getInstance(String imageUrl) {
        FullscreenImageFragment fragment = new FullscreenImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getContext());
        imageView.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.black));
        String imageUrl = getArguments().getString(IMAGE_URL);
        Picasso.with(getContext()).load(imageUrl).into(imageView);
        return imageView;
    }
}
