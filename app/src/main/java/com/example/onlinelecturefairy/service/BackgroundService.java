package com.example.onlinelecturefairy.service;

import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.example.onlinelecturefairy.LoginActivity;
import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.AsyncTaskCallBack;
import com.example.onlinelecturefairy.common.BlackboardInfoCheckBackground;
import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;
import com.example.onlinelecturefairy.ui.onlinelecture.OnlineLectureAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class BackgroundService extends JobService {
    ArrayList<OnlineLecture> lecturesOfWeek;
    int temp;
    Boolean crawlingIsDone = false;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        BackgroundTask task = new BackgroundTask(jobParameters);
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;    //Set true to re-schedule.
    }

    class BackgroundTask extends AsyncTask<Void, Void, Void> {
        JobParameters params;
        Boolean isInfoCorrect;
        Boolean readyToCrawling = false;

        String id;
        String pw;

        Boolean isDone = false;

        BackgroundTask(JobParameters params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Blackboard ID PW validity check
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            id = pref.getString("ID", "");
            pw = pref.getString("PW", "");

            BlackboardInfoCheckBackground.CheckBlackBoard board
                    = new BlackboardInfoCheckBackground.CheckBlackBoard(getApplicationContext(), id, pw, isInfoCorrect, new AsyncTaskCallBack() {
                @Override
                public void onSuccess() {
                    // 로그인에 성공했을 때의 작업 작성.
                    Log.e(TAG, "done");
                    isInfoCorrect = true;
                    isDone = true;
                }

                @Override
                public void onFailure() {

                    // 로그인에 실패했을 때의 작업 작성.
                    Log.e(TAG, "failed");
                    isInfoCorrect = false;
                    isDone = true;
                }
            });
            board.execute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CrawlingBlackBoardWeb crw = new CrawlingBlackBoardWeb();
            crw.execute();
            jobFinished(params, true);   // true로 놓아야 계속 job을 돌림
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //여기에 함수를 실행.
            // TODO: 10초 이상 빠져나가지 않는 경우 오류 내뿜기
            while (!isDone) {

            }
            // TODO: 에브리타임 URL이 적절한지 check
            if (isInfoCorrect) {
                Intent intent = new Intent("my-event");
                intent.putExtra("message", "hello!");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                readyToCrawling = true;
            }

//            //Background
//            MainActivity activity = (MainActivity) MainActivity.context;
//            MainActivity.mID = 2;
//            Log.e(TAG, "doInBackground: " + activity.getResultsFromApi());

            return null;
        }
    }

    private class CrawlingBlackBoardWeb extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (lecturesOfWeek == null) {
                lecturesOfWeek = new ArrayList<>();
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            temp = 0;
            String noneList = "";
            Log.e(TAG, "numberOfLecture: " + lecturesOfWeek.size());
            //이미 들은 웹강 counting
            for (OnlineLecture lecture : lecturesOfWeek) {
                if (lecture.getPass().matches(".*P.*")) {
                    temp++;
                } else {
                    noneList += lecture.getLecture() + ": " + lecture.getWeek() + "\n";
                }
            }


            //TODO: 일요일 오후 1시가 지났으면 하도록 설정해놓음.
            java.util.Calendar calendar1 = java.util.Calendar.getInstance();
            java.util.Calendar calendar2 = java.util.Calendar.getInstance();

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String timeString = pref.getString("notificationCheck", "0");
            Log.e(TAG, "onPostExecute: " + timeString);

            calendar2.set(Calendar.HOUR_OF_DAY, 13);
            calendar2.set(Calendar.MINUTE, 0);

            //오늘이 일요일이고, 일요일 오후 1시가 지났다면,
            if (temp != lecturesOfWeek.size() && calendar1.compareTo(calendar2) == 1 && calendar1.get(Calendar.DAY_OF_WEEK) == 1) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "0417")
                        .setSmallIcon(R.drawable.web_fairy_short)
                        .setContentTitle("이번 주에 듣지 않은 웹강이 " + Integer.toString(lecturesOfWeek.size() - temp) + "개 있습니다.")
                        .setContentText("아래로 스크롤하여 듣지 않은 웹강을 확인하세요.")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(noneList))
                        .setContentIntent(pendingIntent)
                        .setChannelId("0417")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setGroup("WEB_FAIRY")
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(417, builder.build());
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";

                //TODO: 개발 완료 후 defValue 수정
                SharedPreferences appData = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String blackboard_user_id = appData.getString("ID", "12181637");
                String blackboard_user_password = appData.getString("PW", "!dlstjd1105");
                Connection.Response loginPageResponse = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .data("user_id", blackboard_user_id, "password", blackboard_user_password)
                        .method(Connection.Method.POST)
                        .execute();
                Map<String, String> loginTryCookie = loginPageResponse.cookies();

                Map<String, String> userData = new HashMap<>();
                userData.put("user_id", blackboard_user_id);
                userData.put("password", blackboard_user_password);

                Connection.Response res = Jsoup.connect("https://learn.inha.ac.kr/webapps/login/")
                        .userAgent(userAgent)
                        .timeout(3000)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginTryCookie)
                        .data(userData)
                        .method(Connection.Method.POST)
                        .execute();
                Map<String, String> loginCookie = res.cookies();
                if (loginCookie.isEmpty()) {
                    return null;
                }
                Document blackboard = Jsoup.connect("https://learn.inha.ac.kr/webapps/portal/execute/tabs/tabAction")
                        .userAgent(userAgent)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                        .data("action", "refreshAjaxModule")
                        .data("modId", "_3_1")
                        .data("tabId", "_1_1")
                        .data("tab_tab_group_id", "_1_1")
                        .parser(Parser.xmlParser())
                        .post();
                Element contest = blackboard.select("contents").first();
                Document doc = Jsoup.parse(contest.text());

                //현재가 무슨학기인지 알아내자.
                String current_semester = "";
                java.util.Calendar cal = java.util.Calendar.getInstance();
                current_semester += cal.get(java.util.Calendar.YEAR);
                if (cal.get(java.util.Calendar.MONTH) >= 8) {
                    current_semester += "02";
                } else {
                    current_semester += "01";
                }

                //현재 수강중인 과목들의 course_id를 파싱
                Elements elem = doc.select("a");
                int numOfCourse = 0;
                //수강하는 과목의 수가 최대 100개라고 가정함.
                String[] course_id = new String[100];
                for (Element e : elem) {
                    String temp = e.text();
                    if (temp.contains(":") && temp.contains(current_semester)) {
                        temp = e.attr("href");
                        temp = temp.split("_")[1];
                        course_id[numOfCourse] = temp;
                        numOfCourse++;
                    }
                }

                int numOfWeb_temp = 0;
                int numOfWeb = 0;
                String[] course_id_web_temp = new String[100];
                String[] course_id_web = new String[100];
                // "온라인 출결 확인"이 존재하는 과목 parsing
                for (int i = 0; i < numOfCourse; i++) {
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/blackboard/execute/modulepage/view?course_id=_" + course_id[i] + "_1")
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("span");
                    for (Element e : elem) {
                        if (e.text().contains("온라인 출결 확인")) {
                            course_id_web_temp[numOfWeb_temp] = course_id[i];
                            numOfWeb_temp++;
                        }
                    }
                }

                // "온라인 출결 확인" 메뉴만 존재하고 출결이 없는 과목 거르기
                for (int i = 0; i < numOfWeb_temp; i++) {
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/bbgs-OnlineAttendance-BBLEARN/app/atdView?course_id=_" + course_id_web_temp[i] + "_1")
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("div");
                    boolean isCourseWeb = true;
                    for (Element e : elem) {
                        if (!(e.attr("class").contains("noItems"))) {
                            isCourseWeb = true;
                        } else {
                            isCourseWeb = false;
                        }
                    }
                    if (isCourseWeb) {
                        course_id_web[numOfWeb] = course_id_web_temp[i];
                        numOfWeb++;
                    }
                }


                String webLectureName = "";
                ArrayList<OnlineLecture> child;
                for (int i = 0; i < numOfWeb; i++) {
                    java.util.Calendar today = java.util.Calendar.getInstance();
                    java.util.Calendar webStartDate = java.util.Calendar.getInstance();
                    java.util.Calendar webEndDate = java.util.Calendar.getInstance();
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/webapps/bbgs-OnlineAttendance-BBLEARN/app/atdView?sortDir=ASCENDING&showAll=true&editPaging=false&course_id=_" + course_id_web[i] + "_1")
                            .userAgent(userAgent)
                            .cookies(loginCookie)
                            .get();
                    elem = course.select("tr");

                    int startMonth, startDay, startHour, startMinute;
                    String strStartDate;
                    int endMonth, endDay, endHour, endMinute;
                    String strEndDate;

                    String lecture;
                    String week;
                    String date = "";
                    String pass;

                    String lecture_header = "";
                    String week_header = "";
                    String date_header = "";
                    String pass_header = "";

                    child = new ArrayList<>();
                    for (Element e : elem) {
                        date = "";
                        if (e.text().contains("XIN")) {
                            strStartDate = (e.text().split(" / ")[1]).split(" ~ ")[0];
                            strEndDate = (e.text().split(" / ")[1]).split(" ~ ")[1];

                            date += (strEndDate.split(" ")[0]).split("-")[0] + "년 "
                                    + (strEndDate.split(" ")[0]).split("-")[1] + "월 "
                                    + (strEndDate.split(" ")[0]).split("-")[2] + " 일";

                            endMonth = Integer.parseInt((strEndDate.split(" ")[0]).split("-")[1]);
                            endDay = Integer.parseInt((strEndDate.split(" ")[0]).split("-")[2]);
                            endHour = Integer.parseInt((strEndDate.split(" ")[1]).split(":")[0]);
                            endMinute = Integer.parseInt((strEndDate.split(" ")[1]).split(":")[1]);
                            webEndDate.set(java.util.Calendar.MONTH, endMonth - 1);
                            webEndDate.set(java.util.Calendar.DAY_OF_MONTH, endDay);
                            webEndDate.set(java.util.Calendar.HOUR, endHour);
                            webEndDate.set(java.util.Calendar.MINUTE, endMinute);

                            startMonth = Integer.parseInt((strStartDate.split(" ")[0]).split("-")[1]);
                            startDay = Integer.parseInt((strStartDate.split(" ")[0]).split("-")[2]);
                            startHour = Integer.parseInt((strStartDate.split(" ")[1]).split(":")[0]);
                            startMinute = Integer.parseInt((strStartDate.split(" ")[1]).split(":")[1]);
                            webStartDate.set(java.util.Calendar.MONTH, startMonth - 1);
                            webStartDate.set(java.util.Calendar.DAY_OF_MONTH, startDay);
                            webStartDate.set(java.util.Calendar.HOUR, startHour);
                            webStartDate.set(java.util.Calendar.MINUTE, startMinute);

                            if (today.compareTo(webEndDate) == -1 && (today.compareTo(webStartDate) == 1)) {
//                                webEndDate.add(java.util.Calendar.DATE, +7);
//                                webEndDate.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
//                                webEndDate.add(java.util.Calendar.DATE, -7);
//                                SimpleDateFormat simpledateformat;
//                                simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+09:00", Locale.KOREA);

                                webLectureName = ((e.text().split(" > ")[0]).split("XIN")[0]).split(">")[1];
                                String match2 = "\\s{2,}";
                                webLectureName = webLectureName.replaceAll(match2, "");


                                lecture_header = (course.select("a.comboLink").text()).split("\\)")[1].split("-")[0];

                                date_header = date;
                                pass_header = Character.toString(e.text().charAt(e.text().length() - 1));

                                lecture = (course.select("a.comboLink").text());
                                lecture = lecture.replaceFirst(" ", "");
                                week = (e.text().split("XIN -")[1]).split("/")[0];
                                week = week.replaceFirst(" ", "");
                                webLectureName = webLectureName.replace(" ", "");
                                week_header = week;
                                lecture_header += " (" + webLectureName + ")";
                                pass = Character.toString(e.text().charAt(e.text().length() - 1));
                                lecturesOfWeek.add(new OnlineLecture(lecture_header, week_header, date_header, pass_header, OnlineLectureAdapter.HEADER));
                            } else if (today.compareTo(webStartDate) == -1) {
                                break;
                            }
                        }
                    }
                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }
}
