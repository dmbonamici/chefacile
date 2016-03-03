package it.chefacile.app;


import android.util.Log;

public class SentenceGenerator {

    public static String generateTip(){
        int n = (int) (Math.random() * 10);
        Log.d("INT GEN", String.valueOf(n));
        switch (n){
            case 0: return "Hunger is a good cook";
            case 1: return "Food is essential to life: therefore make it good";
            case 2: return "Cooking well doesn't mean cooking fancy";
            case 3: return "Always kiss the cook";
            case 4: return "You only live once: lick the bowl";
            case 5: return "A messy kitchen is a sign of happiness";
            case 6: return "Cooking is love made edible";
            case 7: return "Good cooks never lack friends";
            case 8: return "With enough butter anything is good";
            case 9: return "A clean kitchen is a sign of a wasted life";
            case 10: return "Good food is good mood";
            default: return "I cook with wine: sometimes I even add it to the food";
        }
    }
}