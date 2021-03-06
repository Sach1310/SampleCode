package com.firebaseplayer.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Fade;
import android.view.View;

import com.firebaseplayer.R;
import com.firebaseplayer.adapter.NavItemAdapter;
import com.firebaseplayer.baseclass.BaseAppCompactActivity;
import com.firebaseplayer.databinding.ActivityHomeBinding;
import com.firebaseplayer.fragment.FragmentHome;
import com.firebaseplayer.model.NavItem;
import com.firebaseplayer.transition.DetailsTransition;
import com.firebaseplayer.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.OnClick;



public class HomeActivity extends BaseAppCompactActivity implements View.OnClickListener {


    private ActivityHomeBinding mBinding;
    private NavItemAdapter navItemAdapter;
    private List<NavItem> navItems;
    private Fragment mFragment;
    private FragmentTransaction ft;
    private Stack<String> titleStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        ButterKnife.bind(this);
        setupDrawer();
        setSupportActionBar(mBinding.toolbar);
    }

    private void setupDrawer() {
        navItems = new ArrayList<>();
        titleStack = new Stack<>();
        String[] name = getResources().getStringArray(R.array.menu_list);

        int[] selectedMenuIcon = getSelectedMenuIcon();
        for (int i = 0; i < name.length; i++) {
            navItems.add(new NavItem(name[i], selectedMenuIcon[i]));
        }
        navItemAdapter = new NavItemAdapter(this, navItems, this);
        mBinding.rvNavigationItem.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rvNavigationItem.setAdapter(navItemAdapter);
        navItemAdapter.setSelector(0);
        getSupportFragmentManager().addOnBackStackChangedListener(new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {


            @Override
            public void onBackStackChanged() {
                setToolbarText(titleStack.peek());

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    mBinding.homeIvDrawerMenu.setImageResource(R.drawable.ic_back_btn_header);


                } else {
                    mBinding.homeIvDrawerMenu.setImageResource(R.drawable.ic_menu);

                }

            }
        });
        manageNavigation(0);
    }

    public void setToolbarText(String toolbarText) {
        mBinding.tvHeaderTitle.setText(toolbarText);

    }

    public int[] getSelectedMenuIcon() {
        int icon[] = new int[6];
        icon[0] = R.drawable.ic_home_white;
        icon[1] = R.drawable.ic_library_books_white;
        icon[2] = R.drawable.ic_notifications_white;
        icon[3] = R.drawable.ic_favorite_white;
        icon[4] = R.drawable.ic_share_white;
        icon[5] = R.drawable.ic_star_white;
        return icon;
    }


    @OnClick(R.id.home_profile_rl)
    public void profileClick() {
        Utility.navigationIntent(this, ProfileActivity.class);
    }



    @Override
    public void onClick(View v) {
        navItemAdapter.setSelector((Integer) v.getTag());
        mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        setDelayClickForDrawer((Integer) v.getTag(), 250);
    }

    private void setDelayClickForDrawer(final int pos, int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                manageNavigation(pos);
            }
        }, time);
    }

    @OnClick(R.id.home_iv_drawer_menu)
    public void drawerClick() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            onBackPressed();
        } else {
            if (mBinding.mainDrawerLayout.isDrawerOpen(GravityCompat.START))
                mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
            else
                mBinding.mainDrawerLayout.openDrawer(GravityCompat.START);
        }
    }






    @Override
    public void replaceFragment(Fragment mFragment, String title) {
        if (mFragment.isAdded() || titleStack.peek().equals(title))
            return;
        try {
            ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(mFragment.getClass().getSimpleName());
            ft.replace(R.id.main_fragment_container, mFragment, mFragment.getClass().getSimpleName());
            titleStack.push(title);
            ft.commit();

        } catch (Exception e) {
        }
    }


    @Override
    public void addFragment(Fragment mFragment, String title) {
        if (mFragment.isAdded() || titleStack.peek().equals(title))
            return;
        try {
            ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(mFragment.getClass().getSimpleName());
            ft.add(R.id.main_fragment_container, mFragment, mFragment.getClass().getSimpleName());
            titleStack.push(title);
            ft.commit();

        } catch (Exception e) {
        }
    }


    @Override
    public void replaceSharedFragment(Fragment first, Fragment second, String title, String[] transiton, View... view) {
        if (second.isAdded() || titleStack.peek().equals(title))
            return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


                second.setSharedElementEnterTransition(new DetailsTransition());
                second.setEnterTransition(new Fade());
                first.setExitTransition(new Fade());
                second.setSharedElementReturnTransition(new DetailsTransition());
                ft = getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.main_fragment_container, second, second.getClass().getSimpleName());

                ft.addToBackStack(second.getClass().getSimpleName());
                for (int i = 0; i < view.length; i++) {
                    ft.addSharedElement(view[i], transiton[i]);
                }

                // Apply the transaction
                ft.commit();

                titleStack.push(title);
            } else {
                replaceFragment(second, title);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void manageNavigation(int pos) {
        String title = "";
        switch (pos) {
            case 0:
                mFragment = new FragmentHome();
                title = getString(R.string.home);
                break;



        }


        if (!title.equals("") && mFragment != null && (titleStack.empty() || !titleStack.peek().equals(title))) {
            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            titleStack.clear();
            titleStack.push(title);
            setToolbarText(title);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mFragment, mFragment.getClass().getSimpleName()).commit();
        }

    }


    @Override
    public void onBackPressed() {
        if (mBinding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (titleStack.size() > 1) {
                getSupportFragmentManager().popBackStack();
                titleStack.pop();
            } else {
                titleStack.pop();
                super.onBackPressed();
            }
        }
    }



}