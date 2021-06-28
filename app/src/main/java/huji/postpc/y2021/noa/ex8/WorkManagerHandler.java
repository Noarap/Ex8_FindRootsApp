package huji.postpc.y2021.noa.ex8;

import android.content.Context;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public class WorkManagerHandler extends Worker {

    private int prog = 0;
    Data.Builder dataBuilder;

    public WorkManagerHandler(@NotNull Context context, @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        setProgressAsync(new Data.Builder().putInt("prog", 0).build());
    }

    @NotNull
    @Override
    public Result doWork() {
        dataBuilder = new Data.Builder();
        long curTime;
        long beginTime = System.currentTimeMillis();
        int idKey = getInputData().getInt("idKey", -1);
        long num = getInputData().getLong("num", 0);
        long thisNum = getInputData().getLong("thisNum", 2);
        long maxNum = (num / 2);
        for (long n = thisNum; n < maxNum; n++) {
            curTime = System.currentTimeMillis() - beginTime;
            if (curTime >= 5000) {
                this.dataBuilder.putLong("num", num);
                this.dataBuilder.putLong("thisNum", thisNum);
                this.dataBuilder.putInt("idKey", idKey);
                this.dataBuilder.putBoolean("calc", true);
                int currentProg = (int) (n * 100.0 / num + 0.5);
                if (this.prog != currentProg) {
                    this.prog = currentProg;
                    Data.Builder newDataBuilder = new Data.Builder();
                    setProgressAsync(newDataBuilder.putInt("prog", this.prog).build());
                }

                this.dataBuilder.putLong("thisNum", n);
                this.dataBuilder.putInt("prog", this.prog);
                this.dataBuilder.putBoolean("calc", true);
                return Result.failure(this.dataBuilder.build());
            }

            if (num % n == 0) {
                this.dataBuilder.putLong("firstRoot", n);
                long secRoot = num / n;
                this.dataBuilder.putLong("secondRoot", secRoot);
                this.dataBuilder.putLong("num", num);
                this.dataBuilder.putInt("idKey", idKey);
                this.dataBuilder.putInt("prog", this.prog);
                return Result.success(dataBuilder.build());

            }
        }

        this.dataBuilder.putLong("num", num);
        this.dataBuilder.putInt("idKey", idKey);
        this.dataBuilder.putBoolean("calc", false);
        return Result.failure(this.dataBuilder.build());
    }
}
