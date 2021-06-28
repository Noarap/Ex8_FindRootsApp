package huji.postpc.y2021.noa.ex8;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    public Button calcButton;
    public EditText numEnterText;
    public RecyclerView recyclerView;
    public CalcApplication calcApplication;
    public CalculatorHolder calculatorHolder;
    public CalculatorAdapter calculatorAdapter;
    public Data.Builder dataBuilder;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = MainActivity.this;
        this.calcApplication = new CalcApplication(this);
        setContentView(R.layout.activity_main);

        this.calcButton = findViewById(R.id.button);
        this.numEnterText = findViewById(R.id.numberEntering);
        this.recyclerView = findViewById(R.id.recycler);

        this.calculatorHolder = new CalculatorHolder();
        this.calculatorHolder.calculator = this.calcApplication.calculator;
        if (this.context != null)
        {
            WorkManager workManager = WorkManager.getInstance(this.context);
            CalculatorAdapter newAdapter = new CalculatorAdapter(this.calculatorHolder, workManager, calcApplication);
            this.calculatorAdapter = newAdapter;
        }

        LinearLayoutManager llm = new LinearLayoutManager(this);
        DividerItemDecoration did = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        this.recyclerView.setAdapter(this.calculatorAdapter);
        this.recyclerView.setLayoutManager(llm);
        this.recyclerView.addItemDecoration(did);
        this.dataBuilder = new Data.Builder();

        calcButton.setOnClickListener(v ->{
            long num;
            try {
                num = Long.parseLong(this.numEnterText.getText().toString());
                if (!this.calculatorHolder.isExist(num))
                {
                    calculationsBegin(new RootLogic(num), true);
                }
            }
            catch (NumberFormatException numberFormatException)
            {
                Toast.makeText(this, "ERROR!!!", Toast.LENGTH_LONG).show();
            }
        });

        for (RootLogic rootLogic:this.calculatorHolder.calculator)
        {
            if (rootLogic.state.equals("inProgress"))
            {
                calculationsBegin(rootLogic, false);
            }
        }
    }

    private void calculationsBegin(RootLogic rootLogic, boolean b)
    {
        if(b)
        {
            this.calculatorHolder.insertCalculation(rootLogic);
            this.calcApplication.saveData(this.calculatorHolder.calculator);
            this.calculatorAdapter.notifyItemInserted(this.calculatorHolder.idxOfCalc(rootLogic));
        }

        this.dataBuilder.putLong("thisNum", rootLogic.thisNum);
        this.dataBuilder.putLong("num", rootLogic.num);
        this.dataBuilder.putInt("idKey", rootLogic.id);
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkManagerHandler.class).
                setInputData(this.dataBuilder.build()).build();
        WorkManager.getInstance(this).enqueue(otwr);
        rootLogic.worker = otwr.getId().toString();
        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(getApplicationContext()).
                getWorkInfoByIdLiveData(otwr.getId());
        workInfoLiveData.observeForever(w->{
            if (w != null)
            {
                WorkInfo.State state = w.getState();
                if (WorkInfo.State.SUCCEEDED == state)
                {
                    RootLogic rootLogic1 = calculatorHolder.getCalculation(w.getOutputData().getInt("idKey", -1));
                    if (rootLogic1 != null)
                    {
                        rootLogic1.firstRoot = w.getOutputData().getLong("firstRoot", 0);
                        rootLogic1.secondRoot = w.getOutputData().getLong("secondRoot", 0);
                        calculatorHolder.finishCalc(rootLogic1, "rootDone");
                        this.calcApplication.saveData(this.calculatorHolder.calculator);
                        this.calculatorAdapter.notifyDataSetChanged();
                        CalculatorAdapter.ViewHolder viewHolder = (CalculatorAdapter.ViewHolder)
                                recyclerView.findViewHolderForLayoutPosition(this.calculatorHolder.idxOfCalc(rootLogic1));
                        if (viewHolder != null)
                        {
                            viewHolder.finishCalc(rootLogic1);
                        }
                    }
                }

                else if (WorkInfo.State.FAILED == state)
                {
                    Data data = w.getOutputData();

                    if (isFailed(data))
                    {
                        rootLogic.thisNum = data.getLong("thisNum", 2);
                        rootLogic.progress = data.getInt("prog", 0);
                        this.calculatorHolder.calculator.set(calculatorHolder.idxOfCalc(rootLogic), rootLogic);
                        progHandel(w.getId().toString(), data.getInt("prog", 0));
                        this.calcApplication.saveData(this.calculatorHolder.calculator);
                        calculationsBegin(rootLogic, false);
                    }
                }
            }
        });

    }


    private boolean isFailed(Data data)
    {
        if (data.getBoolean("calc", true))
        {
            return true;
        }
        RootLogic rootLogic = this.calculatorHolder.getCalculation(data.getInt("idKey", -1));
        rootLogic.state = "primeDone";
        this.calculatorAdapter.notifyDataSetChanged();
        CalculatorAdapter.ViewHolder viewHolder = (CalculatorAdapter.ViewHolder)recyclerView.findViewHolderForLayoutPosition(this.calculatorHolder.idxOfCalc(rootLogic));
        if (viewHolder != null)
        {
            viewHolder.finishCalc(rootLogic);
        }
        this.calculatorHolder.finishCalc(rootLogic, "primeDone");
        this.calcApplication.saveData(this.calculatorHolder.calculator);
        return false;
    }

    private void progHandel(String wId, int prog) {
        for (int i = 0; i < this.calculatorHolder.calculator.size(); i++)
        {
            RootLogic rl = calculatorHolder.calculator.get(i);
            if (rl.worker.equals(wId))
            {
                rl.progress = prog;
                CalculatorAdapter.ViewHolder vh = (CalculatorAdapter.ViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
                if (vh != null)
                {
                    if (prog <= 1)
                    {
                        vh.progressBar.setProgress(0);
                    }
                    else if(prog >= 99)
                    {
                        vh.progressBar.setProgress(100);
                    }

                    vh.progressBar.setProgress(prog);
                    vh.calcTextView.setText(adaptTextToState(rl));
                }
            }
        }
    }

    public String adaptTextToState(RootLogic rootLogic) {
        String str;
        switch (rootLogic.state)
        {
            case "inProgress":
                str = "num: " + rootLogic.num + " progress: " + rootLogic.progress;
                break;
            case "rootDone":
                str = "num: " + rootLogic.num + " results: " + rootLogic.firstRoot
                        + ", " + rootLogic.secondRoot;
                break;
            case "primeDone":
                str = "The number " + rootLogic.num + " is prime!";
                break;
            default:
                str = "Something went wrong :(";
                break;
        }
        return str;
    }
}
