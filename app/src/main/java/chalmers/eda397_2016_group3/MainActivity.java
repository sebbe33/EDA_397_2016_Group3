package chalmers.eda397_2016_group3;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Locale;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import chalmers.eda397_2016_group3.trello.CardChangeNotifierService;
import chalmers.eda397_2016_group3.trello.TasksFragment;
import chalmers.eda397_2016_group3.timer.FragmentTimer;
import chalmers.eda397_2016_group3.timer.MyTimer;
import chalmers.eda397_2016_group3.trello.TrelloNotificationsFragment;
import chalmers.eda397_2016_group3.trello.TrelloSetupFragment;


public class MainActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_FRAGMENT_NAME = "fragmentName";

    private DrawerLayout mDrawerLayout;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    private MyTimer timer = new MyTimer(new Handler());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //NAVIGATION
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        navigationView.getMenu().performIdentifierAction(
                getIntent().getIntExtra("StartFragment", R.id.navigation_trello), 0);


        startTrelloNotificatinService();
        // If the intent was to bring up the timer, replace the content fragment with FragmentTimer.
        String fragmentName = getIntent().getStringExtra(INTENT_EXTRA_FRAGMENT_NAME);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentName != null && fragmentName.equals(FragmentTimer.class.getSimpleName()))
            navigationView.getMenu().performIdentifierAction(R.id.navigation_timer, 0);
        else
            navigationView.getMenu().performIdentifierAction(R.id.navigation_trello, 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {

        Fragment fragment = null;
        Class fragmentClass;

        switch(menuItem.getItemId()) {
            case R.id.navigation_trello:
                fragmentClass = TrelloSetupFragment.class;
                break;

            case R.id.navigation_github:
                fragmentClass = FragmentGithub.class;
                break;

            case R.id.navigation_trello_feature:
                fragmentClass = TasksFragment.class;
                break;

            case R.id.navigation_timer:
                fragmentClass = FragmentTimer.class;
                break;

            case R.id.navigation_trello_notifications_fragment:
                fragmentClass = TrelloNotificationsFragment.class;
                break;

            default:
                fragmentClass = TrelloSetupFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());
        /*if(menuItem.getTitle().equals("Trello"))
            setTitle("Trello");
        else if (menuItem.getTitle().equals("Github"))
            setTitle("GitHub");
        else if (menuItem.getTitle().equals("Timer"))
            setTitle("Timer");*/
        mDrawerLayout.closeDrawers();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void showDialog(String text) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //   builder.setIcon(R.drawable.eb28d25);

           builder.setTitle("Alert").setMessage(text)
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                       }
                   });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startTrelloNotificatinService() {
        Intent intent = new Intent(this, CardChangeNotifierService.class);
        startService(intent);

    }

}
