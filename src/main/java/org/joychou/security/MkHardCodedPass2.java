package org.joychou.security;

import org.springframework.web.bind.annotation.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MkHardCodedPass2 {
/**
 * Hard-coded password vulnerability demo (CWE-259 / OWASP A07).
 *
 * Vulnerable:  credentials embedded as string literals in source code.
 * Fixed:       credentials loaded from environment variables or a secrets manager.
 *
 * @author security-researcher
 */
@RestController
@RequestMapping("/hardcodedpassword")
public class HardCodedPassword {

    // -----------------------------------------------------------------------
    // VULNERABLE – hard-coded credentials embedded directly in source code.
    // An attacker who can read the source (e.g. via a leaked repo) immediately
    // obtains valid credentials.
    // -----------------------------------------------------------------------
    private static final String VULN_DB_URL  = "jdbc:mysql://localhost:3306/testdb";
    private static final String VULN_DB_USER = "root";
    private static final String VULN_DB_PASS = "SuperSecret123!";   // CWE-259

    @GetMapping("/vuln/dbconnect")
    public String vulnDbConnect() {
        try (Connection conn = DriverManager.getConnection(
                VULN_DB_URL, VULN_DB_USER, VULN_DB_PASS);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery("SELECT 1")) {

            return "VULN: connected with hard-coded password – connection live: "
                    + rs.next();
        } catch (Exception e) {
            return "VULN: DB connection failed – " + e.getMessage();
        }
    }

    // -----------------------------------------------------------------------
    // FIXED – credentials are read from environment variables at runtime.
    // Never present in source code or compiled class files.
    // -----------------------------------------------------------------------
    @GetMapping("/safe/dbconnect")
    public String safeDbConnect() {
        String dbUrl  = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");   // no hard-coded secret

        if (dbUrl == null || dbUser == null || dbPass == null) {
            return "SAFE: required environment variables are not set.";
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement  stmt = conn.createStatement();
             ResultSet  rs   = stmt.executeQuery("SELECT 1")) {

            return "SAFE: connected via environment-variable credentials – "
                    + rs.next();
        } catch (Exception e) {
            return "SAFE: DB connection failed – " + e.getMessage();
        }
    }
}
    
}
