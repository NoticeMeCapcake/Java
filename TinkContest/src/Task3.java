import java.util.Arrays;
import java.util.Scanner;

public class Task3 {
    public static void main(String[] args) {
        try (var scanner = new Scanner(System.in)){
            var n = Integer.parseInt(scanner.nextLine());

            var A = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            var B = Arrays.stream(scanner.nextLine().split(" ")).mapToInt(Integer::parseInt).toArray();

            int leftBorder = 0;
            int rightBorder = B.length - 1;
            for (; leftBorder < A.length && leftBorder != rightBorder && A[leftBorder] == B[leftBorder]; leftBorder++) {

            }

        }
    }
}
