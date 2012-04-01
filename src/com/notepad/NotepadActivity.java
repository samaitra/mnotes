package com.notepad;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NotepadActivity extends ListActivity{
    /** Called when the activity is first created. */
	public static final int INSERT_ID = Menu.FIRST;
	private NotesDbHelper mDbHelper;	 
	private int mNoteNumber = 1;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST + 1;
	private Cursor mNotesCursor;
	    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notepad_list);
        mDbHelper = new NotesDbHelper(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return result;
    }
   
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case DELETE_ID:
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mDbHelper.deleteNote(info.id);
            fillData();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case INSERT_ID:
            createNote();
            return true;
        }
       
        return super.onOptionsItemSelected(item);
    }

    private void createNote() {
        //int mNoteNumber = 0;
		String noteName = "Note " + mNoteNumber++;
        if (mDbHelper == null){
        	System.out.println("mDbHelper is null");
        }
		mDbHelper.createNote(noteName, "");
        
		fillData();
		
		Intent i = new Intent(this, NoteEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
    }
    private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor c = mDbHelper.fetchAllNotes();
        startManagingCursor(c);

        String[] from = new String[] { NotesDbHelper.KEY_TITLE };
        int[] to = new int[] { R.id.text1 };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new SimpleCursorAdapter(this, R.layout.notes_row, c, from, to);
        setListAdapter(notes);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	Cursor c = mNotesCursor;
    	c.moveToPosition(position);
    	Intent i = new Intent(this, NoteEdit.class);
    	i.putExtra(NotesDbHelper.KEY_ROWID, id);
    	i.putExtra(NotesDbHelper.KEY_TITLE, c.getString(
    	        c.getColumnIndexOrThrow(NotesDbHelper.KEY_TITLE)));
    	i.putExtra(NotesDbHelper.KEY_BODY, c.getString(
    	        c.getColumnIndexOrThrow(NotesDbHelper.KEY_BODY)));
    	startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	super.onActivityResult(requestCode, resultCode, intent);
    	Bundle extras = intent.getExtras();

    	switch(requestCode) {
    	case ACTIVITY_CREATE:
    	    String title = extras.getString(NotesDbHelper.KEY_TITLE);
    	    String body = extras.getString(NotesDbHelper.KEY_BODY);
    	    mDbHelper.createNote(title, body);
    	    fillData();
    	    break;
    	case ACTIVITY_EDIT:
    	    Long mRowId = extras.getLong(NotesDbHelper.KEY_ROWID);
    	    if (mRowId != null) {
    	        String editTitle = extras.getString(NotesDbHelper.KEY_TITLE);
    	        String editBody = extras.getString(NotesDbHelper.KEY_BODY);
    	        mDbHelper.updateNote(mRowId, editTitle, editBody);
    	    }
    	    fillData();
    	    break;
    	}
    }
}