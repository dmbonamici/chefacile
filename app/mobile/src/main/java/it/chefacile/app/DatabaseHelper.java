package it.chefacile.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chefacile.db";
    public static final String TABLE_NAME1 = "ingredients_table";
    public static final String COL_1= "INGREDIENT";
    public static final String COL_2= "COUNT";
    public static final String COL_3= "ID";
    public static final String TABLE_NAME2 = "recipes_table";
    public static final String COL_A= "RECIPE_PREF";
    public static final String COL_B= "IMAGE";
    public static final String TABLE_NAME3 = "ingredients_pref_table";
    public static final String COL_X= "INGREDIENT_PREF";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s1 = "Create table "
                + TABLE_NAME1 + " ( "
                + COL_1 + " TEXT PRIMARY KEY, "
                + COL_2 + " INTEGER, "
                + COL_3 + " INTEGER);";

        String s2 = "Create table "
                + TABLE_NAME2 + " ( "
                + COL_A + " TEXT PRIMARY KEY, "
                + COL_B + " STRING);";

        String s3 = "Create table "
                + TABLE_NAME3 + " ( "
                + COL_X + " TEXT PRIMARY KEY);";

        db.execSQL(s1);
        db.execSQL(s2);
        db.execSQL(s3);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME1);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME3);
        onCreate(db);


    }

    public boolean insertDataIngredient(String ingr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor c = db.rawQuery("select " +COL_3+ " from " +TABLE_NAME1, null);
        int n;

        c.moveToFirst();

        if(c.getCount()> 0) {
            c.moveToLast();
            n = c.getInt(c.getColumnIndex(COL_3)) + 1;

        } else n= 1;

        contentValues.put(COL_1, ingr);
        contentValues.put(COL_2, 1);
        contentValues.put(COL_3, n);


        long result = db.insert(TABLE_NAME1, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }


    public boolean insertDataIngredientPREF(String ingr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_X, ingr);

        long result = db.insert(TABLE_NAME3, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }


    public boolean insertDataRecipe(String rec, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_A, rec);
        contentValues.put(COL_B, image);

        long result = db.insert(TABLE_NAME2, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }


    public Cursor getAllDataIngredients(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +TABLE_NAME1, null);
        return res;
    }


    public Cursor getAllDataRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +TABLE_NAME2, null);
        return res;
    }


    public Cursor getAllDataIngredientsPREF(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +TABLE_NAME3, null);
        return res;
    }


    public boolean findIngredient(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor ing = db.rawQuery("select * from "+TABLE_NAME1+ " where "+COL_1+ " = '"+test+"'", null);
        if (ing.getCount() > 0)
            return true;
        else
            return false;
    }


    public boolean findIngredientPREF(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor ing = db.rawQuery("select * from "+TABLE_NAME3+ " where "+COL_X+ " = '"+test+"'", null);
        if (ing.getCount() > 0)
            return true;
        else
            return false;
    }



    public Integer deleteDataIngredient(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME1, "INGREDIENT = ?", new String[] {test});

    }


    public Integer deleteDataIngredientPREF(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME3, "INGREDIENT_PREF = ?", new String[] {test});

    }


    public Integer deleteDataRecipe(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME2, "INGREDIENT_PREF = ?", new String[] {test});

    }


    public void updateCount(String test) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " +TABLE_NAME1+ " SET " +COL_2+ " = " +COL_2+ " +1 WHERE "+COL_1+ " = '"+test+"'");
        db.close();
    }



    public Map<String,Integer> getDataInMapIngredient(){
        Map<String,Integer> map = new HashMap<String, Integer>();
        Cursor c = getReadableDatabase().rawQuery("Select * from " +TABLE_NAME1, null);

        c.moveToFirst();

        do{
            if(c!=null && c.getCount()>0) {
                String s1 = c.getString(c.getColumnIndex(COL_1));
                int i1 = c.getInt(c.getColumnIndex(COL_2));

                map.put(s1, i1);

            }
        }
        while (c.moveToNext());
        c.close();

        return map;
    }



    public List<String> getDataInListIngredientPREF(){
        List<String> list = new ArrayList<String>();
        Cursor c = getReadableDatabase().rawQuery("Select * from " +TABLE_NAME3, null);

        c.moveToFirst();

        do{
            if(c!=null && c.getCount()>0) {
                String s1 = c.getString(c.getColumnIndex(COL_X));

                list.add(s1);

            }
        }
        while (c.moveToNext());
        c.close();

        return list;
    }


   public boolean occursExceeded(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select " +COL_3+ " from " +TABLE_NAME1, null);
        int n=0;

        c.moveToFirst();

        if(c.getCount()>0) {
            c.moveToLast();
            n = c.getInt(c.getColumnIndex(COL_3));
        }

        if(n==5)
            return true;

        else return false;
   }


    public void deleteMinimum(String test){
        SQLiteDatabase db = this.getWritableDatabase();

        if(occursExceeded()){

            Cursor c = db.rawQuery("select * from " +TABLE_NAME1+ " where " +COL_2+ "= 1 ", null);

            c.moveToFirst();

            if(c.getCount()>0){

                String ing = c.getString(c.getColumnIndex(COL_1));

                deleteDataIngredient(test);
                db.execSQL("UPDATE " +TABLE_NAME1+ " SET " +COL_1+ " = '"+test+"'  WHERE "+COL_1+ " = '"+ing+"'");

            }
        }

    }


    public void decrementedId(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c1 = db.rawQuery("select * from " +TABLE_NAME1, null);
        Cursor c2 = db.rawQuery("select * from " +TABLE_NAME1+ " where " +COL_1+ " = '"+test+"'" , null);

        c1.moveToPosition(c2.getCount());

        do{
            if(c1!=null && c1.getCount()>0) {
                String ingr = c1.getString(c1.getColumnIndex(COL_1));

                db.execSQL("UPDATE " +TABLE_NAME1+ " SET " +COL_3+ " = " +COL_3+ " -1 WHERE " +COL_1+ " = '"+ingr+"'");

            }
        }
        while (c1.moveToNext());
        c1.close();
        c2.close();
    }

}
