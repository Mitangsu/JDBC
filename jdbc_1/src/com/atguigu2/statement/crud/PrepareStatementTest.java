package com.atguigu2.statement.crud;

import com.atguigu5.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * 演示使用PrepareStatement替换Statement,解决SQL注入问题
 * @author shkstart
 * @create 2022-03-05 14:20
 *
 * 除了解决Statement的拼串,sql问题之外,PreparedStatement还要有哪些好处呢？
 * 1.PreparedStatement操作Blob(视频之类)的数据,而Statement做不到
 * 2.PreparedStatement可以实现更高校的批量操作
 */
public class PrepareStatementTest {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("用户名：");
        String userName = scan.nextLine();
        System.out.print("密   码：");
        String password = scan.nextLine();

        // SELECT user,password FROM user_table WHERE USER = '1' or ' AND PASSWORD = '
        // ='1' or '1' = '1';
        String sql = "SELECT user,password FROM user_table WHERE USER = ? and password = ?";

        User user = getInstance(User.class,sql,userName,password );
        if (user != null) {
            System.out.println("登陆成功!");
        } else {
            System.out.println("用户名或密码错误！");
        }
    }



    /**
     * 针对不同的表的通用的查询操作,返回表中的一条记录
     *
     * @param clazz
     * @param sql
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.连接Mysql
            conn = JDBCUtils.getConnection();
            //2.预编译sql语句
            ps = conn.prepareStatement(sql);
            //3.通配符赋值
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //执行:获取结果集
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //获取列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值:通过ResultSet
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名:通过ResultSetMetaData
                    //获取列的列名:getColumnName()--不推荐使用
                    //获取列的别名:getColumnLabel()
//                  String columnName = rsmd.getColumnName(i+1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //通过反射,将对象指定名columnName的属性赋值为指定的值
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columnValue);
                }
                return t;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;

    }
}
