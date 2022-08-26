package com.nic.nammaoorusuper.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NammaOoruSuper";
    private static final int DATABASE_VERSION = 1;


    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String PMAY_LIST_TABLE_NAME = "PMAYList";
    public static final String SAVE_PMAY_DETAILS = "SavePMAYDetails";
    public static final String SAVE_PMAY_IMAGES = "SavePMAYImages";

    ///New Table
    public static final String MASTER_FIN_YEAR_TABLE = "fin_year";
    public static final String MASTER_WORK_TYPE_TABLE = "work_type";
    public static final String MASTER_SELF_HELP_GROUP_TABLE = "self_help_group";
    public static final String MASTER_SELF_HELP_GROUP_MEMBER_TABLE = "self_help_group_member";
    public static final String SAVE_TREE_IMAGE_TABLE = "save_tree_image_table";
    public static final String SAVE_BEFORE_TREE_IMAGE_TABLE = "save_before_tree_image_table";
    public static final String SAVE_AFTER_TREE_IMAGE_TABLE = "save_after_tree_image_table";



    ///////NOS Tables
    public static final String CAMPAIGN_LIST_TABLE = "campaign_list";
    public static final String ACTIVITY_LIST_TABLE = "activity_list";
    public static final String SUB_ACTIVITY_LIST_TABLE = "sub_activity_list";
    public static final String DYNAMIC_WIDGET_DETAILS_TABLE = "dynamic_widget_details";
    public static final String LOCATION_SAVE_DETAILS_TABLE = "location_save_details";
    public static final String GET_CATEGORY_TABLE = "get_category";
    public static final String GET_PHOTO_CATEGORY = "get_photo_category";
    public static final String GET_CATEGORY_DETAILS_TABLE = "get_category_details";
    public static final String SAVE_IMAGE_DETAILS_TABLE = "save_image_details";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname TEXT)");

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name TEXT)");

        db.execSQL("CREATE TABLE " + PMAY_LIST_TABLE_NAME + " ("
                + "pvcode  TEXT," +
                "habcode  TEXT," +
                "beneficiary_name  TEXT," +
                "secc_id  TEXT," +
                "habitation_name TEXT," +
                "person_alive TEXT," +
                "legal_heir_available TEXT," +
                "person_migrated TEXT," +
                "pvname TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_PMAY_DETAILS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habcode TEXT," +
                "pvname TEXT," +
                "habitation_name TEXT," +
                "secc_id TEXT," +
                "beneficiary_name TEXT," +
                "person_alive TEXT," +
                "legal_heir_available TEXT," +
                "person_migrated TEXT," +
                "button_text TEXT," +
                "beneficiary_father_name TEXT)");


        db.execSQL("CREATE TABLE " + SAVE_PMAY_IMAGES + " ("
                + "pmay_id INTEGER,"+
                "image BLOB," +
                "latitude TEXT," +
                "longitude TEXT," +
                "type_of_photo TEXT)");

        ///new
        db.execSQL("CREATE TABLE " + MASTER_FIN_YEAR_TABLE + " ("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "fin_year TEXT)");
        db.execSQL("CREATE TABLE " + MASTER_WORK_TYPE_TABLE + " ("
                +"work_code INTEGER,"+
                "work_name TEXT)");
        db.execSQL("CREATE TABLE " + MASTER_SELF_HELP_GROUP_TABLE + " ("
                +"shg_code INTEGER,"+
                "shg_name TEXT)");
        db.execSQL("CREATE TABLE " + MASTER_SELF_HELP_GROUP_MEMBER_TABLE + " ("
                +"shg_code INTEGER,"+
                "shg_member_code INTEGER,"+
                "member_name TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_TREE_IMAGE_TABLE + " ("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "shg_code INTEGER,"+
                "shg_member_code INTEGER,"+
                "work_code INTEGER,"+
                "shg_name TEXT," +
                "member_name TEXT," +
                "work_name TEXT," +
                "before_photo BLOB," +
                "before_photo_lat TEXT," +
                "before_photo_long TEXT," +
                "after_photo BLOB," +
                "after_photo_lat TEXT," +
                "after_photo_long TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_BEFORE_TREE_IMAGE_TABLE + " ("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "shg_code INTEGER,"+
                "shg_member_code INTEGER,"+
                "work_code INTEGER,"+
                "shg_name TEXT," +
                "fin_year TEXT," +
                "member_name TEXT," +
                "work_name TEXT," +
                "before_photo BLOB," +
                "before_photo_lat TEXT," +
                "before_photo_long TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_AFTER_TREE_IMAGE_TABLE + " ("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "shg_code INTEGER,"+
                "shg_member_code INTEGER,"+
                "work_code INTEGER,"+
                "shg_name TEXT," +
                "fin_year TEXT," +
                "member_name TEXT," +
                "work_name TEXT," +
                "after_photo BLOB," +
                "after_photo_lat TEXT," +
                "after_photo_long TEXT)");


        /*/////////////////******** NOS Tables
         ***********/
        db.execSQL("CREATE TABLE " + CAMPAIGN_LIST_TABLE + " ("
                +"campaign_id TEXT,"+
                "campaign_name TEXT," +
                "campaign_from_date TEXT," +
                "campaign_to_date TEXT)");

        db.execSQL("CREATE TABLE " + GET_PHOTO_CATEGORY + " ("
                +"image_category_id TEXT,"+
                "image_category_name TEXT)");

        db.execSQL("CREATE TABLE " + ACTIVITY_LIST_TABLE + " ("
                +"campaign_id TEXT,"+
                "campaign_activity_details_id TEXT," +
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "no_of_items TEXT," +
                "activity_name TEXT," +
                "no_of_images TEXT," +
                "activity_from_date TEXT," +
                "activity_to_date TEXT)");

        db.execSQL("CREATE TABLE " + SUB_ACTIVITY_LIST_TABLE + " ("
                +"campaign_id TEXT,"+
                "campaign_activity_details_id TEXT," +
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "activity_sub_list TEXT," +
                "item_no TEXT," +
                "is_taken TEXT)");

        db.execSQL("CREATE TABLE " + DYNAMIC_WIDGET_DETAILS_TABLE + " ("
                +"campaign_id TEXT,"+
                "activity_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "campaign_activity_id TEXT," +
                "campaign_activity_data TEXT," +
                "campaign_data_label TEXT," +
                "campaign_data_type TEXT)");

        db.execSQL("CREATE TABLE " + LOCATION_SAVE_DETAILS_TABLE + " ("
                +"location_save_details_primary_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "campaign_id TEXT,"+
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "hab_name TEXT," +
                "activity_name TEXT," +
                "item_no TEXT," +
                "json_value TEXT)");


        db.execSQL("CREATE TABLE " + GET_CATEGORY_TABLE + " ("
                +"campaign_id TEXT,"+
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "campaign_activity_entry_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "item_no TEXT)");


        db.execSQL("CREATE TABLE " + GET_CATEGORY_DETAILS_TABLE + " ("
                +"campaign_id TEXT,"+
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "campaign_activity_entry_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "item_no TEXT," +
                "image_category_id TEXT," +
                "image_category_name TEXT," +
                "is_taken TEXT)");

        db.execSQL("CREATE TABLE " + SAVE_IMAGE_DETAILS_TABLE + " ("
                +"location_save_details_primary_id TEXT,"+
                "campaign_id TEXT,"+
                "activity_id TEXT," +
                "campaign_activity_id TEXT," +
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "activity_name TEXT," +
                "hab_name TEXT," +
                "item_no TEXT," +
                "image_category_id TEXT," +
                "image_path TEXT," +
                "image_serial_no TEXT," +
                "image_lat TEXT," +
                "image_long TEXT," +
                "image_description TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + PMAY_LIST_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_PMAY_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_PMAY_IMAGES);

            //New Tables
            db.execSQL("DROP TABLE IF EXISTS " + MASTER_FIN_YEAR_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MASTER_WORK_TYPE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MASTER_SELF_HELP_GROUP_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MASTER_SELF_HELP_GROUP_MEMBER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_TREE_IMAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_BEFORE_TREE_IMAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_AFTER_TREE_IMAGE_TABLE);


            /////////////////NOS TABLES
            db.execSQL("DROP TABLE IF EXISTS " + CAMPAIGN_LIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITY_LIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SUB_ACTIVITY_LIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DYNAMIC_WIDGET_DETAILS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + LOCATION_SAVE_DETAILS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GET_CATEGORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + GET_PHOTO_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + GET_CATEGORY_DETAILS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_IMAGE_DETAILS_TABLE);
            onCreate(db);
        }
    }


}
