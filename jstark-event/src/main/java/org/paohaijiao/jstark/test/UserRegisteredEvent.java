package org.paohaijiao.jstark.test;

import org.paohaijiao.jstark.event.JApplicationEvent;

public class UserRegisteredEvent extends JApplicationEvent {
        private final String username;

        public UserRegisteredEvent(Object source, String username) {
            super(source);
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
}
