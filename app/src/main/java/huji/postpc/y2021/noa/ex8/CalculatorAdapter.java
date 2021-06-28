package huji.postpc.y2021.noa.ex8;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.ViewHolder> {
    public CalculatorHolder calculatorHolder;
    public WorkManager workManager;
    public CalcApplication calcApplication;

    public CalculatorAdapter(CalculatorHolder calculatorHolder, WorkManager workManager,
                             CalcApplication calcApplication)
    {
        this.calculatorHolder = calculatorHolder;
        this.workManager = workManager;
        this.calcApplication = calcApplication;
    }

    @NonNull
    @NotNull
    @Override
    public CalculatorAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View rowView = layoutInflater.inflate(R.layout.single_row, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CalculatorAdapter.ViewHolder holder, int position) {
        RootLogic rootLogic = this.calculatorHolder.calculator.get(holder.getLayoutPosition());
        holder.calcTextView.setText(holder.adaptTextToState(rootLogic));
        holder.delButton.setOnClickListener(v->{
            if (rootLogic.state.equals("inProgress"))
            {
                workManager.cancelWorkById(UUID.fromString(rootLogic.worker));
            }

            this.calculatorHolder.removeCalculation(rootLogic);
            this.calcApplication.saveData(this.calculatorHolder.calculator);
            notifyItemRangeRemoved(holder.getLayoutPosition(), 1);
        });

        switch (rootLogic.state) {
            case "inProgress":
                holder.progressBar.setProgress(rootLogic.progress);
                holder.calcTextView.setText(holder.adaptTextToState(rootLogic));
                break;
            default:
                holder.progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.calculatorHolder.calculator.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

       public TextView calcTextView;
       public Button delButton;
       public ProgressBar progressBar;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.calcTextView = itemView.findViewById(R.id.rowNum);
            this.delButton = itemView.findViewById(R.id.buttonDel);
            this.progressBar = itemView.findViewById(R.id.progBar);
        }

        public String adaptTextToState(RootLogic rootLogic) {
            String str;
            switch (rootLogic.state)
            {
                case "inProgress":
                    str = "num: " + rootLogic.num + " progress: " + rootLogic.progress;
                    break;
                case "rootDone":
                    str = "num: " + rootLogic.num + " results: " + rootLogic.firstRoot + ", " + rootLogic.secondRoot;
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

        public void finishCalc(RootLogic rootLogic)
        {
            this.calcTextView.setText(adaptTextToState(rootLogic));
            this.progressBar.setVisibility(View.GONE);
        }

    }
}
