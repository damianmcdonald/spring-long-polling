package com.github.damianmcdonald.springlongpolling.longpolling;

import com.github.damianmcdonald.springlongpolling.persistence.service.NodeNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class LongPollingDatabaseListener {

    private static final Logger LOGGER = Logger.getLogger(LongPollingDatabaseListener.class.getName());

    @Value("${cluster.nodeid}")
    private String nodeId;

    @Autowired
    private NodeNotificationService dbService;

    @Autowired
    LongPollingEventSimulator simulator;

    @Scheduled(fixedRate = 5000)
    public void checkNotifications() {
        if (dbService.containsNotifications(nodeId)) {
            simulator.simulateOutgoingNotification(dbService.getAndRemoveNotifications(nodeId));
        }
    }

}
