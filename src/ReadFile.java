

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 读取测试集代码类
 *
 * @author mjc
 */
public class ReadFile {

    /**
     * 读取文件数据的方法
     *
     * @param fileName ： 文件的名字
     * @return ： 返回值的是二维数组
     */
    public static int[][] readFile(String fileName, int arr_x, int arr_y) {

        int[][] user_movie = new int[arr_x][arr_y];//这些蓝色是定义在 Base类中的常量   代表的是具体的数字   创建二位数组存储数据

        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int i = 0;
            while (br.ready()) {
                line = br.readLine();//每次读取数据集中的一行数据   readline
                String[] data = line.split("\t");/*用 tab 号分隔数字*/
                int[] ddd = new int[4];
                for (int j = 0; j < data.length; j++) {
                    ddd[j] = Integer.parseInt(data[j]);
                }
                //读取的数据进行分割成为四部分：1	1	5	874965758
                user_movie[ddd[0] - 1][ddd[1] - 1] = ddd[2];//存储到具体的二位数组之中
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*return preference;*/
        return user_movie;

    }

    public static void writeFile(String fileName, double[][] values) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                if (values[i][j] != 0) {
                    String line = (i + 1) + "\t" + (j + 1) + "\t" + values[i][j];
                    bw.write(line);
                    bw.newLine();
                }
            }
        }
        bw.close();
    }

}



