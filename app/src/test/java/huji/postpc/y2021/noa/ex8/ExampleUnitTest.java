package huji.postpc.y2021.noa.ex8;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void whenCalcIsCreatedStateIsInProgress() {

        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.insertCalculation(new RootLogic(45));
        RootLogic rootLogic = calculatorHolder.calculator.get(0);
        assertEquals(rootLogic.state, "inProgress");
    }
    @Test
    public void whenCalcIsCreatedFirstRootIsZero() {

        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.insertCalculation(new RootLogic(45));
        RootLogic rootLogic = calculatorHolder.calculator.get(0);
        assertEquals(rootLogic.firstRoot, 0);
    }
    @Test
    public void whenCalcIsCreatedSecondRootIsZero() {

        CalculatorHolder calculatorHolder = new CalculatorHolder();
        calculatorHolder.insertCalculation(new RootLogic(45));
        RootLogic rootLogic = calculatorHolder.calculator.get(0);
        assertEquals(rootLogic.secondRoot, 0);
    }
}

