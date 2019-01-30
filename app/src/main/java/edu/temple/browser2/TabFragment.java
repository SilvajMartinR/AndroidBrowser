package edu.temple.browser2;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    private static final String TAG = "CIS3515";

    View v;
    Context context;
    WebView webView;
    TextView urlTextView;
    String URL;
    Button forwardB;
    Button backwardB;


    //public static String URL_KEY = "url_key";
    public static String URL_KEY; // = "url_key";
    String WEB_KEY;
    public String currentURL;


    public TabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Get Bundle
        //Bundle is used to pass data between Activities
        //Bundle is generally used for passing data between
        //various activities of android
        Bundle bundle = getArguments();
        URL = bundle.getString(URL_KEY);
        //URL = bundle.getString(WEB_KEY);
    }

    @Override
    public void onAttach(Context context){
        //Called when a fragment is first attatched to its
        //context
        super.onAttach(context);
        this.context = context;
    }

    //Create new tab fragment (webview)
    public static TabFragment newInstance(String URL){
        //Log.i(TAG, "TabFragment is creating a new instance!");

        //create instance of TabFragment
        TabFragment tabFragment = new TabFragment();
        //Passing Arguments and creating bundle
        //Bundle is used to pass data between Activities
        Bundle bundle = new Bundle();
        bundle.putString(TabFragment.URL_KEY, URL);
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onResume() {
        webView.loadUrl(WEB_KEY);
        currentURL = webView.getUrl();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tab, container, false);

        //inflate the webView
        webView = v.findViewById(R.id.webView);

        //create buttons
        forwardB = v.findViewById(R.id.forwardB);
        backwardB = v.findViewById(R.id.backB);

        //sets the view WebViewClient that will receive various
        //notifications and requests
        webView.setWebViewClient(new AWebViewClient());

        loadUrlFromTextView();


        /*
            Buttons used to move between websites inside a fragment
            Forward button - go forward to websites that have been visited inside
            that fragment
            Back button - go backwards to websites that have been visited inside
            that fragment
         */
        //Button in fragment to go forward
        forwardB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.canGoForward
                //A boolean that indicates whether the next location
                //can be loaded
                //goForward
                //Loads the next location in the backward-forward list
                if(webView.canGoForward()) {
                    webView.goForward();
                    currentURL = webView.getUrl();
                }
            }
        });

        //Button in fragment to go backward
        backwardB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.canGoBack()
                //gets whether this WebView has a back history item
                //goBack()
                //Goes back in the history of this WebView
                if(webView.canGoBack()) {
                    webView.goBack();
                    currentURL = webView.getUrl();
                }
            }
        });

        return v;
    }

    public void loadURL(String url){
        //Log.i(TAG, "In the loadURL method");
        URL = url;
        loadUrlFromTextView();
    }

    //URL method
    private void loadUrlFromTextView() {
        Log.i(TAG, "loadURLFromTextView");

        new Thread(){

            public void run(){


                //grab url from textView
                String sb = URL;
                Message msg = Message.obtain();
                msg.obj = sb.toString();
                WEB_KEY = sb.toString();
                //URL_KEY = sb.toString();
                currentURL = sb.toString();
                responseHandler.sendMessage(msg);

                //Log.i(TAG, "we got the URL");

            }

        }.start();

    }


    //Handler to set the display text
    Handler responseHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //WebView object loading data
            //webView.loadData((String) msg.obj, "text/html", "UTF-8");
            //Log.i(TAG, "In the handler " + (String) msg.obj);
            webView.loadUrl((String) msg.obj);
            return false;
        }
    });


    private class AWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
           // currentURL = url;
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);
            ((GetURL)context).getURL(url, view);
            URL = url;
        }

    }

    interface GetURL{
        void getURL(String loadedURL, WebView webViewPassed);
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        outState.putString(URL_KEY, URL);
        super.onSaveInstanceState(outState);
    }

}
