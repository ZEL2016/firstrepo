package com.zel.screenrecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zel.screenrecorder.data.MediaData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by enlin.zhao on 2017/3/6.
 */

public class VideoFragment extends Fragment {

    private ListView mListView;
    List<MediaData> videos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videos = new ArrayList<>();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cancel();
            }
        }, 1, 1);
    }

    private void initList(){
        String[] path_list = new File(MediaData.DIR_VIDEO).list();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mListView = (ListView) view.findViewById(R.id.list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
