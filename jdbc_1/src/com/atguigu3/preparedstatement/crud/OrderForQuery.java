package com.atguigu3.preparedstatement.crud;

import com.atguigu4.bean.Order;
import com.atguigu5.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对于Order表的通用的查询操作
 * @author shkstart
 * @create 2022-03-04 15:36
 */
public class OrderForQuery {
   /*
      针对于表的字段名与类的属性名不相同的清空:
      1.必须声明sql时,使用类的属性名来命名字段的别名
      2.使用ResultSetMetaData时,需要使用getColumnLabel()来替换getColumnName(),
        获取列的别名.
      补充说明：如果sql中没有给字段起别名,getColumnLabel()获取的就是列名
      用getColumnLabel()就行了

    */
    @Test
    public void testOrderForQuery(){
        String sql ="select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql, 1);
        System.out.println(order);

    }

    /**
     * 通用的针对于Order表的查询操作
     * @param sql
     * @param args
     * @return
     * @throws Exception
     */
    public Order orderForQuery(String sql,Object ...args)  {
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
                ps.setObject(i+1,args[i]);
            }
            //执行:获取结果集
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //获取列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()){
                Order order = new Order();
                    for (int i = 0; i < columnCount; i++) {
                        //获取每个列的列值:通过ResultSet
                        Object columnValue = rs.getObject(i + 1);

                        //获取每个列的列名:通过ResultSetMetaData
                        //获取列的列名:getColumnName()--不推荐使用
                        //获取列的别名:getColumnLabel()
//                      String columnName = rsmd.getColumnName(i+1);
                        String columnLabel = rsmd.getColumnLabel(i+1);

                        //通过反射,将对象指定名columnName的属性赋值为指定的值
                        Field field = Order.class.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(order,columnValue);
                    }
                    return order;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return null;



    }

//    public Order oderForQuery(String sql,Object ...args) throws Exception {
//        //1.连接Mysql
//        Connection conn = JDBCUtils.getConnection();
//        //2.预编译sql语句
//        PreparedStatement ps = conn.prepareStatement(sql);
//        //3.通配符赋值
//        for (int i = 0; i < args.length; i++) {
//            ps.setObject(i+1,args[i]);
//        }
//        //执行:获取结果集
//        ResultSet rs = ps.executeQuery();
//        //获取结果集的元数据
//        ResultSetMetaData rsmd = rs.getMetaData();
//        //获取列数
//        int columnCount = rsmd.getColumnCount();
//        if (rs.next()){
//            Order order = new Order();
//            for (int i = 0; i < columnCount; i++) {
//                //获取每个列的列值:通过ResultSet
//                Object columnValue = rs.getObject(i + 1);
//
//                //获取每个列的列名:通过ResultSetMetaData
//                String columnName = rsmd.getColumnName(i+1);
//
//                //通过反射,将对象指定名columnName的属性赋值为指定的值
//                Field field = Order.class.getDeclaredField(columnName);
//                field.setAccessible(true);
//                field.set(order,columnValue);
//            }
//            return order;
//
//
//
//
//        }
//        JDBCUtils.closeResource(conn,ps,rs);
//
//        return null;
//
//
//
//    }

//    public void testQuery1()  {
//        Connection conn = null;
//        PreparedStatement ps = null;
//        ResultSet resultSet = null;
//        try {
//            conn = JDBCUtils.getConnection();
//
//            String sql = "select order_id,order_name,order_date from `order` where order_id = ?";
//            ps = conn.prepareStatement(sql);
//
//            ps.setObject(1,1);
//
//            resultSet = ps.executeQuery();
//            if (resultSet.next()){
//                int id = resultSet.getInt(1);
//                String name = resultSet.getString(2);
//                Date date = resultSet.getDate(3);
//
//
//                Order order = new Order(id, name, date);
//                System.out.println(order);
//
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            JDBCUtils.closeResource(conn,ps,resultSet);
//        }
//
//
//
//    }

}
