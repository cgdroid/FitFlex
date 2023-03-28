package com.tmhnry.fitflex.extension;

import android.os.CountDownTimer;


public class _CountDownTimer extends CountDownTimer {
    private onTickListener listener;

    public _CountDownTimer(onTickListener listener, long millisInFuture, long countDownInterval){
        super(millisInFuture, countDownInterval);
        this.listener = listener;
    }

    private _CountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {

        int r = (int) l / 1000;
        int m = r / 60;
        int s = r % 60;
        listener.onTick(m, s, l);
    }

    @Override
    public void onFinish() {
        listener.onFinish();
    }

    public interface onTickListener{
        void onTick(int minute, int second, long l);
        void onFinish();
    }
}
