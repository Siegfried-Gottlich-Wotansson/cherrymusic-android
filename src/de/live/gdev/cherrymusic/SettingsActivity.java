package de.live.gdev.cherrymusic;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnCheckedChangeListener {
	public static final int SETTINGS_ACTIVITY_ID = 15;
		
	// UI
	private RadioGroup profileRadioGroup;
	private RadioButton[] profileRadios = new RadioButton[3];
	private Button saveButton;
	private CheckBox autoLogin;
	private CheckBox sslAccept;
	private EditText path;
	private EditText username;
	private EditText password;
	
	// Shared Preference
	private SharedPreferences pref;
	public static final String PREF_AUTOLOGIN = "autologin";
	public static final String PREF_SSLACCEPT = "sslAccept";
	public static final String PREF_PATH = "path";
	public static final String PREF_USERNAME = "username";
	public static final String PREF_PASSWORD = "password";
	public static final String PREF_SELPROFILE = "selProfile";
	
	//Profiles
	private int[] profileRadios_R = new int[3];
	private int selProfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.pref = this.getSharedPreferences(this.getString(R.string.shared_pref), MODE_PRIVATE);
		this.saveButton = (Button) findViewById(R.id.buttonSaveSettings);		
		
		this.profileRadioGroup = (RadioGroup) this.findViewById(R.id.radioProfiles);
		this.profileRadios_R[0] = R.id.radioProfileDefault;
		this.profileRadios_R[1] = R.id.radioProfile2;
		this.profileRadios_R[2] = R.id.radioProfile3; 
		this.profileRadios[0] = (RadioButton) findViewById(this.profileRadios_R[0]);
		this.profileRadios[1] = (RadioButton) findViewById(this.profileRadios_R[1]);
		this.profileRadios[2] = (RadioButton) findViewById(this.profileRadios_R[2]);
		this.sslAccept = (CheckBox) findViewById(R.id.checkBoxAcceptAllSsl);
		this.path = (EditText) findViewById(R.id.editTextWebappPath);
		this.username = (EditText) findViewById(R.id.edUsername);
		this.password = (EditText) findViewById(R.id.edPassword);
		this.autoLogin = (CheckBox) findViewById(R.id.checkboxAutologin);

		// Load profile
		this.selProfile = pref.getInt(PREF_SELPROFILE, 0);
		this.profileRadioGroup.setOnCheckedChangeListener(this);
		this.profileRadioGroup.check(this.profileRadios_R[this.selProfile]);
		
		// ActionBar
		ActionBar ab = this.getActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		ab.setDisplayUseLogoEnabled(true);
		
		// Force load default profile 
		if(this.selProfile == 0)
			this.onCheckedChanged(this.profileRadioGroup, profileRadios_R[0]);
	}
	
	public void onSaveButtonClicked(View v){
		this.saveProfile();
	}
	
	public void saveProfile(){
		try{
			String p = this.path.getText().toString();
			if(p.charAt(p.length()-1) != '/')
				this.path.append("/");
		}catch(Exception e){}
		
		String nr = Integer.toString(this.selProfile);
		Editor editor = this.pref.edit();
		editor.putBoolean(PREF_AUTOLOGIN+nr, autoLogin.isChecked());
		editor.putBoolean(PREF_SSLACCEPT+nr, sslAccept.isChecked());
		editor.putString(PREF_PATH+nr, path.getText().toString());
		editor.putString(PREF_USERNAME+nr, username.getText().toString());
		editor.putString(PREF_PASSWORD+nr, password.getText().toString());
		editor.putInt(PREF_SELPROFILE, selProfile);
		editor.apply();
		
		Toast.makeText(this, this.getString(R.string.save_settings_succ),Toast.LENGTH_SHORT).show();
		this.finish();
	}

	
	@Override
	public void onCheckedChanged(RadioGroup rg, int id) {
		// Determine Profile number
		this.selProfile = getSelectedProfileNr();
		String nr = Integer.toString(this.selProfile);
		
		// Get profile data
		this.path.setText(pref.getString(PREF_PATH+nr, ""));
		this.username.setText(pref.getString(PREF_USERNAME+nr, ""));
		this.password.setText(pref.getString(PREF_PASSWORD+nr, ""));
		this.autoLogin.setChecked(pref.getBoolean(PREF_AUTOLOGIN+nr, false));
		this.sslAccept.setChecked(pref.getBoolean(PREF_SSLACCEPT+nr, false));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:{
				this.finish();
			break;}
			case R.id.action_saveprofile:{
				this.saveProfile();
				break;}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	/**
	 * Get the actual selected profile number
	 * @return The profile no
	 */
	private int getSelectedProfileNr(){
		int id = this.profileRadioGroup.getCheckedRadioButtonId();
		for(int i=0; i < profileRadios_R.length; i++){
			if(profileRadios_R[i] == id)
				return i;
		}
		return 0;
	}

}
