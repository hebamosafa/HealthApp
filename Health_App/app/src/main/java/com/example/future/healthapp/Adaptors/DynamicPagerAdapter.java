package com.example.future.healthapp.Adaptors;



import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class DynamicPagerAdapter
        extends FragmentPagerAdapter {

    private List<Class> mFragmentTypes;
    private List<String> mPageTitles;

    public DynamicPagerAdapter(
            FragmentManager fm,
            List<String> pageTitles,
            List<Class> fragmentTypes) {
        super(fm);
        this.mPageTitles = pageTitles;
        this.mFragmentTypes = fragmentTypes;
    }

    @Override
    public int getCount() {

        if (mFragmentTypes != null) {
            return mFragmentTypes.size();
        }

        return 0;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if (mFragmentTypes != null &&
                position >= 0 &&
                position < mFragmentTypes.size()) {

            Class c = mFragmentTypes.get(position);

            try {
                fragment = (Fragment)Class.forName(c.getName()).newInstance();
            }
            catch (Exception ex) {
                // TODO: log the error
            }
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (mPageTitles != null &&
                position >= 0 &&
                position < mPageTitles.size()) {
            return mPageTitles.get(position);
        }

        return null;
    }
}