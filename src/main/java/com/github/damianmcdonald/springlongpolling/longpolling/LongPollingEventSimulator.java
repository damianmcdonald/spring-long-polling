package com.github.damianmcdonald.springlongpolling.longpolling;

import com.github.damianmcdonald.springlongpolling.persistence.model.NodeNotification;
import com.github.damianmcdonald.springlongpolling.persistence.service.NodeNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class LongPollingEventSimulator {

    private static final Logger LOGGER = Logger.getLogger(LongPollingEventSimulator.class.getName());
    private final BlockingQueue<LongPollingSession> longPollingQueue = new ArrayBlockingQueue<LongPollingSession>(100);

    @Autowired
    private NodeNotificationService dbService;


    @Autowired
    private MessagePayloadUtil messageUtil;

    @Value("${cluster.nodenames}")
    private String[] nodeNames;

    private String createMergedMapKey(final Long dossierId, final String nodeId) {
        return String.format("%d-%s", dossierId, nodeId);
    }

    private List<NodeNotification> mergeNotificationPayloads(final List<NodeNotification> notifications) {
        final Map<String, NodeNotification> mergedNotifications = new ConcurrentHashMap<String, NodeNotification>();
        notifications.stream().forEach(nodeNotification -> {
            final String key = createMergedMapKey(nodeNotification.getDossierId(), nodeNotification.getNodeId());
            if (mergedNotifications.containsKey(key)) {
                final NodeNotification node = mergedNotifications.get(key);
                node.setNotificationPayload(messageUtil.appendMessagePayload(node.getNotificationPayload(), nodeNotification.getNotificationPayload()));
                mergedNotifications.put(key, node);
            }
            mergedNotifications.putIfAbsent(key, nodeNotification);
        });
        return new ArrayList<NodeNotification>(mergedNotifications.values());
    }

    // Simulated event handler
    public void simulateIncomingNotification(final long dossierId) {
        final String randomData = messageUtil.getMessagePayload(); // keep the same payload data per cluster node
        for (final String node : nodeNames) { // create a notification per cluster node
            final NodeNotification notification = new NodeNotification();
            notification.setDossierId(dossierId);
            notification.setTimestamp(new Date());
            notification.setNodeId(node);
            notification.setNotificationPayload(randomData);
            dbService.save(notification);
        }
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