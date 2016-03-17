package com.tencent.tbds.alert.service;

import com.tencent.tbds.alert.dao.AlertRepository;
import com.tencent.tbds.alert.domain.Alert;
import com.tencent.tbds.alert.domain.Condition;
import com.tencent.tbds.alert.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jerryjzhang on 2016/3/17.
 */
@Service
public class AlertService {
    @Autowired
    private AlertRepository alertRepository;

    public void saveAlert(Alert alert){
        for(Condition cond : alert.getConditions()){
            cond.setAlert(alert);
        }
        for(Notification notif : alert.getNotifications()){
            notif.setAlert(alert);
        }
        alertRepository.save(alert);
    }

    public List<Alert> getAlerts(){
        return alertRepository.findAll();
    }

    public void deleteAlert(String alertId){
        alertRepository.delete(alertId);
    }
}