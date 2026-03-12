package com.example.hemolinkbackend.event;

import com.example.hemolinkbackend.entity.PocheSang;
import org.springframework.context.ApplicationEvent;

public class PocheDisponibleEvent extends ApplicationEvent {
    private final PocheSang pocheSang;

    public PocheDisponibleEvent(Object source, PocheSang pocheSang) {
        super(source);
        this.pocheSang = pocheSang;
    }

    public PocheSang getPocheSang() {
        return pocheSang;
    }
}

