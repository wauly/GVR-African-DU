package com.ethernet.app.mainscreen.asynctask;

import android.os.AsyncTask;
import android.os.Environment;


import com.ethernet.app.global.DatabaseHandler;
import com.ethernet.app.mainscreen.model.ContentDataModel;
import com.ethernet.app.utility.Constant;

import java.io.File;
import java.util.ArrayList;

public class DeleteImageAndVideoAsyncTask extends AsyncTask<DatabaseHandler, Void, Void>{

    private static final String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    private static final File folder = new File(extStorageDirectory, "waulypod-resource");

    private DatabaseHandler database;
    private ArrayList<ContentDataModel> listOfContent;

    @Override
    protected Void doInBackground(DatabaseHandler... db) {

        this.database = db[0];
        listOfContent = new ArrayList<>();

        listOfContent.addAll(database.getAllContent());

        if(listOfContent.size() >0){
            ArrayList<String> deleteDataArrayList = new ArrayList<>();
            for (int i = 0; i < listOfContent.size(); i++) {
                deleteDataArrayList.add(Constant.getFileName(listOfContent.get(i).url));
            }
            File file[] = folder.listFiles(); /// return all files and directries on that folder
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile())  /// check file is file type or other ( The method returns true if and only if the file denoted by this abstract pathname is a file else the method returns false.)
                {
                    String filename = file[i].getName();

                    int fileindex = deleteDataArrayList.indexOf(filename);
                    if (fileindex == -1) {
                        file[i].delete();
                    }
                }
            }
        }


        return null;
    }
}
