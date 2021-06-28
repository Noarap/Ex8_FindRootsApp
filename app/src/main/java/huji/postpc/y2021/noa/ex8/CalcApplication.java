package huji.postpc.y2021.noa.ex8;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

public class CalcApplication extends Application {

    Context context;
    SharedPreferences sp;
    public List<RootLogic> calculator;

    public CalcApplication(Context newContext) {
        this.context = newContext;
        this.sp = PreferenceManager.getDefaultSharedPreferences(this.context);
        loadData();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.calculator = new ArrayList<>();
    }

    public void loadData() {
        this.calculator = new ArrayList<>();
        String getSP = sp.getString("text", "");
        if (!getSP.equals("")) {
            Type listType = new TypeToken<ArrayList<RootLogic>>() {
            }.getType();
            calculator = new Gson().fromJson(getSP, listType);
        }
    }

    public void saveData(List<RootLogic> newCalc) {
        this.calculator = newCalc;
        String toSP = new Gson().toJson(this.calculator);
        sp.edit().putString("text", toSP).apply();
    }
}

