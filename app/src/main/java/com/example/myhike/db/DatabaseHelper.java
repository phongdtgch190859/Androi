package com.example.myhike.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myhike.db.entity.HikeData;
import com.example.myhike.db.entity.ObservationData;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static  final  int DATABASE_VERSION = 2;
    private  static  final  String DATABASE_NAME = "my_hike";
    private static DatabaseHelper instance = null;


    public DatabaseHelper(@Nullable Context context) {
        super(context, "DATABASE_NAME", null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(HikeData.CREATE_TABLE);
        database.execSQL(ObservationData.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HikeData.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ObservationData.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public ArrayList<HikeData> getALLHikes(String action) {
        String selectQuery = "SELECT * FROM " + HikeData.TABLE_NAME +
                " ORDER BY " + HikeData.COLUMN_ID + " " + action;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return getListHikes(cursor);
    }

    public void insertHike(HikeData hike){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HikeData.COLUMN_NAME, hike.getNameOfHike());
        values.put(HikeData.COLUMN_LENGTH, hike.getLengthOfHike());
        values.put(HikeData.COLUMN_LEVEL_DIFFICULTY, hike.getLevelDifficulty());
        values.put(HikeData.COLUMN_PARKING_AVAILABLE, hike.isParkingAvailable() ? 1 : 0);
        values.put(HikeData.COLUMN_LOCATION, hike.getLocation());
        values.put(HikeData.COLUMN_DATE, hike.getDate());
        values.put(HikeData.COLUMN_DESCRIPTION, hike.getDescription());
        db.insert(HikeData.TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<HikeData> getHikesByName(String keyword) {

        SQLiteDatabase  database = this.getReadableDatabase();
        Cursor cursor;
        cursor = database.rawQuery("SELECT * FROM " + HikeData.TABLE_NAME + " WHERE " +
                HikeData.COLUMN_NAME  + " LIKE '%" + keyword.replaceAll("[^a-zA-Z0-9]+", "") + "%'", null);

        return getListHikes(cursor);
    }

    public HikeData getHike(int hikeId) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + HikeData.TABLE_NAME + " WHERE " + HikeData.COLUMN_ID + " = " + hikeId, null);

        if (cursor != null) cursor.moveToFirst();
        HikeData hike = null;
        if(cursor.moveToFirst()){
            hike = new HikeData(
                    cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LENGTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LEVEL_DIFFICULTY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_PARKING_AVAILABLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_DESCRIPTION))
            );
            return hike;
        }

        cursor.close();
        database.close();
        return hike;
    }


    public void deleteHike(int hikeId){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(HikeData.TABLE_NAME , HikeData.COLUMN_ID + "=?", new String[]{String.valueOf(hikeId)});
        database.close();
    }

    public void updateHike(int id, HikeData hike){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HikeData.COLUMN_NAME, hike.getNameOfHike());
        values.put(HikeData.COLUMN_LENGTH, hike.getLengthOfHike());
        values.put(HikeData.COLUMN_LEVEL_DIFFICULTY, hike.getLevelDifficulty());
        values.put(HikeData.COLUMN_PARKING_AVAILABLE, hike.isParkingAvailable());
        values.put(HikeData.COLUMN_LOCATION, hike.getLocation());
        values.put(HikeData.COLUMN_DATE, hike.getDate());
        values.put(HikeData.COLUMN_DESCRIPTION, hike.getDescription());
        database.update(HikeData.TABLE_NAME, values,HikeData.COLUMN_ID+"=?", new String[]{String.valueOf(id)});
    }

    public ArrayList<HikeData> getListHikes(Cursor cursor) {
        ArrayList<HikeData> hikes = new ArrayList<>();
        if(cursor.moveToFirst()) {
            do {
                HikeData hike = new HikeData(
                        cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LENGTH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LEVEL_DIFFICULTY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(HikeData.COLUMN_PARKING_AVAILABLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_LOCATION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(HikeData.COLUMN_DESCRIPTION))
                );
                hikes.add(hike);

            } while (cursor.moveToNext());
        }
        return hikes;
    }

    //////

    public List<ObservationData> getAllObservationsByHikeId(int hikeId) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM " + ObservationData.TABLE_NAME +
                " WHERE " +  ObservationData.COLUMN_HIKE_ID + "= ?", new String[]{String.valueOf(hikeId)});
        return cursorToListObservations(cursor);
    }

//    public ArrayList<ObservationData> fetchAllObservations() {
//        database = this.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.OBSERVATIONS_TABLE , null);
//        return cursorToListObservations(cursor);
//    }

    public void insertObservation(ObservationData observation){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ObservationData.COLUMN_TYPE, observation.getType());
        contentValues.put(ObservationData.COLUMN_DATE, observation.getDate());
        contentValues.put(ObservationData.COLUMN_COMMENT, observation.getComment());
        contentValues.put(ObservationData.COLUMN_HIKE_ID, observation.getHike_id());
        database.insert(ObservationData.TABLE_NAME, null, contentValues);
        database.close();
    }

    // details of Observations
    public ObservationData getObservation(int observation_id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ObservationData.TABLE_NAME +
                " WHERE " + ObservationData.COLUMN_ID+ " = " + observation_id, null);
        if(cursor != null)  cursor.moveToFirst();

        ObservationData observation = new  ObservationData (
                cursor.getInt(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_COMMENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_HIKE_ID))
        );
        cursor.close();
        database.close();
        return observation;
    }

    public void deleteObservation(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(ObservationData.TABLE_NAME, ObservationData.COLUMN_ID + "=?",new String[]{String.valueOf(id)});
        database.close();
    }

    public void updateObservation(int observation_id, ObservationData obeservation){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ObservationData.COLUMN_TYPE, obeservation.getType());
        contentValues.put(ObservationData.COLUMN_DATE, obeservation.getDate());
        contentValues.put(ObservationData.COLUMN_COMMENT, obeservation.getComment());
        contentValues.put(ObservationData.COLUMN_HIKE_ID, obeservation.getHike_id());
        database.update(ObservationData.TABLE_NAME, contentValues,ObservationData.COLUMN_ID+"=?",new String[]{String.valueOf(observation_id)});
        database.close();
    }

    public ArrayList<ObservationData> cursorToListObservations(Cursor cursor) {
        ArrayList<ObservationData> observations = new ArrayList<>();
        cursor.moveToFirst();
        if(cursor.moveToFirst()) {
            do {
                ObservationData observation = new ObservationData(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_COMMENT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ObservationData.COLUMN_HIKE_ID))
                );
                observations.add(observation);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return observations;
    }

    public void refreshDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " +  HikeData.TABLE_NAME);
        db.execSQL("DELETE FROM " +   ObservationData.TABLE_NAME);
    }
}