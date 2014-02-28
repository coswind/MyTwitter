package io.github.coswind.mytwitter.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.activity.ComposeDialogActivity;
import io.github.coswind.mytwitter.utils.LogUtils;

/**
 * Created by coswind on 14-2-28.
 */
public class RightMenuFragment extends Fragment implements View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right_menu, container, false);
        initView(view);
        initEvent(view);
        return view;
    }

    private void initView(View view) {

    }

    private void initEvent(View view) {
        view.findViewById(R.id.compose_layout).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compose_layout:
                startActivity(new Intent(getActivity(), ComposeDialogActivity.class));
                break;
            default:
                break;
        }
    }
}
