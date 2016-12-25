package com.wuchao.rest;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;

@SpringBootApplication
@RestController
public class Application {

    private static ComboPooledDataSource cpds= new ComboPooledDataSource("postgresql");

    @RequestMapping("/")
    public String greeting() {
        return "Hello World!";
    }

    @RequestMapping("/hello")
    public ResponseEntity hello() {
        ResourceData result = new ResourceData();
        return new ResponseEntity<ResourceData>(result, HttpStatus.OK);
    }

    @RequestMapping("/data")
    public ResponseEntity data() {
        ResourceData rd = new ResourceData();
        try {
            Connection connection = cpds.getConnection();
            String sql = " select playcount, cmtcount, source, ts from playdata limit 1";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                rd.setPlaycount(rs.getLong(1));
                rd.setCmtcount(rs.getLong(2));
                rd.setSource(rs.getString(3));
                rd.setTs(rs.getLong(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<ResourceData>(rd, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity list() {
        ResourceCollection rc = new ResourceCollection();
        try {
            Connection connection = cpds.getConnection();
            String sql = " select playcount, cmtcount, source, ts from playdata limit 2";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            ResourceData rd = new ResourceData();
            while(rs.next()){
                rd.setPlaycount(rs.getLong(1));
                rd.setCmtcount(rs.getLong(2));
                rd.setSource(rs.getString(3));
                rd.setTs(rs.getLong(4));
                rc.add(rd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<ResourceCollection>(rc, HttpStatus.OK);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}