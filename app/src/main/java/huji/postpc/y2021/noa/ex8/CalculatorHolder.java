package huji.postpc.y2021.noa.ex8;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalculatorHolder extends Activity {
    public List<RootLogic> calculator;

    public CalculatorHolder()
    {
        this.calculator = new ArrayList<>();
    }

    private void sortCalculator()
    {
        Collections.sort(this.calculator);
    }

    public void insertCalculation(RootLogic newCalculation)
    {
        this.calculator.add(newCalculation);
        this.sortCalculator();
    }

    public void removeCalculation(RootLogic toRemove)
    {
        calculator.remove(toRemove);
        this.sortCalculator();
    }

    public RootLogic getCalculation(int calcId)
    {
        for (RootLogic c: this.calculator)
        {
            if (c.id == calcId)
            {
                return c;
            }
        }

        return null;
    }

    public int idxOfCalc(RootLogic rootLogic)
    {
        return this.calculator.indexOf(rootLogic);
    }

    public boolean isExist(long newNum){
        for (RootLogic rt : this.calculator) {
            if (rt.num == newNum) {
                return true;
            }
        }
        return false;
    }

    public void finishCalc(RootLogic rootLogic, String state)
    {
        rootLogic.state = state;
        rootLogic.progress = 100;
        Collections.sort(this.calculator);
    }
}
