package com.exerice.administrator.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exerice.administrator.coolweather.R;
import com.exerice.administrator.coolweather.db.CoolWeatherDB;
import com.exerice.administrator.coolweather.model.City;
import com.exerice.administrator.coolweather.model.Country;
import com.exerice.administrator.coolweather.model.Province;
import com.exerice.administrator.coolweather.util.HttpCallbackListener;
import com.exerice.administrator.coolweather.util.HttpUtil;
import com.exerice.administrator.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-8-29.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTRY=2;

    private ListView listView;
    private TextView titleText;
    List<String> dataList=new ArrayList<String>();
    private CoolWeatherDB coolWeatherDB;
    private int currebtLevel;
    private List<Province> provinceList;
    private Province selectProvince;
    private City selectCity;
    private List<City> cityList;
    private ArrayAdapter<String> adapter;
    private String address;
    private List<Country> countyList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getIntance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currebtLevel==LEVEL_PROVINCE){
                    selectProvince = provinceList.get(position);
                    queryCities();
                }else if(currebtLevel==LEVEL_CITY){
                    selectCity=cityList.get(position);
                    queryCountry();

                }
            }
        });
        queryProvince();//加载省级
    }

    /**
     * 查询省 优先从数据库
     */
    private void queryProvince() {
       provinceList= coolWeatherDB.loadProvince();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province p:provinceList){
                dataList.add(p.getProvinceNme());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currebtLevel=LEVEL_PROVINCE;

        }else{
            queryFromServer(null,"province");
        }
    }

    /**
     * 查询选中省内的城市优先从数据库中查找
     */
    public void queryCities(){
        cityList=coolWeatherDB.laodCity(selectProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvinceNme());
            currebtLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectProvince.getProvinceCode(),"city");
        }


    }
    public void queryCountry(){
        countyList = coolWeatherDB.loadCountry(selectCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(Country country:countyList){
                dataList.add(country.getCountryCode());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCityName());
            currebtLevel=LEVEL_COUNTRY;


        }else{
            queryFromServer(selectCity.getCityCode(),"country");

        }
    }
    /**
     * 从服务器查询数据
     */
    public void queryFromServer(String code, final String type){
        if(!TextUtils.isEmpty(code)){
            address = "http://weather.com.cn/data/list3/city"+code+".xml";

        }else {
            address="http://weather.com.cn/data/list3/city.xml";

        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handlerProvincesResponse(coolWeatherDB,response);

                }else if("city".equals(type)){
                    result=Utility.handlerCitiesResponse(coolWeatherDB,response,selectProvince.getId());


                }else if("country".equals(type)){
                    result=Utility.handlerCountriesResponse(coolWeatherDB,response,selectCity.getId());

                }
                if(result){
                    //通过runonuithread方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            colseProgressDislog();
                            if("province".equals(type)){
                                queryProvince();

                            }else if("city".equals(type)){
                                queryCities();
                            }else if("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        colseProgressDislog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void colseProgressDislog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }
    /**
     * 根据当前级别 此时应该返回列表还是退出
     */
    @Override
    public void onBackPressed() {
        if(currebtLevel==LEVEL_COUNTRY){
            queryCities();
        }else if(currebtLevel==LEVEL_CITY){
            queryProvince();
        }else{
            finish();
        }
    }
}




















