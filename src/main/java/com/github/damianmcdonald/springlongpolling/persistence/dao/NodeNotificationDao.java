package com.github.damianmcdonald.springlongpolling.persistence.dao;

import com.github.damianmcdonald.springlongpolling.persistence.model.NodeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NodeNotificationDao extends JpaRepository<NodeNotification, Long> {

    @Query("SELECT n FROM NodeNotification n WHERE n.nodeId = :nodeId")
    public List<NodeNotification> getNotifications(@Param("nodeId") final String nodeId);

}
