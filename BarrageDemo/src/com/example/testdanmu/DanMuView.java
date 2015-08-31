package com.example.testdanmu;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("NewApi")
public class DanMuView extends RelativeLayout {
	public DanMuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public DanMuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public DanMuView(Context context) {
		super(context);
		initView(context);
	}

	private RelativeLayout mDanMuView;
	private Context mContext;
	private int mWidth;
	private int mHeight;

	private EditText danmuContentEdit;
	private Button buttonSend;
	private LinearLayout mDanInputLayout;
	private TextView danMuHintBall;

	private void initView(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.danmu_view, this);
		mDanMuView = (RelativeLayout) findViewById(R.id.yuedu_danmu_view);
		mDanInputLayout = (LinearLayout) findViewById(R.id.danmu_input_ll);
		danMuHintBall = (TextView) findViewById(R.id.yuedu_danmu_hint_ball);

		mWidth = DeviceUtils.getScreenWidthPx(mContext.getApplicationContext());
		mHeight = DeviceUtils.getScreenHeightPx(mContext
				.getApplicationContext());
		
		RelativeLayout.LayoutParams rl =  (RelativeLayout.LayoutParams)danMuHintBall.getLayoutParams();
		rl.setMargins(rl.leftMargin, (int)(mHeight * 0.4), rl.rightMargin, rl.bottomMargin);
		danMuHintBall.setLayoutParams(rl);
		
		danmuContentEdit = (EditText) findViewById(R.id.sent_danmu_content);
		buttonSend = (Button) findViewById(R.id.send_danmu);
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					sendDanMuToServer();
					danmuContentEdit.clearFocus();
					hideSoftKeyboard();
					danmuContentEdit.setText("");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		danMuHintBall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeInputLLVisible();
			}
		});
	}

	public void changeInputLLVisible() {
		if (this.getVisibility() == View.GONE
				|| mDanInputLayout.getVisibility() == View.GONE) {
			mDanInputLayout.setVisibility(View.VISIBLE);
			this.setVisibility(View.VISIBLE);
		} else {
			mDanInputLayout.setVisibility(View.GONE);
		}
	}

	private void sendDanMuToServer() throws JSONException {
		String content = danmuContentEdit.getText().toString();
		JSONObject jObjectContent = new JSONObject();
		jObjectContent.put(DanMuConstant.BOOK_NAME, bookName);
		jObjectContent.put(DanMuConstant.USER_NAME, userName);
		jObjectContent.put(DanMuConstant.MESSAGE, content);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String timeStr = formatter.format(curDate);

		JSONObject jsSend = new JSONObject();
		jsSend.put("content", jObjectContent.toString());
		jsSend.put("to_client_id", "all");
		jsSend.put("from_client_id", 145);
		jsSend.put("type", "send");
		jsSend.put("time", timeStr);

		android.util.Log.e("sendDanMuToServer",
				"sendDanMuToServer: " + jsSend.toString());
		client.sendDanMu(jsSend.toString());

	}

	private DanMuClient client;
	private String userName;
	private String bookName;

	public void setDanMuClient(DanMuClient client, String userName,
			String bookName) {
		this.client = client;
		this.userName = userName;
		this.bookName = bookName;
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(((Activity) mContext).getWindow()
					.getDecorView().getWindowToken(), 0);
		}
	}
	
	public void createDanMuText(String txt) {
		final TextView txtView = new TextView(mContext);
		JSONObject js;
		String userName = null;
		String bookName = null;
		String msgRecv = null;
		Log.e("createDanMuText", "createDanMuText" + txt);

		try {
			js = new JSONObject(txt);
			bookName = js.optString(DanMuConstant.BOOK_NAME);
			userName = js.optString(DanMuConstant.USER_NAME);
			msgRecv = js.optString(DanMuConstant.MESSAGE);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (userName == null || userName.isEmpty()) {
			userName = "匿名";
		}

		if (bookName == null || bookName.isEmpty()) {
			bookName = " ";
		}

		if (bookName != null && bookName.length() > 4) {
			bookName = bookName.substring(0, 4) + "...";
		}

		if (msgRecv == null || msgRecv.isEmpty()) {
			msgRecv = "消息坏掉啦，喊一哥";
		}

		SpannableStringBuilder buildString = new SpannableStringBuilder();
		String firstPart = userName + " 《" + bookName + "》\n";
		buildString.append(firstPart);
		buildString.append(msgRecv);

		buildString.setSpan(new AbsoluteSizeSpan(8, true), 0,
				firstPart.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

		txtView.setText(buildString);
		txtView.setMaxLines(3);
		int random_bear = (int) (Math.random() * 4);
		Log.e("random_bear", "random_bear" + random_bear);
		switch (random_bear) {
		case 0:
			txtView.setBackgroundResource(R.drawable.cutebear0);
			break;
		case 1:
			txtView.setBackgroundResource(R.drawable.cutebear1);
			break;
		case 2:
			txtView.setBackgroundResource(R.drawable.cutebear2);
			break;
		case 3:
			txtView.setBackgroundResource(R.drawable.cutebear3);
			break;
		}

		txtView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		double random_position = Math.random();
		int startY = (int) ((random_position * 0.6) * (double) mHeight);
		Log.e("random_bear", "startY: " + startY + " mHeight: " + mHeight);

		int txtWidth = txtView.getMeasuredWidth();

		RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rlParams.setMargins(mWidth, startY, 0, 0);
		txtView.setLayoutParams(rlParams);

		mDanMuView.addView(txtView);

		ObjectAnimator outAnim = ObjectAnimator.ofFloat(txtView,
				View.TRANSLATION_X, -mWidth - txtWidth);
		outAnim.setDuration(10000);
		outAnim.start();		

		outAnim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// txtView.setTag("Added");
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mDanMuView.removeView(txtView);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

		});
	}

}
