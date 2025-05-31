package org.paohaijiao.jstark.session;

import javax.security.auth.login.Configuration;
import java.sql.Connection;

public interface JSqlSessionFactory {

    JSqlSession openSession();

    JSqlSession openSession(boolean autoCommit);


    JSqlSession openSession(Connection connection);


    Configuration getConfiguration();
}
