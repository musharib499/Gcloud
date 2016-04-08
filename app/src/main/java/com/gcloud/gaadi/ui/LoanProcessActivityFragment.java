package com.gcloud.gaadi.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcloud.gaadi.R;
import com.gcloud.gaadi.adapter.LoanProcessAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoanProcessActivityFragment extends Fragment {
    RecyclerView recyclerView;
    LoanProcessAdapter loanProcessAdapter;

    List<String> listItem;

    public LoanProcessActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_process, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        listItem = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            listItem.add("Item List" + i);

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        loanProcessAdapter = new LoanProcessAdapter(getActivity(), listItem);
        recyclerView.setAdapter(loanProcessAdapter);


        return view;
    }
}
