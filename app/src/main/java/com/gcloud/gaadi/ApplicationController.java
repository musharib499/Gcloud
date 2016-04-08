package com.gcloud.gaadi;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.util.SparseIntArray;
import android.view.ViewConfiguration;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.gcloud.gaadi.constants.Constants;
import com.gcloud.gaadi.db.FinanceDB;
import com.gcloud.gaadi.db.GCloudStocksDB;
import com.gcloud.gaadi.db.InsuranceDB;
import com.gcloud.gaadi.db.LeadsOfflineDB;
import com.gcloud.gaadi.db.MakeModelVersionDB;
import com.gcloud.gaadi.db.ManageLeadDB;
import com.gcloud.gaadi.db.StocksDB;
import com.gcloud.gaadi.utils.GAHelper;
import com.gcloud.gaadi.utils.GCLog;
import com.imageuploadlib.Databases.StockImagesDB;
import com.imageuploadlib.Utils.ActiveDocumentMetrics;
import com.squareup.otto.Bus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ankit on 17/11/14.
 */
public class ApplicationController extends Application {

    public static final SparseIntArray makeLogoMap = new SparseIntArray() {

        {
            put(12, R.drawable.maruti);
            put(10, R.drawable.hyundai);
            put(9, R.drawable.honda);
            put(19, R.drawable.toyota);
            put(18, R.drawable.tata);
            put(11, R.drawable.mahindra);
            put(6, R.drawable.fiat);
            put(7, R.drawable.ford);
            put(4, R.drawable.chevrolet);
            put(2, R.drawable.audi);
            put(30, R.drawable.volkswagen);
            put(3, R.drawable.bmw);
            put(41, R.drawable.renault);
            put(13, R.drawable.mercedes);
            put(22, R.drawable.nissan);
            put(17, R.drawable.skoda);
            put(25, R.drawable.lamborghini);
            put(38, R.drawable.bugatti);
            put(44, R.drawable.polaris);
            put(42, R.drawable.ferrari);
            put(40, R.drawable.aston_martin);
            put(46, R.drawable.mini);
            put(43, R.drawable.peugeot);
            //put(48, R.drawable.willy);
            put(51, R.drawable.lexus);
            put(53, R.drawable.leyland);
            put(68, R.drawable.mazda);
            put(63, R.drawable.morris);
            //put(64, R.drawable.studebaker);
            put(67, R.drawable.chrysler);
            put(8, R.drawable.hindustan_motors);
            put(15, R.drawable.opel);
            put(23, R.drawable.reva);
            put(24, R.drawable.mahindra_renault);
            put(26, R.drawable.maybach);
            put(29, R.drawable.san_motors);
            put(5, R.drawable.daewoo);
            put(58, R.drawable.ssangyong);
            put(59, R.drawable.isuzu);
            put(39, R.drawable.maserati);
            put(45, R.drawable.force);
            put(14, R.drawable.mitsubishi);
            put(16, R.drawable.premier);
            put(20, R.drawable.porsche);
            put(27, R.drawable.icml);
            put(28, R.drawable.rolls_royce);
            put(31, R.drawable.volvo);
            put(36, R.drawable.land_rover);
            put(21, R.drawable.bentley);
            put(33, R.drawable.jaguar);
            put(55, R.drawable.datsun);
            put(57, R.drawable.hummer);
            put(60, R.drawable.cadillac);
        }
    };
    public static final SparseIntArray makeLabelMap = new SparseIntArray() {
        {
            put(12, R.string.maruti);
            put(10, R.string.hyundai);
            put(9, R.string.honda);
            put(19, R.string.toyota);
            put(18, R.string.tata);
            put(11, R.string.mahindra);
            put(7, R.string.ford);
            put(4, R.string.chevrolet);
            put(2, R.string.audi);
            put(30, R.string.volkswagen);
            put(3, R.string.bmw);
            put(41, R.string.renault);
            put(13, R.string.mercedes);
            put(22, R.string.nissan);
            put(17, R.string.skoda);
            put(25, R.string.lamborghini);
            put(38, R.string.bugatti);
            put(44, R.string.polaris);
            put(42, R.string.ferrari);
            put(40, R.string.aston_martin);
            put(46, R.string.mini);
            put(48, R.string.willy);
            put(51, R.string.lexus);
            put(53, R.string.ashok_leyland);
            put(68, R.string.mazda);
            put(63, R.string.morris);
            put(64, R.string.studebaker);
            put(67, R.string.chrysler);
            put(8, R.string.hindustan_motors);
            put(15, R.string.opel);
            put(23, R.string.reva);
            put(24, R.string.mahindra_renault);
            put(26, R.string.maybach);
            put(29, R.string.san_motors);
            put(5, R.string.daewoo);
            put(58, R.string.ssangyong);
            put(59, R.string.isuzu);
            put(39, R.string.maserati);
            put(45, R.string.force);
            put(14, R.string.mitsubishi);
            put(16, R.string.premier);
            put(20, R.string.porsche);
            put(27, R.string.icml);
            put(28, R.string.rolls_royce);
            put(31, R.string.volvo);
            put(36, R.string.land_rover);
            put(21, R.string.bentley);
            put(55, R.string.datsun);
            put(57, R.string.hummer);
            put(60, R.string.cadillac);
        }
    };
    public static ArrayList<String> orderImages = new ArrayList<String>();
    public static LinkedHashMap<String, String> kmDrivenLabelMap = new LinkedHashMap<String, String>() {
        {
            put("2500", "0 - 5,000km");
            put("7500", "5,000 - 10,000km");
            put("12500", "10,000 - 15,000km");
            put("17500", "15,000 - 20,000km");
            put("22500", "20,000 - 25,000km");
            put("27500", "25,000 - 30,000km");
            put("32500", "30,000 - 35,000km");
            put("37500", "35,000 - 40,000km");
            put("42500", "40,000 - 45,000km");
            put("47500", "45,000 - 50,000km");
            put("52500", "50,000 - 55,000km");
            put("57500", "55,000 - 60,000km");
            put("62500", "60,000 - 65,000km");
            put("67500", "65,000 - 70,000km");
            put("72500", "70,000 - 75,000km");
            put("77500", "75,000 - 80,000km");
            put("82500", "80,000 - 85,000km");
            put("87500", "85,000 - 90,000km");
            put("92500", "90,000 - 95,000km");
            put("97500", "95,000 - 1,00,000km");
            put("102500", "1,00,000 - 1,05,000km");
            put("107500", "1,05,000 - 1,10,000km");
            put("112500", "1,10,000 - 1,15,000km");
            put("117500", "1,15,000 - 1,20,000km");
            put("122500", "1,20,000 - 1,25,000km");
            put("127500", "1,25,000 - 1,30,000km");
            put("132500", "1,30,000 - 1,35,000km");
            put("137500", "1,35,000 - 1,40,000km");
            put("142500", "1,40,000 - 1,45,000km");
            put("147500", "1,45,000 - 1,50,000km");
        }
    };
    public static LinkedHashMap<String, String> kmValuesMap = new LinkedHashMap<String, String>() {
        {
            put("2500", "0 - 5000");
            put("7500", "5000 - 10000");
            put("12500", "10000 - 15000");
            put("17500", "15000 - 20000");
            put("22500", "20000 - 25000");
            put("27500", "25000 - 30000");
            put("32500", "30000 - 35000");
            put("37500", "35000 - 40000");
            put("42500", "40000 - 45000");
            put("47500", "45000 - 50000");
            put("52500", "50000 - 55000");
            put("57500", "55000 - 60000");
            put("62500", "60000 - 65000");
            put("67500", "65000 - 70000");
            put("72500", "70000 - 75000");
            put("77500", "75000 - 80000");
            put("82500", "80000 - 85000");
            put("87500", "85000 - 90000");
            put("92500", "90000 - 95000");
            put("97500", "95000 - 100000");
            put("102500", "100000 - 105000");
            put("107500", "105000 - 110000");
            put("112500", "110000 - 115000");
            put("117500", "115000 - 120000");
            put("122500", "120000 - 125000");
            put("127500", "125000 - 130000");
            put("132500", "130000 - 135000");
            put("137500", "135000 - 140000");
            put("142500", "140000 - 145000");
            put("147500", "145000 - 150000");
        }
    };
    public static LinkedHashMap<String, String> budget = new LinkedHashMap<String, String>() {
        {

            put("0-100000", "0 - 1 Lakh");
            put("100000-200000", "1 - 2 Lacs");
            put("200000-300000", "2 - 3 Lacs");
            put("300000-400000", "3 - 4 Lacs");
            put("400000-500000", "4 - 5 Lacs");
            put("500000-800000", "5 - 8 Lacs");
            put("800000-1200000", "8 - 12 Lacs");
            put("1200000-1500000", "12 - 15 Lacs");
            put("1500000-0", "Above 15 Lacs");


        }
    };
    public static LinkedHashMap<String, String> budgetFrom = new LinkedHashMap<String, String>() {
        {

            put("0", "0");
            put("100000", "1 Lac");
            put("200000", "2 Lacs");
            put("300000", "3 Lacs");
            put("400000", "4 Lacs");
            put("500000", "5 Lacs");
            put("800000", "8 Lacs");
            put("1200000", "12 Lacs");
            put("1500000", "15 Lacs and above");
        }
    };
    public static LinkedHashMap<String, String> budgetFromKey = new LinkedHashMap<String, String>() {
        {

            put("0", "0");
            put("1 Lac","100000");
            put("2 Lacs","200000");
            put("3 Lacs","300000");
            put("4 Lacs","400000");
            put("5 Lacs","500000");
            put("8 Lacs","800000");
            put("12 Lacs","1200000");
            put("15 Lacs and above","1500000");
        }
    };
    public static LinkedHashMap<String, String> budgetTo = new LinkedHashMap<String, String>() {
        {
            put("0", "0");
            put("100000", "1 Lac");
            put("200000", "2 Lacs");
            put("300000", "3 Lacs");
            put("400000", "4 Lacs");
            put("500000", "5 Lacs");
            put("800000", "8 Lacs");
            put("1200000", "12 Lacs");
            put("1500000", "15 Lacs and above");
        }
    };
    public static LinkedHashMap<String, String> numOwnersMap = new LinkedHashMap<String, String>() {
        {

            put("1", "First");
            put("2", "Second");
            put("3", "Third");
            put("4", "Fourth");
            put("5", "Four +");
            put("0", "Unregistered");
        }
    };
    public static LinkedHashMap<String, String> numOwnersMapUsedCarValuation = new LinkedHashMap<String, String>() {
        {

            put("1", "First");
            put("2", "Second");
            put("3", "Third");
            put("4", "Fourth");
            put("5", "Four +");
        }
    };
    public static LinkedHashMap<String, String> inspectedCarsReqType = new LinkedHashMap<String, String>() {
        {

            put(Constants.POLICY_NOT_EXPIRED_NCB_TRANSFER, "Policy Not Expired, NCB Transfer");
            put(Constants.POLICY_NOT_EXPIRED_NO_NCB_TRANSFER, "Policy Not Expired, No NCB Transfer");
            put(Constants.POLICY_EXPIRED, "Policy Expired");

        }
    };
    public static LinkedHashMap<String, String> otherCarsReqType = new LinkedHashMap<String, String>() {
        {

            put(Constants.BRAND_NEW_CAR, "Brand New Car");
            put(Constants.ROLLOVER, "Rollover Case");

        }
    };
    public static LinkedHashMap<String, String> inspectedCarsNcbPolicy = new LinkedHashMap<String, String>() {
        {

            put("20", "20");
            put("25", "25");
            put("35", "35");
            put("45", "45");
            put("50", "50");

        }
    };
    public static LinkedHashMap<String, String[]> otherCarsNcbPolicy = new LinkedHashMap<String, String[]>() {
        {
            String[] a = {"0", "20"};
            String[] b = {"0", "20", "25"};
            String[] c = {"0", "20", "25", "35"};
            String[] d = {"0", "20", "25", "35", "45"};
            String[] e = {"0%", "20%", "25%", "35%", "45%", "50%"};

            put("1", a);
            put("2", b);
            put("3", c);
            put("4", d);
            put("5", e);

        }
    };
    public static LinkedHashMap<String, String> colorMap = new LinkedHashMap<String, String>() {
        {
            put("Black", "Black");
            put("Blue", "Blue");
            put("Bright Green", "Bright Green");
            put("Burgundy", "Burgundy");
            put("Charcoal", "Charcoal");
            put("Cobalt", "Cobalt");
            put("Dark Blue", "Dark Blue");
            put("Dark Red", "Dark Red");
            put("Green", "Green");
            put("Grey", "Grey");
            put("Lavender", "Lavender");
            put("Light Orange", "Light Orange");
            put("Medium Blue", "Medium Blue");
            put("Orange", "Orange");
            put("Pink", "Pink");
            put("Purple", "Purple");
            put("Sienna", "Sienna");
            put("Silver", "Silver");
            put("Sky Blue", "Sky Blue");
            put("Tan", "Tan");
            put("Teal", "Teal");
            put("Turquoise", "Turquoise");
            put("White", "White");
            put("Yellow", "Yellow");
            put("Golden", "Golden");
            put("Platinum", "Platinum");
            put("Other", "Other");
        }
    };
    public static LinkedHashMap<String, String> monthShortMap = new LinkedHashMap<String, String>() {
        {
            put("01", "Jan");
            put("02", "Feb");
            put("03", "Mar");
            put("04", "Apr");
            put("05", "May");
            put("06", "Jun");
            put("07", "Jul");
            put("08", "Aug");
            put("09", "Sep");
            put("1", "Jan");
            put("2", "Feb");
            put("3", "Mar");
            put("4", "Apr");
            put("5", "May");
            put("6", "Jun");
            put("7", "Jul");
            put("8", "Aug");
            put("9", "Sep");
            put("10", "Oct");
            put("11", "Nov");
            put("12", "Dec");
        }
    };
    public static LinkedHashMap<String, String> monthNameMap = new LinkedHashMap<String, String>() {
        {
            put("Jan", "1");
            put("Feb", "2");
            put("Mar", "3");
            put("Apr", "4");
            put("May", "5");
            put("Jun", "6");
            put("Jul", "7");
            put("Aug", "8");
            put("Sep", "9");
            put("Oct", "10");
            put("Nov", "11");
            put("Dec", "12");
        }
    };
    public static LinkedHashMap<String, String> overallConditionMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Excellent");
            put("2", "Good");
            put("3", "Fair");
            put("4", "Bad");
        }
    };

    //    public static ArrayList<String> imagesList;
    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }*/
    public static LinkedHashMap<String, String> exteriorMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Scratchless");
            put("2", "Original Paint");
            put("3", "Minor scratches are present");
            put("4", "Some deep scratches and dents are present");
            put("5", "Major dents and scratches, needs extensive paint work");
            put("6", "Minor paint work done");
            put("7", "Some dent and paint work done");
        }
    };
    public static LinkedHashMap<String, String> bodyFrameMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Frame and panels are original");
            put("2", "Some panels have been repaired");
            put("3", "Some panels have been replaced");
            put("4", "Frame has been repaired");
            put("5", "Windshield has no cracks");
            put("6", "Windshield has minor spots");
            put("7", "Windshield has cracks to be replaced");
        }
    };
    public static LinkedHashMap<String, String> etcMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Excellent mechanical condition");
            put("2", "Excellent pick up");
            put("3", "Smooth operation and gear shifting");
            put("4", "No abnormal noises");
            put("5", "Clean engine compartment,no leakages");
            put("6", "Low/poor pick up");
            put("7", "Gear shifting is slightly hard");
            put("8", "Clutch Hard");
            put("9", "blue/black smoke from exhaust");
        }
    };
    public static LinkedHashMap<String, String> ssMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Smooth operation, no abnormal noise");
            put("2", "Suspension has minor noise / wear");
            put("3", "Suspension has abnormal noise needs repair");
            put("4", "Steering has minor play");
            put("5", "Power steering is hard needs repair");
        }
    };

    /* public RequestQueue getRequestQueue() {
         if (mRequestQueue == null) {
             mRequestQueue = Volley.newRequestQueue(getApplicationContext());
         }

         return mRequestQueue;
     }

     public ImageLoader getImageLoader() {
         getRequestQueue();
         if (mImageLoader == null) {
             mImageLoader = new ImageLoader(this.mRequestQueue,
                     new LruBitmapCache());
         }
         return this.mImageLoader;
     }

     public <T> void addToRequestQueue(Request<T> req, String tag, boolean showFullPageError, OnNoInternetConnectionListener listener) {

         if (checkInternetConnectivity()) {
             // set the default tag if tag is empty
             req.setTag(TextUtils.isEmpty(tag) ? Constants.TAG : tag);
             getRequestQueue().add(req);
         } else {
             //ApplicationController.getEventBus().post(new NetworkEvent(NetworkEvent.NetworkError.NO_INTERNET_CONNECTION, true));
             if (listener != null) {
                 listener.onNoInternetConnection(new NetworkEvent(NetworkEvent.NetworkError.NO_INTERNET_CONNECTION, showFullPageError));
             }

         }
     }

     public <T> void addToRequestQueue(Request<T> req, String tag) {
         if (checkInternetConnectivity()) {
             req.setTag(TextUtils.isEmpty(tag) ? Constants.TAG : tag);
             getRequestQueue().add(req);
         }
     }


     public <T> void addToRequestQueue(Request<T> req) {
         req.setTag(Constants.TAG);
         getRequestQueue().add(req);
     }


     public void cancelPendingRequests(Object tag) {
         if (mRequestQueue != null) {
             mRequestQueue.cancelAll(tag);
         }
     }*/
    public static LinkedHashMap<String, String> tyresMap = new LinkedHashMap<String, String>() {
        {
            put("1", "New or Like new");
            put("2", "All tires are matching including spare");
            put("3", "Over 50% tread left");
            put("4", "Some tires are not matching");
            put("5", "Tyres are worn out Needs replacement");
        }
    };
    public static LinkedHashMap<String, String> brakesMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Excellent working condition");
            put("2", "Brake pads over 50% life left");
            put("3", "Brake pads need replacement");
            put("4", "Brake disc / drum need repair");
        }
    };
    public static LinkedHashMap<String, String> interiorMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Clean, no odour. All electronics, switches, etc are in proper working condition");
            put("2", "Minor Wear/Stains/Fading of upholstery");
            put("3", "Electronics, switches,etc are functional with minor defects");
            put("4", "Torn/Burns/Cracked upholstery, dash, plastics");
            put("5", "Electronics, switches,etc need repairs");
            put("6", "Airbag Deployed Light is ON");
            put("7", "Malfunction Indicator Light is ON");
        }
    };
    // single select
    public static LinkedHashMap<String, String> electricalsMap = new LinkedHashMap<String, String>() {
        {
            put("1", "Fully functional in excellent condition");
            put("2", "Minor faults");
            put("3", "Some malfunction, needs repair");
            put("4", "Fully functional in good condition");
            put("5", "Functional need some repair");
        }
    };
    // single select
    public static LinkedHashMap<String, String> acheaterMap = new LinkedHashMap<String, String>() {
        {
            put("1", "AC/Heater in excellent working condition");
            put("2", "Low cooling, needs gas top up");
            put("3", "No cooling, needs repair");
        }
    };
    // single select
    public static LinkedHashMap<String, String> batteryMap = new LinkedHashMap<String, String>() {
        {
            put("1", "New Battery");
            put("2", "Battery in good condition with proper cold cranking, free of rust and all leads properly secured");
            put("3", "Weak battery needs replacement");
            put("4", "Battery is Dead / Weak needs replacement");
        }
    };
    public static HashMap<String, String[]> leadManageDialogData = new HashMap<String, String[]>() {
        {
            put(Constants.NotYetCalled_CallUnsuccess, new String[]{
                    "Call Back",
                    "Mark Closed"});
            put(Constants.NotYetCalled_CallUnsuccess_Callback, new String[]{
                    "Call Now",
                    "After 1 hour",
                    "After 2 hours",
                    "Schedule Date and Time"});
            put(Constants.NotYetCalled_CallSuccess, new String[]{
                    "Yes",
                    "No"});
            put(Constants.NotYetCalled_CallSuccess_Yes, new String[]{
                    "Schedule Follow Up",
                    "Schedule Walk In",
                    "Mark Closed"});
        }
    };
    public static HashMap<String, Integer> leadManageDialogMessage = new HashMap<String, Integer>() {
        {
            put(Constants.NotYetCalled_CallUnsuccess, R.string.notYetCalled_callUnsuccess);
            put(Constants.NotYetCalled_CallUnsuccess_Callback, R.string.notYetCalled_callUnsuccess_callback);
            put(Constants.NotYetCalled_CallSuccess, R.string.notYetCalled_callSuccess);
            put(Constants.NotYetCalled_CallSuccess_Yes, R.string.notYetCalled_callSuccess_yes);
        }
    };
    public static HashMap<Integer, String> leadManageKeyMap = new HashMap<Integer, String>() {
        {
            put(0, Constants.NotYetCalled_CallUnsuccess);
            put(1, Constants.NotYetCalled_CallUnsuccess_Callback);
            put(7, Constants.NotYetCalled_CallSuccess);
            put(8, Constants.NotYetCalled_CallSuccess_Yes);
        }
    };
    public static HashMap<Integer, String> loanStatus = new HashMap<Integer, String>() {
        {
            put(0, "Case picked");
            put(1, "Pending");
            put(2, "Approved");
            put(3, "Rejected");
            put(4, "Disbursed");
            put(5, "Washout");
            put(6, "Closed");
            put(-2, "Action Required");

        }
    };
    private static Bus mEventBus;
    private static Context mContext;
    private static MakeModelVersionDB makeModelVersionDB;
    private static StocksDB stocksDB;
    private static StockImagesDB stocksImagesDB;
    private static FinanceDB financeDB;
    private static ManageLeadDB manageLeadDB;
    private static LeadsOfflineDB leadsOfflineDB;
    private static InsuranceDB insuranceDB;
    private static ApplicationController mInstance;
    private static GCloudStocksDB gcloudStockDB = null;
    private GAHelper mGAHelper;
    private ArrayList<String> documentCategories;
    private HashMap<String, ActiveDocumentMetrics> activeDocumentMetricsHashMap;

    public synchronized static SQLiteDatabase getReadableDB() {
        return gcloudStockDB.getReadableDatabase();
    }

    public synchronized static SQLiteDatabase getWritableDB() {
        return gcloudStockDB.getWritableDatabase();
    }


    public static Bus getEventBus() {
        return mEventBus;
    }

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }

    public static boolean checkInternetConnectivity() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static synchronized MakeModelVersionDB getMakeModelVersionDB() {
        return makeModelVersionDB;
    }

    public static synchronized StocksDB getStocksDB() {
        return stocksDB;
    }

    public static synchronized FinanceDB getFinanceDB() {
        return financeDB;
    }

    public static SQLiteDatabase getFinanceDB(boolean writable) {
        if (writable) {
            return getFinanceDB().getWritableDatabase();
        } else {
            return getFinanceDB().getReadableDatabase();
        }
    }

    public static synchronized ManageLeadDB getManageLeadDB() {
        return manageLeadDB;
    }

    public static synchronized LeadsOfflineDB getLeadsOfflineDB() {
        return leadsOfflineDB;
    }

    public static synchronized InsuranceDB getInsuranceDB() {
        return insuranceDB;
    }

    public HashMap<String, ActiveDocumentMetrics> getActiveDocumentMetricsHashMap() {
        return activeDocumentMetricsHashMap;
    }

    public void setActiveDocumentMetricsHashMap(HashMap<String, ActiveDocumentMetrics> activeDocumentMetricsHashMap) {
        this.activeDocumentMetricsHashMap = activeDocumentMetricsHashMap;
    }

    public ArrayList<String> getDocumentCategories() {
        return documentCategories;
    }

    public void setDocumentCategories(ArrayList<String> documentCategories) {
        this.documentCategories = documentCategories;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getOverflowMenu();

        //disabling fabric in case of debug build (causes problem with unit testing)
        if (BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        } else {
            Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build(), new Crashlytics());
        }
