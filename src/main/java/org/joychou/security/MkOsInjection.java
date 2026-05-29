import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MkOsInjection {
    private static final Pattern HOST_PATTERN =
            Pattern.compile("^[a-zA-Z0-9.-]{1,253}$");

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter host to ping: ");
        String host = scanner.nextLine();

        if (!HOST_PATTERN.matcher(host).matches()) {
            throw new IllegalArgumentException("Invalid host");
        }

        // Safe: no shell, each argument is separated
        ProcessBuilder pb = new ProcessBuilder("ping", "-c", "1", host);
        pb.inheritIO();
        Process p = pb.start();
        int exit = p.waitFor();
        System.out.println("Exit code: " + exit);
    }
}