package de.live.gdev.cherrymusic;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends Activity 
{
	private static boolean loadInDesktopMode = false;
	WebView webView;
	SharedPreferences pref;

	boolean prefAutoLogin;
	boolean prefSslAccept;
	String prefUsername;
	String prefPassword;
	String prefProfileNo;
	String prefPath;
	Uri url;

	@Override
	@SuppressLint("JavascriptInterface")
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.webView = (WebView) findViewById(R.id.webView);
		this.pref = this.getSharedPreferences(this.getString(R.string.shared_pref), MODE_PRIVATE);

		this.webView.setWebChromeClient(new WebChromeClient());
		this.webView.setWebViewClient(new WebViewClient() {
		    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error){
		        if(MainActivity.this.prefSslAccept){
		        	handler.proceed();
		        }
		        else{
		        	Toast.makeText(MainActivity.this, getString(R.string.ssl_toast_error), Toast.LENGTH_SHORT).show();
		        	webView.loadData(getString(R.string.ssl_webview_error_str), "text/html", "UTF-16");
		        }
		    }
		});
		
		// Javascript interface
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
    	}
		
		
		// Apply web settings
		WebSettings settings = this.webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true);		
		if(loadInDesktopMode){
			settings.setBuiltInZoomControls(true);
			settings.setSupportZoom(true);
			settings.setLoadWithOverviewMode(true);
			settings.setUseWideViewPort(true);
		}
		
		// ActionBar
		ActionBar ab = this.getActionBar();
		ab.setIcon(R.drawable.null_void);
		

		this.loadPreferences();
		this.loadWebapp(this.prefAutoLogin);
	}
	
	/**
	 * Load webapp
	 * @param doLogin if user should be logged in
	 */
	public void loadWebapp(boolean doLogin){
		try {this.url = Uri.parse(this.prefPath);
		}catch(Exception e){
			url = null;
			this.webView.loadData(this.getString(R.string.no_valid_path), "text/html", "UTF-16");
			return;
		}
		
		if(url.toString().equals("")){
			this.webView.loadData(this.getString(R.string.no_valid_path), "text/html", "UTF-16");
		}
		else{
			this.webView.loadUrl(url.toString());
			if(doLogin){
				String postData = "username=" + this.prefUsername + "&password=" + this.prefPassword + "&login=login";
				this.webView.postUrl(url.toString(), EncodingUtils.getBytes(postData, "base64"));
			}
		}
	}
	
	/**
	 * Load all preferences
	 */
	public void loadPreferences(){
		this.prefProfileNo= Integer.toString(pref.getInt(SettingsActivity.PREF_SELPROFILE, 0));
		this.prefUsername = pref.getString(SettingsActivity.PREF_USERNAME+prefProfileNo, "");
		this.prefPassword = pref.getString(SettingsActivity.PREF_PASSWORD+prefProfileNo, "");
		this.prefPath 	  = pref.getString(SettingsActivity.PREF_PATH+prefProfileNo, "");
		this.prefAutoLogin= pref.getBoolean(SettingsActivity.PREF_AUTOLOGIN+prefProfileNo, false);
		this.prefSslAccept= pref.getBoolean(SettingsActivity.PREF_SSLACCEPT+prefProfileNo, false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode){
			case SettingsActivity.SETTINGS_ACTIVITY_ID:{
				this.loadPreferences();
				this.loadWebapp(this.prefAutoLogin);
			}break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case R.id.action_settings:{
				Intent i = new Intent(this, SettingsActivity.class);
				startActivityForResult(i,  SettingsActivity.SETTINGS_ACTIVITY_ID);
				break;}
			case R.id.action_info:{
				Intent i = new Intent(this, InfoActivity.class);
				startActivity(i);
				break;}
			case R.id.action_login:{
				this.loadWebapp(true);
				break;}
			case R.id.action_exit:{
				this.finish();
				break;}
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
	 public boolean onKeyDown(int key, KeyEvent e) {
	     if ((key == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	    	 webView.goBack();
	         return true;
	     }
	     return super.onKeyDown(key, e);
	 }
	 
	 /**
	  *  Java methods from this class can be called from javascript
	  */
	 public class JavaScriptInterface {
	     Context context;
	     JavaScriptInterface(Context c) {
	         context = c;
	     }
	 }
}
