package com.example.hemolinkbackend.event;

import com.example.hemolinkbackend.entity.TestLabo;
import org.springframework.context.ApplicationEvent;

public class TestPositifEvent extends ApplicationEvent {
    private final TestLabo testLabo;

    public TestPositifEvent(Object source, TestLabo testLabo) {
        super(source);
        this.testLabo = testLabo;
    }

    public TestLabo getTestLabo() {
        return testLabo;
    }
}

