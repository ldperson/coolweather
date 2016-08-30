package com.exerice.administrator.coolweather.util;

import android.text.TextUtils;

import com.exerice.administrator.coolweather.db.CoolWeatherDB;
import com.exerice.administrator.coolweather.model.City;
import com.exerice.administrator.coolweather.model.Country;
import com.exerice.administrator.coolweather.model.Province;

/**
 * Created by Administrator on 2016-8-29.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的数据
     */
    public synchronized static boolean handlerProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvince=response.split(",");
            if(allProvince!=null&&allProvince.length>0) {
                for(String p:allProvince){
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceNme(array[1]);
                    //将结果存储到province
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析处理市的数据
     */
    public static boolean handlerCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if(TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null&&allCities.length>0){
                for(String c:allCities){
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);

                }
                return true;
            }

        }
        return false;

    }
    /**
     * 处理县级
     */
    public static boolean handlerCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCountries=response.split(",");
            if(allCountries!=null&&allCountries.length>0){
            for(String co:allCountries){
                Country cou=new Country();
                String[] array=co.split("\\|");
                cou.setCountryCode(array[0]);
                cou.setCountryName(array[1]);
                cou.setCityId(cityId);
                coolWeatherDB.saveCountry(cou);
            }
            return true;
            }
        }
        return  false;

    }















}
