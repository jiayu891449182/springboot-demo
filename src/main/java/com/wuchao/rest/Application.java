package com.wuchao.rest;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootApplication
@RestController
//@Controller
public class Application {

    private static ComboPooledDataSource cpds;

    @Autowired
    private UserDAO userDAO;

    static {
        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl("jdbc:mysql://hostname:port/dbname");
        cpds.setUser("username");
        cpds.setPassword("password");
        cpds.setInitialPoolSize(10);
        cpds.setMaxIdleTime(30);
        cpds.setMaxPoolSize(100);
        cpds.setMinPoolSize(10);
        cpds.setMaxStatements(200);
    }

    @RequestMapping("/")
    public String index(Model map) {
        map.addAttribute("host", "http://blog.didispace.com");
        return "index";
    }

    @RequestMapping("/hello")
    public ResponseEntity hello() {
        ResourceData result = new ResourceData();
        return new ResponseEntity<ResourceData>(result, HttpStatus.OK);
    }

    @RequestMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name, Model model) {
        model.addAttribute("name", name);
        return "hello";
    }

    @RequestMapping("/data")
    public ResponseEntity data() {
        ResourceData user = new ResourceData();
        try {
            Connection connection = cpds.getConnection();
            String sql = " select id, name, age from test limit 1";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                user.setId(rs.getString(1));
                user.setName(rs.getString(2));
                user.setAge(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<ResourceData>(user, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity list() {
        ResourceCollection rc = new ResourceCollection();
        try {
            Connection connection = cpds.getConnection();
            String sql = " select id, name, age from test limit 1";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            ResourceData rd = new ResourceData();
            while(rs.next()){
                rc.add(rd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<ResourceCollection>(rc, HttpStatus.OK);
    }

    @RequestMapping("/user")
    public User findByUserName(){
        return userDAO.findByName("lucy");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}