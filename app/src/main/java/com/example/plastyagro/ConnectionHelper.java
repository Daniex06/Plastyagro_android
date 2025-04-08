package com.example.plastyagro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {


    public void mandarDatos(String email, String nombre, String password, String repetirPassword, String dni){




    }

    public Connection Conn(){

        String user = "root";
        String pass = "admin";

        Connection conn = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://192.168.0.17:3307/plastyagro";
            conn = DriverManager.getConnection(connectionString, user, pass);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        return conn;
    }

}
