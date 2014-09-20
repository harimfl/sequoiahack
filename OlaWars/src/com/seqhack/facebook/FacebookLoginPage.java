package com.seqhack.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import com.seqhack.olawars.R;

import com.sromku.simple.fb.SimpleFacebook;
import com.seqhack.facebook.FacebookPage;
// import com.sromku.simple.fb.example.utils.Utils;

public class FacebookLoginPage extends Activity {
	protected static final String TAG = FacebookLoginPage.class.getName();

	private SimpleFacebook mSimpleFacebook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSimpleFacebook = SimpleFacebook.getInstance(this);

		// test local language
		// Utils.updateLanguage(getApplicationContext(), "en");
		// Utils.printHashKey(getApplicationContext());

		setContentView(R.layout.activity_main);
		addFragment();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
	}

	private void addFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.frame_layout, new FacebookPage());
		fragmentTransaction.commit();
	}

}
