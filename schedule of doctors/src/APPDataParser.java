import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Float.NaN;

public class APPDataParser {
    public static String[][] grafic = {
            {"ИВАНОВ ИВАН ИВАНОВИЧ", "12:00-16:00", "12:00-16:00", "12:00-16:00", "12:00-16:00", "12:00-16:00", "12:00-16:00", ""},
            {"ИВАНОВ ИВАН ИВАНОВИЧ", "08:00-12:00", "08:00-10:00", "08:00-16:00", "", "", "", ""},
            {"ПЕТРОВ ПЕТР ПЕТРОВИЧ", "12:00-16:00", "", "12:00-16:00", "", "12:00-16:00", "", ""},
            {"ИВАНОВ ИВАН ИВАНОВИЧ", "09:00-10:00", "10:00-11:00", "", "", "", "", ""},
            {"ПЕТРОВ ПЕТР ПЕТРОВИЧ", "11:00-15:00", "", "13:00-16:15", "", "12:00-14:00", "", ""},
    };

    public static void main(String[] args){
        normalizeGrafic();
        System.out.println(Arrays.deepToString(grafic));
    }

    public static void normalizeGrafic(){
        //производим конкатинацию строк по одинаковым именам, повторяющиеся имена приравниваем к null
        int count = 0; // подсчитывает количество строк которые затем удалятся
        for(int i = 0; i < grafic.length; i++){
            for(int j = i + 1; j < grafic.length; j++){
                if((grafic[i][0] != null) && grafic[i][0].equals(grafic[j][0])){
                    grafic[j][0] = null;
                    count++;
                    for(int day = 1; day < grafic[0].length; day++){
                        grafic[i][day] = grafic[i][day] + "/" + grafic[j][day];
                    }
                }
            }
        }

        //избавляемся от повторяющихся строк
        String[][] graficNew = new String[grafic.length-count][grafic[0].length];
        int j = 0;
        for(int i = 0; i < grafic.length; i++){
            if(grafic[i][0] != null){
                graficNew[j] = grafic[i];
                j++;
            }
        }
        grafic = graficNew;

        //подготовка строк со временем для сравнения
        for(int i = 0; i < grafic.length; i++){
            for(int n = 1; n < grafic[0].length; n++){
                String[] time = grafic[i][n].split("/");
                float[][] allTime = new float[time.length][2];
                for(int k = 0; k < time.length; k++){
                    if(!time[k].equals("")){
                        allTime[k][0] = Float.valueOf(time[k].substring(0, 2)) + Float.valueOf(time[k].substring(3, 5)) / 60;
                        allTime[k][1] = Float.valueOf(time[k].substring(6, 8)) + Float.valueOf(time[k].substring(9, 11)) / 60;
                    }
                    else {
                        allTime[k][0] = NaN;
                        allTime[k][1] = NaN;
                    }
                }
                List<float[]> mergeTime = merge(allTime);
                String timeStr = "";
                for(int k = 0; k < mergeTime.size(); k++){
                    if (!(mergeTime.get(k)[0] != mergeTime.get(k)[0])) {
                        int hStart = (int) mergeTime.get(k)[0];
                        int mStart = (int) ((mergeTime.get(k)[0] - hStart) * 60);
                        int hFinish = (int) mergeTime.get(k)[1];
                        int mFinish = (int) ((mergeTime.get(k)[1] - hFinish) * 60);
                        timeStr += String.format("%02d", hStart) + ":" + String.format("%02d", mStart) +
                                "-" + String.format("%02d", hFinish) + ":" + String.format("%02d", mFinish);
                        if (k != mergeTime.size() - 1) {
                            timeStr = timeStr + ",";
                        }
                    }
                }
                grafic[i][n] = timeStr;
            }
        }
    }

    //объединение временных промежутков (склейка)
    public static List<float[]>  merge(float[][] array){
        float[][] actual = sort(array, 0);
        List<float[]> merged = new ArrayList<float[]>();

        if (array.length == 0){
            return merged;
        }

        merged.add(actual[0]);
        for (int i = 0; i < (actual.length); i++){
            float previousEnd = merged.get(merged.size()-1)[1];
            float currentStart = actual[i][0];
            float currentEnd = actual[i][1];
            if (previousEnd >= currentStart){
                merged.get(merged.size()-1)[1] = Math.max(previousEnd, currentEnd);
            }
            else{
                merged.add(actual[i]);
            }
        }
        return merged;
    }

    //сортировка временных промежутков по возрастанию
    public static float[][] sort(float[][] array, int column) {
        if (array.length == 0){
            return array;
        }
        float[] temp = new float[array[0].length];
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 1; i < array.length; i++) {
                if (array[i][column] < array[i - 1][column]) {
                    temp = array[i - 1];
                    array[i - 1] = array[i];
                    array[i] = temp;
                    changed = true;
                }
            }
        }
        return array;
    }
}

