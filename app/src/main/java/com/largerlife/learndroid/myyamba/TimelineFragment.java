package com.largerlife.learndroid.myyamba;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimeLineFragment";
    private ArrayAdapter<String> mTimeLineAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    public static TimelineFragment newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance");
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public TimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        ArrayList<String> fakeTimelineEntries = new ArrayList<String>();
        String numSuffix = "th";
        for (int i = 0; i < 50; i++) {
            fakeTimelineEntries.add(getFakeEntry(i + 1));
        }
        mTimeLineAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_timeline,
                        R.id.tv_list_item_timeline,
                        fakeTimelineEntries);
        ListView lvTimeline = (ListView) rootView.findViewById(R.id.lv_fragment_timeline);
        lvTimeline.setAdapter(mTimeLineAdapter);
        // Inflate the layout for this fragment
        return rootView;
    }

    private String getFakeEntry(int i) {
        int modulo = i % 10;
        String seq = "th";
        switch (modulo) {
            case 1:
                seq = "st";
                break;
            case 2:
                seq = "nd";
                break;
            case 3:
                seq = "rd";
                break;
            default:
                seq = "th";
        }
        return String.format("This is my %d %s status who the hell gives a freaking shit?", i, seq);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach:" + activity.getComponentName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }
}
