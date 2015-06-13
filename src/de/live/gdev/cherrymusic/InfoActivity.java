package de.live.gdev.cherrymusic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class InfoActivity extends Activity {

	Button verionField;
	PackageManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		((Button) this.findViewById(R.id.button_androidAppAuthor)).setBackgroundDrawable(null);
		((Button) this.findViewById(R.id.button_additionalContact)).setBackgroundDrawable(null);	
		this.verionField = (Button) this.findViewById(R.id.textView_Version);
		this.manager = this.getPackageManager();
		
		// ActionBar
		ActionBar ab = this.getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayUseLogoEnabled(true);
		
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			this.verionField.setText("App Version   v" + info.versionName);
		} catch (Exception e) {
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:{
				this.finish();
			break;}
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onAdditionalPageClicked(View v) {
		this.openWebpage(this.getString(R.string.page_additional_homepage));
	}
	
	public void onAuthorClicked(View v){
		this.openWebpage(this.getString(R.string.page_author));
	}
	
	public void onVersionClicked(View v){
		this.openWebpage(this.getString(R.string.page_versioning));
	}
	
	private void openWebpage(String url){
		try {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		} catch (Exception e) {}
	}
}
