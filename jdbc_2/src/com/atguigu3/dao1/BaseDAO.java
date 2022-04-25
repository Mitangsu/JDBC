package com.atguigu3.dao1;

import com.atguigu1.util.JDBCUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO:data(base) access object 数据库访问对象
 * 封装了针对于数据表的通用的操作
 *
 * @author shkstart
 * @create 2022-03-06 23:31
 */
public abstract class BaseDAO<T> {

    private Class<T> clazz = null;

    {
        //获取当前BaseDAO的子类结成父类中的泛型
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        ParameterizedType paramTpye =(ParameterizedType)genericSuperclass;

        Type[] typeArguments = paramTpye.getActualTypeArguments();//获取父类的泛型参数
        clazz = (Class<T>) typeArguments[0];//泛型的第一个参数
    }



    //通用的增删改---version 2.0(考虑上事务了)
    public int update(Connection conn, String sql, Object... args) {//sql当中占位符的个数与可变形参的长度相同
        PreparedStatement ps = null;
        try {
            //1.预编译sql语句,返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);

            //2.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);//小心参数声明错误

            }
            //3.执行操作
            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //4.关闭资源
            JDBCUtils.closeResource(null, ps);
        }
        return 0;

    }

    //通用的查询操作,用于返回数据表中的一条记录(version 2.0,考虑上事务)
    public  T getInstance(Connection conn, String sql, Object... args) {

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
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
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;

    }

    //通用的查询操作,用于返回数据表中的多条记录构成的集合(version 2.0,考虑上事务)
    public List<T> getForList(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

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
            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;
    }

    //查询特殊值的通用方法比如函数
    public <E>E getValue(Connection conn, String sql, Object... args)  {

        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);

            }

            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return (E) resultSet.getObject(1);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null,ps,resultSet);
        }
        return null;



    }
}