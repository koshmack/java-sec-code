package org.joychou.security;

public class MkHardCodedPass{

    public static void main(String[] args) {
        String username = "admin";
        String password = "P@ssw0rd123"; // Vulnerability: hard-coded credential

        if (authenticate(username, password)) {
            System.out.println("Login successful");
        } else {
            System.out.println("Login failed");
        }
    }

    private static boolean authenticate(String username, String password) {
        // Simulated auth check
        return "admin".equals(username) && "P@ssw0rd123".equals(password);
    }
}