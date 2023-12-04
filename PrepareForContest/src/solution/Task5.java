package solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class Task5 {
    public static int compareArrays(int[] array1, int[] array2, int beginIndex) {
        var result = beginIndex;
        var len = Math.min(array1.length, array2.length);
        for (int i = beginIndex; i < len; i++) {
            if (array1[i] == array2[i]) {
                result += 1;
                continue;
            }
            break;
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        var scanner = new BufferedReader(new InputStreamReader(System.in));
        var n = Integer.parseInt(scanner.readLine());
        int[][] arrays = new int[n][];
        for (int i = 0; i < n; i++) {
            arrays[i] = new int[Integer.parseInt(scanner.readLine())];
            arrays[i] = Stream.of(scanner.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
        }
        var result = 0;
        var leftArray = new int[n];
        for (var i = 1; i < n; i++) {
            var prefixLen = compareArrays(arrays[0], arrays[i], 0);
            result += prefixLen;
            leftArray[i] = prefixLen;
        }

        for (var i = 1; i < n; i++) {
            for (var j = i + 1; j < n; j++) {
                int a = leftArray[i];
                int b = leftArray[j];
                if (a != b) {
                    var prefixLen = Math.min(a, b);
                    result += prefixLen;
                    leftArray[j] = prefixLen;
                    continue;
                }
                var prefixLen = compareArrays(arrays[i], arrays[j], a);
                result += prefixLen;
                leftArray[j] = prefixLen;
            }
        }
        System.out.println(result);
    }
}
