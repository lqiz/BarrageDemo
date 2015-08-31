package com.example.testdanmu;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.TargetApi;
import android.util.Log;

public class DanMuClient {
	private WebSocketClient mWebSocketClient;
	private Handler myHandler;

	private String URL_PATH = "wss://45.78.11.92:3232";
	private String KEY_CONTENT = "content";
	private Context context;

	public DanMuClient(Context context, Handler handler) {
		this.context = context;
		myHandler = handler;
	}

	public Handler getDanMuHandler() {
		return myHandler;
	}

	private void connectWebSocket() {
		URI uri;
		try {
			uri = new URI(URL_PATH);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		mWebSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {
				Log.e("Websocket", "Websocket Opened");
			}

			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@Override
			public void onMessage(String s) {
				Log.e("Websocket", "Websocket onMessage" + s);
				String content = null;
				try {
					JSONObject js = new JSONObject(s);
					content = js.optString("content");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (content != null && !content.isEmpty()) {
					Bundle bundle = new Bundle();
					bundle.putString(KEY_CONTENT, content);
					Message msg = new Message();
					msg.setData(bundle);
					myHandler.sendMessage(msg);
				}
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.e("Websocket", "Websocket: Closed " + s);
			}

			@Override
			public void onError(Exception e) {
				Log.e("Websocket", "Websocket: Error " + e.getMessage());
			}
		};

		mWebSocketClient.connect();
	}

	public void startDanMu() {
		connectWebSocket();
	}

	public void stopDanMu() {
		if (mWebSocketClient != null) {
			mWebSocketClient.close();
		}
	}
	
	// It needs to be more perfect here.
	public void sendDanMu(String text) {
		if (mWebSocketClient == null) {
			connectWebSocket();
			return;
		}
		
		if (mWebSocketClient.getReadyState() != WebSocket.READY_STATE_OPEN) {
			return;
		}
		
		mWebSocketClient.send(text);	
	}
}
