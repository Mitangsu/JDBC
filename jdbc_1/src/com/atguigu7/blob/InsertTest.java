package com.atguigu7.blob;

import com.atguigu5.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 使用PreparedStatement实现批量数据的操作
 *
 * update、delete本身就具有批量操作的效果.
 * 此时的批量操作,主要指的是批量插入,使用PreparedStatement如何实现更高效的批量插入？
 *
 * 题目,向goods表中插入20000条数据
 * CREATE TABLE goods(
    id INT PRIMARY KEY AUTO_INCREMENT,
    NAME VARCHAR(25)
    );
 * 方式一：使用Statement
 * Connection conn = JDBCUtils.getConnection();
 *
 *
 * @author shkstart
 * @create 2022-03-06 18:04
 */
public class InsertTest {
    //批量插入的方法二：使用PreparedStatement
    @Test
    public void testInsert1() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name)values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                ps.setObject(1, "name_" + i);

                ps.execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }

    /*
        批量插入的方法三：
        1.addBatch()、executeBatch()、clearBatch()

     */
    @Test
    public void testInsert2() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {

            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();
            String sql = "insert into goods(name)values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                ps.setObject(1, "name_" + i);

                //1."攒"sql
                ps.addBatch();

                if (i % 500 == 0) {

                    ps.executeBatch();

                    ps.clearBatch();

                }

            }

            long end = System.currentTimeMillis();

            System.out.println("花费时间为："+(end - start));//20000:39685 --37729

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }
    //批量插入的方式四：设置连接不允许自动提交
    @Test
    public void testInsert3() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {

            long start = System.currentTimeMillis();

            conn = JDBCUtils.getConnection();

            //设置不允许字段提交数据
            conn.setAutoCommit(false);

            String sql = "insert into goods(name)values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                ps.setObject(1, "name_" + i);
                //1."攒"sql
                ps.addBatch();
                if (i % 500 == 0) {
                    ps.executeBatch();

                    ps.clearBatch();
                }

            }
            //提交数据
            conn.commit();

            long end = System.currentTimeMillis();

            System.out.println("花费时间为："+(end - start));//20000:39685 --37729 --4561

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }


    }
}
