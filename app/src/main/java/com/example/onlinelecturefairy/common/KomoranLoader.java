package com.example.onlinelecturefairy.common;

import android.os.AsyncTask;
import android.util.Log;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;

// A KOMORAN loader.
public class KomoranLoader {
    public static Komoran komoran;

    public static class Loader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            komoran = new Komoran(DEFAULT_MODEL.LIGHT);
            Log.e("", "KOMORAN load complete!");
            return null;
        }
    }
}
