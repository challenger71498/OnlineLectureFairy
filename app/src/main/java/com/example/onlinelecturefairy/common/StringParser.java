package com.example.onlinelecturefairy.common;

import android.util.Log;

import com.example.onlinelecturefairy.grade.CommonGrade;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BooleanSupplier;

import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

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
            List<String> nouns = result.getMorphesByTags("NNP", "NNG", "NNB", "SL");
            for (int i = start; i < nouns.size(); ++i) {
                if (nouns.get(i).equals("휴")) {
                    try {
                        if (nouns.get(i + 1).equals("강")) {
                            out.add("휴강");
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (nouns.get(i).equals("공")) {
                    try {
                        if (nouns.get(i + 1).equals("강")) {
                            out.add("공강");
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

        if (checkIfFree.getAsBoolean()) {
            return new ArrayList<>(out);
        }

        for (int i = start; i < nouns.size(); ++i) {
            if (nouns.get(i).equals("중간") ||
                    nouns.get(i).equals("중간고사") ||
                    nouns.get(i).equals("midterm")) {  //  중간일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("중간");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("기말") ||
                    nouns.get(i).equals("기말고사") ||
                    nouns.get(i).equals("final")) {  //  기말일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("기말");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("성적") ||
                    nouns.get(i).equals("점수")) {     // 성적일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("성적");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("보고서")) {  // 보고서일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("보고서");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("보강")) {  // 보강일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("보강");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("퀴즈") ||
                    nouns.get(i).equals("Quiz")) {  // 퀴즈일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("퀴즈");
                } else {
                    break;
                }
            } else if (nouns.get(i).equals("과제")) {  // 과제일 경우
                if (!checkIfFree.getAsBoolean()) {
                    out.add("과제");
                } else {
                    break;
                }
            }
        }

        return new ArrayList<>(out);
    }

    public static List<String> findGradeAtString(KomoranResult result, String lecture, TreeSet<String> nouns) {
        ArrayList<Token> temp = new ArrayList<>(result.getTokenList());
        ArrayList<Token> tokens = new ArrayList<>();

        for (Token t : temp) {
            if (t.getPos().equals("NNG") ||
                    t.getPos().equals("NNB") ||
                    t.getPos().equals("SL") ||
                    t.getPos().equals("SN") ||
                    t.getPos().equals("SF") ||
                    t.getPos().equals("SO")) {
                tokens.add(t);
            }
        }

        HashSet<String> s = new HashSet<>();

        for (int i = 0; i < tokens.size(); ++i) {
            String tokenMorph = tokens.get(i).getMorph();
            if (tokenMorph.equals("평균") ||
                    tokenMorph.equals("표준편차") ||
                    tokenMorph.equals("편차") ||
                    tokenMorph.equals("분산") ||
                    tokenMorph.equals("중앙값") ||
                    tokenMorph.equals("중앙") ||
                    tokenMorph.equals("최고점") ||
                    tokenMorph.equals("최저점")) { // 통계 데이터를 가지고 있을 때
                String scoreRaw;
                float score = -1;

                boolean isCorrect = true;

                Log.e(TAG, "findGradeAtString: VAL : " + tokens.get(i + 1).getMorph() + tokens.get(i + 2).getMorph() + tokens.get(i + 3).getMorph());

                if (i < tokens.size() && tokens.get(i + 1).getPos().equals("SN")) {             //평균 뒤에 숫자가 있을 때    평균 3
                    if (i + 1 < tokens.size() && tokens.get(i + 2).getMorph().equals(".")) {        //평균 뒤에 점이 있을 때     평균 3.
                        if (i + 2 < tokens.size() && tokens.get(i + 3).getPos().equals("SN")) {          //점 뒤에 숫자가 있을 때     평균 3.14
                            if(i + 3 < tokens.size() && tokens.get(i + 4).getPos().equals("SO")) {      // 오류. 예) 30.5~30.8점.
                                isCorrect = false;
                            } else {
                                scoreRaw = tokens.get(i + 1).getMorph() +
                                        tokens.get(i + 2).getMorph() +
                                        tokens.get(i + 3).getMorph();
                                Log.e(TAG, "findGradeAtString: SCORE : " + scoreRaw);
                                score = Float.parseFloat(scoreRaw);
                            }
                        } else {  // 오류. 예) 과목 평균\n 3. 안내 사항
                            isCorrect = false;
                        }
                    } else if (i + 1 < tokens.size() && tokens.get(i + 2).getPos().equals("SO")) {  // 오류. 예) 평균은 약 30~35점 예상
                        isCorrect = false;
                    } else {
                        scoreRaw = tokens.get(i + 1).getMorph();
                        score = (float) Integer.parseInt(scoreRaw);
                    }
                }

                if (isCorrect) { //해당 성적을 과목에 추가.
                    s.add(tokenMorph + "_" + score);
                    if (tokenMorph.equals("평균") && score != -1 && lecture != null) {    //평균일 때는 값을 common grade에 추가.
                        if (nouns.contains("중간")) {
                            CommonGrade.setScore(lecture, "중간", score);
                        } else if (nouns.contains("기말")) {
                            CommonGrade.setScore(lecture, "기말", score);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(s);
    }
}
