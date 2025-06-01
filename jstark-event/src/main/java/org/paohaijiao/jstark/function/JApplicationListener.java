package org.paohaijiao.jstark.function;

import org.paohaijiao.jstark.event.JApplicationEvent;

@FunctionalInterface
public interface JApplicationListener <E extends JApplicationEvent> {
    void onApplicationEvent( E event);

}
