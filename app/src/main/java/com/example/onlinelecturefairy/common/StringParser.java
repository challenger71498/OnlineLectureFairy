package com.example.onlinelecturefairy.common;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.function.BooleanSupplier;

import kr.co.shineware.nlp.komoran.model.KomoranResult;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class StringParser {
    public static ArrayList<Date> findDateAtString(KomoranResult result, int start) {
        ArrayList<Date> dates = new ArrayList<>();
        List<String> nouns = result.getMorphesByTags("NNG", "NNB", "SN", "SP");

        for (int i = start; i < nouns.size(); ++i) {
            Integer day = null;
            Integer month = null;
            String dayTemp = "";
            String monthTemp = "";

            if (nouns.get(i).equals("일")) {  // 4월 17일
                try {
                    dayTemp = nouns.get(i - 1);
                    int maybeMonth = Integer.parseInt(nouns.get(i - 3));  // 보통 x월 x일이므로
                    if (0 <= maybeMonth && maybeMonth <= 12 && nouns.get(i - 2).equals("월")) {
                        monthTemp = nouns.get(i - 3);
                    } else {  //보통의 경우가 아닌 경우
                        for (int j = i - 2; j >= 0; j--) {
                            if (nouns.get(j).equals("일")) {  // 일 다음에 일이 왔으므로 일정이 아님.
                                break;
                            } else if (nouns.get(j).equals("월")) { // 월 다음에 일이 왔으므로 일정
                                monthTemp = nouns.get(j - 1);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (nouns.get(i).equals("/")) {  // 4/17
                try {
                    dayTemp = nouns.get(i + 1);
                    monthTemp = nouns.get(i - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                day = Integer.parseInt(dayTemp);
                month = Integer.parseInt(monthTemp);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (day != null && month != null) {  //올바른 포맷일 경우
                Date date = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), month - 1, day).getTime();
                dates.add(date);
                ArrayList<Date> findMore = findDateAtString(result, i + 1);
                if (findMore != null) {
                    dates.addAll(findMore);
                }

                return dates;   //하나 찾고 그 다음 인덱스부터 재귀를 돌린다.
            }
        }
        return dates;
    }

    public static List<String> findTestAtString(KomoranResult result, int start) {
        HashSet<String> out = new HashSet<>();

        BooleanSupplier checkIfFree = () -> {
            List<String> nouns = result.getMorphesByTags( "NNP", "NNG", "NNB", "SL");
            for(int i =  start; i < nouns.size(); ++i) {
                if(nouns.get(i).equals("휴")) {
                    try {
                        if(nouns.get(i+1).equals("강")) {
                            out.add("휴강");
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
            return false;
        };

        List<String> nouns = result.getMorphesByTags("NNG", "NNB", "SL");

        if(checkIfFree.getAsBoolean()) {
            return new ArrayList<>(out);
        }

        for (int i = start; i < nouns.size(); ++i) {
            if (nouns.get(i).equals("중간") ||
                    nouns.get(i).equals("중간고사") ||
                    nouns.get(i).equals("midterm")) {  //  중간일 경우
                if(!checkIfFree.getAsBoolean()) {
                    out.add("중간");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("기말") ||
                    nouns.get(i).equals("기말고사") ||
                    nouns.get(i).equals("final")) {  //  기말일 경우
                if(!checkIfFree.getAsBoolean()) {
                    out.add("기말");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("성적") ||
                    nouns.get(i).equals("점수")) {     // 성적일 경우
                if(!checkIfFree.getAsBoolean()) {
                    out.add("성적");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("보고서")) {  // 보고서일 경우
                if(!checkIfFree.getAsBoolean()) {
                    out.add("보고서");
                } else {
                    break;
                }
            }
        }

        return new ArrayList<>(out);
    }
}
