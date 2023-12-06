package com.example.myhike.db.entity;

public class ObservationData {
    public  static  final  String TABLE_NAME = "observations";
    public  static  final  String COLUMN_ID = "observation_ID";
    public  static  final  String COLUMN_TYPE = "observation_type";
    public  static  final  String COLUMN_DATE = "observation_date";
    public  static  final  String COLUMN_COMMENT= "observation_comment";
    public  static  final  String COLUMN_HIKE_ID= "hike_id";

    private int id;
    private String type;
    private String date;
    private String comment;
    private int hike_id;

    public ObservationData(int id, String type, String date, String comment, int hike_id) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.comment = comment;
        this.hike_id = hike_id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getHike_id() {
        return hike_id;
    }

    public void setHike_id(int hike_id) {
        this.hike_id = hike_id;
    }

    public  static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_TYPE + " TEXT, "
                    + COLUMN_DATE + " TEXT, "
                    + COLUMN_COMMENT + " TEXT, "
                    + COLUMN_HIKE_ID + " INTEGER" + " )";

}
