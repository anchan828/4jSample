package jp.sojou.twitter4j;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Twitter4jSampleActivity extends Activity {
	/** Called when the activity is first created. */
	private Twitter twitter;
	private RequestToken oAuthRequestToken;
	private Button oauthButton;
	private TextView screenName;
	private ListView timeLine;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// 認証ボタン
		oauthButton = (Button) findViewById(R.id.oauthButton);
		// ユーザー名
		screenName = (TextView) findViewById(R.id.screenName);
		// つぶやきのリスト
		timeLine = (ListView) findViewById(R.id.listView1);
		// 認証ボタンのイベント処理
		oauthButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 認証へゴー
				oAuth();
			}
		});

	}

	private void oAuth() {
		try {
			// Twitterへの認証を行う準備
			twitter = new TwitterFactory().getInstance();
			// TwitterDevelopサイトで事前にアプリを登録しておく。登録の仕方はググッてみてね。多分簡単に見つかる。
			// コンシューマーキーとコンシューマーシークレットキーをセットする
			twitter.setOAuthConsumer("98nlTvi5Qbb4fT2W1wXyg",
					"7ZRhHHyE7t5Qd8vz14pxCoWg4gcIggIHCcyQRvD0");
			// Twitterにはリクエストトークンとアクセストークンがある
			// まずTwitter側へ「こいつ認証させて大丈夫？」っていう確認のためのリクエストトークンを送信する
			// Twitter側が「大丈夫」っていう返事があるとアクセストークンを取得できます
			// そしてつぶやいたりTwitterの機能を使うときはこのアクセストークンを「もう認証してるからユーザーの情報取らせてね」みたいな証明証として使うことが出来る

			// リクエストトークンを取得する
			// コールバック先もここで指定する。sample://callbackってやつ
			//ここでマニフェストへ移動して設定する
			oAuthRequestToken = twitter
					.getOAuthRequestToken("sample://callback");

			// 認証ページのURL渡してブラウザ起動！
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(oAuthRequestToken.getAuthorizationURL()));
			startActivity(intent);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

	// 認証終わるとここに返ってくるよー
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (twitter != null) {
			try {
				//返ってきたインテントのデータからアクセストークンを取得する
				//「oauth_verifier」ってのは本当にリクエストしてきたっていう証明で、
				//これをさらに使用してアクセストークンを取得するっていう仕組み
				twitter.getOAuthAccessToken(oAuthRequestToken, intent.getData()
						.getQueryParameter("oauth_verifier"));
				//↑以上で認証は終了！あとは好き勝手出来るよ！
				
				/*-----------------------------------------*/
				
				//twitterっていう変数にいろんな情報が格納されているから色々いじってみてね！
				
				screenName.setText(twitter.getScreenName());
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1);
				ResponseList<Status> homeTimeline = twitter.getHomeTimeline();
				for (Status status : homeTimeline) {
					arrayAdapter.add(status.getText());
				}
				timeLine.setAdapter(arrayAdapter);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
	}
}