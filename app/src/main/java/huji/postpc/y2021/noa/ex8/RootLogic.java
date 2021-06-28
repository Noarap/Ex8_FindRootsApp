package huji.postpc.y2021.noa.ex8;

import java.io.Serializable;
import java.util.Random;

public class RootLogic implements Serializable, Comparable<RootLogic> {

    public int id;
    public int progress;
    public long num;
    public long thisNum;
    public long firstRoot;
    public long secondRoot;
    public String state;
    public String worker;

    public RootLogic(long num)
    {
        this.id = new Random().nextInt(999999);
        this.progress = 0;
        this.num = num;
        this.thisNum = 2;
        this.firstRoot = 0;
        this.secondRoot = 0;
        this.state = "inProgress";
        this.worker = "";
    }

    @Override
    public int compareTo(RootLogic rootLogic) {
        if((!this.state.equals("inProgress")) && rootLogic.state.equals("inProgress"))
        {
            return 1;
        }

        else if(this.state.equals("inProgress") && (!rootLogic.state.equals("inProgress")))
        {
            return -1;
        }

        else if (rootLogic.num < this.num)
        {
            return 1;
        }

        else {
            return -1;
        }
    }
}
