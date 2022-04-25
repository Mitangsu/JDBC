package com.atguigu6.exer;

import com.atguigu5.util.JDBCUtils;
import org.junit.Test;
import sun.misc.OSEnvironment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

/**
 * 课后练习1
 *
 * @author shkstart
 * @create 2022-03-05 17:03
 */
public class Exer1Test {

   @Test
   public void test1Insert(){
       Scanner scanner = new Scanner(System.in);
       System.out.println("输入用户名:");
       String name = scanner.next();
       System.out.println("输入邮箱:");
       String email = scanner.next();
       System.out.println("输入生日:");
       String birthday = scanner.next();

       String sql = "insert into customers(name,email,birth)values(?,?,?)";
       int insertCount = update(sql, name, email, birthday);
       if (insertCount > 0) {
           System.out.println("添加成功");
       } else {
           System.out.println("添加失败");
       }


   }



    public int update(String sql, Object... args) {

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);

            }
            //4.执行
            /*
               如果执行的是查询操作,有返回结果，则此方法返回true;
               如果执行的是增、删、改操作,没有返回结果,则此方法返回false.
             */
            //会返回数值来修改了几条
            //方式一：ps.execute()
            //方式二:
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //5.
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;


    }

}
