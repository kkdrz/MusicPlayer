package pl.edu.pwr.drozd.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.ViewGroup;

import com.enrique.stackblur.StackBlurManager;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

class Utils {

    static final ArrayList<String> albumCovers = new ArrayList<>(
            Arrays.asList(
                    "http://glamrap.pl/images/stories/g2/okladka_styl_zycia_gnoja.jpg",
                    "http://www.klipon.pl/pliki/drugi_poczatek_kl.jpg",
                    "http://t-eska.cdn.smcloud.net/common/8/0/s/801414DWKZ.jpg/ru-0-r-650,0-n-801414DWKZ_tede.jpg",
                    "http://www.rapfundament.pl/wp-content/uploads/2013/10/potwierdzone-info-okladka.jpg",
                    "http://i1.pudelekx.pl/b9ed5d24752556634089fe9f2a3af001e1a3b6a4/12145160_409635802568055_1611935362_n-jpg",
                    "http://s.bravo.pl/download/multimedia/pictures/1002/72246020321361151389759.jpg",
                    "https://www.smashingmagazine.com/images/music-cd-covers/43.jpg",
                    "https://le0pard13.files.wordpress.com/2013/08/pink-floyd-dark-side-of-the-moon-album-cover.jpg",
                    "http://6uh9u7hhy8-flywheel.netdna-ssl.com/wp-content/uploads/2016/03/beyonce-formation-tracklist-1.jpg"));

    private static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    static class BackgroundImageSetterTask extends AsyncTask<String, Void, Bitmap> {
        ViewGroup viewGroup;
        Context context;

        BackgroundImageSetterTask(ViewGroup view, Context context) {
            viewGroup = view;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bm) {
            StackBlurManager blurManager = new StackBlurManager(bm);
            blurManager.process(20);
            viewGroup.setBackground(new BitmapDrawable(context.getResources(), blurManager.returnBlurredImage()));

        }
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        return finalTimerString + minutes + ":" + secondsString;
    }

    static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        return percentage.intValue();
    }

    static int progressToTimer(int progress, int totalDuration) {
        int currentDuration;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }
}
