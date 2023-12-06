package com.example.myhike.db.entity;

public class HikeData {

    public  static  final  String TABLE_NAME = "hikes";
    public  static  final  String COLUMN_ID = "hike_ID";
    public  static  final  String COLUMN_NAME = "hike_name";
    public  static  final  String COLUMN_LENGTH = "hike_length";
    public  static  final  String COLUMN_PARKING_AVAILABLE = "parking_available";
    public  static  final  String COLUMN_LEVEL_DIFFICULTY = "level_difficulty";
    public  static  final  String COLUMN_LOCATION= "hike_location";
    public  static  final  String COLUMN_DATE = "hike_date";
    public  static  final  String COLUMN_DESCRIPTION = "hike_description";

    private  int id;
    private String nameOfHike;
    private int lengthOfHike;
    private String levelDifficulty;
    private boolean parkingAvailable;
    private String location;
    private String date;
    private String description;

    public HikeData(int id, String nameOfHike, int lengthOfHike, String levelDifficulty,
                    int parkingAvailable, String location, String date, String description) {
        this.id = id;
        this.nameOfHike = nameOfHike;
        this.lengthOfHike = lengthOfHike;
        this.levelDifficulty = levelDifficulty;
        this.parkingAvailable = parkingAvailable == 1 ? true : false;
        this.location = location;
        this.date = date;
        this.description = description;
    }

    public  int getId() {
        return  id;
    }
    public String getNameOfHike() {
        return nameOfHike;
    }

    public int getLengthOfHike() {
        return lengthOfHike;
    }

    public String getLevelDifficulty() {
        return levelDifficulty;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }

    public  static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_NAME + " TEXT, "
                        + COLUMN_LENGTH + " INTEGER, "
                        + COLUMN_LEVEL_DIFFICULTY + " TEXT, "
                        + COLUMN_PARKING_AVAILABLE + " INTEGER, "
                        + COLUMN_LOCATION + " TEXT, "
                        + COLUMN_DATE + " TEXT, "
                        + COLUMN_DESCRIPTION + " TEXT" + " )";
}

