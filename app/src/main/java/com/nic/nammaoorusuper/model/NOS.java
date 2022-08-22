package com.nic.nammaoorusuper.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class NOS {

    private String distictCode;
    private String districtName;

    private String blockCode;

    public String getHabCode() {
        return HabCode;
    }

    public void setHabCode(String habCode) {
        HabCode = habCode;
    }

    private String HabCode;

    private String Description;
    private String Latitude;
    private String BeneficiaryName;
    private String HabitationName;
    private String pmayId;
    private String fatherName;
    private String personAlive;
    private String buttonText;

    /////New Variable
    private String fin_year;
    private int  work_code;
    private String  work_name;
    private int shg_code;
    private int shg_member_code;
    private String shg_name;
    private String member_name;
    private String before_photo_lat;
    private String before_photo_long;
    private String after_photo_lat;
    private String after_photo_long;
    private Bitmap before_photo;
    private Bitmap after_photo;

    public String getBefore_photo_lat() {
        return before_photo_lat;
    }

    public void setBefore_photo_lat(String before_photo_lat) {
        this.before_photo_lat = before_photo_lat;
    }

    public String getBefore_photo_long() {
        return before_photo_long;
    }

    public void setBefore_photo_long(String before_photo_long) {
        this.before_photo_long = before_photo_long;
    }

    public String getAfter_photo_lat() {
        return after_photo_lat;
    }

    public void setAfter_photo_lat(String after_photo_lat) {
        this.after_photo_lat = after_photo_lat;
    }

    public String getAfter_photo_long() {
        return after_photo_long;
    }

    public void setAfter_photo_long(String after_photo_long) {
        this.after_photo_long = after_photo_long;
    }

    public Bitmap getBefore_photo() {
        return before_photo;
    }

    public void setBefore_photo(Bitmap before_photo) {
        this.before_photo = before_photo;
    }

    public Bitmap getAfter_photo() {
        return after_photo;
    }

    public void setAfter_photo(Bitmap after_photo) {
        this.after_photo = after_photo;
    }

    public String getFin_year() {
        return fin_year;
    }

    public void setFin_year(String fin_year) {
        this.fin_year = fin_year;
    }

    public int getWork_code() {
        return work_code;
    }

    public void setWork_code(int work_code) {
        this.work_code = work_code;
    }

    public String getWork_name() {
        return work_name;
    }

    public void setWork_name(String work_name) {
        this.work_name = work_name;
    }

    public int getShg_code() {
        return shg_code;
    }

    public void setShg_code(int shg_code) {
        this.shg_code = shg_code;
    }

    public int getShg_member_code() {
        return shg_member_code;
    }

    public void setShg_member_code(int shg_member_code) {
        this.shg_member_code = shg_member_code;
    }

    public String getShg_name() {
        return shg_name;
    }

    public void setShg_name(String shg_name) {
        this.shg_name = shg_name;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getPersonAlive() {
        return personAlive;
    }

    public void setPersonAlive(String personAlive) {
        this.personAlive = personAlive;
    }

    public String getIsLegel() {
        return isLegel;
    }

    public void setIsLegel(String isLegel) {
        this.isLegel = isLegel;
    }

    public String getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(String isMigrated) {
        this.isMigrated = isMigrated;
    }

    private String isLegel;
    private String isMigrated;

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getPmayId() {
        return pmayId;
    }

    public void setPmayId(String pmayId) {
        this.pmayId = pmayId;
    }


    public String getBeneficiaryName() {
        return BeneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        BeneficiaryName = beneficiaryName;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public void setHabitationName(String habitationName) {
        HabitationName = habitationName;
    }

    public String getSeccId() {
        return SeccId;
    }

    public void setSeccId(String seccId) {
        SeccId = seccId;
    }

    private String SeccId;


    private String PvCode;
    private String PvName;

    private String blockName;

    public String getTypeOfPhoto() {
        return typeOfPhoto;
    }

    public void setTypeOfPhoto(String typeOfPhoto) {
        this.typeOfPhoto = typeOfPhoto;
    }

    private String typeOfPhoto;
    private String imageRemark;
    private String dateTime;
    private String imageAvailable;



    public String getImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(String imageAvailable) {
        this.imageAvailable = imageAvailable;
    }



    public String getImageRemark() {
        return imageRemark;
    }

    public void setImageRemark(String imageRemark) {
        this.imageRemark = imageRemark;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }









    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }


    public String getPvName() {
        return PvName;
    }

    public void setPvName(String name) {
        PvName = name;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }





    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    private String Longitude;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    private Bitmap Image;



    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        this.PvCode = pvCode;
    }




    /////////////////////////////New Data
    private String campaign_id;
    private String campaign_name;
    private String campaign_from_date;
    private String campaign_to_date;

    public String getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(String campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public String getCampaign_from_date() {
        return campaign_from_date;
    }

    public void setCampaign_from_date(String campaign_from_date) {
        this.campaign_from_date = campaign_from_date;
    }

    public String getCampaign_to_date() {
        return campaign_to_date;
    }

    public void setCampaign_to_date(String campaign_to_date) {
        this.campaign_to_date = campaign_to_date;
    }

    ////Activty List
    private String campaign_activity_details_id;
    private String activity_id;
    private String no_of_items;
    private String activity_name;
    private String no_of_images;
    private String activity_from_date;
    private String activity_to_date;
    private String campaign_activity_id;

    public String getCampaign_activity_id() {
        return campaign_activity_id;
    }

    public void setCampaign_activity_id(String campaign_activity_id) {
        this.campaign_activity_id = campaign_activity_id;
    }

    public String getCampaign_activity_details_id() {
        return campaign_activity_details_id;
    }

    public void setCampaign_activity_details_id(String campaign_activity_details_id) {
        this.campaign_activity_details_id = campaign_activity_details_id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getNo_of_items() {
        return no_of_items;
    }

    public void setNo_of_items(String no_of_items) {
        this.no_of_items = no_of_items;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getNo_of_images() {
        return no_of_images;
    }

    public void setNo_of_images(String no_of_images) {
        this.no_of_images = no_of_images;
    }

    public String getActivity_from_date() {
        return activity_from_date;
    }

    public void setActivity_from_date(String activity_from_date) {
        this.activity_from_date = activity_from_date;
    }

    public String getActivity_to_date() {
        return activity_to_date;
    }

    public void setActivity_to_date(String activity_to_date) {
        this.activity_to_date = activity_to_date;
    }

    ////Activity SUb List
    private  String activity_sub_list;
    private  String item_no;
    private  String is_taken_survey;
    private  String is_taken_before_photo;
    private  String is_taken_after_photo;

    public String getActivity_sub_list() {
        return activity_sub_list;
    }

    public void setActivity_sub_list(String activity_sub_list) {
        this.activity_sub_list = activity_sub_list;
    }

    public String getItem_no() {
        return item_no;
    }

    public void setItem_no(String item_no) {
        this.item_no = item_no;
    }

    public String getIs_taken_survey() {
        return is_taken_survey;
    }

    public void setIs_taken_survey(String is_taken_survey) {
        this.is_taken_survey = is_taken_survey;
    }

    public String getIs_taken_before_photo() {
        return is_taken_before_photo;
    }

    public void setIs_taken_before_photo(String is_taken_before_photo) {
        this.is_taken_before_photo = is_taken_before_photo;
    }

    public String getIs_taken_after_photo() {
        return is_taken_after_photo;
    }

    public void setIs_taken_after_photo(String is_taken_after_photo) {
        this.is_taken_after_photo = is_taken_after_photo;
    }

    //Dynamic Widet Type Values;
    private String campaign_activity_data;
    private String campaign_data_label;
    private String campaign_data_type;

    public String getCampaign_activity_data() {
        return campaign_activity_data;
    }

    public void setCampaign_activity_data(String campaign_activity_data) {
        this.campaign_activity_data = campaign_activity_data;
    }

    public String getCampaign_data_label() {
        return campaign_data_label;
    }

    public void setCampaign_data_label(String campaign_data_label) {
        this.campaign_data_label = campaign_data_label;
    }

    public String getCampaign_data_type() {
        return campaign_data_type;
    }

    public void setCampaign_data_type(String campaign_data_type) {
        this.campaign_data_type = campaign_data_type;
    }


    //////Photo Category List
    private String image_category_id;
    private String image_category_name;
    private String is_taken;
    private String campaign_activity_entry_id;

    public String getImage_category_id() {
        return image_category_id;
    }

    public void setImage_category_id(String image_category_id) {
        this.image_category_id = image_category_id;
    }

    public String getImage_category_name() {
        return image_category_name;
    }

    public void setImage_category_name(String image_category_name) {
        this.image_category_name = image_category_name;
    }

    public String getIs_taken() {
        return is_taken;
    }

    public void setIs_taken(String is_taken) {
        this.is_taken = is_taken;
    }

    public String getCampaign_activity_entry_id() {
        return campaign_activity_entry_id;
    }

    public void setCampaign_activity_entry_id(String campaign_activity_entry_id) {
        this.campaign_activity_entry_id = campaign_activity_entry_id;
    }

    /////View Form
    private  String label_value;

    public String getLabel_value() {
        return label_value;
    }

    public void setLabel_value(String label_value) {
        this.label_value = label_value;
    }
}