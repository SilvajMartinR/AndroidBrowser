package edu.temple.browser2;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/*
    Bug
    with loading url with frag buttons
    when swiping back to a tab it will display the url of the 2nd
    visited website in that tab, but once you click
    the frag buttons it will display the correct website

 */

public class MainActivity extends AppCompatActivity implements TabFragment.GetURL {

    private static final String TAG = "CIS3515";

    Button button; //Go button
    TabFragment tf;
    TextView textViewURL; //url text bar
    FragmentManager fm = getSupportFragmentManager();
    Boolean first = true;
    String implicitURL;

    //View Pager for Fragments
    //is a convenient way to supply and manage the lifecycle
    //of each page
    //Layout manager that allows the user to flip left and right
    //through pages of data
    ViewPager viewPager;

    //Array list to hold tabs
    ArrayList<TabFragment> tabList = new ArrayList();
    int index = 0; //index of the tab(s)
    FragmentStatePagerAdapter fspa;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String url2 = getIntent().getData().toString();

        button = findViewById(R.id.button);

        textViewURL = findViewById(R.id.editText);

        //if button is pushed create instance of fragment
        tf = TabFragment.newInstance("");

        tabList.add(0,tf);

        viewPager = findViewById(R.id.viewPager);
        //save up to 100 tabs
        viewPager.setOffscreenPageLimit(100);

        //View pager
        //.OnPageChangeListener
        //Callback interface for responding to changing state of the selected page
        //.addOnPageChangeListener
        //Add a listener that will be invoked whenever the page changes or
        //is incrementally scrolled

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if(i > index){
                    index = i;
                    textViewURL.setText(tabList.get(i).currentURL);
                    //textViewURL.setText(tabList.get(i).URL);
                }if(i < index){
                    index = i;
                    textViewURL.setText(tabList.get(i).currentURL);
                    //textViewURL.setText(tabList.get(i).URL);
                }if(i == index){
                    index = i;
                    textViewURL.setText(tabList.get(i).currentURL);
                    //textViewURL.setText(tabList.get(i).URL);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Button clicked!");
                //Log.i(TAG, "index: " + index);

                if(first) {
                    //Log.i(TAG, "We are here");
                    first = false;
                    fm.beginTransaction().replace(R.id.viewPager, tabList.get(0))
                            .commit();
                    tf.loadURL(textViewURL.getText().toString());


                    //fspa.notifyDataSetChanged();
                }else{
                    // Log.i(TAG, "Already loaded fragment the first time!  Loading new URL.");
                    tf = tabList.get(index);
                    tf.loadURL(textViewURL.getText().toString());
                    fspa.notifyDataSetChanged();
                }

            }
        });


        fspa = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return tabList.get(i);
            }

            @Override
            public int getCount() {
                return tabList.size();
            }
        };

        viewPager.setAdapter(fspa);


        Intent intent = getIntent();
        if(intent.getData() != null){
            implicitURL = intent.getData().toString();
        }

        if(implicitURL != null){
            fspa.notifyDataSetChanged();
            tabList.add(TabFragment.newInstance(implicitURL));
            fspa.notifyDataSetChanged();
            index = tabList.size() - 1;
            first = false;
            viewPager.setCurrentItem(index);
        }


    }

    //Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.browser_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Action bar button to go to new fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (id){
            case R.id.NewT:{
                //Log.i(TAG, "New button: index: " + index);
                TabFragment frag;

                frag = TabFragment.newInstance("");
                index=index+1;
                //Reset Url text
                textViewURL.setText("");
                //start
                tabList.add(frag);
                fspa.notifyDataSetChanged();
                viewPager.setCurrentItem(index);

                //Log.i(TAG, "after button push: index: " + index);
                break;
            }

            case R.id.Prev:{
               // Log.i(TAG, "Previous button index: " + index);
                //Save current tab
               // tabList.add(index, tf);

                if(index != 0){

                    index=index-1;

                    viewPager.setCurrentItem(index);
                    fspa.notifyDataSetChanged();
                    textViewURL.setText(tabList.get(index).currentURL);

                }else {
                    Toast.makeText(this, "First Tab!", Toast.LENGTH_SHORT).show();
                }
                //Log.i(TAG, "after button push: index: " + index);
                break;
            }


            case R.id.ForwardB:{


                if(index==tabList.size()-1)
                {
                    Toast.makeText(this,"Last Tab!",Toast.LENGTH_SHORT).show();
                }

                else {
                    //Save current tab
                    Log.i(TAG, "Forward button index: " + index);
                    //tabList.add(index, tf);
                    //index = index + 1;
                    index = index + 1;
                    fspa.notifyDataSetChanged();

                    viewPager.setCurrentItem(index);
                    //fspa.notifyDataSetChanged();

                    textViewURL.setText(tabList.get(index).currentURL);


                    Log.i(TAG, "after button push: index: " + index);


                    break;
                }
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getURL(String loadedURL, WebView webViewPassed) {
        if(tabList.get(viewPager.getCurrentItem()).webView==webViewPassed) textViewURL.setText(loadedURL);
    }
}
