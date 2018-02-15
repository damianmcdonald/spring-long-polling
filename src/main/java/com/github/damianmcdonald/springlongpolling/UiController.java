package com.github.damianmcdonald.springlongpolling;

import com.github.damianmcdonald.springlongpolling.longpolling.LongPollingEventSimulator;
import com.github.damianmcdonald.springlongpolling.longpolling.LongPollingSession;
import com.github.damianmcdonald.springlongpolling.persistence.dao.NodeNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class UiController {


    private static final Logger LOGGER = Logger.getLogger(UiController.class.getName());

    @Autowired
    private NodeNotificationDao dao;

    @Autowired
    LongPollingEventSimulator simulator;

    @RequestMapping("/register/{dossierId}")
    @ResponseBody
    public DeferredResult<String> registerClient(@PathVariable("dossierId") final long dossierId) {
        LOGGER.log(Level.INFO, "Registering client for dossier id: " + dossierId);
        final DeferredResult<String> deferredResult = new DeferredResult<>();
        // Add paused http requests to event queue
        simulator.getPollingQueue().add(new LongPollingSession(dossierId, deferredResult));
        return deferredResult;
    }

    @RequestMapping("/simulate/{dossierId}")
    @ResponseBody
    public String simulateEvent(@PathVariable("dossierId") final long dossierId) {
        LOGGER.log(Level.INFO, "Simulating event for dossier id: " + dossierId);
        simulator.simulateIncomingNotification(dossierId);
        return "Simulating event for dossier Id: " + dossierId;
    }
}


