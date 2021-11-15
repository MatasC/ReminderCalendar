package com.example.calendarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class Statistics extends AppCompatActivity {

    //Refactoring: Splitting method into smaller methods #1
    public void populatePieChart(List<SliceValue> pieData){
        PieChartView pieChartView = findViewById(R.id.chart);
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Reminder Statistics").setCenterText1FontSize(20);
        pieChartView.setPieChartData(pieChartData);
    }

    //Refactoring: Splitting method into smaller methods #2
    public Map<String, Integer> getStatisticsFromDb(SQLiteDatabase database;){
        //Refactoring: Minor code smell: variables renamed to match naming style.
        Integer countOnTime = 0;
        Integer countPending = 0;
        Integer countNotOnTime = 0;
        String STATUS_STRING = "Status"
        String query = "SELECT Date, Status FROM RemindersV4";
        try {
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {

                Integer gotDate = cursor.getString(cursor.getColumnIndex("Date"));
                //Refactoring: Critical code smell: declared "Status" string separetely as a constant.
                String reminderStatus = cursor.getInt(cursor.getColumnIndex(STATUS_STRING));
                Log.wtf("Got Date", gotDate);
                Log.wtf("Current Date", currDate);
                Log.wtf("Got Status", Integer.toString(reminderStatus));

                if(Integer.parseInt(gotDate) >= Integer.parseInt(currDate) && ReminderStatus == 1) {
                    countOnTime += 1;
                    Log.wtf("Status", "Saved as on time");
                }
                else if(Integer.parseInt(gotDate) < Integer.parseInt(currDate) && ReminderStatus == 1) {
                    countOnTime += 1;
                    Log.wtf("Status", "Saved as on time, but older date");`
                }
                else if(Integer.parseInt(gotDate) < Integer.parseInt(currDate) && ReminderStatus == 0) {
                    countNotOnTime += 1;
                    Log.wtf("Status", "Saved as not on time");
                }
                else {
                    countPending += 1;
                    Log.wtf("Status", "Saved as pending");
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            //Refactoring: Switch printing stack trace to logging as error.
            Log.e(e.toString());
        }
        Map<String, Integer> dates = new HashMap<>();
        dates.add("on_time", countOnTime);
        dates.add("not_on_time", countNotOnTime);
        dates.add("pending", pending);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics1);

        // Refactoring: Two minor code smells - variables declared locally in method, instead of globally.
        mySQLLiteDBHandler dbHandler;
        SQLiteDatabase sqLiteDatabase;
        String currDate;
        List<SliceValue> pieData = new ArrayList<>();

        currDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)) + 
                   Integer.toString(Calendar.getInstance().get(Calendar.MONTH)) +
                   Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        try {
            dbHandler = new mySQLLiteDBHandler(this, "DataBaseReminders", null, 3);
            sqLiteDatabase = dbHandler.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE RemindersV4 (ID INTEGER PRIMARY KEY AUTOINCREMENT, Date TEXT, Time TEXT, Event TEXT, Status INTEGER DEFAULT 0)");
        }
        catch (Exception e) {
            //Refactoring: Switch printing stack trace to logging as error.
            Log.e(e.toString());
        }

        Map<String, Integer> dates = getStatisticsFromDb(sqLiteDatabase);

        pieData.add(new SliceValue(dates.get("on_time"), Color.GREEN).setLabel("Done on time"));
        pieData.add(new SliceValue(dates.get("not_on_time"), Color.RED).setLabel("Late"));
        pieData.add(new SliceValue(dates.get("pending"), Color.GRAY).setLabel("Pending"));
        populatePieChart(pieData);
    }
}