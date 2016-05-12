package com.laughing8.attendancecheckin.view.fragment.statistical;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laughing8.attendancecheckin.R;
import com.laughing8.attendancecheckin.bmobobject.MUser;
import com.laughing8.attendancecheckin.utils.network.DataQuery;
import com.laughing8.attendancecheckin.view.activity.SecondActivity;
import com.laughing8.attendancecheckin.view.fragment.found.FoundFragmentMouth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Laughing8 on 2016/5/8.
 */
public class CheckFragment extends Fragment implements AdapterView.OnItemClickListener, DataQuery.OnQueryFinishListener {

    private SecondActivity mActivity;
    private ListView mListView;
    private List<MUser> mUsers;
    private MyAdapter mAdapter;
    private ProgressBar mProgressBar;

    private final int contactQuery=1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (SecondActivity) getActivity();
        DataQuery dataSync = new DataQuery(mActivity,this);
        mUsers = new ArrayList<>();
        dataSync.contactQuery(contactQuery);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistical_check, null);
        mProgressBar = (ProgressBar) view.findViewById(R.id.check_progressBar);
        if (mUsers.size() > 0) {
            mProgressBar.setVisibility(View.GONE);
        }
        mListView = (ListView) view.findViewById(R.id.contact_list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        return view;
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MUser user=mUsers.get(position);
        FoundFragmentMouth fragment=new FoundFragmentMouth();
        Bundle bundle=new Bundle();
        bundle.putString("userName",user.getUsername());
        fragment.setArguments(bundle);
        mActivity.jumpToFragment(fragment,true);
    }

    @Override
    public void queryFinish(List result, int queryCode) {
        if (queryCode==contactQuery){
            mUsers = result;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            if (mProgressBar != null) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int position) {
            return mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            MUser user = mUsers.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mActivity.getLayoutInflater().inflate(R.layout.contact_item, null);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.contact_list_icon);
                viewHolder.name = (TextView) convertView.findViewById(R.id.contact_list_name);
                viewHolder.position = (TextView) convertView.findViewById(R.id.contact_list_position);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.name.setText(user.getName());
            viewHolder.position.setText(user.getPosition());

            return convertView;
        }


        class ViewHolder {
            public ImageView icon;
            public TextView name;
            public TextView position;
        }
    }

}
