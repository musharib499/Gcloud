package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gcloud.gaadi.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CaseBaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaseBaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaseBaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;

    protected ProgressBar mProgressBar;
    protected RelativeLayout errorLayout;
    protected String mStatus;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaseBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaseBaseFragment newInstance(String param1, String param2) {
        CaseBaseFragment fragment = new CaseBaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public CaseBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mProgressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        errorLayout = (RelativeLayout) getView().findViewById(R.id.errorLayout);

        // Intent intent = getIntent();
        //mStatus = intent.getStringExtra(Constants.LOAN_CASE_STATUS);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_case_base, container, false);
    }


}
