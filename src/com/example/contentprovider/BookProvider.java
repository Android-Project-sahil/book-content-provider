package com.example.contentprovider;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;

public class BookProvider extends ContentProvider {
	public static final String PROVIDER_NAME="net.learn2develop.provider.Books";
private static final Uri CONTENT_URI=Uri.parse("content://"+PROVIDER_NAME+"/books");
static final String _ID = "_id";
static final String TITLE = "title";
static final String ISBN ="isbn";
private  static final int BOOKS = 1;
private  static final int BOOK_ID=2;
private static final UriMatcher uriMatcher;
//to check whether our URI is correct or not i.e if not matched den it'll overwrite
static{
	uriMatcher= new UriMatcher(UriMatcher.NO_MATCH);
	uriMatcher.addURI(PROVIDER_NAME, "book",BOOKS);
	uriMatcher.addURI(PROVIDER_NAME, "books/#", BOOK_ID); //# means URI can't be changed
			
}
// for database use
private SQLiteDatabase booksDB;
private static final String DATABASE_NAME="Books";
private static final String DATABASE_TABLE="titles";
private static final int DATABASE_VERSION=1;
private static final String DATABASE_CREATE="create table"+DATABASE_TABLE+"(id.integer primary key autoincrement"+"title text not null,isbn text not null";

private static class DatabaseHelper extends SQLiteOpenHelper
{
	DatabaseHelper(Context context)
	{
	super(context, DATABASE_NAME,null,DATABASE_VERSION);
	}
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE);
	}
public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
	
Log.w("Content provider database", "Upgrading database from version"+ oldVersion+"to"+ newVersion);
onCreate(db);

}
}




@Override
public Uri insert(Uri uri, ContentValues values) {
	// TODO Auto-generated method stub
	//-- add a a new book
	long rowID= booksDB.insert(
		DATABASE_TABLE,"",values);
		
		// if added successfully
		if(rowID>0)
		{
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
	}
throw new SQLException("Failed to insert row into"+uri);
}


@Override
public boolean onCreate() {
	// TODO Auto-generated method stub
	Context context = getContext();
	DatabaseHelper dbHelper = new DatabaseHelper(context);
	booksDB = dbHelper.getWritableDatabase();
	return(booksDB == null)?false:true;
	}

@Override
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
		String sortOrder) {
	// TODO Auto-generated method stub
	SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
	sqlBuilder.setTables(DATABASE_TABLE);
	if(uriMatcher.match(uri)== BOOK_ID)
		// If getting a particular book
		sqlBuilder.appendWhere(_ID + "="+ uri.getPathSegments().get(1));
	if(sortOrder == null || sortOrder == "")
		sortOrder = TITLE;
	Cursor c = sqlBuilder.query(booksDB, projection, selection, selectionArgs, null, null, sortOrder);
	// register to watch a content URI for changes
	c.setNotificationUri(getContext().getContentResolver(),uri );
	return c;
}


@Override
public int delete(Uri arg0, String arg1, String[] arg2) {
	// TODO Auto-generated method stub
	 // arg0 = uri 
    // arg1 = selection
    // arg2 = selectionArgs
    int count=0;
    switch (uriMatcher.match(arg0)){
        case BOOKS:
            count = booksDB.delete(
                    DATABASE_TABLE,
                    arg1, 
                    arg2);
            break;
        case BOOK_ID:
            String id = arg0.getPathSegments().get(1);
            count = booksDB.delete(
                    DATABASE_TABLE,                        
                    _ID + " = " + id + 
                    (!TextUtils.isEmpty(arg1) ? " AND (" + 
                            arg1 + ')' : ""), 
                            arg2);
            break;
        default: throw new IllegalArgumentException("Unknown URI " + arg0);    
    }       
    getContext().getContentResolver().notifyChange(arg0, null);
    return count;
}



@Override
public String getType(Uri uri) {
	// TODO Auto-generated method stub
	switch(uriMatcher.match(uri))
	{
	// get books
	case BOOKS:
		return "vnd.android.cursor.dir/vnd.learn2develop.books";// create a  a path to a cursor
	//-- get a particular book
	case BOOK_ID:
		return "vnd.android.cursor.item/vnd.learn2develop.books";
		default:
			throw new  IllegalArgumentException("Unsupported URL:"+uri);
	}
	
	
}


@Override

	public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case BOOKS:
                count = booksDB.update(
                        DATABASE_TABLE, 
                        values,
                        selection, 
                        selectionArgs);
                break;
            case BOOK_ID:                
                count = booksDB.update(
                        DATABASE_TABLE, 
                        values,
                        _ID + " = " + uri.getPathSegments().get(1) + 
                        (!TextUtils.isEmpty(selection) ? " AND (" + 
                            selection + ')' : ""), 
                        selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);    
        }       
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



	
	
}



