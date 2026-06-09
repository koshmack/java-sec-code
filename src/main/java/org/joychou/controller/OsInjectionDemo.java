package org.joychou.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * OS Command Injection Demo — for security research/education only.
 *
 * VULNERABLE endpoint:
 *   GET /osinjection/vuln?host=google.com
 *   Exploit: GET /osinjection/vuln?host=google.com%3Bwhoami
 *   (semicolon separates a second shell command, e.g. "ping google.com; whoami")
 *
 * SECURE endpoint:
 *   GET /osinjection/safe?host=google.com
 *   Uses an allowlist and avoids shell interpolation.
 */
@RestController
@RequestMapping("/osinjection")
public class OsInjectionDemo {

    // ---------------------------------------------------------------------------
    // VULNERABLE — never do this in production
    // ---------------------------------------------------------------------------

    /**
     * Passes user-supplied input directly into a shell command via string
     * concatenation. An attacker can append arbitrary commands with ; | && etc.
     *
     * Example exploit:
     *   GET /osinjection/vuln?host=127.0.0.1%3Bcat+/etc/passwd
     *   Executes: ping -c 1 127.0.0.1 ; cat /etc/passwd
     */
    @GetMapping("/vuln")
    public String vulnerableOsCommand(@RequestParam String host) throws IOException {
        // VULNERABLE: unsanitised input concatenated into a shell string
        String command = "ping -c 1 " + host;
        String[] cmdArray = new String[]{"sh", "-c", command};

        ProcessBuilder pb = new ProcessBuilder(cmdArray);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        return readOutput(process);
    }

    // ---------------------------------------------------------------------------
    // SECURE — allowlist validation + no shell interpolation
    // ---------------------------------------------------------------------------

    // Only characters valid for a hostname/IPv4 address
    private static final String SAFE_HOST_PATTERN = "^[a-zA-Z0-9.\\-]{1,253}$";

    // Hard-coded argument list — no shell, no string concatenation
    private static final List<String> ALLOWED_HOSTS = Arrays.asList(
            "127.0.0.1", "localhost", "example.com"
    );

    /**
     * Secure version:
     *  1. Validates the host against a strict regex (no shell metacharacters).
     *  2. Optionally restricts to an explicit allowlist.
     *  3. Passes arguments as a discrete list — bypasses shell entirely,
     *     so metacharacters ( ; | & ` $ ) have no effect.
     */
    @GetMapping("/safe")
    public String secureOsCommand(@RequestParam String host) throws IOException {
        // 1. Reject anything that doesn't look like a hostname / IP
        if (!host.matches(SAFE_HOST_PATTERN)) {
            return "Invalid host parameter.";
        }

        // 2. Allowlist check (optional but recommended)
        if (!ALLOWED_HOSTS.contains(host)) {
            return "Host not permitted.";
        }

        // 3. Pass each argument as a separate element — no shell expansion
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        return readOutput(process);
    }

    // ---------------------------------------------------------------------------
    // Helper
    // ---------------------------------------------------------------------------

    private String readOutput(Process process) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
}
