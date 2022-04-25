package com.example.interviewcoach;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/**
 * ViewPagerAdapter for PracticeFragment, QuestionsFragment & TipsFragment.
 * @author Emmy
 */
public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    //Return selected Fragment.
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new PracticeFragment();
            case 2:
                return new QuestionsFragment();
        }

        return new TipsFragment();
    }

    //Return total count for Fragments.
    @Override
    public int getItemCount() {
        return 3;
    }
}
