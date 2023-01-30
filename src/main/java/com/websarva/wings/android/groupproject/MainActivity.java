package com.websarva.wings.android.groupproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.location.Location;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

public class MainActivity extends AppCompatActivity {
	/**
	 * ログに記載するタグ用の文字列。
	 */
	private static final String DEBUG_TAG = "AsyncSample";

	private static final String WEATHERINFO_URL1 = "https://api.openweathermap.org/data/2.5/weather?lang=ja";
	private static final String WEATHERINFO_URL2 = "https://api.openweathermap.org/data/2.5/weather?lang=en";

	/**
	 * お天気APIにアクセスすするためのAPIキー。
	 */
	private static final String APP_ID = "47d0b1b21a5c9105a9703f39c8736bf8";

	private Button weatherbutton;
	private ImageButton tcuAddvertise;

	private int count = 0;

	public String putCityName;
	public String putWeather;

	Intent intent;

	String WIurl;

	/**
	 * 緯度フィールド。
	 */
	private double _latitude = 0;
	/**
	 * 経度フィールド
	 */
	private double _longitude = 0;
	/**
	 * FusedLocationProviderClientオブジェクトフィールド。
	 */
	private FusedLocationProviderClient _fusedLocationClient;
	/**
	 * LocationRequestオブジェクトフィールド。
	 */
	private LocationRequest _locationRequest;
	/**
	 * 位置情報が変更された時の処理を行うコールバックオブジェクトフィールド。
	 */
//	private OnUpdateLocation _onUpdateLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//		目的地を入力するEditText
		EditText editText = findViewById(R.id.destination);
		String text = editText.getText().toString();

		// my Custom Fonts
		Typeface cf_nicokaku = Typeface.createFromAsset(getAssets(), "nicokaku_v1.ttf");
		Typeface cf_spUI = Typeface.createFromAsset(getAssets(), "smartPhoneUI.otf");

//		editextに入力したtextを取得するbutton
//		天気を表示するためのボタン
		weatherbutton = findViewById(R.id.weatherButton);
		weatherbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
//				ボタンが繰り返し押すことで，現在地，経由地，目的地をそれぞれ表示する
				if(count == 0) {
					EditText current = findViewById(R.id.current_location);
					String q_current = current.getText().toString();
					current.setTypeface(cf_spUI);
					String urlFull_current = WIurl + "&q=" + q_current + "&appid=" + APP_ID;
					receiveWeatherInfo(urlFull_current);
					count = 1;
				} else if (count == 1) {
					EditText waypoint = findViewById(R.id.waypoint);
					String q_waypoint = waypoint.getText().toString();
					waypoint.setTypeface(cf_spUI);
					String urlFull_destination = WIurl + "&q=" + q_waypoint + "&appid=" + APP_ID;
					receiveWeatherInfo(urlFull_destination);
					count = 2;
				} else if (count == 2) {
					EditText destination = findViewById(R.id.destination);
					String q_destination = destination.getText().toString();
					destination.setTypeface(cf_spUI);
					String urlFull_destination = WIurl + "&q=" + q_destination + "&appid=" + APP_ID;
					receiveWeatherInfo(urlFull_destination);
					count = 0;
				}
			}
		});
		tcuAddvertise = (ImageButton) findViewById(R.id.adv);
		tcuAddvertise.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickTcuAddvertise();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//main.xmlの内容を読み込む
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mail:
				intent = new Intent(getApplication(), MailActivity.class);
				startActivity(intent);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void MailActivity() {
		Intent intent = new Intent(this, MailActivity.class);
		startActivity(intent);
	}

	public void onClickTcuAddvertise() {
		Intent intent = new Intent(this, AddActivity.class);
		startActivity(intent);
	}

	//	mapbuttonが押された時の処理
	public void onMapShowCurrentButtonClick(View view) {
		// 入力欄に入力されたキーワード文字列を取得
		EditText current = findViewById(R.id.current_location);
		String present = current.getText().toString();
		EditText etSearchWord = findViewById(R.id.destination);
		String searchWord = etSearchWord.getText().toString();

		try {
			// 入力されたキーワードをURLエンコード。
			searchWord = URLEncoder.encode(searchWord, "UTF-8");
			// マップアプリと連携するURI文字列を生成。
			// 起動した際,現在地から目的地までのルートが表示される
			String uriStr = "geo:0,0?q=" + present + "から" + searchWord;
			// URI文字列からURIオブジェクトを生成。
			Uri uri = Uri.parse(uriStr);
			// Intentオブジェクトを生成。
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			// アクティビティを起動。
			startActivity(intent);
		}
		catch(UnsupportedEncodingException ex) {
			Log.e("MainActivity", "検索キーワード変換失敗", ex);
		}
	}

	//	待ち合わせボタンが押された時の処理
	public void showAppoint(View view) {
		// 「出発点」に入力されたキーワード文字列を取得
		EditText start = findViewById(R.id.current_location);
		String present = start.getText().toString();
		EditText etSearchWord = findViewById(R.id.destination);
		String searchWord = etSearchWord.getText().toString();

		try {
			// 入力されたキーワードをURLエンコード。
			searchWord = URLEncoder.encode(searchWord, "UTF-8");
			// マップアプリと連携するURI文字列を生成。
			// 起動した際,現在地から目的地までのルートが表示される
			String uriStr = "geo:0,0?q=" + present + " 周辺の駅";
			// URI文字列からURIオブジェクトを生成。
			Uri uri = Uri.parse(uriStr);
			// Intentオブジェクトを生成。
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			// アクティビティを起動。
			startActivity(intent);
		}
		catch(UnsupportedEncodingException ex) {
			Log.e("MainActivity", "検索キーワード変換失敗", ex);
		}
	}

