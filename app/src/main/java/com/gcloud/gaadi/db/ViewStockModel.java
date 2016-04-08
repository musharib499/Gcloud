package com.gcloud.gaadi.db;

/**
 * Created by alokmishra on 24/2/16.
 */
public class ViewStockModel {

    public static String STOCK_ID = "stockId";
    public static String TYPE = "type";
    public static String DEALER_PLATFORM = "dealer_platform";
    public static String FINANCE_LIST = "finance_list";
    public static String CHANGE_TIME = "changetime";
    public static String CAR_CERTIFICATION = "car_certification";
    public static String USER_NAME = "username";
    public static String MAKE = "make";
    public static String MODEL_VERSION = "modelVersion";
    public static String KMS = "kms";
    public static String STOCK_PRICE = "stockPrice";
    public static String COLOR = "color";
    public static String MM = "mm";
    public static String FUEL_TYPE = "fuelType";
    public static String REG_NO = "reg_no";
    public static String MODEL_YEAR = "year";
    public static String MOBILE = "mobile";
    public static String EMAIL = "email";
    public static String SHOWROOM_ID = "showroomId";
    public static String GAADI_ID = "gaadi_id";
    public static String CAR_ID = "car_id";
    public static String CREATE_DATE = "createdDate";
    public static String SHARE_TEXT = "shareText";
    public static String HEX_CODE = "hexCode";
    public static String TRUST_MARK_CERTIFY = "trustmarkCertify";
    public static String ACTIVE = "active";
    public static String DOMAIN  = "domain";
    public static String IMAGE_ICON = "imageIcon";
    public static String AREAOFCOVER  = "areaOfCover";
    public static String INSPECTED_CAR  = "inspectedCar";
    public static String TOTAL_LEADS = "totalLeads";
    public static String DEALER_PRICE = "dealerPrice";
    public static String KM_SORT = "kmSort";
    public static String PRICE_SORT = "priceSort";
    public static String MAKE_NAME = "makeName";
    public static String MODEL_NAME = "modelName";
    public static String VERSION_NAME = "versionName";

    public static String UPDATE_TIME = "updateTime";

    public static String AVAILABLE_TABLE = "availableStock";

    public static String[] TABLE_COLUMNS = new String[]{STOCK_ID,TYPE,DEALER_PLATFORM,FINANCE_LIST,CHANGE_TIME,CAR_CERTIFICATION,USER_NAME,MAKE_NAME,MODEL_NAME,VERSION_NAME, MODEL_VERSION,
                                                         MAKE, MODEL_YEAR, KMS,STOCK_PRICE,FUEL_TYPE,COLOR,MM,REG_NO,MOBILE,EMAIL,SHOWROOM_ID,GAADI_ID,
                                                        CAR_ID,CREATE_DATE,SHARE_TEXT,HEX_CODE,TRUST_MARK_CERTIFY,ACTIVE,DOMAIN,IMAGE_ICON,AREAOFCOVER,
                                                        INSPECTED_CAR,TOTAL_LEADS,DEALER_PRICE,UPDATE_TIME,KM_SORT,PRICE_SORT};


    public static String FILTER_ACTIVE = "filterActive";
    public static String FILTER_INSPECTED = "filterInspected";

    public static String FILTER_TABLE = "filterStocks";

    public static String[] FILTER_COLUMNS = new String[]{FILTER_ACTIVE,FILTER_INSPECTED};

}