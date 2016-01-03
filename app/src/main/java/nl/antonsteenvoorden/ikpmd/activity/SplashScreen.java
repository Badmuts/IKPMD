package nl.antonsteenvoorden.ikpmd.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import nl.antonsteenvoorden.ikpmd.App;
import nl.antonsteenvoorden.ikpmd.R;
import nl.antonsteenvoorden.ikpmd.database.DatabaseHelper;
import nl.antonsteenvoorden.ikpmd.database.DatabaseInfo;
import nl.antonsteenvoorden.ikpmd.model.Module;
import nl.antonsteenvoorden.ikpmd.model.Modules;

public class SplashScreen extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String PREFS_NAME = "LaunchPreferences";
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = getSharedPreferences(PREFS_NAME, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        TextView welkom = (TextView) findViewById(R.id.splashScreenWelcome);

        if (settings.getBoolean("first_run", true)) {
            welkom.setText("");
        } else {
            welkom.setText("Welkom terug " + String.valueOf(settings.getString("name", ""))+ " !");
        }

        handleAfterSplash();
    }

    private void handleAfterSplash() {


        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {

                // This method will be executed once the timer is over
                if (settings.getBoolean("first_run", true)) {
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(getBaseContext());
                    dbHelper.insertFromJson(((App) getApplication()).getModuleService().findAll(successListener(), errorListener()));

                    //the app is being launched for first time, do something
                    Log.d("Comments", "First time, opening get to know you screen");
                    Intent i = new Intent(SplashScreen.this, WhoAreYouActivity.class);
                    startActivity(i);
                    // record the fact that the app has been started at least once
                    //settings.edit().putBoolean("first_run", false).commit();
                } else {
                    Log.d("Comments", "Opening main activity");
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }

        }, SPLASH_TIME_OUT);
    }

    private Response.Listener<List<Module>> successListener() {
        return new Response.Listener<List<Module>>() {
            @Override
            public void onResponse(List<Module> modules) {
                ActiveAndroid.beginTransaction();
                try {
                    for (Module module: modules) {
                        nl.antonsteenvoorden.ikpmd.orm.Module dbModule =
                                new nl.antonsteenvoorden.ikpmd.orm.Module();
                        dbModule.setName(module.getName());
                        dbModule.setEcts(module.getEcts());
                        dbModule.setGrade(module.getGrade());
                        dbModule.setPeriod(module.getPeriod());
                        dbModule.save();
                        Log.d("Volley", module.toString());
                    }
                    ActiveAndroid.setTransactionSuccessful();
                } finally {
                    ActiveAndroid.endTransaction();
                }
            }
        };
    }

    private Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Snackbar.make(getCurrentFocus(), "Kan modules niet ophalen", Snackbar.LENGTH_LONG).show();
                Log.e("Volley error", error.getMessage());
            }
        };
    }
}
