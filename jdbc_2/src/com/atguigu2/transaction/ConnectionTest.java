package com.atguigu2.transaction;

import com.atguigu1.util.JDBCUtils;
import org.junit.Test;

import java.lang.annotation.Target;
import java.sql.Connection;
import java.util.jar.JarEntry;

/**
 * @author shkstart
 * @create 2022-03-06 20:29
 */
public class ConnectionTest {
    @Test
    public void testGetConnection() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);

    }
}