// おススメスポット
	public void onSpotshow(View view) {
		// 入力欄に入力されたキーワード文字列を取得。
		EditText gSearchWord = findViewById(R.id.destination);
		String gsearchWord = gSearchWord.getText().toString();

		try {
			// 入力されたキーワードをURLエンコード。
			gsearchWord = URLEncoder.encode(gsearchWord, "UTF-8");
			// マップアプリと連携するURI文字列を生成。
			String uriStr = "https://www.google.com/search?q=" + gsearchWord + "+おすすめスポット";
			// URI文字列からURIオブジェクトを生成。
			Uri uri = Uri.parse(uriStr);
			// Intentオブジェクトを生成。
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			// アクティビティを起動。
			startActivity(intent);
		}
		catch(UnsupportedEncodingException ex) {
			Log.e("MainActivity", "検索キーワード変換失敗", ex);
		}
	}

	// 天気を表示
	@UiThread
	private void receiveWeatherInfo(final String urlFull) {
		Looper mainLooper = Looper.getMainLooper();
		Handler handler = HandlerCompat.createAsync(mainLooper);
		WeatherInfoBackgroundReceiver backgroundReceiver = new WeatherInfoBackgroundReceiver(handler, urlFull);
		ExecutorService executorService  = Executors.newSingleThreadExecutor();
		executorService.submit(backgroundReceiver);
	}

	/**
	 * 非同期でお天気情報APIにアクセスするためのクラス。
	 */
	private class WeatherInfoBackgroundReceiver implements Runnable {
		/**
		 * ハンドラオブジェクト。
		 */
		private final Handler _handler;
		/**
		 * お天気情報を取得するURL。
		 */
		private final String _urlFull;

		/**
		 * コンストラクタ。
		 * 非同期でお天気情報Web APIにアクセスするのに必要な情報を取得する。
		 *
		 * @param handler ハンドラオブジェクト。
		 * @param urlFull お天気情報を取得するURL。
		 */
		public WeatherInfoBackgroundReceiver(Handler handler , String urlFull) {
			_handler = handler;
			_urlFull = urlFull;
		}

		@WorkerThread
		@Override
		public void run() {
			// HTTP接続を行うHttpURLConnectionオブジェクトを宣言。finallyで解放するためにtry外で宣言。
			HttpURLConnection con = null;
			// HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言。
			InputStream is = null;
			// 天気情報サービスから取得したJSON文字列。天気情報が格納されている。
			String result = "";
			try {
				// URLオブジェクトを生成。
				URL url = new URL(_urlFull);
				// URLオブジェクトからHttpURLConnectionオブジェクトを取得。
				con = (HttpURLConnection) url.openConnection();
				// 接続に使ってもよい時間を設定。
				con.setConnectTimeout(1000);
				// データ取得に使ってもよい時間。
				con.setReadTimeout(1000);
				// HTTP接続メソッドをGETに設定。
				con.setRequestMethod("GET");
				// 接続。
				con.connect();
				// HttpURLConnectionオブジェクトからレスポンスデータを取得。
				is = con.getInputStream();
				// レスポンスデータであるInputStreamオブジェクトを文字列に変換。
				result = is2String(is);
			}
			catch(MalformedURLException ex) {
				Log.e(DEBUG_TAG, "URL変換失敗", ex);
			}
			// タイムアウトの場合の例外処理。
			catch(SocketTimeoutException ex) {
				Log.w(DEBUG_TAG, "通信タイムアウト", ex);
			}
			catch(IOException ex) {
				Log.e(DEBUG_TAG, "通信失敗", ex);
			}
			finally {
				// HttpURLConnectionオブジェクトがnullでないなら解放。
				if(con != null) {
					con.disconnect();
				}
				// InputStreamオブジェクトがnullでないなら解放。
				if(is != null) {
					try {
						is.close();
					}
					catch(IOException ex) {
						Log.e(DEBUG_TAG, "InputStream解放失敗", ex);
					}
				}
			}
			WeatherInfoPostExecutor postExecutor = new WeatherInfoPostExecutor(result);
			_handler.post(postExecutor);
		}

		/**
		 * InputStreamオブジェクトを文字列に変換するメソッド。 変換文字コードはUTF-8。
		 *
		 * @param is 変換対象のInputStreamオブジェクト。
		 * @return 変換された文字列。
		 * @throws IOException 変換に失敗した時に発生。
		 */
		private String is2String(InputStream is) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			StringBuffer sb = new StringBuffer();
			char[] b = new char[1024];
			int line;
			while(0 <= (line = reader.read(b))) {
				sb.append(b, 0, line);
			}
			return sb.toString();
		}
	}

	/**
	 * 非同期でお天気情報を取得した後にUIスレッドでその情報を表示するためのクラス。
	 */
	private class WeatherInfoPostExecutor implements Runnable {
		/**
		 * 取得したお天気情報JSON文字列。
		 */
		private final String _result;

		// my Custom Fonts
		Typeface cf_nicokaku = Typeface.createFromAsset(getAssets(), "nicokaku_v1.ttf");
		Typeface cf_sfUI = Typeface.createFromAsset(getAssets(), "smartPhoneUI.otf");

		/**
		 * コンストラクタ。
		 *
		 * @param result Web APIから取得したお天気情報JSON文字列。
		 */
		public WeatherInfoPostExecutor(String result) {
			_result = result;
		}

		@UiThread
		@Override
		public void run() {
			// 都市名。
			String cityName = "";
			// 天気。
			String weather = "";
			// 緯度
			String latitude = "";
			// 経度。
			String longitude = "";
			try {
				// ルートJSONオブジェクトを生成。
				JSONObject rootJSON = new JSONObject(_result);
				// 都市名文字列を取得。
				cityName = rootJSON.getString("name");
				// 緯度経度情報JSONオブジェクトを取得。
				JSONObject coordJSON = rootJSON.getJSONObject("coord");
				// 緯度情報文字列を取得。
				latitude = coordJSON.getString("lat");
				// 経度情報文字列を取得。
				longitude = coordJSON.getString("lon");
				// 天気情報JSON配列オブジェクトを取得。
				JSONArray weatherJSONArray = rootJSON.getJSONArray("weather");
				// 現在の天気情報JSONオブジェクトを取得。
				JSONObject weatherJSON = weatherJSONArray.getJSONObject(0);
				// 現在の天気情報文字列を取得。
				weather = weatherJSON.getString("description");
			}
			catch(JSONException ex) {
				Log.e(DEBUG_TAG, "JSON解析失敗", ex);
			}

			putCityName = cityName;
			putWeather = weather;

			// 画面に表示する「〇〇の天気」文字列を生成。
			String weather01 = getString(R.string.weather);
			String telop = cityName + weather01;
			// 天気の詳細情報を表示する文字列を生成。
			String now = getString(R.string.now);
			String desc = now + weather ;
			// 天気情報を表示するTextViewを取得。
			TextView tvWeatherTelop = findViewById(R.id.tvWeatherTelop);
			TextView tvWeatherDesc = findViewById(R.id.tvWeatherDesc);

			// 天気情報を表示。
			tvWeatherTelop.setText(telop);
			tvWeatherDesc.setText(desc);
			tvWeatherTelop.setTypeface(cf_nicokaku);
			tvWeatherDesc.setTypeface(cf_nicokaku);
		}
	}

	public void SpotShow(View view){
		Intent intent = new Intent(MainActivity.this, com.websarva.wings.android.groupproject.SpotShow.class);

		intent.putExtra("etSearchWord3",putCityName);
		intent.putExtra("weather",putWeather);

		startActivity(intent);
	}


	@Override
	protected void onResume() {
		super.onResume();

		if(getString(R.string.current_hint).equals("現在地")){
			WIurl = WEATHERINFO_URL1;
		} else if (getString(R.string.current_hint).equals("Current")){
			WIurl = WEATHERINFO_URL2;
		}
	}



