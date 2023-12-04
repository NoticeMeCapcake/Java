package solution;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class TestInput {
    public static void main(String[] args) throws IOException {
        long now = System.currentTimeMillis();
//        FileWriter writer = new FileWriter("111.txt", false);
//        writer.write("1 ".repeat(1000_000));
//        writer.write("\n");
//        writer.write("A ".repeat(1000_000));
        var scanner = new BufferedReader(new InputStreamReader(System.in));
//        var n = Integer.parseInt(scanner.readLine());
        var languages = scanner.readLine().split(" ");
        var employeesST = new StringTokenizer(scanner.readLine(), " ");
        System.out.println(System.currentTimeMillis() - now);
    }
}
