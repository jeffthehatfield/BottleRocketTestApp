package hatfield.jeff.bottlerockettestapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

public class Animations {

    public static ObjectAnimator fadeInAnimation(View view, int animDuration, int delay) {
        return fadeInAnimation(view, 1, animDuration, delay);
    }

    public static ObjectAnimator fadeInAnimation(View view, float percent, int animDuration, int delay){
        ObjectAnimator fadeOutAnim = ObjectAnimator.ofFloat(view, "alpha", percent);
        fadeOutAnim.setDuration(animDuration);
        fadeOutAnim.setStartDelay(delay);
        return fadeOutAnim;
    }

    public static ObjectAnimator fadeOutAnimation(View view, int animDuration, int delay) {
        return fadeOutAnimation(view, 0, animDuration, delay);
    }

    public static ObjectAnimator fadeOutAnimation(View view, float percent, int animDuration, int delay) {
        ObjectAnimator fadeOutAnim = ObjectAnimator.ofFloat(view, "alpha", percent);
        fadeOutAnim.setDuration(animDuration);
        fadeOutAnim.setStartDelay(delay);
        return fadeOutAnim;
    }

    public static AnimatorSet crossFadeAnimation(View viewIn, View viewOut, int animDuration, int delay){
        ObjectAnimator fadeIn = fadeInAnimation(viewIn, animDuration, delay);
        ObjectAnimator fadeOut = fadeOutAnimation(viewOut, animDuration, delay);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeIn, fadeOut);
        return animatorSet;
    }
}
