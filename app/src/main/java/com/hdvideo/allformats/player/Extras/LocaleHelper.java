package com.hdvideo.allformats.player.Extras;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleHelper {
    public static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language.Notebook";
    static Locale myLocale;
    static String currentLang;

    public static void setLocale(Context context, String localeName, String activityName) {
        persist(context, localeName);
        myLocale = new Locale(localeName);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        if (activityName.equals("sp")) {
				/*Intent refresh = new Intent(context, OnboardingActivity.class);
				refresh.putExtra(currentLang, localeName);
				context.startActivity(refresh);*/
        } else {
				/*Intent refresh = new Intent(context, DashboardActivity.class);
				refresh.putExtra(currentLang, localeName);
				context.startActivity(refresh);*/
        }
    }

    private static void persist(Context context, String language) {
        SharePreferences sharePreferences = new SharePreferences(context);
        sharePreferences.putString(SELECTED_LANGUAGE, language);
    }

    // the method is used update the language of application by creating
    // object of inbuilt Locale class and passing language argument to it
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }


    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
