package com.example.mobilebrowser;

import static android.content.Context.INPUT_METHOD_SERVICE;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Controller {
    Activity activity;
    String urlHomepage = "https://www.wgtn.ac.nz";
    String lastUrl;
    WebView myWebView;
    static List<HistoryItem> historyList = new ArrayList<>();;
    static List<BookmarksItem> bookmarksList = new ArrayList<>();;
    private ListView listViewHistory;
    private ListViewHistoryAdapter adapterH;
    private ListView listViewBookmarks;
    private ListViewBookmarksAdapter adapterB;
    private String websitetitle="";
//    private List<String> historyUrl = new ArrayList<>();
//    private int historyIndex = historyUrl.size();


    public Controller(Activity activity){
        this.activity = activity;
//        historyList = new ArrayList<>();
//        bookmarksList = new ArrayList<>();
        //historyList = loadHistoryList();
//        bookmarksList = loadBookmarksList();
        setupActivity_main();
        setupWebViewBackground();
    }

    // set the background of webview
    public void setupWebViewBackground(){
        WebViewClient myWebViewClient = new WebViewClient();
        myWebView = (WebView) activity.findViewById(R.id.webview);
        String htmlContent = "<html><head></head><body style=\"background-image:url('file:///android_asset/webviewbackground.png');background-size:cover;background-repeat:no-repeat;\">Your content goes here</body></html>";
        myWebView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", null);
    }

    private void setupActivity_main(){
        activity.setContentView(R.layout.activity_main);

        TextInputEditText inputUrlBorwser = (TextInputEditText) activity.findViewById(R.id.inputURL);
        TextView urlBookmark = activity.findViewById(R.id.editText_addBookmarksUrl);
        TextView edittextWebsiteTitle = activity.findViewById(R.id.editText_addBookmarksTitle);
        ImageButton buttonAddBookmark = activity.findViewById(R.id.imageButton_addbookmarks);
        ImageButton buttonForward = (ImageButton) this.activity.findViewById(R.id.imageButton_forward);

        ImageButton buttonBack = (ImageButton) this.activity.findViewById(R.id.imageButton_back);

        WebViewClient myWebViewClient = new WebViewClient();
        myWebView = (WebView) activity.findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(myWebViewClient);

        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String currentUrl) {
            super.onPageFinished(view, currentUrl);

//                historyStack.push(currentUrl);

               // set disenable and enable images for button back and forward
                if (myWebView.canGoBack()) {
                    buttonBack. setImageResource(R.drawable.back);
                } else {
                    buttonBack. setImageResource(R.drawable.backin);
                }

                if (myWebView.canGoForward()) {
                    buttonForward. setImageResource(R.drawable.forward);
                } else {
                    buttonForward. setImageResource(R.drawable.forwardin);
                }

            // get the time of close the webpage    
            LocalDateTime endTime = LocalDateTime.now();
                String formattedTime = endTime.format(DateTimeFormatter.ofPattern("yyyy/mm/dd hh:mm:ss"));

                // Update the TextInputEditText with the current URL on the UI thread
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(currentUrl.equals("file:///android_asset/")) {
                        inputUrlBorwser.setText("Search or enter website...");
                        inputUrlBorwser.setTextColor(Color.parseColor("#868689"));
                    }else if(!(currentUrl.equals("file:///android_asset/"))){
                        inputUrlBorwser.setText(currentUrl);
                        inputUrlBorwser.setTextColor(Color.parseColor("#000000"));
                    }
                }
            });

            // change the addbookmark image if the website is saved
            for(BookmarksItem item:bookmarksList) {
                if (item.getUrl().toString().equals(currentUrl)) {
                    buttonAddBookmark.setImageResource(R.drawable.addedbookmarks);
                    break;
                }else{
                    buttonAddBookmark.setImageResource(R.drawable.addbookmarks);
                }
            }

            // create new history item and add it into history list
            HistoryItem newItem = new HistoryItem(formattedTime,currentUrl);
                if(!currentUrl.equals("file:///android_asset/")) {
                    historyList.add(newItem);
//                    historyUrl.add(currentUrl);
                }
            }
        });

        // load the website after input URL
        inputUrlBorwser.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    Log.d("INPUT","url entered: "+inputUrlBorwser.getText());
                    // Perform action on key press
                    String url = "https://www."+inputUrlBorwser.getText();
                    myWebView.loadUrl(url);
                    // hide soft keyboard
                    InputMethodManager img = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    img.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // reload
        ImageButton buttonReload = (ImageButton) this.activity.findViewById(R.id.imageButton_reload);
        buttonReload.setOnClickListener((view -> {
            myWebView.reload();
            myWebView.scrollTo(0, 0);
        }));

        // back
        buttonBack.setOnClickListener((view -> {
            if (myWebView.canGoBack()){
                myWebView.goBack();
//            if ( historyIndex>0){
//                historyIndex--;
//                String url = historyUrl.get(historyIndex);
//                myWebView.loadUrl(url);
            }
        }));

        // forward
        buttonForward.setOnClickListener((view -> {
            if (myWebView.canGoForward()){
                myWebView.goForward();
            }
        }));

        // homepage
        ImageButton buttonHomepage = (ImageButton) this.activity.findViewById(R.id.imageButton_homepage);
        buttonHomepage.setOnClickListener((view -> {
            myWebView.loadUrl(urlHomepage);
        }));

        // add bookmark
        RelativeLayout addBookmark = activity.findViewById(R.id.addBookmarks);
        buttonAddBookmark.setOnClickListener((view -> {
            addBookmark.setVisibility(View.VISIBLE);
            websitetitle = myWebView.getTitle().toString();
            String url = myWebView.getUrl().toString();
            edittextWebsiteTitle.setText(websitetitle);
            urlBookmark.setText(url);

            // edit the website title
            edittextWebsiteTitle.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                        String websitetitleNew = edittextWebsiteTitle.getText().toString();
                        websitetitle=websitetitleNew;
                        // hide soft keyboard
                        InputMethodManager img = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                        img.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });

            // save
            BookmarksItem newItem = new BookmarksItem(websitetitle, url);
            Button addBookmarkSave = activity.findViewById(R.id.button_addbookmarksSave);
            addBookmarkSave.setOnClickListener((v -> {
                boolean exist = false;
                for(BookmarksItem item:bookmarksList) {
                    if (item.getUrl().toString().equals(url)) {
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    bookmarksList.add(newItem);
                    Toast.makeText(activity, "Added to Bookmarks.", Toast.LENGTH_SHORT).show();
                    buttonAddBookmark.setImageResource(R.drawable.addedbookmarks);
                }else{
                    Toast.makeText(activity, "This website is already in Bookmarks.", Toast.LENGTH_SHORT).show();
                }
                addBookmark.setVisibility(View.INVISIBLE);
            }));

            // cancel
            Button addBookmarkCancel = activity.findViewById(R.id.button_addbookmarksCancel);
            addBookmarkCancel.setOnClickListener((v -> {
                addBookmark.setVisibility(View.INVISIBLE);
            }));
        }));

        // bookmarks
        ImageButton buttonBookmarks = (ImageButton) this.activity.findViewById(R.id.imageButton_bookmarks);
        buttonBookmarks.setOnClickListener((view -> {
            if(historyList.size()!=0) {
                lastUrl = historyList.get(historyList.size() - 1).getUrl();
            }
            setupBookmarks();
        }));

        // history
        ImageButton buttonHistory = (ImageButton) this.activity.findViewById(R.id.imageButton_history);
        buttonHistory.setOnClickListener((view -> {
            if(historyList.size()!=0) {
                lastUrl = historyList.get(historyList.size() - 1).getUrl();
            }
            setupHistory();
        }));
    }

    private void setupBookmarks() {
        activity.setContentView(R.layout.layout_bookmarks);

        // show bookmarks list
        listViewBookmarks = activity.findViewById(R.id.bookmarksListView);
        adapterB = new ListViewBookmarksAdapter(activity, bookmarksList);
        listViewBookmarks.setAdapter(adapterB);

        // click the website in the history list and load the website
        adapterB.setOnItemClickListener(new ListViewBookmarksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url) {
                setupActivity_main();
                myWebView.loadUrl(url);
            }
        });

        // search bookmark by keyword
        TextInputEditText inputBookmarksSearch = (TextInputEditText) activity.findViewById(R.id.textView_BookmarksSearch);
        inputBookmarksSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String keyWord = inputBookmarksSearch.getText().toString();
                    // hide soft keyboard
                    InputMethodManager img = (InputMethodManager)v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    img.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Perform action on key press
                    if(inputBookmarksSearch.getText()!=null) {
                        ArrayList<BookmarksItem> filteredList = new ArrayList<>();
                        boolean exist = false;
                        for (BookmarksItem bookmarksItem : bookmarksList) {
                            if (bookmarksItem.getUrl().contains(keyWord)) {
                                filteredList.add(bookmarksItem);
                                exist = true;
                            }
                        }
                        if (!exist) {
                            Toast.makeText(activity, "No record", Toast.LENGTH_SHORT).show();
                            adapterB = new ListViewBookmarksAdapter(activity, filteredList);
                        }else {
                            adapterB = new ListViewBookmarksAdapter(activity, filteredList);
                        }
                    }else{
                        adapterB = new ListViewBookmarksAdapter(activity, bookmarksList);
                    }
                    listViewBookmarks.setAdapter(adapterB);
                    adapterB.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        // delete button
        Button btnDelete = (Button)activity.findViewById(R.id.button_bookmarksDelete);
        btnDelete.setOnClickListener((View v)->{
            List<BookmarksItem>bookmarksListTemp = ListViewBookmarksAdapter.getSelectedItems();
            for (BookmarksItem selected : bookmarksListTemp) {
                for (BookmarksItem item : bookmarksList) {
                    if (selected.getUrl().equals(item.getUrl())) {
                        bookmarksList.remove(bookmarksList.indexOf(item));
                        break;
                    }
                }
            }
            bookmarksListTemp.clear();
            ListViewBookmarksAdapter adapter = new ListViewBookmarksAdapter(activity, bookmarksList);
            listViewBookmarks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        // clear button
        Button btnClear = (Button)activity.findViewById(R.id.button_bookmarksClear);
        btnClear.setOnClickListener((View v)->{
            bookmarksList.clear();
            ListViewBookmarksAdapter adapter = new ListViewBookmarksAdapter(activity, bookmarksList);
            listViewBookmarks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        });

        // close button
        Button btnClose = (Button)activity.findViewById(R.id.button_bookmarksClose);
        btnClose.setOnClickListener((View v)->{
            setupActivity_main();
            if(lastUrl==null){
                setupWebViewBackground();
            }else {
                myWebView.loadUrl(lastUrl);
            }
        });
    }

    private void setupHistory() {
        activity.setContentView(R.layout.layout_history);

        // sort the historyList before listing in listview
        Comparator<HistoryItem> timeComparator = new Comparator<HistoryItem>() {
            @Override
            public int compare(HistoryItem item1, HistoryItem item2) {
                String time1 = item1.getTime();
                String time2 = item2.getTime();
                return time2.compareTo(time1);
            }
        };
        Collections.sort(historyList, timeComparator);

        // show history list
        listViewHistory = activity.findViewById(R.id.historyListView);
        adapterH = new ListViewHistoryAdapter(activity, historyList);
        listViewHistory.setAdapter(adapterH);

        // click the website in the history list and load the website
        adapterH.setOnItemClickListener(new ListViewHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url) {
                setupActivity_main();
                myWebView.loadUrl(url);
            }
        });

        // search history by keyword
        TextInputEditText inputHistorySearch = (TextInputEditText) activity.findViewById(R.id.textView_historySearch);
        inputHistorySearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String keyWord = inputHistorySearch.getText().toString();
                    // hide soft keyboard
                    InputMethodManager img = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                    img.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Perform action on key press
                    if (inputHistorySearch.getText() != null) {
                        ArrayList<HistoryItem> filteredList = new ArrayList<>();
                        boolean exist = false;
                        for (HistoryItem historyItem : historyList) {
                            if (historyItem.getUrl().contains(keyWord)) {
                                filteredList.add(historyItem);
                                exist = true;
                            }
                        }
                        if (!exist) {
                            Toast.makeText(activity, "No record", Toast.LENGTH_SHORT).show();
                            adapterH = new ListViewHistoryAdapter(activity, filteredList);
                        } else {
                            adapterH = new ListViewHistoryAdapter(activity, filteredList);
                        }
                    } else {
                        adapterH = new ListViewHistoryAdapter(activity, historyList);
                    }
                    listViewHistory.setAdapter(adapterH);
                    adapterH.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        // delete button
        Button btnDelete = (Button) activity.findViewById(R.id.button_historyDelete);
        btnDelete.setOnClickListener((View v) -> {
            List<HistoryItem> historyListTemp = ListViewHistoryAdapter.getSelectedItems();
            for (HistoryItem selected : historyListTemp) {
                for (HistoryItem item : historyList) {
                    if (selected.getUrl().equals(item.getUrl())) {
                        historyList.remove(historyList.indexOf(item));
                        break;
                    }
                }
            }
            historyListTemp.clear();
            adapterH = new ListViewHistoryAdapter(activity, historyList);
            listViewHistory.setAdapter(adapterH);
            adapterH.notifyDataSetChanged();
        });

        // clear button
        Button btnClear = (Button) activity.findViewById(R.id.button_historyClear);
        btnClear.setOnClickListener((View v) -> {
            historyList.clear();
            adapterH = new ListViewHistoryAdapter(activity, historyList);
            listViewHistory.setAdapter(adapterH);
            adapterH.notifyDataSetChanged();
        });

        // close button
        Button btnClose = (Button) activity.findViewById(R.id.button_historyClose);
        btnClose.setOnClickListener((View v) -> {
            setupActivity_main();
            if(lastUrl==null){
                setupWebViewBackground();
            }else {
                myWebView.loadUrl(lastUrl);
            }
        });
    }

    // Save bookmarks and history to a single SharedPreferences object
    public void saveData() {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();

        // Save bookmarks
        String bookmarksJson = gson.toJson(bookmarksList);
        editor.putString("bookmarks", bookmarksJson);

        // Save history
        String historyJson = gson.toJson(historyList);
        editor.putString("history", historyJson);

        // save back/forward history
//        String historyStackJson = gson.toJson(historyStack);
//        editor.putString("history_stack", historyStackJson);

        editor.apply();
    }

    // Load bookmarks and history from a single SharedPreferences object
    public List<HistoryItem> loadHistoryList(){
        List<HistoryItem> historyList = new ArrayList<>();
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String historyJson = preferences.getString("history", null);
        if(historyJson!=null) {
            Type historyType = new TypeToken<List<HistoryItem>>() {
            }.getType();
            historyList = gson.fromJson(historyJson, historyType);
        }
        return historyList;
    }

    public List<BookmarksItem>loadBookmarksList(){
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String bookmarksJson = preferences.getString("bookmarks", null);
        if(bookmarksJson!=null) {
            Type bookmarksType = new TypeToken<List<BookmarksItem>>() {
            }.getType();
            bookmarksList = gson.fromJson(bookmarksJson, bookmarksType);
        }
        return bookmarksList;
    }

}
