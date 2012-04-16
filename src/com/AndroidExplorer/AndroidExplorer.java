package com.AndroidExplorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidExplorer extends ListActivity {
	
	public static final String ANDROID_EXPLORER = "ANDROID_EXPLORER";
	
	
	private static final int OPEN_ID = 0;
	private static final int DELETE_ID =1;
	private static final int ROTATE_AUTO_ID =2;
	private static final int ROTATE_LANDSCAPE_ID =3;
	private static final int ROTATE_POTRAIT_ID =4;
	
	private List<String> item = null;
	private List<String> path = null;
	private String root="/";
	private TextView myPath;
	private String CurrentDir;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       /*File f = new File("/mnt/sdcard/Download/test.avi");
        try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        SharedPreferences settings = getSharedPreferences(ANDROID_EXPLORER, 0);
        String lp = settings.getString("LATEST_PATH", "");
        if(lp == ""){
        	lp="/mnt/sdcard";

        } 
        int ori = settings.getInt("LATEST_ORIENT", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setRequestedOrientation(ori);
        
        
        myPath = (TextView)findViewById(R.id.path);
        getDir(lp);
        
        //ListView list = (ListView)findViewById(R.id.list);
        
        
        
        registerForContextMenu(getListView());   
        
    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	if(event.getAction() == KeyEvent.ACTION_DOWN)     
    	{   
    		
    		switch(keyCode)         
    		{ 
    			case KeyEvent.KEYCODE_MENU:
    				Log.i("JIM","MENUMENUMENUMENUMENUMENU");
    				
    			return true;         
    		}     
    	}      
    	return super.onKeyDown(keyCode, event); 
    } */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {    	
    	menu.add(0,ROTATE_AUTO_ID, 0, R.string.op_rotate_auto);   
    	menu.add(0, ROTATE_LANDSCAPE_ID, 0,  R.string.op_rotate_landscape);  
    	menu.add(0, ROTATE_POTRAIT_ID, 0,  R.string.op_rotate_portrait);  

    	return true;

    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	SharedPreferences settings = getSharedPreferences(ANDROID_EXPLORER, 0);
    	SharedPreferences.Editor editor = settings.edit();
        switch (item.getItemId()) {
        case ROTATE_AUTO_ID:
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        	editor.putInt("LATEST_ORIENT", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        	editor.commit();  
            return true;
        case ROTATE_LANDSCAPE_ID:
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        	editor.putInt("LATEST_ORIENT", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        	editor.commit();  
            return true;
        case ROTATE_POTRAIT_ID:
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        	editor.putInt("LATEST_ORIENT", ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        	editor.commit();  
        	return true;
        }
        

    	
    	
        return true;
    }
    
    
    
    private void getDir(String dirPath)
    {
    	SharedPreferences settings = getSharedPreferences(ANDROID_EXPLORER, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("LATEST_PATH", dirPath);
        // Commit the edits!
        editor.commit();   	
    	
    	CurrentDir = dirPath;
    	Log.i("JIM","Path="+CurrentDir);
    	myPath.setText("Location: " + dirPath);
    	
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{

    		item.add(root);
    		path.add(root);
    		
    		item.add("../");
    		path.add(f.getParent());
            
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
    			File file = files[i];
    			path.add(file.getPath());
    			if(file.isDirectory())
    				item.add(file.getName() + "/");
    			else
    				item.add(file.getName() + " " + file.length() + " bytes");
    	}

    	ArrayAdapter<String> fileList =
    		new ArrayAdapter<String>(this, R.layout.row, item);
    	setListAdapter(fileList);
    }
    private boolean rmFile(String fname){
    	File f = new File(fname);
    	if(!f.exists()){
    		
    		return true;
    	}
    	
    	if (f.isDirectory()){
    		
    		File[] files = f.listFiles(); 
    	   	for(int i=0; i < files.length; i++)
        	{
    	   		
        			files[i].delete();
        	}    		
  
    	}		
    	
    	boolean result = f.delete();
    	if(result){
    		Log.i("JIM","delete ok");
    	}else{
    		Log.i("JIM","delete fail");
    	}
    	
    	return result;
    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		File file = new File(path.get(position));
		
		if (file.isDirectory())
		{
			/*if(!file.canWrite()){
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon)
				.setTitle("[" + file.getName() + "] folder can't be write!")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								return;
							}
						}).show();			
			}*/
			
			if(file.canRead())
				getDir(path.get(position));
			else
			{
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								return;
							}
						}).show();
			}
		}
		else
		{

			
			
				if (file.getName().endsWith(".apk")){
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					startActivity(intent);           
				}else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".avi")){
					Intent intent = new Intent(); 
					intent.setAction(android.content.Intent.ACTION_VIEW); 
					intent.setDataAndType(Uri.fromFile(file), "video/*"); 
					startActivity(intent);
				}
				else if (file.getName().endsWith(".mp3")) {               
					Intent intent = new Intent();
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "audio/*");
					startActivity(intent);
				} else{
					new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle("[ Support .mp3 .mp4 .avi .apk only]")
					.setPositiveButton("OK", 
							new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).show();
				}
				
			
				
		}
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    	 super.onCreateContextMenu(menu, v, menuInfo);
    	 
    	 AdapterView.AdapterContextMenuInfo info;

         try {

              info = (AdapterView.AdapterContextMenuInfo) menuInfo;     

         } catch (ClassCastException e) {

             Log.e("JIM", "bad menuInfo", e);

             return;

         }
   	 
    	 File file = new File(path.get(info.position));
    	 
    	 menu.setHeaderTitle(path.get(info.position));
    	 menu.add(0,OPEN_ID, 0, R.string.op_open);   
    	 menu.add(0, DELETE_ID, 0,  R.string.op_delete);  
    	 
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	 final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();   
   	 
    	  switch (item.getItemId()) {   
    	    case OPEN_ID:   
    	    	File file = new File(path.get(info.position));
    	    	if (file.isDirectory())
    			{
    				if(file.canRead()){
    					getDir(path.get(info.position));
    				}
    			}else{
    				if (file.getName().endsWith(".apk")){
    					Intent intent = new Intent();
    					intent.setAction(android.content.Intent.ACTION_VIEW);
    					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
    					startActivity(intent);           
    				}else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".avi")){
    					Intent intent = new Intent(); 
    					intent.setAction(android.content.Intent.ACTION_VIEW); 
    					intent.setDataAndType(Uri.fromFile(file), "video/*"); 
    					startActivity(intent);
    				}
    				else if (file.getName().endsWith(".mp3")) {               
    					Intent intent = new Intent();
    					intent.setAction(android.content.Intent.ACTION_VIEW);
    					intent.setDataAndType(Uri.fromFile(file), "audio/*");
    					startActivity(intent);
    				} else{
    					new AlertDialog.Builder(this)
    					.setIcon(R.drawable.icon)
    					.setTitle("[ Support .mp3 .mp4 .avi .apk only]")
    					.setPositiveButton("OK", 
    							new DialogInterface.OnClickListener() {
    								
    								public void onClick(DialogInterface dialog, int which) {
    									// TODO Auto-generated method stub
    								}
    							}).show();
    				}    			
    			}
    	    return true;   
    	    case DELETE_ID:   
    	    {
    	    	new AlertDialog.Builder(this)
    	    	.setIcon(android.R.drawable.ic_dialog_alert)
    	    	.setTitle(R.string.msg_confirm)
    	    	.setMessage(R.string.msg_confirm_verbose)
    	    	.setPositiveButton(R.string.msg_confirm_yes, new DialogInterface.OnClickListener() {		
    	    		public void onClick(DialogInterface dialog, int which) {
    	    	    	rmFile(path.get(info.position));
    	    	    	getDir(CurrentDir);
    	    		}          
    	    	})
    	    	.setNegativeButton(R.string.msg_confirm_cancel, null).show(); 

    	    }
    	    return true;   
    	  default:   
    	    return super.onContextItemSelected(item);   
    	 }   

    }    
    
}
    
   
    
    