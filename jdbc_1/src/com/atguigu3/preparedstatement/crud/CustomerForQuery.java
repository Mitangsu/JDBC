package com.atguigu3.preparedstatement.crud;

import com.atguigu4.bean.Customer;
import com.atguigu5.util.JDBCUtils;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.junit.experimental.theories.FromDataPoints;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对于Customers表的查询操作
 *
 * @author shkstart
 * @create 2022-03-04 10:11
 */
public class CustomerForQuery {
    /**
     * 针对于customers表来通用查询操作
     */
    @Test
    public void testqueryForCustomers(){
        String sql = "select id,name,birth,email from customers where id = ?";
        Customer customer = queryForCustomers(sql, 13);
        System.out.println(customer);

        sql = "select name,email from customers where name= ?";
        Customer customer1 = queryForCustomers(sql, "周杰伦");
        System.out.println(customer1);


    }


    /**
     * 针对于customers表来通用查询操作
     */
    public Customer queryForCustomers(String sql,Object...args)  {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //1.
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1,args[i]);

            }

            rs = ps.executeQuery();
            //获取结果集的元数据:ResultSetMetaData
            ResultSetMetaData rsmd = rs.getMetaData();

            //通过ResultSetMetaData获取结果集中的列数
            int columnCount = rsmd.getColumnCount();

            if (rs.next()){
                Customer cust = new Customer();
                //处理一行结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; i++) {

                    //获取列值
                    Object columnValue = rs.getObject(i + 1);

                    //获取每个列的列名
                    String columnName = rsmd.getColumnLabel(i+1);

                    //给cust对象指定的columnName属性,赋值为columnValue.通过反射
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(cust,columnValue);


                }
                    return cust;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源关闭
            JDBCUtils.closeResource(conn,ps,rs);
        }

        return  null;


    }

//    /**
//     * 针对于customers表来通用查询操作
//     */
//    public Customer queryForCustomers1(String sql,Object...args) throws Exception {
//        //1.
//        Connection conn = JDBCUtils.getConnection();
//
//        PreparedStatement ps = conn.prepareStatement(sql);
//
//        for (int i = 0; i < args.length; i++) {
//            ps.setObject(i+1,args[i]);
//
//        }
//
//        ResultSet rs = ps.executeQuery();
//        //获取结果集的元数据:ResultSetMetaData
//        ResultSetMetaData rsmd = rs.getMetaData();
//
//        //通过ResultSetMetaData获取结果集中的列数
//        int columnCount = rsmd.getColumnCount();
//
//        if (rs.next()){
//            Customer cust = new Customer();
//            //处理一行结果集一行数据中的每一个列
//            for (int i = 0; i < columnCount; i++) {
//                //获取列值
//                Object columnValue = rs.getObject(i + 1);
//
//                //获取每个列的列名
//                String columnName = rsmd.getCatalogName(i + 1);
//
//                //给cust对象指定的columnName属性,赋值为columnValue.通过反射
//                Field field = Customer.class.getDeclaredField(columnName);
//                field.setAccessible(true);
//                field.set(cust,columnValue);
//
//
//            }
//            return cust;
//        }
//        //资源关闭
//        JDBCUtils.closeResource(conn,ps,rs);
//
//        return  null;
//
//
//    }



    public void testQuery1() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            //1.获取连接
            conn = JDBCUtils.getConnection();

            //2.预编译sql语句
            String sql ="select id,name,email,birth from customers where id = ?";
            ps = conn.prepareStatement(sql);

            //3.填充占位符
            ps.setObject(1,1);

            //4.执行，并返回结果集
            resultSet = ps.executeQuery();
            //4.处理结果集
            if (resultSet.next()){//判断结果集下一条是否有数据,返回true,并指针下移,如果返回false指针不下移.

                //5.获取当前这条数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //方式一：
    //            System.out.println("id = "+id + ",name =" + name....);

                //方式二：
    //            Object[] data = new Object[]{id,name,email,birth};

                //6.方式三:将数据封装为一个对象(推荐)
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7.关闭资源
            JDBCUtils.closeResource(conn,ps,resultSet);
        }

    }

//    /**
//     * 没有处理的写法 针对customers表的查询
//     * @throws Exception
//     */
//    @Test
//    public void testQuery2() throws Exception {
//        //1.第一步获取连接
//        Connection conn = JDBCUtils.getConnection();
//
//        //2.编译sql语句
//        String sql = "select id,name,email,birth from customers where id = ?";
//        PreparedStatement ps = conn.prepareStatement(sql);
//
//        //3.占位符赋值
//        ps.setObject(1,10);
//
//        //4.结果集
//        ResultSet resultSet = ps.executeQuery();
//        //5.处理结果集
//        if (resultSet.next()){
//            //6.获取结果集
//            int id = resultSet.getInt(1);
//            String name = resultSet.getString(2);
//            String email = resultSet.getString(3);
//            Date birth = resultSet.getDate(4);
//
//            //7.第三种方法表现出来ORM编程思想
//            Customer customer = new Customer(id, name, email, birth);
//            System.out.println(customer);
//
//        }
//        //8.关闭资源
//        JDBCUtils.closeResource(conn,ps,resultSet);
//
//
//    }
}


















