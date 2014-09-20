package com.seqhack.facebook;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seqhack.olawars.Olawars;
import com.seqhack.olawars.R;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
// import com.sromku.simple.fb.example.R;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class FacebookPage extends Fragment implements OnItemClickListener{

	protected static final String TAG = FacebookPage.class.getName();
	private Button mButtonLogin;
	private Button mButtonLogout;
	private TextView mTextStatus;
	private ListView mListView;

	private ArrayList<Example> mExamples;

	private SimpleFacebook mSimpleFacebook;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSimpleFacebook = SimpleFacebook.getInstance();
		
		mExamples = new ArrayList<Example>();
		// mExamples.add(new Example("Invite", InviteFragment.class));
		// mExamples.add(new Example("Publish", null));
		// mExamples.add(new Example("Publish feed - dialog", PublishFeedDialogFragment.class));
		// mExamples.add(new Example("Publish feed - no dialog", PublishFeedFragment.class));
		// mExamples.add(new Example("Publish feed more options - no dialog", PublishFeedMoreFragment.class));
		// mExamples.add(new Example("Publish story - no dialog", PublishStoryFragment.class));
		// mExamples.add(new Example("Publish photo", PublishPhotoFragment.class));
		// mExamples.add(new Example("Publish score", PublishScoreFragment.class));
		// mExamples.add(new Example("Get", null));
		// mExamples.add(new Example("Get app requests", GetAppRequestsFragment.class));
		// mExamples.add(new Example("Get comments", GetCommentsFragment.class));
		// mExamples.add(new Example("Get events (attending)", GetEventsFragment.class));
		// mExamples.add(new Example("Get friends", GetFriendsFragment.class));
		// mExamples.add(new Example("Get notifications", GetNotificationsFragment.class));
		// mExamples.add(new Example("Get posts", GetPostsFragment.class));
		// mExamples.add(new Example("Get profile", GetProfileFragment.class));
		// mExamples.add(new Example("Get scores", GetScoresFragment.class));
		// mExamples.add(new Example("Get objects (open graph)", GetStoryObjectsFragment.class));
		// mExamples.add(new Example("Misc", null));

//		mExamples.add(new Example("Privacy", PermissionsFragment.class));
//		mExamples.add(new Example("Configuration", PermissionsFragment.class));
//		mExamples.add(new Example("Granted permissions", PermissionsFragment.class));
//		mExamples.add(new Example("Request new permissions", PermissionsFragment.class));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle("Ola Wars");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		mButtonLogin = (Button) view.findViewById(R.id.button_login);
		mButtonLogout = (Button) view.findViewById(R.id.button_logout);
		mTextStatus = (TextView) view.findViewById(R.id.text_status);
		mListView = (ListView) view.findViewById(R.id.list);

		setLogin();
		setLogout();
		
		mListView.setAdapter(new ExamplesAdapter(mExamples));
		mListView.setOnItemClickListener(this);
		
		setUIState();
		return view;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Class<? extends Fragment> fragment = mExamples.get(position).getFragment();
		if (fragment != null) {
			addFragment(fragment);
		}
	}

	private void addFragment(Class<? extends Fragment> fragment) {
		try {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.replace(R.id.frame_layout, fragment.newInstance());
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
		catch (Exception e) {
			Log.e(TAG, "Failed to add fragment", e);
		}
	}
	
	/**
	 * Login example.
	 */
	private void setLogin() {
		// Login listener
		final OnLoginListener onLoginListener = new OnLoginListener() {

			@Override
			public void onFail(String reason) {
				mTextStatus.setText(reason);
				Log.w(TAG, "Failed to login");
			}

			@Override
			public void onException(Throwable throwable) {
				mTextStatus.setText("Exception: " + throwable.getMessage());
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
				// show progress bar or something to the user while login is
				// happening
				mTextStatus.setText("Thinking...");
			}

			@Override
			public void onLogin() {
				// change the state of the button or do whatever you want
				mTextStatus.setText("Howdy!!");
				Intent i = new Intent(getActivity(), Olawars.class);                      
				startActivity(i);
				// loggedInUIState();
			}

			@Override
			public void onNotAcceptingPermissions(Permission.Type type) {
//				toast(String.format("You didn't accept %s permissions", type.name()));
			}
		};

		mButtonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.login(onLoginListener);
			}
		});
	}

	/**
	 * Logout example
	 */
	private void setLogout() {
		final OnLogoutListener onLogoutListener = new OnLogoutListener() {

			@Override
			public void onFail(String reason) {
				mTextStatus.setText(reason);
				Log.w(TAG, "Failed to login");
			}

			@Override
			public void onException(Throwable throwable) {
				mTextStatus.setText("Exception: " + throwable.getMessage());
				Log.e(TAG, "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
				// show progress bar or something to the user while login is
				// happening
				mTextStatus.setText("Thinking...");
			}

			@Override
			public void onLogout() {
				// change the state of the button or do whatever you want
				mTextStatus.setText("Logged out");
				loggedOutUIState();
			}

		};

		mButtonLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.logout(onLogoutListener);
			}
		});
	}
	
	private void setUIState() {
		if (mSimpleFacebook.isLogin()) {
			loggedInUIState();
		}
		else {
			loggedOutUIState();
		}
	}

	private void loggedInUIState() {
		mButtonLogin.setEnabled(false);
		mButtonLogout.setEnabled(true);
		mListView.setVisibility(View.VISIBLE);
		mTextStatus.setText("Logged in");
	}

	private void loggedOutUIState() {
		mButtonLogin.setEnabled(true);
		mButtonLogout.setEnabled(false);
		mListView.setVisibility(View.INVISIBLE);
		mTextStatus.setText("Logged out");
	}
}
