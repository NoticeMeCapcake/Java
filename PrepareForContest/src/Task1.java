//package solution;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        var scanner = new Scanner(System.in);
//        var n = Integer.parseInt(scanner.nextLine());
//        var languages = scanner.nextLine().split(" ");
////        var employees = scanner.nextLine().split(" ");
//
//        var stack = new LinkedList<Node>();
//
//        stack.push(new Node(0, "AB", -1, -1));
//
//        var results = new int[n];
//
//        scanner.nextInt();
//
//        for (int i = 0; i < (2 * n); i++) {
//
////        }
//
////        for (int i = 1; i < employees.length - 1; i++) {
//            var employee = scanner.nextInt();
//            var previous = stack.peek();
//
//            if (previous.number != employee) {
//
//                var node = new Node(
//                        employee, languages[employee - 1],
//                        previous.heightToA + 1,
//                        previous.heightToB + 1
//                );
//
//                if (previous.language.contains("A")) {
//                    node.heightToA = 0;
//                }
//                else {
//                    node.heightToB = 0;
//                }
//
//                stack.push(node);
//            }
//            else {
//                stack.pop();
//
//                results[employee - 1] = previous.language.equals("A") ?
//                        previous.heightToA : previous.heightToB;
//            }
//        }
//
//        for (var barrier : results) {
//            System.out.print(barrier + " ");
//        }
//    }
//}
//
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
//
//
