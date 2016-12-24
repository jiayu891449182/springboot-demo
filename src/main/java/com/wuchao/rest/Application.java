package com.wuchao.rest;

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
        Connection connection = null;
        ResourceCollection rc = new ResourceCollection();
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://hostname:port/dbname","username", "password");
            String sql = " select playcount, cmtcount, source, ts from playdata limit 2";
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while(rs.next()){
                ResourceData rd = new ResourceData();
                rd.setPlaycount(rs.getLong(1));
                rd.setCmtcount(rs.getLong(2));
                rd.setSource(rs.getString(3));
                rd.setTs(rs.getLong(4));
                rc.add(rd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<ResourceCollection>(rc, HttpStatus.OK);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}