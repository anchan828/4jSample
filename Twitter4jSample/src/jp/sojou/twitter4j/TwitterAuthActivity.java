package jp.sojou.twitter4j;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class TwitterAuthActivity extends Activity {
	private WebView webView_ = null;
	private final static String CALLBACK_URL = "http://www.google.jp"; // �m1�n

	@Override
	public void onCreate(Bundle savedInstanceState) { // �m2�n
		super.onCreate(savedInstanceState);
		setupLayout();
		System.out.println("CALLAUTH");
		String authUrl = getIntent().getExtras().getString("auth_url");
		System.out.println(authUrl);
		// webView_.loadUrl(authUrl);
	}

	void setupLayout() { // �m3�n
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		webView_ = new WebView(this);
		webView_.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) { // �m4�n
				Log.d("onPageStarted", url);
				if (url != null && url.startsWith(CALLBACK_URL)) {
					String oauthToken = "";
					String oauthVerifier = "";
					String[] urlParameters = url.split("\\?")[1].split("&");
					if (urlParameters[0].startsWith("oauth_token")) {
						oauthToken = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_token")) {
						oauthToken = urlParameters[1].split("=")[1];
					}
					if (urlParameters[0].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[0].split("=")[1];
					} else if (urlParameters[1].startsWith("oauth_verifier")) {
						oauthVerifier = urlParameters[1].split("=")[1];
					}
					Intent intent = getIntent();
					intent.putExtra("oauth_token", oauthToken);
					intent.putExtra("oauth_verifier", oauthVerifier);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
		layout.addView(webView_, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
	}
}
