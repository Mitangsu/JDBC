package com.atguigu3.dao;

import com.atguigu4.bean.Customer;
import com.sun.xml.internal.bind.v2.model.core.ID;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

/**
 * @author shkstart
 * @create 2022-03-06 23:54
 */
public class CustomerDAOImpl extends BaseDAO implements CustomerDAO {
    @Override
    public void insert(Connection conn, Customer cust) {
        String sql = "insert into customers(name,email,birth)values(?,?,?)";
        update(conn, sql, cust.getName(), cust.getEmail(), cust.getBirth());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from customers where id = ?";
        update(conn, sql, id);

    }

    @Override
    public void update(Connection conn, Customer cust) {
        String sql = "update customers set name =?,email = ?,birth = ? where id = ?";
        update(conn, sql, cust.getName(), cust.getEmail(), cust.getBirth(), cust.getId());

    }

    @Override
    public Customer getCustomerById(Connection conn, int id) {
        String sql = "select id,name,email,birth from customers where id =?";
        Customer customer = getInstance(conn, Customer.class, sql, id);
        return customer;
    }


    @Override
    public List<Customer> getAll(Connection conn) {
        String sql = "select id,name,email,birth from customers";
        List<Customer> list = getForList(conn, Customer.class, sql);
        return list;
    }

    @Override
    public Long getCount(Connection conn) {
        String sql = "select count(*) from customers";
        return getValue(conn, sql);
    }

    @Override
    public Date getMaxBirth(Connection conn) {
        String sql = "select max(birth) from customers";
        return getValue(conn, sql);
    }
}
