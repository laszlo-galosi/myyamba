package com.largerlife.learndroid.myyamba;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import winterwell.jtwitter.Status;

import static java.lang.String.format;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {

    public static final String TAG = "TimeLineFragment";
    private ItemAdapter mTimeLineAdapter;
    private RecyclerView mRecyclerView;
    private List<StatusWrapper> mTimeLineItems = new ArrayList<>(50);

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
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_main_content);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<String> fakeTimelineEntries = new ArrayList<>();
        String numSuffix = "th";
        for (int i = 0; i < 50; i++) {
            fakeTimelineEntries.add(getFakeEntry(i + 1));
        }
        mTimeLineAdapter =
              new ItemAdapter(getActivity(), mRecyclerView, R.layout.list_item_timeline,
                              getYambaApp().getTimeLine()) {
                  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                        int viewType) {
                      View view = LayoutInflater.from(mContext)
                                                .inflate(R.layout.list_item_timeline, parent,
                                                         false);
                      return new StatusViewHolder(view, getActivity());
                  }
              };
        mRecyclerView.setAdapter(mTimeLineAdapter);
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
        return format("This is my %d %s status who the hell gives a freaking shit?", i, seq);
    }

    private YambaApp getYambaApp() {
        return ((BaseActivity) getActivity()).getYambaApp();
    }

    @Override public void onResume() {
        super.onResume();
        Intent intentRefresher = new Intent(getActivity(), RefreshService.class);
        getActivity().startService(intentRefresher);
        YambaApp app = getYambaApp();
        app.clearTimeLine();
        app.setTimeLineObserver(new TimeLineObserver());
    }

    @Override public void onPause() {
        super.onPause();
        YambaApp app = getYambaApp();
        app.setTimeLineObserver(null);
        app.clearTimeLine();
    }

    static class AdapterHelper {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void update(ArrayAdapter arrayAdapter, ArrayList<Object> listOfObject) {
            arrayAdapter.clear();
            for (Object object : listOfObject) {
                arrayAdapter.add(object);
            }
        }
    }

    static class StatusViewHolder extends ItemViewHolderBase {
        public StatusViewHolder(final View itemView, final Context context) {
            super(itemView, context);
        }

        @Override public void bind(final Object dataItem) {
            super.bind(dataItem);
            final Status status = (Status) dataItem;
            TextView tvItemTitle = (TextView) mItemView.findViewById(R.id.itemTitle);
            TextView tvItemText = (TextView) mItemView.findViewById(R.id.itemText);

            if (tvItemTitle != null) {
                String postedAt = mContext.getString(R.string.posted_at);
                String dateTime =
                      SimpleDateFormat.getDateTimeInstance().format(status.getCreatedAt());
                tvItemTitle.setText(format("%s %s", postedAt, dateTime));
            }
            if (tvItemText != null) {
                tvItemText.setText(status.getDisplayText());
            }
        }
    }

    static class StatusWrapper implements Bindable {
        Status mStatus;

        public StatusWrapper(final Status status) {
            mStatus = status;
        }

        @Override public void bind(final Object dataItem) {
            mStatus = (Status) dataItem;
        }

        @Override public Object getDatatItem() {
            return mStatus;
        }
    }

    class TimeLineObserver extends RecyclerView.AdapterDataObserver {
        @Override public void onChanged() {
            mTimeLineAdapter.notifyDataSetChanged();
        }

        @Override public void onItemRangeInserted(final int positionStart, final int itemCount) {
            mTimeLineAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            mTimeLineAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }
    }
}
