package com.github.damianmcdonald.springlongpolling.persistence.service;

import com.github.damianmcdonald.springlongpolling.persistence.dao.NodeNotificationDao;
import com.github.damianmcdonald.springlongpolling.persistence.model.NodeNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public class NodeNotificationServiceImpl implements NodeNotificationService {

    @Autowired
    private NodeNotificationDao dao;

    private Date getTimestamp() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -10);
        return cal.getTime();
    }

    @Override
    public List<NodeNotification> getNotifications(final String nodeId) {
        return dao.getNotifications(nodeId, getTimestamp());
    }

    @Override
    public List<NodeNotification> getAndRemoveNotifications(final String nodeId) {
        final List<NodeNotification> notifications = getNotifications(nodeId);

        // Create a copy of the list before we delete the database entities
        final List<NodeNotification> clonedNotifications = new ArrayList<NodeNotification>(notifications);

        // delete the database entities so we don't repeat notification sending
        notifications.stream().forEach(node -> dao.delete(node));
        flush();

        // return the copied list
        return clonedNotifications;
    }

    @Override
    public boolean containsNotifications(final String nodeId) {
        return !dao.getNotifications(nodeId, getTimestamp()).isEmpty();
    }

    @Override
    public NodeNotification save(final NodeNotification node) {
        return dao.save(node);
    }

    @Override
    public void flush() {
        dao.flush();
    }

}
