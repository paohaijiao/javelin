package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.anno.JComponent;
import org.paohaijiao.jstark.anno.JEventListener;
@JComponent
public class ParentEventService {
    private boolean parentEventReceived = false;
    private String lastParentMessage;

    @JEventListener
    public void handleParentEvent(JunitTest.ParentTestEvent event) {
        this.parentEventReceived = true;
        this.lastParentMessage = event.getMessage();
    }

    public boolean isParentEventReceived() {
        return parentEventReceived;
    }

    public String getLastParentMessage() {
        return lastParentMessage;
    }
}
