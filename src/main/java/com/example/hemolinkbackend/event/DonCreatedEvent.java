package com.example.hemolinkbackend.event;

import com.example.hemolinkbackend.entity.Don;
import org.springframework.context.ApplicationEvent;

public class DonCreatedEvent extends ApplicationEvent {
    private final Don don;

    public DonCreatedEvent(Object source, Don don) {
        super(source);
        this.don = don;
    }

    public Don getDon() {
        return don;
    }
}

