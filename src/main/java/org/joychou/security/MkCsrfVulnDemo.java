package org.joychou.security;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * CSRF Vulnerability Demo — FOR SECURITY RESEARCH / EDUCATION ONLY
 *
 * Vulnerabilities demonstrated:
 *  1. State-changing action over GET (transfers money via GET param)
 *  2. POST endpoint with no CSRF token validation
 *  3. Overly permissive CORS + no SameSite cookie policy
 *
 * Attack scenario:
 *   An attacker hosts a page with:
 *     <img src="http://bank.example.com/csrf-vuln/transfer?to=attacker&amount=1000">
 *   or a hidden auto-submitting form targeting /csrf-vuln/change-email,
 *   and any authenticated user who visits the attacker page is silently exploited.
 */
@RestController
@RequestMapping("/csrf-vuln")
public class MkCsrfVulnDemo {

    // ----------------------------------------------------------------
    // VULNERABILITY 1: State-changing operation via HTTP GET
    //   GET requests should be idempotent and read-only.
    //   Exposing a money transfer on GET allows simple <img> tag attacks.
    // ----------------------------------------------------------------
    @GetMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @RequestParam String to,
            @RequestParam double amount,
            HttpServletRequest request) {

        // BUG: No CSRF token checked. Any page the user visits can
        //      silently trigger this request using an <img> tag or
        //      fetch() with credentials:include.
        Map<String, String> result = new HashMap<>();
        result.put("status", "transferred");
        result.put("to", to);
        result.put("amount", String.valueOf(amount));
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // VULNERABILITY 2: State-changing POST with no CSRF token
    //   Spring Security CSRF protection is disabled in SecurityConfig
    //   (http.csrf().disable()), so any cross-origin POST succeeds.
    // ----------------------------------------------------------------
    @PostMapping("/change-email")
    public ResponseEntity<Map<String, String>> changeEmail(
            @RequestParam String newEmail,
            HttpServletRequest request) {

        // BUG: No X-CSRF-Token or hidden _csrf field is validated here.
        //      An attacker's page can POST a form cross-origin and the
        //      browser will attach the victim's session cookie.
        Map<String, String> result = new HashMap<>();
        result.put("status", "email updated");
        result.put("newEmail", newEmail);
        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // VULNERABILITY 3: JSON endpoint NOT protected by CSRF token
    //   Content-Type: application/json blocks simple-form exploits,
    //   but modern fetch() with credentials:include bypasses this
    //   when CORS is misconfigured to allow the attacker's origin.
    // ----------------------------------------------------------------
    @PostMapping(value = "/update-password", consumes = "application/json")
    public ResponseEntity<Map<String, String>> updatePassword(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        // BUG: No CSRF token + permissive CORS (Access-Control-Allow-Origin: *)
        //      allows a cross-origin fetch to both send and read the response.
        String newPassword = body.get("password");
        Map<String, String> result = new HashMap<>();
        result.put("status", "password changed");
        return ResponseEntity.ok(result);
    }
}