package com.bandgear.apfree.service.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.bandgear.apfree.bean.Device;
import com.bandgear.apfree.bean.Router;
import com.bandgear.apfree.dao.Dao;
import com.bandgear.apfree.dao.impl.DeviceDao;
import com.bandgear.apfree.service.DeviceService;
import com.bandgear.apfree.utils.Utils4Service;

public class DeviceServiceImpl implements DeviceService {
	Dao<Device> d=new DeviceDao();
	@Override
	public String getDevices(String deviceToken) {
		JSONObject resultObj=new JSONObject();
		try {
			if(Utils4Service.checkDeviceToken(deviceToken)!=1){//判断device_token是否合法
				resultObj.put("code", "0");
				resultObj.put("message", "device_token is invalid!");
				return resultObj.toString();
			}
			
			List<Device> devices = ((DeviceDao)d).findByDeviceToken(deviceToken);
			resultObj.put("result", devices);
			resultObj.put("code", "1");
			resultObj.put("message", "success!");
			return resultObj.toString();
		} catch (SQLException e) {
			e.printStackTrace();
			resultObj.put("code", "0");
			resultObj.put("message", "error!");
			return resultObj.toString();
		}
	}
	@Override
	public void addByDevId(Device device, String devId) {
		try {
			device.setUpdate_time(new Date());
			device.setKind(1);
			device.setStatus(1);
			device.setLogin_time(new Date());
			//1.通过devId获取device
			Device findByMacAndDevId = ((DeviceDao)d).findByMacAndDevId(device,devId);
			//2.1如果没有获取到device，执行add操作
			if(findByMacAndDevId==null){
				device.setLogin_count(1);
				((DeviceDao)d).addByDevId(device,devId);
			//2.2如果获取到device，执行update操作
			}else{
				//2.2.1如果token相同，是同一次登录，login_count不改变
				if(findByMacAndDevId.getToken().equals(device.getToken())){
					device.setLogin_count(findByMacAndDevId.getLogin_count()+1);
				//2.2.2如果token不相同，则是新的一次登录，login_count加一次
				}else{
					device.setLogin_count(findByMacAndDevId.getLogin_count()+1);
				}
				((DeviceDao)d).updateByMacAndDevId(device,devId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
