package com.star.criminalintent;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final int REQUIRES_POLICE = 1;
    public static final int NOT_REQUIRES_POLICE = 0;

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyDataSetChanged();
        }
    }

    private abstract class CrimeHolder extends RecyclerView.ViewHolder {

        protected Crime mCrime;

        protected TextView mTitleTextView;
        protected TextView mDateTextView;
        protected ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup container, @LayoutRes int resource) {
            super(inflater.inflate(resource, container, false));

            itemView.setOnClickListener(v -> {
                Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                startActivity(intent);
            });

            mTitleTextView = itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = itemView.findViewById(R.id.list_item_crime_date_text_view);
            mSolvedImageView = itemView.findViewById(R.id.list_item_crime_solved_image_view);
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getFormattedDate());
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }

    private class RegularCrimeHolder extends CrimeHolder {

        public RegularCrimeHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater, container, R.layout.list_item_crime);
        }
    }

    private class SeriousCrimeHolder extends CrimeHolder {

        private Button mRequiresPoliceButton;

        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater, container, R.layout.list_item_crime_requires_police);

            mRequiresPoliceButton = itemView.findViewById(R.id.list_item_crime_requires_police_button);

            mRequiresPoliceButton.setOnClickListener(v -> Toast.makeText(getActivity(),
                    mCrime.getTitle() + " requires police!", Toast.LENGTH_LONG).show());
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            switch (viewType) {
                case REQUIRES_POLICE:
                    return new SeriousCrimeHolder(layoutInflater, parent);
                case NOT_REQUIRES_POLICE:
                    return new RegularCrimeHolder(layoutInflater, parent);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            return crime.isRequiresPolice() ? REQUIRES_POLICE : NOT_REQUIRES_POLICE;
        }
    }
}
