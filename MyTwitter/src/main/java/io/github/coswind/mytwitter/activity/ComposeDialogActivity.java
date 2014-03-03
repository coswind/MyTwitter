package io.github.coswind.mytwitter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import io.github.coswind.mytwitter.MainActivity;
import io.github.coswind.mytwitter.MyApplication;
import io.github.coswind.mytwitter.R;
import io.github.coswind.mytwitter.api.UpdateStatusTask;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

/**
 * Created by coswind on 14-2-28.
 */
public class ComposeDialogActivity extends Activity implements View.OnClickListener {
    private EditText editText;
    private Twitter twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compose);

        twitter = MyApplication.getInstance(this).getTwitter();

        initView();
        initEvent();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.edit_text);
    }

    private void initEvent() {
        findViewById(R.id.action_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_send:
                updateStatus();
                break;
            default:
                break;
        }
    }

    private void updateStatus() {
        String content = editText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        StatusUpdate statusUpdate = new StatusUpdate(content);
        new UpdateStatusTask(twitter, MyApplication.getInstance(this)).execute(statusUpdate);
        finish();
    }
}
