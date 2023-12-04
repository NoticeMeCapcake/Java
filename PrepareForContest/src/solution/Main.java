package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {
        var scanner = new BufferedReader(new InputStreamReader(System.in));
        var n = Integer.parseInt(scanner.readLine());
        var languages = scanner.readLine().split(" ");
        var employeesST = new StringTokenizer(scanner.readLine(), " ");

        int[][] matrix = new int[n + 1][4];
        matrix[0][1] = 3;
        matrix[0][2] = -1;
        matrix[0][3] = -1;

        var stack = new LinkedList<Integer>();

        stack.push(0);

        var results = new int[n];

        Integer.parseInt(employeesST.nextToken());
        var employee = Integer.parseInt(employeesST.nextToken());
        while (employeesST.hasMoreTokens()) {

            var previous = stack.peek();

            if (previous != employee) {
                matrix[employee][0] = employee;
                matrix[employee][1] = languages[employee - 1].equals("A") ? 1 : 2;
                matrix[employee][2] = matrix[previous][2] + 1;
                matrix[employee][3] = matrix[previous][3] + 1;

                if ((matrix[previous][1] & 1) == 1) {
                    matrix[employee][2] = 0;
                }
                else {
                    matrix[employee][3] = 0;
                }

                stack.push(employee);
            }
            else {
                stack.pop();

                results[employee - 1] = matrix[previous][1] == 1 ?
                        matrix[previous][2] : matrix[previous][3];
            }
            employee = Integer.parseInt(employeesST.nextToken());
        }

        for (var barrier : results) {
            System.out.print(barrier + " ");
        }
    }
}

//class Node {
//    public Node(int _number, String _language, int _heightToA, int _heightToB) {
//        number = _number;
//        language = _language;
//        heightToA = _heightToA;
//        heightToB = _heightToB;
//    }
//
//    public int number;
//    public String language;
//
//    public int heightToA;
//    public int heightToB;
//}


