package com.notepad;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NoteEdit extends Activity {

	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private NotesDbHelper mDbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new NotesDbHelper(this);
		mDbHelper.open();

		setContentView(R.layout.note_edit);

		mTitleText = (EditText) findViewById(R.id.title);
		mBodyText = (EditText) findViewById(R.id.body);

		Button confirmButton = (Button) findViewById(R.id.confirm);

		mRowId = (savedInstanceState == null) ? null :
		    (Long) savedInstanceState.getSerializable(NotesDbHelper.KEY_ROWID);
		if (mRowId == null) {
		    Bundle extras = getIntent().getExtras();
		    mRowId = extras != null ? extras.getLong(NotesDbHelper.KEY_ROWID)
		                            : null;
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

		    public void onClick(View view) {
		        setResult(RESULT_OK);
		        finish();
		    }

		});
	}
	private void populateFields() {
	    if (mRowId != null) {
	        Cursor note = mDbHelper.fetchNote(mRowId);
	        startManagingCursor(note);
	        mTitleText.setText(note.getString(
	                    note.getColumnIndexOrThrow(NotesDbHelper.KEY_TITLE)));
	        mBodyText.setText(note.getString(
	                note.getColumnIndexOrThrow(NotesDbHelper.KEY_BODY)));
	    }
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbHelper.KEY_ROWID, mRowId);
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
	
	 @Override
	    protected void onResume() {
	        super.onResume();
	        populateFields();
	    }
	
	 private void saveState() {
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();

	        if (mRowId == null) {
	            long id = mDbHelper.createNote(title, body);
	            if (id > 0) {
	                mRowId = id;
	            }
	        } else {
	            mDbHelper.updateNote(mRowId, title, body);
	        }
	    }
}
