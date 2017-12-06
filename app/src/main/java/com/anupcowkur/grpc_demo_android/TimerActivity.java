package com.anupcowkur.grpc_demo_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class TimerActivity extends AppCompatActivity {

    private ManagedChannel grpcChannel;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTime.setText("Connecting...");
    }

    @Override protected void onStart() {
        super.onStart();
        buildChannel();
        subscribeToChannel();
    }

    private void buildChannel() {
        grpcChannel = ManagedChannelBuilder.forAddress("localhost", 8080)
                                           .usePlaintext(true)
                                           .build();
    }

    private void subscribeToChannel() {
        TimerGrpc.newStub(grpcChannel).timer(TimeRequest.getDefaultInstance(), new StreamObserver<TimeResponse>() {
            @Override public void onNext(final TimeResponse timeResponse) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        tvTime.setText(timeResponse.getTime());
                    }
                });
            }

            @Override public void onError(final Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        tvTime.setText("Server down");
                    }
                });

            }

            @Override public void onCompleted() {

            }
        });
    }

    @Override protected void onStop() {
        super.onStop();
        try {
            grpcChannel.shutdownNow();
        } catch (Exception e) {
            Log.e("TimerActivity", "Failed to shut down channel");
        }
    }
}
