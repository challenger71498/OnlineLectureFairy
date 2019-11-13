package com.example.onlinelecturefairy.ui.onlinelecture;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.common.ColorPicker;
import com.example.onlinelecturefairy.onlinelecture.OnlineLecture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class OnlineLectureFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;
    OnlineLectureFragmentViewModel model;
    private ArrayList<OnlineLecture> lectures;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_lecture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(this).get(OnlineLectureFragmentViewModel.class);


        model.getLectures().observe(getActivity(), lectures -> {
            //UI updates.
            if(getView()!=null){
                RecyclerView recyclerView = getView().findViewById(R.id.onlineLectureRecycleView);
                OnlineLectureAdapter adapter = (OnlineLectureAdapter) recyclerView.getAdapter();
                if(adapter != null) {
                    adapter.setLectures(lectures);
                    adapter.notifyDataSetChanged();
                } else {
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    adapter = new OnlineLectureAdapter(lectures);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(manager);
                }
            }
        });

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.onlineLectureSwipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setProgressBackgroundColorSchemeColor(Color.BLACK);
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        //Crawling
        CrawlingBlackBoardWeb crw = new CrawlingBlackBoardWeb();
        crw.execute();
    }

    @Override
    public void onRefresh() {
        //Crawling
        CrawlingBlackBoardWeb crw = new CrawlingBlackBoardWeb();
        crw.execute();
    }

    /**
     * 웹강 요정의 화룡점정!!
     */
    private class CrawlingBlackBoardWeb extends AsyncTask<Void, Void, Void> {
        ArrayList<OnlineLecture> lecturesWaiting = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Set refresh ui to true.
            if(!swipe.isRefreshing()){
                swipe.setRefreshing(true);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipe.setRefreshing(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";

                //TODO: 개발 완료 후 defValue 수정
                SharedPreferences appData = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String blackboard_user_id = appData.getString("ID", "12181637");
                String blackboard_user_password = appData.getString("PW","!dlstjd1105");
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
                Map<String,String> loginCookie = res.cookies();
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
                if(lectures == null){
                    lectures = new ArrayList<>();
                }
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
                    String date="";
                    String pass;

                    String lecture_header="";
                    String week_header="";
                    String date_header="";
                    String pass_header="";

                    child = new ArrayList<>();
                    for (Element e : elem) {
                        date="";
                        if (e.text().contains("XIN")) {
                            strStartDate = (e.text().split(" / ")[1]).split(" ~ ")[0];
                            strEndDate = (e.text().split(" / ")[1]).split(" ~ ")[1];

                            date+= (strEndDate.split(" ")[0]).split("-")[0] + "년 "
                                    +(strEndDate.split(" ")[0]).split("-")[1] + "월 "
                                    +(strEndDate.split(" ")[0]).split("-")[2]+" 일";

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


                                lecture_header = (course.select("a.comboLink").text());
                                week_header = webLectureName;
                                date_header = date;
                                pass_header = Character.toString(e.text().charAt(e.text().length()-1));

                                lecture = (course.select("a.comboLink").text());
                                lecture = lecture.replaceFirst(" ", "");
                                Log.e(TAG, "lecture :" + lecture+"@"+date);
                                week = (e.text().split("XIN -")[1]).split("/")[0];
                                pass = Character.toString(e.text().charAt(e.text().length()-1));
                                child.add(new OnlineLecture(lecture,week,date,pass, OnlineLectureAdapter.CHILD));
                                //lectureInfo += ((course.select("a.comboLink").text()).split("\\)")[1]).split("-")[0] + "@" + webLectureName + "@" + simpledateformat.format(webEndDate.getTime()) + "\n";
                            }
                            else if((today.compareTo(webStartDate) == 1)){
                                lecture = (course.select("a.comboLink").text());
                                lecture = lecture.replaceFirst(" ", "");
                                Log.e(TAG, "lecture :" + lecture);
                                week = (e.text().split("XIN - ")[1]).split("/")[0];
                                pass = Character.toString(e.text().charAt(e.text().length()-1));
                                child.add(new OnlineLecture(lecture,week,date,pass, OnlineLectureAdapter.CHILD));
                            }
                            else{
                                break;
                            }
                        }
                    }
                    lecturesWaiting.add(new OnlineLecture(lecture_header,week_header,date_header,pass_header,OnlineLectureAdapter.HEADER,child));
                    ColorPicker.addLectureId(lecture_header);

                    //Should use postValue because it is on background thread.
                    model.postLectures(lecturesWaiting);
                }
            } catch (IOException o) {
                o.printStackTrace();
            }
            return null;
        }

    }
}