//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		// ACCESS_FINE_LOCATIONの許可が下りていないなら…
//		if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			// ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際、リクエストコードを1000に設定。
//			String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
//			ActivityCompat.requestPermissions(MainActivity.this, permissions, 1000);
//			// onResume()メソッドを終了。
//			return;
//		}
//		// 位置情報の追跡を開始。
//		_fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
//	}
//
//	@Override
//	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//		// ACCESS_FINE_LOCATIONに対するパーミションダイアログでかつ許可を選択したなら…
//		if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//			// 再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止。
//			if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//				return;
//			}
//			// 位置情報の追跡を開始。
//			_fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
//		}
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//
//		// 位置情報の追跡を停止。
//		_fusedLocationClient.removeLocationUpdates(_onUpdateLocation);
//	}	// 位置情報の追跡を中止
//
//	/**
//	 * 位置情報が変更された時の処理を行うコールバッククラス。
//	 */
//	private class OnUpdateLocation extends LocationCallback {
//		@Override
//		public void onLocationResult(LocationResult locationResult) {
//			if(locationResult != null) {
//				// 直近の位置情報を取得。
//				Location location = locationResult.getLastLocation();
//				if(location != null) {
//					// locationオブジェクトから緯度を取得。
//					_latitude = location.getLatitude();
//					// locationオブジェクトから経度を取得。
//					_longitude = location.getLongitude();
//
//					// 取得した緯度をTextViewに表示。
//					TextView tvLatitude = findViewById(R.id.tvLatitude);
//					tvLatitude.setText(Double.toString(_latitude));
//				}
//			}
//		}
//	}
}

