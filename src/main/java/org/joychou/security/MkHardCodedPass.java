package org.joychou.security;

public class MkHardCodedPass{
// Vulnerability: hard-coded secret in source code
    private static final String ADMIN_PASSWORD = "SuperSecret123!";

    public static boolean login(String username, String password) {
        return "admin".equals(username) && ADMIN_PASSWORD.equals(password);
    }

    public static void main(String[] args) {
        String inputUser = "admin";
        String inputPass = "SuperSecret123!";

        if (login(inputUser, inputPass)) {
            System.out.println("Login success");
        } else {
            System.out.println("Login failed");
        }
    }
}