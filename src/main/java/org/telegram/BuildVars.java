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
    public static final int PORT = 8443;
    public static final String EXTERNALWEBHOOKURL = "https://example.changeme.com:" + PORT; // https://(xyz.)externaldomain.tld
    public static final String INTERNALWEBHOOKURL = "https://localhost.changeme.com:" + PORT; // https://(xyz.)localip/domain(.tld)
    public static final String pathToCertificatePublicKey = "./YOURPEM.pem"; //only for self-signed webhooks
    public static final String pathToCertificateStore = "./YOURSTORE.jks"; //self-signed and non-self-signed.
    public static final String certificateStorePassword = "yourpass"; //password for your certificate-store

    public static final String OPENWEATHERAPIKEY = "117d5fa1db04067b40a31da2c1b139ae";

    public static final String DirectionsApiKey = "<your-api-key>";

    public static final String TRANSIFEXUSER = "<transifex-user>";
    public static final String TRANSIFEXPASSWORD = "<transifex-password>";
    public static final List<Integer> ADMINS = new ArrayList<>();

    public static final String pathToLogs = "./";


    public static final String linkDB = "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11204678";
    public static final String controllerDB = "com.mysql.jdbc.Driver";
    public static final String userDB = "sql11204678";
    public static final String password = "k59fUGB4Ej";

/*
    public static final String linkDB = "jdbc:sqlite:getweatherbotdb.sqlite3";
    public static final String controllerDB = "org.sqlite.JDBC";
*/
    
    static {
        // Add elements to ADMIN array here
    }
}
