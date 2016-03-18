package com.tencent.tbds.alert.service;

import com.tencent.tbds.alert.dao.AlertRepository;
import com.tencent.tbds.alert.domain.Alert;
import com.tencent.tbds.alert.domain.Notification;
import com.tencent.tbds.alert.utils.AlertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by jerryjzhang on 2016/3/17.
 */
@Service
public class AlertService {
    private static final Logger LOG = LoggerFactory.getLogger(AlertService.class);

    private ScheduledExecutorService scheduler;
    private ConcurrentHashMap<String, ScheduledFuture> scheduledTasks = new ConcurrentHashMap<>();

    @Autowired
    private MetricService metricService;

    @PostConstruct
    public void initialize(){
        scheduler = Executors.newScheduledThreadPool(20);
    }

    @Autowired
    private AlertRepository alertRepository;

    public void saveAlert(Alert alert){
        for(Notification notif : alert.getNotifications()){
            notif.setAlert(alert);
        }
        alert.setLastUpdateTime(AlertUtils.now());
        if(alert.getId() != null){
            ScheduledFuture task = scheduledTasks.get(alert.getId());
            if(task != null){
                if(!task.cancel(true)){
                    LOG.warn("Failed to cancel previous alert monitoring task");
                }
            }
        }
        alertRepository.save(alert);
        ScheduledFuture task = scheduler.scheduleAtFixedRate(
                new AlertMonitorTask(alert), 0, alert.getCondition().getPeriod(), TimeUnit.MINUTES);
        scheduledTasks.put(alert.getId(), task);
        LOG.info("Scheduled alert monitoring task for alert id={}, name={}", alert.getId(), alert.getName());
    }

    public List<Alert> getAlerts(){
        return alertRepository.findAll();
    }

    public Alert getAlert(String alertId){
        return alertRepository.findOne(alertId);
    }

    public void deleteAlert(String alertId){
        alertRepository.delete(alertId);
    }

    public class AlertMonitorTask implements Runnable {
        private Alert alert;

        public AlertMonitorTask(Alert alert){
            this.alert = alert;
        }

        public void run(){
            double value = metricService.getMetricValue(alert.getCondition().getAppId(), alert.getCondition().getMetricName(),
                    alert.getCondition().getPeriod(), alert.getCondition().getStatistic());
            boolean reachThreshold = false;
            switch(alert.getCondition().getRelation()) {
                case GT:
                    reachThreshold = value > alert.getCondition().getThreshold();
                    break;
                case LT:
                    reachThreshold = value < alert.getCondition().getThreshold();
                    break;
                case EQ:
                    reachThreshold = value == alert.getCondition().getThreshold();
                    break;
                case GTEQ:
                    reachThreshold = value >= alert.getCondition().getThreshold();
                    break;
                case LTEQ:
                    reachThreshold = value <= alert.getCondition().getThreshold();
                    break;
            }

            if(reachThreshold){
                System.out.println("Junz Notified");
            }
        }
    }
}
