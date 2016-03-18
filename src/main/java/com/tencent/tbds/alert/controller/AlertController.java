package com.tencent.tbds.alert.controller;

import com.tencent.tbds.alert.domain.Alert;
import com.tencent.tbds.alert.dto.GetAlertsResult;
import com.tencent.tbds.alert.service.AlertService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by jerryjzhang on 2016/3/16.
 */
@RestController
@RequestMapping(value="/alerts")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @RequestMapping(method= RequestMethod.POST)
    @ApiOperation(value = "创建告警或保存修改的告警", notes = "返回对象是告警的ID", response = String.class)
    public String saveAlert(@RequestBody Alert alert){
        alertService.saveAlert(alert);
        return alert.getId();
    }

    @RequestMapping(method= RequestMethod.GET)
    @ApiOperation(value = "获取所有的告警", notes = "get all alerts")
    public GetAlertsResult getAlerts(){
        return new GetAlertsResult(alertService.getAlerts());
    }

    @RequestMapping(value="/{alertId}", method= RequestMethod.GET)
    @ApiOperation(value = "根据ID获取告警", notes = "get an alert by id")
    public Alert getAlert(@PathVariable String alertId){
        return alertService.getAlert(alertId);
    }

    @RequestMapping(value="/{alertId}", method= RequestMethod.DELETE)
    @ApiOperation(value = "根据ID删除告警", notes = "delete an alert by id")
    public void deleteAlert(@PathVariable String alertId){
        alertService.deleteAlert(alertId);
    }
}
