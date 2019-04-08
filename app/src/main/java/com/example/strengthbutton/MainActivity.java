package com.example.strengthbutton;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button getLocationBtn;
    TextView locationText,providerText,strengthText;

    LocationManager locationManager;
    String provider;
    int st;
    double lat=0.0,longi=0.0;
    private static final int PERMISSION_ACCESS_COURSE_LOCATION = 0;

    //for db
    SQLiteDatabase sqLiteDatabaseObj;
    String SQLiteDataBaseQueryHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        locationText = (TextView)findViewById(R.id.locationText);
        providerText=(TextView)findViewById(R.id.providerText);
        strengthText=(TextView)findViewById(R.id.strengthText);


        Context context=this;
        getProvider(context);
        providerText.setText("provider: " + provider);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText.setText("Current Location: " + location.getLatitude() + ", " + location.getLongitude());
        lat=  location.getLatitude();
        longi=location.getLongitude();
        //providerText.setText("provider: " + provider);
        String info="blank";
        info="Current Location: " + location.getLatitude() + ", " + location.getLongitude();
        Toast.makeText(MainActivity.this,"value"+info , Toast.LENGTH_SHORT).show();
        Context context=this;
        getCellSignalStrength(context);
        //String strength=String.valueOf(st)
        strengthText.setText("Current Strength: " + String.valueOf(st));

        Toast.makeText(MainActivity.this,"strength:" +String.valueOf(st) , Toast.LENGTH_SHORT).show();

        SQLiteDataBaseBuild();

        SQLiteTableBuild();

        InsertDataIntoSQLiteDatabase();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void getProvider(Context context)
    {
        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        provider = manager.getNetworkOperatorName();
    }

    public void getCellSignalStrength(Context context) {
        int strength = 0;
        //Context ct;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_ACCESS_COURSE_LOCATION);
        } else {

            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile


            if (cellInfos != null && cellInfos.size() > 0) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (cellInfos.get(i) instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                        strength = cellSignalStrengthWcdma.getDbm();
                        Toast.makeText(getBaseContext(),"file saved",Toast.LENGTH_SHORT).show();
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                        CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                        strength = cellSignalStrengthGsm.getDbm();
                        break;
                    } else if (cellInfos.get(i) instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                        strength = cellSignalStrengthLte.getDbm();
                        break;
                    }
                }
            }
        }
        //txt.setText("hello");
        st=strength;


    }

    //db methods

    public void SQLiteDataBaseBuild(){

        sqLiteDatabaseObj = openOrCreateDatabase("AndroidJSonDataBase", Context.MODE_PRIVATE, null);

    }

    public void SQLiteTableBuild(){

        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " +
                "AndroidJSonTable(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,latitude REAL,longitude REAL,strength INTEGER,provider VARCHAR);");

    }

    public void InsertDataIntoSQLiteDatabase(){



            SQLiteDataBaseQueryHolder = "INSERT INTO AndroidJSonTable (latitude,longitude,strength,provider) VALUES('"+lat+"', '"+longi+"','"+st+"', '"+provider+"');";

            sqLiteDatabaseObj.execSQL(SQLiteDataBaseQueryHolder);

            Toast.makeText(MainActivity.this,"Data Inserted Successfully", Toast.LENGTH_LONG).show();



    }

}
