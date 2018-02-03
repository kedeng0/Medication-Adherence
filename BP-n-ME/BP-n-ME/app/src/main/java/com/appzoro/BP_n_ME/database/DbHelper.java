package com.appzoro.BP_n_ME.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appzoro.BP_n_ME.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Appzoro_ 5 on 9/16/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "amc_n_me";
    // tasks table name
  //  private static final String TABLE_QUEST = "quest";
    private static final String TABLE_SYMPTOM_SERVEY = "sympton";
    // tasks Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_QUES = "question";
    private static final String KEY_ANSWER = "answer"; //correct option
    private static final String KEY_OPTA= "opta"; //option a
    private static final String KEY_OPTB= "optb"; //option b
   /* private static final String KEY_OPTC= "optc"; //option c
    private static final String KEY_OPTD= "optd"; //option d*/
    private SQLiteDatabase dbase;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        dbase=db;
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_SYMPTOM_SERVEY + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_QUES
                + " TEXT, " + KEY_ANSWER+ " TEXT, "+KEY_OPTA +" TEXT, "
                +KEY_OPTB +" TEXT)";
        db.execSQL(sql);
        addSymptomQuestions();
        //db.close();
    }
    private void addSymptomQuestions()
    {
        Question q1=new Question("Coughing?","Yes", "No", "Yes");
        this.addQuestion(q1);
        Question q2=new Question("Wheezing?","Yes", "No", "Yes");
        this.addQuestion(q2);
        Question q3=new Question("Chest tightness?","Yes", "No", "Yes" );
        this.addQuestion(q3);
        Question q4=new Question("Increased swelling?","Yes", "No", "Yes");
        this.addQuestion(q4);
        Question q5=new Question("Weight gain?","Yes", "No", "Yes");
        this.addQuestion(q5);
        Question q6=new Question("Shortness of breath?","Yes", "No", "Yes");
        this.addQuestion(q6);
        Question q7=new Question("A decrease in your ability to maintain your normal activity level?","Yes", "No", "Yes");
        this.addQuestion(q7);
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYMPTOM_SERVEY);
        // Create tables again
        onCreate(db);
    }
    // Adding new question
    public void addQuestion(Question quest) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_QUES, quest.getQUESTION());
        values.put(KEY_ANSWER, quest.getANSWER());
        values.put(KEY_OPTA, quest.getOPTA());
        values.put(KEY_OPTB, quest.getOPTB());
    /*    values.put(KEY_OPTC, quest.getOPTC());
        values.put(KEY_OPTD, quest.getOPTD());*/
        // Inserting Row
        dbase.insert(TABLE_SYMPTOM_SERVEY, null, values);
    }
    public List<Question> getAllQuestions() {
        List<Question> quesList = new ArrayList<Question>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SYMPTOM_SERVEY;
        dbase=this.getReadableDatabase();
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question quest = new Question();
                quest.setID(cursor.getInt(0));
                quest.setQUESTION(cursor.getString(1));
                quest.setANSWER(cursor.getString(2));
                quest.setOPTA(cursor.getString(3));
                quest.setOPTB(cursor.getString(4));
             /*   quest.setOPTC(cursor.getString(5));
                quest.setOPTD(cursor.getString(6));*/
                quesList.add(quest);
            } while (cursor.moveToNext());
        }
        // return quest list
        return quesList;
    }
    public int rowcount()
    {
        int row=0;
        String selectQuery = "SELECT  * FROM " + TABLE_SYMPTOM_SERVEY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        row=cursor.getCount();
        return row;
    }
}
