package com.example.testdanmu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private DanMuView mDanMuLayout;
	private DanMuClient danMuClient;
	private SharedPreferences sharedPreferences;
	private String mUserName;
	private String mTitle;
	private Button mBtnSwitch;
	public static final String BUNDLE_USER_NAME = "userName";
	public static final String BUNDLE_TITLE = "title";

	public static final String KEY_DANMU_STATUS = "switch_dan_mu"; // 弹幕

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = null;
		try {
			bundle = getIntent().getExtras();
		} catch (Exception e) {
			e.printStackTrace();
		}

		setContentView(R.layout.activity_main);
		mDanMuLayout = (DanMuView) this
				.findViewById(R.id.view_danmu);
		mBtnSwitch = (Button) findViewById(R.id.dan_mu_switch);
		// Demo, change it to your need.
		mTitle = "342679617";
		mUserName = "qq";
		if (bundle != null) {
			mUserName = bundle.getString(BUNDLE_USER_NAME);
			mTitle = bundle.getString(BUNDLE_TITLE);
		}

		sharedPreferences = getSharedPreferences("danmu_demo",
				Context.MODE_PRIVATE); // 私有数据

		// Change to your own switch
		mBtnSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				boolean danmuStatus = sharedPreferences.getBoolean(
						KEY_DANMU_STATUS, true);

				if (danmuStatus) {
					Editor editor = sharedPreferences.edit();
					editor.putBoolean(KEY_DANMU_STATUS, false);
					editor.commit();

					danMuClient.stopDanMu();
					mDanMuLayout.setVisibility(View.GONE);
				} else {
					Editor editor = sharedPreferences.edit();
					editor.putBoolean(KEY_DANMU_STATUS, true);
					editor.commit();

					danMuClient.startDanMu();
					mDanMuLayout.setVisibility(View.VISIBLE);
				}

			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		boolean danmuStatus = sharedPreferences.getBoolean(KEY_DANMU_STATUS,
				true);

		MyHandler myHandler = new MyHandler();
		danMuClient = new DanMuClient(this, myHandler);
		mDanMuLayout.setDanMuClient(danMuClient, mUserName, mTitle);
		android.util.Log.e("danmuStatus", "danmuStatus: " + danmuStatus);
		if (danmuStatus) {
			danMuClient.startDanMu();
		} else {
			mDanMuLayout.setVisibility(View.GONE);
		}
	}

	/*
	 * {@"msg":msg,@"book":book,@"user":username}
	 */
	private class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			String KEY_CONTENT = "content";
			String content = (String) bundle.get(KEY_CONTENT);
			mDanMuLayout.createDanMuText(content);
		}
	}

	public void onStop() {
		danMuClient.stopDanMu();
		super.onStop();
	}

	@Override
	public void finish() {
		super.finish();
		danMuClient.stopDanMu();
	}

}
