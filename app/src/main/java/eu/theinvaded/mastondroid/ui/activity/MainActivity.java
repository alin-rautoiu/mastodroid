package eu.theinvaded.mastondroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import eu.theinvaded.mastondroid.R;

import eu.theinvaded.mastondroid.databinding.ActivityMainBinding;
import eu.theinvaded.mastondroid.ui.fragment.FragmentMain;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private DrawerLayout drawerLayout;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDataBinding();

        fragmentTransactionReplace(FragmentMain.getInstance());
        setupDrawerContent(activityMainBinding.navView);

        // temporary disable drawer
        activityMainBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void initDataBinding() {
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }


    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

//                    case R.id.nav_home:
//                        fragmentTransactionReplace(FragmentOne.getInstance());
//                        break;
//
//                    case R.id.nav_messages:
//                        fragmentTransactionReplace(FragmentTwo.getInstance());
//                        break;
//
//                    case R.id.nav_tabs:
//                        fragmentTransactionReplace(FragmentThree.getInstance());
//
//                        break;
                }

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void fragmentTransactionReplace(Fragment fragmentInstance) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragmentInstance)
                .commit();
    }
}
