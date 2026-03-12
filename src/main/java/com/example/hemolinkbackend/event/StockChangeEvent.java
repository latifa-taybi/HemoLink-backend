package com.example.hemolinkbackend.event;

import com.example.hemolinkbackend.enums.GroupeSanguin;
import org.springframework.context.ApplicationEvent;

public class StockChangeEvent extends ApplicationEvent {
    private final GroupeSanguin groupeSanguin;
    private final String changeType;
    private final Long pocheSangId;

    public StockChangeEvent(Object source, GroupeSanguin groupeSanguin, String changeType, Long pocheSangId) {
        super(source);
        this.groupeSanguin = groupeSanguin;
        this.changeType = changeType;
        this.pocheSangId = pocheSangId;
    }

    public GroupeSanguin getGroupeSanguin() {
        return groupeSanguin;
    }

    public String getChangeType() {
        return changeType;
    }

    public Long getPocheSangId() {
        return pocheSangId;
    }
}

