package com.example.onlinelecturefairy.common;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class StringParser {
    public static HashMap<Token, String> wordMap = new HashMap<>();

    public static ArrayList<Date> findDateAtString(KomoranResult result, int start) {
        ArrayList<Date> dates = new ArrayList<>();
        List<String> nouns = result.getMorphesByTags("NNG", "NNB", "SN", "SP");
        Log.e(TAG, TextUtils.join(" ", nouns));

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
}
