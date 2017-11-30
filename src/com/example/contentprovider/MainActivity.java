package com.example.contentprovider;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import android.widget.EditText;


import android.database.Cursor;
import android.net.Uri;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ContentValues editedValues = new ContentValues();
        editedValues.put(BookProvider.TITLE, "Android Tips and Tricks");        
        getContentResolver().update(
            Uri.parse(
                "content://com.example.contentprovider.provider.Books/books/2"), 
            editedValues, 
            null, 
            null);
        
        getContentResolver().delete(
                Uri.parse("content://com.example.contentprovider.Books/books/2"), 
                null, null);

        getContentResolver().delete(
                Uri.parse("content://com.example.contentprovider.Books/books"), 
                null, null);
        
        Button btnAdd = (Button) findViewById(R.id.button1);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	/*
                //---add a book---
                ContentValues values = new ContentValues();
                values.put(BooksProvider.TITLE, ((EditText) findViewById(R.id.txtTitle)).getText().toString());
                values.put(BooksProvider.ISBN, ((EditText) findViewById(R.id.txtISBN)).getText().toString());        
                Uri uri = getContentResolver().insert(BooksProvider.CONTENT_URI, values);                
    	    	Toast.makeText(getBaseContext(),uri.toString(), Toast.LENGTH_LONG).show();
    	    	*/
    	    	
    	        ContentValues values = new ContentValues();
    	        values.put("title", ((EditText) findViewById(R.id.editText1)).getText().toString());
    	        values.put("isbn", ((EditText) findViewById(R.id.editText2)).getText().toString());        
    	        Uri uri = getContentResolver().insert(
    	                Uri.parse("content://com.example.contentprovider.provider.Books/books"), 
    	                values);
    	        Toast.makeText(getBaseContext(),uri.toString(), Toast.LENGTH_LONG).show();
    	    }
        });
        
        Button btnRetrieve = (Button) findViewById(R.id.button2);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {            	
                //---retrieve the titles---
                Uri allTitles = Uri.parse(
                    "content://com.example.contentprovider.provider.Books/books");
                Cursor c = managedQuery(allTitles, null, null, null, "title desc");
                if (c.moveToFirst()) {
                    do{                    	
                    	Log.v("ContentProviders",
                            c.getString(c.getColumnIndex(
                                BookProvider._ID)) + ", " +                     
                            c.getString(c.getColumnIndex(
                                BookProvider.TITLE)) + ", " +                     
                            c.getString(c.getColumnIndex(
                                BookProvider.ISBN)));               
                    } while (c.moveToNext());
                }
            }
        });
        
    }
}