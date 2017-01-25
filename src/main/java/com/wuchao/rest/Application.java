package com.wuchao.rest;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyVetoException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    private static ComboPooledDataSource cpds;

    static {
        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        cpds.setJdbcUrl("jdbc:postgresql://hostname:port/dbname");
        cpds.setUser("username");
        cpds.setPassword("password");
        cpds.setInitialPoolSize(10);
        cpds.setMaxIdleTime(30);
        cpds.setMaxPoolSize(100);
        cpds.setMinPoolSize(10);
        cpds.setMaxStatements(200);
    }

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
    public ResponseEntity data() throws SQLException {
        ResourceData rd = new ResourceData();
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
        return new ResponseEntity<ResourceData>(rd, HttpStatus.OK);
    }

    @RequestMapping("/list")
    public ResponseEntity list() throws SQLException {
        ResourceCollection rc = new ResourceCollection();
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
        return new ResponseEntity<ResourceCollection>(rc, HttpStatus.OK);
    }

    @RequestMapping("/elastic")
    public ResponseEntity elastic() throws UnknownHostException {
        ResourceCollection rc = new ResourceCollection();
        Settings setting = Settings.EMPTY;
        TransportClient client = new PreBuiltTransportClient(setting);
        client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        SearchResponse searchResponse = client.prepareSearch("csss").setTypes("comments").execute().actionGet();
        SearchHit[] hits = searchResponse.getHits().getHits();
        ResourceData rd = new ResourceData();
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSource();
            rd.setPlaycount(1000);
            rd.setCmtcount(1000);
            rd.setSource(source.get("source").toString());
            rd.setTs(Long.valueOf(source.get("ts").toString()));
            rc.add(rd);
        }
        return new ResponseEntity<ResourceCollection>(rc, HttpStatus.OK);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}