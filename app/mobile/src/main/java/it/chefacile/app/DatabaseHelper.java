package it.chefacile.app;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "chefacile.db";
    public static final String TABLE_NAME1 = "ingredients_table";
    public static final String COL_1= "INGREDIENT_PREF";
    public static final String COL_2= "COUNT";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String s = "Create table "
                + TABLE_NAME1 + " ( "
                + COL_1 + " TEXT PRIMARY KEY, "
                + COL_2 + " INTEGER);";
        db.execSQL(s);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME1);
        onCreate(db);
    }

    public boolean insertData(String ingr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, ingr);
        contentValues.put(COL_2, 1);

        long result = db.insert(TABLE_NAME1, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " +TABLE_NAME1, null);
        return res;
    }


    public boolean findIngredient(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor ing = db.rawQuery("select * from "+TABLE_NAME1+ " where "+COL_1+ " = '"+test+"'",null);
        if (ing.getCount() > 0)
            return true;
        else
            return false;
    }


    public Integer deleteData(String test){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME1, "INGREDIENT_PREF = ?", new String[] {test});

    }


  /*  public long insertTermList(String ing, int c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues initialValues = new ContentValues();

        initialValues.put(COL_1, ing);
        initialValues.put(COL_2, c);

        return db.insert(TABLE_NAME, null, initialValues);
    }


    public Cursor getTermValues(String test) {
        SQLiteDatabase db = this.getWritableDatabase();

        String from[] = { COL_1, COL_2 };
        String where = COL_1 + "=?";
        String[] whereArgs = new String[]{test+""};
        Cursor cursor = db.query(TABLE_NAME, from, where, whereArgs, null, null, null, null);
        return cursor;
    }

    private void getData(String test) {

        Cursor c = getTermValues(test);

        if(c != null)
        {
            while(c.moveToNext()){
                String ingredients_pref  = c.getString(c.getColumnIndex(COL_1));
                int count = c.getColumnIndex(COL_2);

            }
        }
    }


    public Map<String,Integer> putIngredientsInMap(){
        SQLiteDatabase db = this.getWritableDatabase();
        Map<String,Integer> map = new HashMap<String, Integer>();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME,null);

        String array[] = new String[cursor.getCount()];
        int i = 0;

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            array[i] = cursor.getString(0);
            i++;
            cursor.moveToNext();
        }
    }*/

}
