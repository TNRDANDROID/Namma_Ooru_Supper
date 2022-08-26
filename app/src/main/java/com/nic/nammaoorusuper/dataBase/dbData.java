package com.nic.nammaoorusuper.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nic.nammaoorusuper.constant.AppConstant;
import com.nic.nammaoorusuper.model.NOS;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public NOS insertVillage(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<NOS> getAll_Village(String dcode, String bcode) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }



    public NOS insertHabitation(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, pmgsySurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, pmgsySurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HABB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return pmgsySurvey;
    }
    public ArrayList<NOS> getAll_Habitation(String dcode, String bcode) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public NOS insertPMAY(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.PV_CODE, pmgsySurvey.getPvCode());
        values.put(AppConstant.HAB_CODE, pmgsySurvey.getHabCode());
        values.put(AppConstant.BENEFICIARY_NAME, pmgsySurvey.getBeneficiaryName());
        values.put(AppConstant.SECC_ID, pmgsySurvey.getSeccId());
        values.put(AppConstant.HABITATION_NAME, pmgsySurvey.getHabitationName());
        values.put(AppConstant.PV_NAME, pmgsySurvey.getPvName());
        values.put(AppConstant.PERSON_ALIVE, pmgsySurvey.getPersonAlive());
        values.put(AppConstant.LEGAL_HEIR_AVAILABLE, pmgsySurvey.getIsLegel());
        values.put(AppConstant.PERSON_MIGRATED, pmgsySurvey.getIsMigrated());

        long id = db.insert(DBHelper.PMAY_LIST_TABLE_NAME,null,values);
        Log.d("Inserted_id_PMAY_LIST", String.valueOf(id));

        return pmgsySurvey;
    }

    public ArrayList<NOS> getAll_PMAYList(String pvcode, String habcode) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        String condition = "";

        if (habcode != "") {
            condition = " where pvcode = '" + pvcode+"' and habcode = '" + habcode+"'" ;
        }else {
            condition = "";
        }

        try {
            cursor = db.rawQuery("select * from "+DBHelper.PMAY_LIST_TABLE_NAME + condition,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<NOS> getSavedPMAYDetails() {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;


        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_PMAY_DETAILS+" where id in (select pmay_id from "+DBHelper.SAVE_PMAY_IMAGES+")",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    NOS card = new NOS();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_PMAY_DETAILS+" where button_text = 'Save details'",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    NOS card = new NOS();

                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setSeccId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.SECC_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setPersonAlive(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ALIVE)));
                    card.setIsLegel(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.LEGAL_HEIR_AVAILABLE)));
                    card.setIsMigrated(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_MIGRATED)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));


                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public ArrayList<NOS> getSavedPMAYImages(String pmay_id, String type_of_photo) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        if(!type_of_photo.isEmpty()){
            selection = "pmay_id = ? and type_of_photo = ? ";
            selectionArgs = new String[]{pmay_id,type_of_photo};
        }
        else if(type_of_photo.isEmpty()) {
            selection = "pmay_id = ? ";
            selectionArgs = new String[]{pmay_id};
        }


        try {
            cursor = db.query(DBHelper.SAVE_PMAY_IMAGES,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    NOS card = new NOS();


                    card.setPmayId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PMAY_ID)));
                    card.setTypeOfPhoto(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));

                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    /////New ******************** Task****************
    public void insert_Master_Fin_Year(NOS pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("fin_year", pmgsySurvey.getFin_year());
        long id = db.insert(DBHelper.MASTER_FIN_YEAR_TABLE,null,values);
        Log.d("Inserted_id_fin_year", String.valueOf(id));
    }
    public void insert_Master_Work_Type(NOS pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("work_code", pmgsySurvey.getWork_code());
        values.put("work_name", pmgsySurvey.getWork_name());
        long id = db.insert(DBHelper.MASTER_WORK_TYPE_TABLE,null,values);
        Log.d("Inserted_id_work_type", String.valueOf(id));
    }
    public void insert_Master_Self_Help_Group(NOS pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("shg_code", pmgsySurvey.getShg_code());
        values.put("shg_name", pmgsySurvey.getShg_name());
        long id = db.insert(DBHelper.MASTER_SELF_HELP_GROUP_TABLE,null,values);
        Log.d("Inserted_id_s_g_t", String.valueOf(id));
    }
    public void insert_Master_Self_Help_Group_Member(NOS pmgsySurvey) {
        ContentValues values = new ContentValues();
        values.put("shg_code", pmgsySurvey.getShg_code());
        values.put("shg_member_code", pmgsySurvey.getShg_member_code());
        values.put("member_name", pmgsySurvey.getMember_name());
        long id = db.insert(DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null,values);
        Log.d("Inserted_id_s_g_m_t", String.valueOf(id));
    }

    public ArrayList<NOS> getAll_Master_Fin_Year() {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_FIN_YEAR_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setFin_year(cursor.getString(cursor
                            .getColumnIndexOrThrow("fin_year")));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getAll_Master_Work_Type() {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_WORK_TYPE_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setWork_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("work_code")));
                    card.setWork_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("work_name")));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getAll_Master_Self_Help_Group() {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_TABLE,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("shg_name")));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getAll_Master_Self_Help_Group_Member(int shg_code) {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code)};
            cursor = db.query(DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getParticular_Before_Save_Tree_Image_Table(int shg_code, int shg_member_code) {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? and shg_member_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code),String.valueOf(shg_member_code)};
            cursor = db.query(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setBefore_photo_lat(cursor.getString(cursor
                            .getColumnIndexOrThrow("before_photo_lat")));
                    card.setBefore_photo_long(cursor.getString(cursor
                            .getColumnIndexOrThrow("before_photo_long")));
                    byte[] before_photo = cursor.getBlob(cursor.getColumnIndexOrThrow("before_photo"));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(before_photo, 0, before_photo.length, options);
                    card.setBefore_photo((bitmap));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getParticular_After_Save_Tree_Image_Table(int shg_code, int shg_member_code) {
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            //cursor = db.rawQuery("select * from "+DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE,null);
            selection = "shg_code = ? and shg_member_code = ? ";
            selectionArgs = new String[]{String.valueOf(shg_code),String.valueOf(shg_member_code)};
            cursor = db.query(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setAfter_photo_lat(cursor.getString(cursor
                            .getColumnIndexOrThrow("after_photo_lat")));
                    card.setAfter_photo_long(cursor.getString(cursor
                            .getColumnIndexOrThrow("after_photo_long")));
                    byte[] after_photo = cursor.getBlob(cursor.getColumnIndexOrThrow("after_photo"));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(after_photo, 0, after_photo.length, options);
                    card.setAfter_photo((bitmap));
                    cards.add(card);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getAllTreeImages(){
        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {

            cursor = db.rawQuery(AppConstant.SQL_QUERY,null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();

                    card.setShg_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_code")));
                    card.setShg_member_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("shg_member_code")));
                    card.setWork_code(cursor.getInt(cursor
                            .getColumnIndexOrThrow("work_code")));
                    card.setShg_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("shg_name")));
                    card.setMember_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("member_name")));
                    card.setWork_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("work_name")));
                    card.setFin_year(cursor.getString(cursor
                            .getColumnIndexOrThrow("fin_year")));



                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    //////////////////////NOS Tables Insert and Get
    public void insertCampaignList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("campaign_id", pmgsySurvey.getCampaign_id());
        values.put("campaign_name", pmgsySurvey.getCampaign_name());
        values.put("campaign_from_date", pmgsySurvey.getCampaign_from_date());
        values.put("campaign_to_date", pmgsySurvey.getCampaign_to_date());

        long id = db.insert(DBHelper.CAMPAIGN_LIST_TABLE,null,values);
        Log.d("Inserted_id_campaign", String.valueOf(id));

    }
    public void insertPhotoCategoryList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("image_category_id", pmgsySurvey.getImage_category_id());
        values.put("image_category_name", pmgsySurvey.getImage_category_name());
        long id = db.insert(DBHelper.GET_PHOTO_CATEGORY,null,values);
        Log.d("Inserted_id_category", String.valueOf(id));

    }
    public ArrayList<NOS> getAll_Campaign() {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
            cursor = db.query(DBHelper.CAMPAIGN_LIST_TABLE,
                    null, null, null, null, null, "");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> getAll_Photo_Category() {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
            cursor = db.query(DBHelper.GET_PHOTO_CATEGORY,
                    null, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setImage_category_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("image_category_id")));
                    card.setImage_category_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("image_category_name")));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public void insertActivityList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("campaign_activity_details_id", pmgsySurvey.getCampaign_activity_details_id());
        values.put("campaign_id", pmgsySurvey.getCampaign_id());
        values.put("activity_id", pmgsySurvey.getActivity_id());
        values.put("dcode", pmgsySurvey.getDistictCode());
        values.put("bcode", pmgsySurvey.getBlockCode());
        values.put("pvcode", pmgsySurvey.getPvCode());
        values.put("hab_code", pmgsySurvey.getHabCode());
        values.put("no_of_items", pmgsySurvey.getNo_of_items());
        values.put("activity_name", pmgsySurvey.getActivity_name());
        values.put("no_of_images", pmgsySurvey.getNo_of_images());
        values.put("campaign_activity_id", pmgsySurvey.getCampaign_activity_id());
        values.put("activity_from_date", pmgsySurvey.getActivity_from_date());
        values.put("activity_to_date", pmgsySurvey.getActivity_to_date());

        long id = db.insert(DBHelper.ACTIVITY_LIST_TABLE,null,values);
        Log.d("Inserted_id_activity", String.valueOf(id));

    }
    public void insertSubActivityList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("campaign_activity_details_id", pmgsySurvey.getCampaign_activity_details_id());
        values.put("campaign_id", pmgsySurvey.getCampaign_id());
        values.put("activity_id", pmgsySurvey.getActivity_id());
        values.put("dcode", pmgsySurvey.getDistictCode());
        values.put("bcode", pmgsySurvey.getBlockCode());
        values.put("pvcode", pmgsySurvey.getPvCode());
        values.put("hab_code", pmgsySurvey.getHabCode());
        values.put("activity_sub_list", pmgsySurvey.getActivity_sub_list());
        values.put("item_no", pmgsySurvey.getItem_no());
        values.put("is_taken", pmgsySurvey.getIs_taken());
        values.put("campaign_activity_id", pmgsySurvey.getCampaign_activity_id());


        long id = db.insert(DBHelper.SUB_ACTIVITY_LIST_TABLE,null,values);
        Log.d("Inserted_id_sub", String.valueOf(id));

    }
    public void insertDynamicWidgetList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("dcode", pmgsySurvey.getDistictCode());
        values.put("bcode", pmgsySurvey.getBlockCode());
        values.put("pvcode", pmgsySurvey.getPvCode());
        values.put("hab_code", pmgsySurvey.getHabCode());
        values.put("campaign_activity_data", pmgsySurvey.getCampaign_activity_data());
        values.put("campaign_id", pmgsySurvey.getCampaign_id());
        values.put("activity_id", pmgsySurvey.getActivity_id());
        values.put("campaign_data_label", pmgsySurvey.getCampaign_data_label());
        values.put("campaign_data_type", pmgsySurvey.getCampaign_data_type());
        values.put("campaign_activity_id", pmgsySurvey.getCampaign_activity_id());


        long id = db.insert(DBHelper.DYNAMIC_WIDGET_DETAILS_TABLE,null,values);
        Log.d("Inserted_id_sub", String.valueOf(id));

    }
    public void insertCategoryDetailsList(NOS pmgsySurvey) {

        ContentValues values = new ContentValues();
        values.put("campaign_id", pmgsySurvey.getCampaign_id());
        values.put("activity_id", pmgsySurvey.getActivity_id());
        values.put("campaign_activity_id", pmgsySurvey.getCampaign_activity_id());
        values.put("campaign_activity_entry_id", pmgsySurvey.getCampaign_activity_entry_id());
        values.put("dcode", pmgsySurvey.getDistictCode());
        values.put("bcode", pmgsySurvey.getBlockCode());
        values.put("pvcode", pmgsySurvey.getPvCode());
        values.put("hab_code", pmgsySurvey.getHabCode());
        values.put("item_no", pmgsySurvey.getItem_no());
        values.put("image_category_id", pmgsySurvey.getImage_category_id());
        values.put("image_category_name", pmgsySurvey.getImage_category_name());
        values.put("is_taken", pmgsySurvey.getIs_taken());

        long id = db.insert(DBHelper.GET_CATEGORY_DETAILS_TABLE,null,values);
        Log.d("Inserted_id_activity", String.valueOf(id));

    }

    public ArrayList<NOS> get_Particular_Campaign_Activity_List(String campaign_id,String hab_code) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            selection = "campaign_id = ? and hab_code = ? ";
            selectionArgs = new String[]{campaign_id,hab_code};
            //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
            cursor = db.query(DBHelper.ACTIVITY_LIST_TABLE,
                    null, selection, selectionArgs, null, null, "activity_from_date");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_activity_details_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_details_id")));
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setNo_of_items(cursor.getString(cursor
                            .getColumnIndexOrThrow("no_of_items")));
                    card.setActivity_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_name")));
                    card.setNo_of_images(cursor.getString(cursor
                            .getColumnIndexOrThrow("no_of_images")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setActivity_from_date(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_from_date")));
                    card.setActivity_to_date(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_to_date")));


                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> get_Particular_Campaign_Activity_Sub_List(String campaign_id,String hab_code,String activity_id,String campaign_activity_id) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            selection = "campaign_id = ? and hab_code = ? and activity_id = ? and campaign_activity_id = ? ";
            selectionArgs = new String[]{campaign_id,hab_code,activity_id,campaign_activity_id};
            //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
            cursor = db.query(DBHelper.SUB_ACTIVITY_LIST_TABLE,
                    null, selection, selectionArgs, null, null, "item_no");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_activity_details_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_details_id")));
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setActivity_sub_list(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_sub_list")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setItem_no(cursor.getString(cursor
                            .getColumnIndexOrThrow("item_no")));
                    card.setIs_taken(cursor.getString(cursor
                            .getColumnIndexOrThrow("is_taken")));


                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> get_Particular_Dynamic_Widget_List(String campaign_id,String hab_code,String activity_id,String campaign_activity_id) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            selection = "campaign_id = ? and hab_code = ? and activity_id = ? and campaign_activity_id = ? ";
            selectionArgs = new String[]{campaign_id,hab_code,activity_id,campaign_activity_id};
            //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
            cursor = db.query(DBHelper.DYNAMIC_WIDGET_DETAILS_TABLE,
                    null, selection, selectionArgs, null, null, "campaign_activity_data");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_activity_data(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_data")));
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setCampaign_data_label(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_data_label")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setCampaign_data_type(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_data_type")));



                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> get_Particular_Location_Save_List(String campaign_id,String activity_id,String campaign_activity_id,String dcode,String bcode,String pvcode,String hab_code,String item_no,String type) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(type.equals("All")){
                cursor = db.rawQuery("select * from "+DBHelper.LOCATION_SAVE_DETAILS_TABLE,null,null);
            }
            else {
                selection = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                selectionArgs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};
                //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
                cursor = db.query(DBHelper.LOCATION_SAVE_DETAILS_TABLE,
                        null, selection, selectionArgs, null, null, null);
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setLocation_save_details_primary_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("location_save_details_primary_id")));
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setActivity_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_name")));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_name")));
                    card.setItem_no(cursor.getString(cursor
                            .getColumnIndexOrThrow("item_no")));
                    card.setJson_value(cursor.getString(cursor
                            .getColumnIndexOrThrow("json_value")));



                    cards.add(card);
                }
            }
        } catch (Exception e){
               Log.d("DEBUG_TAG", "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<NOS> get_Particular_Category_Get_List(String campaign_id,String activity_id,String campaign_activity_id,String dcode,String bcode,String pvcode,String hab_code,String item_no,String type) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
            if(type.equals("All")){
                cursor = db.rawQuery("select * from "+DBHelper.GET_CATEGORY_TABLE,null,null);
            }
            else {
                selection = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                selectionArgs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};
                //cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME,null,null);
                cursor = db.query(DBHelper.GET_CATEGORY_TABLE,
                        null, selection, selectionArgs, null, null, null);
            }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setCampaign_activity_entry_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_entry_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setItem_no(cursor.getString(cursor
                            .getColumnIndexOrThrow("item_no")));




                    cards.add(card);
                }
            }
        } catch (Exception e){
            Log.d("DEBUG_TAG", "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<NOS> get_Particular_Category_Get_DetailsList(String campaign_id,String activity_id,String campaign_activity_id,String dcode,String bcode,String pvcode,String hab_code,String item_no) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
                selection = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? ";
                selectionArgs = new String[]{campaign_id,activity_id,campaign_activity_id,dcode,bcode,pvcode,hab_code,item_no};

                cursor = db.query(DBHelper.GET_CATEGORY_DETAILS_TABLE,
                        null, selection, selectionArgs,null,null,null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setCampaign_activity_entry_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_entry_id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setItem_no(cursor.getString(cursor
                            .getColumnIndexOrThrow("item_no")));
                    card.setImage_category_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("image_category_id")));
                    card.setImage_category_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("image_category_name")));
                    card.setIs_taken(cursor.getString(cursor
                            .getColumnIndexOrThrow("is_taken")));




                    cards.add(card);
                }
            }
        } catch (Exception e){
            Log.d("DEBUG_TAG", "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<NOS> get_Particular_Save_Image_Details_List(String campaign_id,String activity_id,String campaign_activity_id,String location_save_details_primary_id,String item_no,String dcode,String bcode,String pvcode,String hab_code,String image_category_id,String type) {

        ArrayList<NOS> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection;
        String[] selectionArgs;
        try {
          if(type.equals("All")){
            String query = "select distinct campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,dcode,bcode,pvcode,hab_code,item_no,image_category_id,activity_name,hab_name from save_image_details";
            cursor = db.rawQuery(query,null);
          }
          else if(type.equals("")){
              selection = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ? and image_category_id = ?";
              selectionArgs = new String[]{campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,dcode,bcode,pvcode,hab_code,item_no,image_category_id};

              cursor = db.query(DBHelper.SAVE_IMAGE_DETAILS_TABLE,
                      null, selection, selectionArgs,null,null,null);

          }
          else {
               {
                  selection = "campaign_id = ? and activity_id = ? and campaign_activity_id = ? and location_save_details_primary_id = ? and dcode = ? and bcode = ? and pvcode = ? and hab_code = ? and item_no = ?";
                  selectionArgs = new String[]{campaign_id,activity_id,campaign_activity_id,location_save_details_primary_id,dcode,bcode,pvcode,hab_code,item_no};

                  cursor = db.query(DBHelper.SAVE_IMAGE_DETAILS_TABLE,
                          null, selection, selectionArgs,null,null,null);

              }
          }
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NOS card = new NOS();
                    card.setCampaign_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_id")));
                    card.setActivity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_id")));
                    card.setCampaign_activity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("campaign_activity_id")));
                    card.setLocation_save_details_primary_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("location_save_details_primary_id")));
                    card.setItem_no(cursor.getString(cursor
                            .getColumnIndexOrThrow("item_no")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_code")));
                    card.setActivity_name(cursor.getString(cursor
                            .getColumnIndexOrThrow("activity_name")));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow("hab_name")));
                    card.setImage_category_id(cursor.getString(cursor
                            .getColumnIndexOrThrow("image_category_id")));
                   if(!type.equals("All")){
                       card.setImage_path(cursor.getString(cursor
                               .getColumnIndexOrThrow("image_path")));
                       card.setImage_serial_no(cursor.getString(cursor
                               .getColumnIndexOrThrow("image_serial_no")));
                       card.setImage_lat(cursor.getString(cursor
                               .getColumnIndexOrThrow("image_lat")));
                       card.setImage_long(cursor.getString(cursor
                               .getColumnIndexOrThrow("image_long")));
                       card.setImage_description(cursor.getString(cursor
                               .getColumnIndexOrThrow("image_description")));
                   }

                    cards.add(card);
                }
            }
        } catch (Exception e){
            Log.d("DEBUG_TAG", "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }




    public Bitmap bytearrtoBitmap(byte[] photo){
        byte[] imgbytes = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgbytes, 0,
                imgbytes.length);
        return bitmap;
    }

    //////////////////////*****************/////////////


    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }

    public void deletePMAYTable() {
        db.execSQL("delete from " + DBHelper.PMAY_LIST_TABLE_NAME);
    }

    public void deletePMAYDetails() { db.execSQL("delete from " + DBHelper.SAVE_PMAY_DETAILS); }

    public void deletePMAYImages() { db.execSQL("delete from " + DBHelper.SAVE_PMAY_IMAGES);}

    public void deletefin_year() { db.execSQL("delete from " + DBHelper.MASTER_FIN_YEAR_TABLE);}
    public void deletework_type() { db.execSQL("delete from " + DBHelper.MASTER_WORK_TYPE_TABLE);}
    public void deleteself_help_group() { db.execSQL("delete from " + DBHelper.MASTER_SELF_HELP_GROUP_TABLE);}
    public void deleteself_help_group_member() { db.execSQL("delete from " + DBHelper.MASTER_SELF_HELP_GROUP_MEMBER_TABLE);}
    public void deleteSAVE_BEFORE_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE);}
    public void deleteSAVE_AFTER_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE);}
    public void deleteSAVE_TREE_IMAGE_TABLE() { db.execSQL("delete from " + DBHelper.SAVE_TREE_IMAGE_TABLE);}


    public void delete_campaign_list_table() { db.execSQL("delete from " + DBHelper.CAMPAIGN_LIST_TABLE);}
    public void delete_activity_list_table() { db.execSQL("delete from " + DBHelper.ACTIVITY_LIST_TABLE);}
    public void delete_sub_activity_list_table() { db.execSQL("delete from " + DBHelper.SUB_ACTIVITY_LIST_TABLE);}
    public void delete_dynamic_widget_details_table() { db.execSQL("delete from " + DBHelper.DYNAMIC_WIDGET_DETAILS_TABLE);}
    public void delete_location_save_details_table() { db.execSQL("delete from " + DBHelper.LOCATION_SAVE_DETAILS_TABLE);}
    public void delete_get_category_table() { db.execSQL("delete from " + DBHelper.GET_CATEGORY_TABLE);}
    public void delete_get_photo_category_table() { db.execSQL("delete from " + DBHelper.GET_PHOTO_CATEGORY);}
    public void delete_get_category_details_table() { db.execSQL("delete from " + DBHelper.GET_CATEGORY_DETAILS_TABLE);}
    public void delete_save_image_details_table() { db.execSQL("delete from " + DBHelper.SAVE_IMAGE_DETAILS_TABLE);}




    public void deleteAll() {

        deleteVillageTable();
        deletePMAYTable();
        deletePMAYDetails();
        deletePMAYImages();

        deletefin_year();
        deletework_type();
        deleteself_help_group();
        deleteself_help_group_member();

        deleteSAVE_BEFORE_TREE_IMAGE_TABLE();
        deleteSAVE_AFTER_TREE_IMAGE_TABLE();
        deleteSAVE_TREE_IMAGE_TABLE();


        /////NOS Tables
        delete_campaign_list_table();
        delete_activity_list_table();
        delete_sub_activity_list_table();
        delete_dynamic_widget_details_table();
        delete_location_save_details_table();
        delete_get_category_table();
        delete_get_photo_category_table();
        delete_get_category_details_table();
        delete_save_image_details_table();
    }



}
