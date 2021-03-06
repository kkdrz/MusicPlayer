package pl.edu.pwr.drozd.musicplayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.enrique.stackblur.StackBlurManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

class Utils {

    static final ArrayList<Integer> albumCovers = new ArrayList<>(
            Arrays.asList(
                    R.drawable.cover1,
                    R.drawable.cover2,
                    R.drawable.cover3,
                    R.drawable.cover4,
                    R.drawable.cover5,
                    R.drawable.cover6,
                    R.drawable.cover7,
                    R.drawable.cover8));

    static int displayRandomAlbumCover(Context context, ImageView imageView) {
        int random = ThreadLocalRandom.current().nextInt(0, Utils.albumCovers.size()-1);
        Glide.with(context)
                .load(Utils.albumCovers.get(random))
                .into(imageView);
        return Utils.albumCovers.get(random);
    }

    static void setBlurryBackground(int drawable, ViewGroup viewGroup, Context context) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawable);
        StackBlurManager blurManager = new StackBlurManager(bm);
        blurManager.process(20);
        viewGroup.setBackground(new BitmapDrawable(context.getResources(), blurManager.returnBlurredImage()));
    }

    static String milliSecondsToTimer(long milliseconds) {
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
