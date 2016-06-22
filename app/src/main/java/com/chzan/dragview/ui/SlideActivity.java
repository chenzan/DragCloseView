package com.chzan.dragview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chzan.dragview.R;
import com.chzan.dragview.manager.AppManager;
import com.chzan.dragview.manager.DragHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzan on 2016/6/17.
 */
public class SlideActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<String> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_slide);
        DragHelper.Builder builder = new DragHelper.Builder();
//        builder.setCurrentPage(this)
//                .setBeforeLastPage(AppManager.getAppManager().getActivity(MainActivity.class))
//                .setDragOrientation(DragHelper.VERTICAL)
//                .setSlideStateListener(new DragHelper.SimpleSlideStateListener() {
//                    @Override
//                    public void onViewScroll(int left, int top, int dx, int dy) {
//                        super.onViewScroll(left, top, dx, dy);
//                        Log.e("SlideActivity", "left:" + left + "---" + "top:" + top + "---dx:" + dx + "---dy:" + dy);
//                    }
//                })
//                .build();
        builder.setCurrentPage(this)
                .setBeforeLastPage(AppManager.getAppManager().getActivity(MainActivity.class))
                .setDragOrientation(DragHelper.HORIZONTAL)
                .setSlideStateListener(new DragHelper.SimpleSlideStateListener() {
                    @Override
                    public void onViewScroll(int left, int top, int dx, int dy) {
                        super.onViewScroll(left, top, dx, dy);
                        Log.e("SlideActivity", "left:" + left + "---" + "top:" + top + "---dx:" + dx + "---dy:" + dy);
                    }
                })
                .build();
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        intData();
    }

    private void intData() {
        datas = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            datas.add(i + "");
        }
        recyclerView.setAdapter(new RecycleAdapter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SlideActivity.this).inflate(R.layout.item_recycle, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv);
            }
        }
    }

}
