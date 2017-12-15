package org.telegram;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Custom build vars FILL EVERYTHING CORRECTLY
 * @date 20 of June of 2015
 */

public class BuildVars {
    public static final Boolean debug = true;
    public static final Boolean useWebHook = false;
    
    public static final String OPENWEATHERAPIKEY = "117d5fa1db04067b40a31da2c1b139ae";

    public static final String pathToLogs = "./";


/* 
    public static final String linkDB = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11204678";
    public static final String controllerDB = "com.mysql.jdbc.Driver";
    public static final String userDB = "sql11204678";
    public static final String password = "k59fUGB4Ej"; 
 
    public static final String linkDB = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12206087";
    public static final String controllerDB = "com.mysql.jdbc.Driver";
    public static final String userDB = "sql12206087";
    public static final String password = "ShqdDgSRua"; 
*/
    public static final String linkDB = "jdbc:mysql://sql10.freemysqlhosting.net:3306/sql10207847";
    public static final String controllerDB = "com.mysql.jdbc.Driver";
    public static final String userDB = "sql10207847";
    public static final String password = "wBwYAtycsg"; 

/*
    public static final String linkDB = "jdbc:sqlite:getweatherbotdb.sqlite3";
    public static final String controllerDB = "org.sqlite.JDBC";
*/
    
    static {
        // Add elements to ADMIN array here
    }
}
