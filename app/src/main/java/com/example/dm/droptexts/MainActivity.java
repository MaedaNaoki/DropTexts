package com.example.dm.droptexts;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener, View.OnClickListener{

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private LocationManager mLocationManager;
    private Criteria criteria;
    private List<String> locations;
    private TextView text;
    private Button button1,button2;
    private Texts txts;
    private EditText editText;
    private String data;
    private String provider;
    private boolean flag; //ボタンの処理変更

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        text = (TextView) findViewById(R.id.text);
        button1 = new Button(this);
        button2 = new Button(this);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        editText = new EditText(this);
        editText = (EditText) findViewById(R.id.inputText);

        locations = new ArrayList<String>();
        txts = new Texts();
        data = "";
        flag = true;

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(criteria, true);

    }

    @Override
    public void onLocationChanged(Location location) {
        String hoge = location.getLatitude() +" "+location.getLongitude()+"\n";
        locations.add(hoge);
        data += hoge;
        text.setText(hoge);
        System.out.println(hoge);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onClick(View v) {
        if(v != null) {
            switch(v.getId()){
                case R.id.button1:
                    if(flag) {
                        flag = false;
                        button1.setText("停止");
                        Toast.makeText(this, "取得を開始しました", Toast.LENGTH_SHORT).show();
                        mLocationManager.requestLocationUpdates(provider,1,0,this);
                    }else {
                        flag = true;
                        button1.setText("再開");
                        Toast.makeText(this, "取得を停止しました", Toast.LENGTH_SHORT).show();
                        mLocationManager.removeUpdates(this);
                    }
                    break;
                case R.id.button2:
                    if(flag) {
                        txts.write(locations,editText.getText().toString());
                        connectDropBox();
                        try {
                            System.out.println("kokomadekita");
                            upload();
                        }catch(Exception e){

                        }
                    }else {
                        Toast.makeText(this, "位置情報の取得を\n停止させてください", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
    public void connectDropBox() {
        DropboxUtils dropboxUtils = new DropboxUtils(this);
        if(!dropboxUtils.hasLoadAndroidAuthSession()){
            AppKeyPair appKeys = new AppKeyPair(DropboxUtils.APPKEY, DropboxUtils.APPKEYSECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys);
            mDBApi = new DropboxAPI<>(session);
            mDBApi.getSession().startOAuth2Authentication(this); //認証していない場合は認証ページを表示
        }else {
            mDBApi = new DropboxAPI<>(dropboxUtils.loadAndroidAuthSession()); //SharedPreferencesから認証情報取得
        }
    }

    public void upload() throws FileNotFoundException, DropboxException {
        File file = new File(txts.filename+".txt");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            DropboxAPI.Entry response = mDBApi.putFile(txts.filename+".txt", inputStream, file.length(), null, null);
            Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
            Toast.makeText(this, "アップロード完了！", Toast.LENGTH_SHORT).show();
        }catch(Exception e) {
            Log.i("DbExampleLog","Exception :" +e);
        }
    }
}