package com.github.damianmcdonald.springlongpolling.longpolling;

import com.github.damianmcdonald.springlongpolling.persistence.model.NodeNotification;
import com.github.damianmcdonald.springlongpolling.persistence.service.NodeNotificationService;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

@Component
public class LongPollingEventSimulator {

    private static final Logger LOGGER = Logger.getLogger(LongPollingEventSimulator.class.getName());
    private static final DataFactory DATA_FACTORY = new DataFactory();
    private final BlockingQueue<LongPollingSession> longPollingQueue = new ArrayBlockingQueue<LongPollingSession>(100);

    @Autowired
    private NodeNotificationService dbService;

    @Value("${cluster.nodenames}")
    private String[] nodeNames;

    private String createMergedMapKey(final Long dossierId, final String nodeId) {
        return String.format("%d-%s", dossierId, nodeId);
    }

    private List<NodeNotification> mergeNotificationPayloads(final List<NodeNotification> notifications) {
        final Map<String, NodeNotification> mergedNotifications = new HashMap<String, NodeNotification>();
        for (final NodeNotification nodeNotification : notifications) {
            if (mergedNotifications.containsKey(createMergedMapKey(nodeNotification.getDossierId(), nodeNotification.getNodeId()))) {
                final NodeNotification node = mergedNotifications.get(createMergedMapKey(nodeNotification.getDossierId(), nodeNotification.getNodeId()));
                node.setNotificationPayload(node.getNotificationPayload() + " AND " + nodeNotification.getNotificationPayload());
                mergedNotifications.put(createMergedMapKey(node.getDossierId(), node.getNodeId()), node);
            } else {
                mergedNotifications.put(createMergedMapKey(nodeNotification.getDossierId(), nodeNotification.getNodeId()), nodeNotification);
            }
        }
        return new ArrayList<NodeNotification>(mergedNotifications.values());
    }

    // generate random data to be sent to the UI
    private static String randomData() {
        final String address = DATA_FACTORY.getAddress() + "," + DATA_FACTORY.getCity() + "," + DATA_FACTORY.getNumberText(5);
        final String business = DATA_FACTORY.getBusinessName();
        return String.format("%s located at %s", business, address);
    }

    // Simulated event handler
    public void simulateIncomingNotification() {
        // write the event to the database
        getPollingQueue().stream()
                .forEach((LongPollingSession lps) -> {
                    final String randomData = randomData(); // keep the same payload data per cluster node
                    for (final String node : nodeNames) { // create a notification per cluster node
                        final NodeNotification notification = new NodeNotification();
                        notification.setDossierId(lps.getDossierId());
                        notification.setTimestamp(new Date());
                        notification.setNodeId(node);
                        notification.setNotificationPayload(randomData);
                        dbService.save(notification);
                    }
                });
        dbService.flush(); // force the changes to the DB
    }

    public void simulateOutgoingNotification(final List<NodeNotification> notifications) {
        for (final NodeNotification nodeNotification : mergeNotificationPayloads(notifications)) {
            getPollingQueue().stream()
                    .filter(e -> e.getDossierId() == nodeNotification.getDossierId())
                    .forEach((final LongPollingSession lps) -> {
                        try {
                            lps.getDeferredResult().setResult(nodeNotification.getNotificationPayload());
                        } catch (Exception e) {
                            throw new RuntimeException();
                        }
                    });
        }
        getPollingQueue().removeIf(e -> e.getDeferredResult().isSetOrExpired());
    }

    public BlockingQueue<LongPollingSession> getPollingQueue() {
        return longPollingQueue;
    }
}