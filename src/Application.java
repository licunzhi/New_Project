import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Application
 * @Description 功能描述
 * @Author lcz
 * @Date 2019/03/02 19:52
 */
public class Application {

    // 代码中常量设置位置

    // K值设置
    private static final int K = 10;
//    private static final int K = 20;
//    private static final int K = 30;
//    private static final int K = 40;
//    private static final int K = 50;
//    private static final int K = 60;
//    private static final int K = 70;
//    private static final int K = 80;
//    private static final int K = 90;

    public static void main(String[] args) throws IOException {

        // base数据集信息
        int[][] base = ReadFile.readFile("u1.base", 943, 1682);

        // test数据集
        int[][] test = ReadFile.readFile("u1.test", 943, 1682);

        // 用户相似度矩阵
        double[][] similarityMatrix = Application.produceSimilarityMatrix(base);

        // 分数预测结果
        double[][] matrix = predictScore(base, similarityMatrix);
        ReadFile.writeFile("k-10.txt", matrix);
        //double[][] similarityMatrix = ReadFile.readFile("k-10.txt", 943, 1682);
    }



    // 预测评分
    private static double[][] predictScore(int[][] base, double[][] similarityMatrix) {

        double[][] matrix = new double[943][1682];// 预测的分数结果矩阵 （用户index 预测分数）


        for (int i = 0; i < base.length; i++) {
            System.out.println("总数:" + base.length + ",当前结算位置:" + (i + 1));
            for (int j = 0; j < base[i].length; j++) {

                // 未进行评分,需要进行预测的项目
                if (base[i][j] == 0) {

                    // 查找K近邻信息结果返回 用户index 相似度数据value
                    Map<Integer, Double> nebers = findNeBers(base, similarityMatrix, i, j);
                    //System.out.println(nebers.size());

                    double similaritySum = 0;// 分母  比如 0.5 + 0.6
                    double sum = 0;// 分子
                    double score = 0;
                    for (Map.Entry<Integer, Double> entry : nebers.entrySet()) {
                        sum += similarityMatrix[i][entry.getKey()]* base[entry.getKey()][j];//预测分数的算法：不同的相关系数*同一用户的不同电影的分数之和
                        similaritySum += similarityMatrix[i][entry.getKey()];//相似度值和
                    }

                    // 预测分数计算
                    if (similaritySum == 0)// 排除分母为零的情况
                        score = 0;
                    else
                        score = sum / similaritySum;

                    matrix[i][j] = score;
                }

            }
        }
        return matrix;
    }

    private static Map<Integer, Double> findNeBers(int[][] base, double[][] similarityMatrix, int i, int j) {
        Map<Integer, Double> neberMap = new HashMap<>();

        // 查找K近邻的方法
        for (int u = 0; u < 943; u++) {
            if (base[u][j] != 0) { // 用户u对物品j存在评分
                neberMap.put(u, similarityMatrix[i][u]);
            } else {
                neberMap.put(u, 0.0);
            }
        }
        // 相似度的存储数据格式为：用户index 用户相似度value
        Map<Integer, Double> result = sortByValueDescending(neberMap);

        // 按照顺序新取出最大的K个
        Map<Integer, Double> final_result = new HashMap<>();
        int k_value = 0;
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            if (entry.getValue() != 0) {
                final_result.put(entry.getKey(), entry.getValue());
                k_value++;
            }
            if (entry.getValue() == 0) {
                break;
            }
            if (k_value >= K) {
                break;
            }
        }
        //===========================================================
        // 临时添加舍弃不满足K个的
        if (k_value < K) {
            final_result.clear();
        }
        //===========================================================
        return final_result;
    }

    //降序排序map中的value数据
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> {
            int compare = (o1.getValue()).compareTo(o2.getValue());
            return -compare;
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }






    // 计算相似度
    private static double[][] produceSimilarityMatrix(int[][] base) {
        double[][] similarityMatrix = new double[943][943];

        for (int i = 0; i < 943; i++) {
            for (int j = 0; j < 943; j++) {
                // 用户自己和自己相似度为1
                if (i == j) {
                    similarityMatrix[i][j] = 1;
                }
                // 计算和其他用户相似度
                else {
                    similarityMatrix[i][j] = Application.computeSimilarity(base[i], base[j]);
                }
            }
        }
        return similarityMatrix;
    }

    // 计算相似度具体方法
    private static double computeSimilarity(int[] item1, int[] item2) {
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < item1.length; i++) {
            if(item1[i] != 0 && item2[i] !=0) {
                list1.add(item1[i]);
                list2.add(item2[i]);
            }
            j++;
        }
        //研究计算相似度的方法具体的实现   返回的是相似度的参数
        return Application.pearsonCorrelation(list1,list2);
    }

    private static double pearsonCorrelation(List<Integer> a, List<Integer> b) {
        int num = a.size();//集合a作为参照集  用户i
        int sum_prefOne = 0;
        int sum_prefTwo = 0;
        int sum_squareOne = 0;
        int sum_squareTwo = 0;
        int sum_product = 0;
        for (int i = 0; i < num; i++) {
            sum_prefOne += a.get(i);//∑xi
            sum_prefTwo += b.get(i);//∑yi
            sum_squareOne += Math.pow(a.get(i), 2);//∑xi2
            sum_squareTwo += Math.pow(b.get(i), 2);//∑yi2
            sum_product += a.get(i) * b.get(i);//∑xiyi
        }
        double sum = num * sum_product - sum_prefOne * sum_prefTwo;//n∑xiyi-∑xi∑yi
        double den = Math.sqrt((num * sum_squareOne - Math.pow(sum_squareOne, 2)) * (num * sum_squareTwo - Math.pow(sum_squareTwo, 2)));
        //√((n∑xi-(∑xi2)2)*(n∑yi-(∑yi2)2))------这就是皮尔逊相关系数
        double result;
        if(den==0) result=0;
        else result = sum / den;
        //n∑xiyi-∑xi∑yi/√((n∑xi-(∑xi2)2)*(n∑yi-(∑yi2)2))
        return result;
    }






}