//        Fabric.with(this, new Crashlytics());

        mInstance = this;
        mEventBus = new AndroidOttoBus();
        mContext = this;

        //mEventBus.register(this);

        makeModelVersionDB = new MakeModelVersionDB(mContext);
        stocksDB = new StocksDB(mContext);
        stocksImagesDB = new StockImagesDB(mContext);
        financeDB = new FinanceDB(mContext);
        manageLeadDB = new ManageLeadDB(mContext);
        leadsOfflineDB = new LeadsOfflineDB(mContext);
        insuranceDB = new InsuranceDB(mContext);

        if (gcloudStockDB == null) {
            gcloudStockDB = new GCloudStocksDB(this);
        }


        GCLog.e("mmv db invoked");
        mGAHelper = new GAHelper(this);

//        FinanceUtils.performSync();

      /*throw new IllegalStateException("Test Exception");*/
    }

    public synchronized GAHelper getGAHelper() {
        return mGAHelper;
    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            if (e != null) {
                Crashlytics.logException(e.getCause());
            }
        }
    }

    // map specifying SFA rights id and their meaning.
    /*public static HashMap<String, String> sfaUserRightsMap = new HashMap<String, String>() {
        {
            put("1", "Check in");
            put("2", "Full Access G Cloud");
            put("3", "View dealer stock (All)");
            put("4", "Add \\u0026 Modify dealer stock");
            put("5", "View dealer stock (Mapped)");
            put("6", "File Reimbursements / Conveyance");
            put("7", "Get notifications");
            put("8", "Report issue");
            put("9", "Add new leads");
            put("10", "Call Support");
            put("11", "Punch RSA");
            put("12", "Punch Warranty");
            put("13", "Get reminders Alerts");
            put("14", "Used car valuation access");
            put("15", "View visit history");
            put("16", "View Performance Reports");
            put("17", "Certification requests");
            put("18", "Chat feature");
            put("19", "Follow up setting");
            put("20", "File Finance case");
            put("21", "File Insurance case");
            put("22", "View dealer reports");
            put("23", "View upcoming renewals");
            put("24", "Access LMS");
            put("25", "Access Dealer Platform");
            put("26", "Show upcoming follow up tab");
            put("27", "Show overdue tab");
            put("28", "Show done tab");
            put("29", "Show pending tab");
            put("30", "View Attendance");
        }
    };*/
}
