package com.github.damianmcdonald.springlongpolling.longpolling;


import org.springframework.web.context.request.async.DeferredResult;

public class LongPollingSession {

    private final long dossierId;
    private final DeferredResult<String> deferredResult;

    public LongPollingSession(final long dossierId, final DeferredResult<String> deferredResult) {
        this.dossierId = dossierId;
        this.deferredResult = deferredResult;
    }

    public long getDossierId() {
        return dossierId;
    }

    public DeferredResult<String> getDeferredResult() {
        return deferredResult;
    }
}
