package com.atguigu3.preparedstatement.crud;

import com.atguigu4.bean.Customer;
import com.atguigu4.bean.Order;
import com.atguigu5.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 使用PreparedStatement来实现针对与不同表的通用查询操作
 *
 * @author shkstart
 * @create 2022-03-05 13:19
 */
public class PreparedStatementQueryTest {

    @Test
    public void testGetForList() {
        String sql = "select id,name,email from customers where id < ?";
        List<Customer> list = getForList(Customer.class, sql, 12);

        String sql1 = "select order_id orderId,order_name orderName from `order`";
        List<Order> list1 = getForList(Order.class, sql1);

        //第一种lambda表达式
        list.forEach(System.out::println);
        list1.forEach(System.out::println);

        //迭代器的遍历
//      Iterator<Customer> iterator = list.iterator();
//      while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }

        //增强for遍历
//        for (Customer c1 : list){
//            System.out.println(c1);
//        }


    }

    public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
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
            //创建集合对象
            ArrayList<T> list = new ArrayList<>();

            while (rs.next()) {
                T t = clazz.newInstance();
                //处理结果集一行数据中的每一列：给t对象指定的属性赋值
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
                //把对象都添加到list
                list.add(t);

            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return null;
    }

    @Test
    public void testGetInstance() {

        String sql = "select id,name,email from customers where id = ?";
        Customer customer = getInstance(Customer.class, sql, 10);
        System.out.println(customer);

        String sql1 = "select order_id orderId,order_name orderName from `order` where order_id = ?";
        Order order = getInstance(Order.class, sql1, 1);
        System.out.println(order);


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
    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {
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
