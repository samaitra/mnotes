package com.notepad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {

	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_edit);
		setTitle(R.string.edit_note);
		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);
		Button confirmButton = (Button) findViewById(R.id.confirm);
		
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    String title = extras.getString(NotesDbHelper.KEY_TITLE);
		    String body = extras.getString(NotesDbHelper.KEY_BODY);
		    mRowId = extras.getLong(NotesDbHelper.KEY_ROWID);

		    if (title != null) {
		        mTitleText.setText(title);
		    }
		    if (body != null) {
		        mBodyText.setText(body);
		    }
		}
		
		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		    	Bundle bundle = new Bundle();

		    	bundle.putString(NotesDbHelper.KEY_TITLE, mTitleText.getText().toString());
		    	bundle.putString(NotesDbHelper.KEY_BODY, mBodyText.getText().toString());
		    	if (mRowId != null) {
		    	    bundle.putLong(NotesDbHelper.KEY_ROWID, mRowId);
		    	}
		    	Intent mIntent = new Intent();
		    	mIntent.putExtras(bundle);
		    	setResult(RESULT_OK, mIntent);
		    	finish();
		    }

		});
	}

}
