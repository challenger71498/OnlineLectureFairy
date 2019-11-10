package com.example.onlinelecturefairy.ui.notice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinelecturefairy.GoogleCalendarSyncTest;
import com.example.onlinelecturefairy.R;
import com.example.onlinelecturefairy.notice.Notice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

public class NoticeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipe;
    NoticesViewModel model;
    private ArrayList<Notice> arrNotice;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(this).get(NoticesViewModel.class);

        CrawlingBlackBoard crw =  new CrawlingBlackBoard();
        crw.execute();

//        Log.e(TAG, "arrNotice size : " + arrNotice.size());

        //당겨서 새로고침
        swipe = getView().findViewById(R.id.swipeRefresh);
        swipe.setOnRefreshListener(this);
        //Color changes by each color.
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    @Override
    public void onRefresh() {

        CrawlingBlackBoard crw =  new CrawlingBlackBoard();
        crw.execute();
        //Should stop refreshing.
        swipe.setRefreshing(false);
    }

    /**
     * crawling blackboard url
     */
    ProgressDialog progressDialog;
    private class CrawlingBlackBoard extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //model.setNotices(arrNotice);
            model.setNotices(arrNotice);
            model.getNotices().observe(getActivity(), notices -> {
                //UI updates.
                RecyclerView recyclerView = getView().findViewById(R.id.homeRecyclerView);
                NoticeRecyclerAdapter adapter = (NoticeRecyclerAdapter) recyclerView.getAdapter();
                if(adapter != null) {
                    adapter.setNotices(notices);
                } else {
                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                    adapter = new NoticeRecyclerAdapter(notices);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(manager);
                }
                Log.e(TAG, "Adapter size : " + adapter.getItemCount());
            });

            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.36";

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
                Document blackBoard = Jsoup.connect("https://learn.inha.ac.kr/webapps/portal/execute/tabs/tabAction")
                        .userAgent(userAgent)
                        .header("Origin", "https://learn.inha.ac.kr")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .cookies(loginCookie) // 위에서 얻은 '로그인 된' 쿠키
                        .data("action", "refreshAjaxModule")
                        .data("modId", "_1_1")
                        .data("tabId", "_1_1")
                        .data("tab_tab_group_id", "_1_1")
                        .parser(Parser.xmlParser())
                        .post();
                String temp1 = "";
                Element contest = blackBoard.select("contents").first();
                Document doc = Jsoup.parse(contest.text().split("<!-- Display course/org announcements -->")[1]);
                int idx = 0;
                int numOfSub;
                String[] blackboard_subject = new String[10];
                String[] blackboard_noticeTitle = new String[10];
                String[] blackboard_noticeLink = new String[10];
                //selecting subject name

                Elements elem = doc.select("h3");
                for (Element e : elem) {
                    blackboard_subject[idx] = e.text().split("\\)")[1];
                    idx++;
                }
                numOfSub = idx;

                //selecting notice of each subject
                String[] temp2 = doc.html().split("</div>");
                for (int i = 0; i < temp2.length; i++) {
                    doc = Jsoup.parse(temp2[i]);
                    elem = doc.select("li");
                    blackboard_noticeTitle[i] = "";
                    for (Element e : elem) {
                        blackboard_noticeTitle[i] += e.text();
                        blackboard_noticeTitle[i] += "\n";
                    }

                    blackboard_noticeLink[i] = "";
                    elem = doc.select("a");
                    for (Element e : elem) {
                        blackboard_noticeLink[i] = e.attr("href");
                    }
                }

                String[] arrTitle;
                arrNotice = new ArrayList<>();
                String lecture;
                String title;
                String calendar;
                String description;
                for(int i=0;i<numOfSub;i++){
                    arrTitle = blackboard_noticeTitle[i].split("\n");
                    Document course = Jsoup.connect("https://learn.inha.ac.kr/"+blackboard_noticeLink[i])
                            .userAgent(userAgent)
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
                            .cookies(loginCookie)
                            .get();
                    Document docNotice = Jsoup.parse(course.select("ul#announcementList").html());

                    for(String s:arrTitle){
                        elem = docNotice.select("li");
                        for(Element e:elem){
                            if(e.text().contains(s)){
                                title = e.text().split("게시 날짜:")[0];
                                calendar = (e.text().split("게시 날짜:")[1]).split("KST")[0];
                                lecture = e.text().split("게시한 곳:")[1];
                                description = (e.text().split("KST")[2]).split("작성자:")[0];
                                arrNotice.add(new Notice(lecture,title,calendar,description));
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