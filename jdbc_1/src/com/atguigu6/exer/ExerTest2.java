package com.atguigu6.exer;

import com.atguigu4.bean.Order;
import com.atguigu4.bean.Student1;
import com.atguigu5.util.JDBCUtils;
import com.sun.xml.internal.bind.v2.model.core.ID;
import jdk.management.resource.internal.inst.SocketOutputStreamRMHooks;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * 课后练习2
 * @author shkstart
 * @create 2022-03-05 18:43
 */
public class ExerTest2 {

    //问题一：向examstudent表中添加一条记录
    /*
        Type:
        IDCard:
        ExamCard:
        StudentName:
        Location:
        Grade:
     */
    @Test
    public void testInsert(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("四级/六级：");
        int type = scanner.nextInt();
        System.out.print("身份证号：");
        String IDCard = scanner.next();
        System.out.print("准考证号：");
        int examCard = scanner.nextInt();
        System.out.print("学生姓名：");
        String studentName = scanner.next();
        System.out.print("所在城市：");
        String location = scanner.next();
        System.out.print("考试成绩：");
        int grade = scanner.nextInt();

        String sql = "insert into examstudent(type,IDCard,examCard,StudentName,Location,Grade)values(?,?,?,?,?,?)";
         int insertCount = update(sql, type, IDCard, examCard, studentName, location,grade);
        if (insertCount > 0){
            System.out.println("添加成功");
        }else{
            System.out.println("添加失败");
        }


    }

    //问题2:根据身份证号或者准考证号查询学生成绩信息
    @Test
    public void queryWithIDCardOrexamCard(){
        System.out.println("请选择您要输入的类型");
        System.out.println("a.准考证号");
        System.out.println("b.身份证号");
        Scanner scanner = new Scanner(System.in);
        String selection = scanner.next();
        if ("a".equalsIgnoreCase(selection)){
            System.out.println("请输入准考证号");
            String examCard = scanner.next();
            String sql = "select flowID flowID,type Type,idcard IDCard,examCard ExamCard,StudentName,Location,Grade from examstudent where examCard = ?";
            Student1 student1 = getInstance(Student1.class, sql, examCard);
            if (student1 != null){
                System.out.println(student1);
            }else{
                System.out.println("输入的准考证号有误");
            }
        } else if ("b".equalsIgnoreCase(selection)) {
            System.out.println("请输入身份证号");
            String IDCard = scanner.next();
            String sql = "select flowID flowID,type Type,idcard IDCard,examCard ExamCard,StudentName,Location,Grade from examstudent where IDCard = ?";
            Student1 student1 = getInstance(Student1.class, sql, IDCard);
            if (student1 != null){
                System.out.println(student1);
            }else{
                System.out.println("输入的身份证号有误");
            }
        }else{
            System.out.println("您的输入有误,请重新进入程序");
        }


    }

    //问题3：完成学生信息的删除功能
    @Test
    public void testDeleteByExamCard(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学生的考号:");
        String examCard = scanner.next();
        //查询
        String sql = "select flowID flowID,type Type,idcard IDCard,examCard ExamCard,StudentName,Location,Grade from examstudent where ExamCard = ?";
        Student1 student1 = getInstance(Student1.class, sql, examCard);
        if (student1 == null){
            System.out.println("查无此人,请重新输入");

        }else{
            String sql1 = "delete from examstudent where examCard = ?";
            int deleteCount = update(sql1, examCard);
            if (deleteCount > 0){
                System.out.println("删除成功");
            }else{
                System.out.println("删除失败");
            }
        }
    }
    //问题3：优化以后的操作
    @Test
    public void testDeleteByExamCard1(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入学生的考号:");
        String examCard = scanner.next();
        String sql1 = "delete from examstudent where examCard = ?";
        int deleteCount = update(sql1, examCard);
        if (deleteCount > 0){
            System.out.println("删除成功");
        }else{
            System.out.println("查无此人,请重新输入");
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



    //通用增删改
    public int update(String sql, Object ...args)  {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1,args[i]);

            }

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,ps);
        }

        return 0;


    }



}
