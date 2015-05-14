package com.shortstack.hackertracker.Misc;

/**
 * Created by Whitney Champion on 4/18/14.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.OfficialDatabaseAdapter;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Contest;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.Event;
import com.shortstack.hackertracker.Model.Party;
import com.shortstack.hackertracker.Model.Speaker;
import com.shortstack.hackertracker.Model.Vendor;

import java.util.ArrayList;
import java.util.List;

public class HackerTrackerFragment extends Fragment {

    private FragmentManager fragmentManager;
    private Context context;
    private OfficialDatabaseAdapter myOfficialDbHelper;
    private DatabaseAdapter myDbHelper;

    public HackerTrackerFragment() {

        // get fragment manager
        fragmentManager = HomeActivity.fragmentManager;

        // get context
        context = HackerTrackerApplication.getAppContext();

        // database
        myOfficialDbHelper = HackerTrackerApplication.myOfficialDbHelper;
        myDbHelper = HackerTrackerApplication.myDbHelper;

    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage, int shape) {
        int targetWidth = shape;
        int targetHeight = shape;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public static String getTitle(Context ctxt, int position) {
        switch (position) {
            case -1:
                return (String.format("Wed, Aug 6"));
            case 0:
                return (String.format("Thurs, Aug 7"));
            case 1:
                return (String.format("Fri, Aug 8"));
            case 2:
                return (String.format("Sat, Aug 9"));
            case 3:
                return (String.format("Sun, Aug 10"));
            default:
                return "";
        }
    }

    /*
     * get list of {type} for {day}
     *
     * speaker = 1
     * contest = 2
     * event = 3
     * party = 4
     * vendors = 5
     *
     */
    public List<Default> getItemByDate(String day, int type) {
        ArrayList<Default> result = new ArrayList<Default>();

        SQLiteDatabase db;

        if (type==1) {
            db = HackerTrackerApplication.myOfficialDbHelper.getWritableDatabase();
        } else {
            db = HackerTrackerApplication.myDbHelper.getWritableDatabase();
        }

        Cursor myCursor = db.rawQuery("SELECT * FROM data WHERE date=? AND type=? ORDER BY startTime", new String[] {day, String.valueOf(type)});

        try{
            if (myCursor.moveToFirst()){
                do{

                    Default item;

                    switch (type) {
                        case 1:
                            item = new Speaker();
                            break;
                        case 2:
                            item = new Contest();
                            break;
                        case 3:
                            item = new Event();
                            break;
                        case 4:
                            item = new Party();
                            break;
                        case 5:
                            item = new Vendor();
                            break;
                        default:
                            item = new Speaker();
                            break;
                    }

                    item.setId(myCursor.getInt(myCursor.getColumnIndex("id")));
                    item.setType(myCursor.getInt(myCursor.getColumnIndex("type")));
                    item.setTitle(myCursor.getString(myCursor.getColumnIndex("title")));
                    item.setBody(myCursor.getString(myCursor.getColumnIndex("body")));
                    item.setName(myCursor.getString(myCursor.getColumnIndex("name")));
                    item.setDate(myCursor.getInt(myCursor.getColumnIndex("date")));
                    item.setEndTime(myCursor.getString(myCursor.getColumnIndex("endTime")));
                    item.setStartTime(myCursor.getString(myCursor.getColumnIndex("startTime")));
                    item.setLocation(myCursor.getString(myCursor.getColumnIndex("location")));
                    item.setStarred(myCursor.getInt(myCursor.getColumnIndex("starred")));
                    item.setImage(myCursor.getString(myCursor.getColumnIndex("image")));
                    item.setForum(myCursor.getString(myCursor.getColumnIndex("forum")));
                    item.setIsNew(myCursor.getInt(myCursor.getColumnIndex("is_new")));
                    item.setDemo(myCursor.getInt(myCursor.getColumnIndex("demo")));
                    item.setTool(myCursor.getInt(myCursor.getColumnIndex("tool")));
                    item.setExploit(myCursor.getInt(myCursor.getColumnIndex("exploit")));

                    result.add(item);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();

        return result;
    }

    public List<Default> getStars(String day) {
        ArrayList<Default> result = new ArrayList<Default>();

        SQLiteDatabase dbOfficial = myOfficialDbHelper.getWritableDatabase();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myOfficialCursor = dbOfficial.rawQuery("SELECT * FROM data WHERE date=? AND starred=1 ORDER BY startTime", new String[]{day});
        Cursor myCursor = db.rawQuery("SELECT * FROM data WHERE date=? AND starred=1 ORDER BY startTime", new String[] {day});

        // get speakers from official database

        try{
            if (myOfficialCursor.moveToFirst()){
                do{

                    Default item = new Default();

                    item.setId(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("id")));
                    item.setType(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("type")));
                    item.setTitle(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("title")));
                    item.setBody(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("body")));
                    item.setName(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("name")));
                    item.setDate(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("date")));
                    item.setEndTime(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("endTime")));
                    item.setStartTime(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("startTime")));
                    item.setLocation(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("location")));
                    item.setStarred(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("starred")));
                    item.setImage(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("image")));
                    item.setForum(myOfficialCursor.getString(myOfficialCursor.getColumnIndex("forum")));
                    item.setIsNew(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("is_new")));
                    item.setDemo(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("demo")));
                    item.setTool(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("tool")));
                    item.setExploit(myOfficialCursor.getInt(myOfficialCursor.getColumnIndex("exploit")));

                    result.add(item);

                }while(myOfficialCursor.moveToNext());
            }
        }finally{
            myOfficialCursor.close();
        }

        dbOfficial.close();

        // get all other events from unofficial database

        try{
            if (myCursor.moveToFirst()){
                do{

                    Default item = new Default();

                    item.setId(myCursor.getInt(myCursor.getColumnIndex("id")));
                    item.setType(myCursor.getInt(myCursor.getColumnIndex("type")));
                    item.setTitle(myCursor.getString(myCursor.getColumnIndex("title")));
                    item.setBody(myCursor.getString(myCursor.getColumnIndex("body")));
                    item.setName(myCursor.getString(myCursor.getColumnIndex("name")));
                    item.setDate(myCursor.getInt(myCursor.getColumnIndex("date")));
                    item.setEndTime(myCursor.getString(myCursor.getColumnIndex("endTime")));
                    item.setStartTime(myCursor.getString(myCursor.getColumnIndex("startTime")));
                    item.setLocation(myCursor.getString(myCursor.getColumnIndex("location")));
                    item.setStarred(myCursor.getInt(myCursor.getColumnIndex("starred")));
                    item.setImage(myCursor.getString(myCursor.getColumnIndex("image")));
                    item.setForum(myCursor.getString(myCursor.getColumnIndex("forum")));
                    item.setIsNew(myCursor.getInt(myCursor.getColumnIndex("is_new")));
                    item.setDemo(myCursor.getInt(myCursor.getColumnIndex("demo")));
                    item.setTool(myCursor.getInt(myCursor.getColumnIndex("tool")));
                    item.setExploit(myCursor.getInt(myCursor.getColumnIndex("exploit")));

                    result.add(item);

                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }

        db.close();

        // return all items

        return result;
    }
}
