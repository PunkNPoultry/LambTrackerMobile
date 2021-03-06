package com.weyr_associates.lambtracker;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import java.util.Calendar;
import com.weyr_associates.lambtracker.LambingSheep.IncomingHandler;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.LightingColorFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CreateEweBreedingRecord extends ListActivity {
	private DatabaseHandler dbh;
	public Spinner which_breeding_spinner;
	public int 		thissheep_id, nRecs, this_service;
	public Cursor 	cursor;
	public Object 	crsr;
	String     	cmd, cmd2;
	private int year;
	public String currentyear,nextyear;
	public List<String> service_type;
	public List<Integer> which_service;
	ArrayAdapter<String> dataAdapter;
	public List<String> test_names;
    public List<Integer> test_sheep_id;
    public SparseBooleanArray sparse_array;
    ListView test_name_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_ewe_breeding_record);
		String 	dbfile = getString(R.string.real_database_file) ;
	    dbh = new DatabaseHandler( this, dbfile );
//		Added the variable definitions here    	
	    thissheep_id = 0;
	    which_breeding_spinner = (Spinner) findViewById(R.id.which_breeding_spinner);
	   	service_type = new ArrayList<String>();      	
	   	which_service = new ArrayList<Integer>();
	   	final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        currentyear = String.valueOf(year) + "%";
        Log.i ("current year ",currentyear );
	   	// Select All active breeding records to build the spinner
       cmd = String.format( "select breeding_record_table.id_breedingid as _id, flock_prefix_table.flock_name," +
       		"sheep_table.sheep_name, breeding_record_table.date_ram_in, breeding_record_table.time_ram_in, " +
       		"service_type_table.service_type " +
       		"from breeding_record_table " +
       		"inner join flock_prefix_table on flock_prefix_table.flock_prefixid = sheep_table.flock_prefix " +
    		"inner join sheep_table on breeding_record_table.ram_id = sheep_table.sheep_id " +
       		"inner join service_type_table on service_type_table.id_servicetypeid  = breeding_record_table.service_type " +
       		"where breeding_record_table.date_ram_out = '' " +
       		"order by sheep_table.sheep_name asc ");
       Log.i("set spinner ", "before cmd " + cmd); 
       crsr = dbh.exec( cmd );  
       cursor   = ( Cursor ) crsr;
       nRecs    = cursor.getCount();
       Log.i("countrams", " nRecs is " + String.valueOf(nRecs));
       dbh.moveToFirstRecord();
       service_type.add("Select a Breeding Record");
        // looping through all rows and adding to list
	   	for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
	   		service_type.add(cursor.getString(1)+ " " + cursor.getString(2) + " " + cursor.getString(5) + " " + cursor.getString(3)+ " " + cursor.getString(4));
			Log.i("EweBreeding", " the service type is " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3)); 
			which_service.add (cursor.getInt(0));
			Log.i("EweBreeding", " The breeding record id is " + String.valueOf(cursor.getInt(0)) );
	   	}	
	   	
//	   	year  = year+1;
//        nextyear = String.valueOf(year) + "%";
//        Log.i ("next year ",nextyear );
//	   	// Select All active breeding records to build the spinner
//       cmd = String.format( "select breeding_record_table.id_breedingid as _id, flock_prefix_table.flock_name," +
//       		"sheep_table.sheep_name, breeding_record_table.date_ram_in, breeding_record_table.time_ram_in, " +
//       		"service_type_table.service_type " +
//       		"from breeding_record_table " +
//       		"inner join flock_prefix_table on flock_prefix_table.flock_prefixid = sheep_table.flock_prefix " +
//    		"inner join sheep_table on breeding_record_table.ram_id = sheep_table.sheep_id " +
//       		"inner join service_type_table on service_type_table.id_servicetypeid  = breeding_record_table.service_type " +
//       		"where breeding_record_table.date_ram_out = '' " +
//       		"order by sheep_table.sheep_name asc ");
//       Log.i("set spinner ", "before cmd " + cmd); 
//       crsr = dbh.exec( cmd );  
//       cursor   = ( Cursor ) crsr;
//       dbh.moveToFirstRecord();
//        // looping through all rows and adding to list
//	   	for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
//	   		service_type.add(cursor.getString(1)+ " " + cursor.getString(2) + " " + cursor.getString(5) + " " + cursor.getString(3)+ " " + cursor.getString(4));
//			Log.i("EweBreeding", " the service type is " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3)); 
//			which_service.add (cursor.getInt(0));
//			Log.i("EweBreeding", " The breeding record id is " + String.valueOf(cursor.getInt(0)) );
//	   	}	   	
	   	// Creating adapter for spinner
	   	dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, service_type);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		which_breeding_spinner.setAdapter (dataAdapter);
		which_breeding_spinner.setSelection(0);

    	test_name_list = (ListView) findViewById(android.R.id.list);
    	//	Now go get all the current sheep names and format them
    	// 	Hard coded for not this year's lambs. Need to edit to handle not this current year lambs or remove the restriction
		cmd = String.format( "select sheep_table.sheep_id as _id, flock_prefix_table.flock_name, sheep_table.sheep_name, " +
				"sheep_table.alert01 from sheep_table inner join flock_prefix_table " +
				"on flock_prefix_table.flock_prefixid = sheep_table.flock_prefix " +
				"where (sheep_table.remove_date IS NULL or sheep_table.remove_date is '') and sheep_table.sex = 2 " +
				"and sheep_table.birth_date not like '%s' "+
				"order by sheep_table.sheep_name asc ", currentyear );  	        	
		Log.i("format record", " command is  " + cmd);
		crsr = dbh.exec( cmd );
		cursor   = ( Cursor ) crsr; 
		nRecs    = cursor.getCount();
		Log.i("countewes", " nRecs is " + String.valueOf(nRecs));
		test_names = new ArrayList<String>(); 
       	test_sheep_id = new ArrayList<Integer>();
		cursor.moveToFirst();	
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			test_names.add (cursor.getString(1) + " " + cursor.getString(2)+ " " + cursor.getString(3));
			test_sheep_id.add(cursor.getInt(0));
