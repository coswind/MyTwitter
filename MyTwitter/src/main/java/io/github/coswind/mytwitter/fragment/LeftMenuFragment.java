package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.coswind.mytwitter.R;

/**
 * Created by coswind on 14-2-28.
 */
public class LeftMenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_menu, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