//			Log.i("EweBreeeding", " the current sheep is " + cursor.getString(1)+ " " + cursor.getString(2) + " " + cursor.getString(3));
    	}
		cursor.moveToFirst();				
		if (nRecs > 0) {
	    	ArrayAdapter<String> adapter = (new ArrayAdapter<String>(this, R.layout.list_entry_rams,test_names));
		    test_name_list.setAdapter(adapter);
	        test_name_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        test_name_list.setOnItemClickListener(new OnItemClickListener(){
	            public void onItemClick(AdapterView<?> parent, View view,int position,long id) {
	                View v = test_name_list.getChildAt(position);
	                Log.i("in click","I am inside onItemClick and position is:"+String.valueOf(position));
	            }
	        
	        });
	        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        sparse_array=getListView().getCheckedItemPositions();
		}  		
		else {
			// No sheep data - publish an empty list to clear sheep names
			Log.i("LookForSheep", "no current sheep");
		} 	
	}
	public void updateDatabase( View v ){
		boolean temp_value;
		int temp_location, temp_size;
		String ram_name, this_alert;
		
		which_breeding_spinner = (Spinner) findViewById(R.id.which_breeding_spinner);
		this_service = which_breeding_spinner.getSelectedItemPosition();
		this_service = which_service.get (this_service-1);
		
		// Add code to get ram name for alert update for sort
		cmd = String.format( "select sheep_table.sheep_id, sheep_table.sheep_name " +
				"from breeding_record_table " +
				"inner join sheep_table on breeding_record_table.ram_id = sheep_table.sheep_id " +
				"where breeding_record_table.id_breedingid = %s ", this_service );  
		Log.i("update", " command is  " + cmd);
		crsr = dbh.exec( cmd );
		cursor   = ( Cursor ) crsr; 
		cursor.moveToFirst();
		ram_name = cursor.getString(1);
		Log.i("update", " Ram is  " + ram_name);
		
		temp_size = sparse_array.size();
		Log.i("in Update ", "sparse array size is " + String.valueOf(temp_size));
        Log.i ("before loop", " the service record id is  " + String.valueOf(this_service));
    	for (int i=0; i<temp_size; i++){
    		temp_value = sparse_array.valueAt(i);
    		temp_location = sparse_array.keyAt(i);
    		if (temp_value){
    			thissheep_id = test_sheep_id.get(temp_location);
    			Log.i ("for loop", "the sheep " + " " + test_names.get(temp_location)+ " is checked");
    			Log.i ("for loop", "the sheep id is " + String.valueOf(test_sheep_id.get(temp_location)));
    			Log.i ("for loop", "the service id is " + String.valueOf(this_service));
     			cmd = String.format("insert into sheep_breeding_table (ewe_id, breeding_id) values (%s,%s)" , thissheep_id, this_service );
    			Log.i("add record ", "before cmd " + cmd);
    			dbh.exec( cmd);
    			Log.i("add record ", "after cmd " + cmd);	 
    			cmd = String.format("select sheep_table.alert01 from sheep_table where sheep_table.sheep_id = %s ", thissheep_id);
    			Log.i("add record ", "before cmd " + cmd);
    			crsr = dbh.exec( cmd );
    			cursor   = ( Cursor ) crsr; 
    			cursor.moveToFirst();
    			this_alert = cursor.getString(0);
    			Log.i("add record ", "after cmd " + cmd);
    			Log.i("add record ", "Before alert is " + this_alert);
    			this_alert = ram_name + ' ' + "\n" + this_alert;
    			Log.i("add record ", "After alert is " + this_alert);		
    			cmd = String.format("update sheep_table set alert01 = '%s' where sheep_id =%d ",
    					this_alert, thissheep_id ) ;
    			Log.i("update alert ", "before cmd " + cmd);
    			dbh.exec( cmd );
    			Log.i("update alert ", "after cmd " + cmd);
    			
    		}   		
    	}// for loop
    	Log.i("after for ", "loop in add record.");  
    	// Now need to go back 
		try { 
			cursor.close();
		}
		catch (Exception e) {
			Log.i("end of ", "add record. In catch stmt cursor");  
					}
       	dbh.closeDB();  	
       	this.finish();		   	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_ewe_breeding_record, menu);
		return true;
	}

}
