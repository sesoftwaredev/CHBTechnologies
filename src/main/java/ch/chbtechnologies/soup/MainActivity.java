package ch.chbtechnologies.soup;

import android.content.Context;
import android.os.Bundle;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static String page = " ";
    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private TextView tvOutput;
    private TextView tvSearchName;
    private String searchKey = "a";
    private ListView lvContact;
    private String x = " ";
    private String url;
    private String type;
    private int pagenum = 1;
    private String uri, btnClicked;
    public static Context context;
   // private String url = "http://www.teleservices.mu/index.php/front/yellow/searchHeading/heading/926/h_name/Architects/keyword/B/where//country";
    //http://www.teleservices.mu/index.php/front/residential/searchByChar/keyword/A/where
    ///                                      /index.php/front/yellow/searchHeading/heading/926/h_name/Architects/keyword/A/where//country//localitydd
    //            http://www.teleservices.mu/index.php/front/yellow/searchHeading/heading/926/h_name/Architects/keyword/A/where//country//page/2

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        tvOutput = (TextView) findViewById(R.id.tvOutput);
        ImageButton btGetUrls = (ImageButton) findViewById(R.id.logobutton);
        lvContact = (ListView) findViewById(R.id.lvContacts);
        tvSearchName = (TextView) findViewById(R.id.tvSearchName);

        // Capture button click
        btGetUrls.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                searchKey = String.valueOf(tvSearchName.getText());
                url = "http://www.teleservices.mu/index.php/front/residential/search?keyword="+searchKey+"&where=&yt0=Search";
                new getUrls().execute();
            }
        });

        findViewById(R.id.btJewellery).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "455/h_name/Jewellers/keyword/Jewellers";
                uri = "http://www.teleservices.mu/index.php/front/yellow/searchHeading/heading/"+type+"/where//country/1/localitydd//page/"+pagenum;
                btnClicked = "btJewellery";
                new getContactDetails().execute();
            }
        });

        findViewById(R.id.btRestaurant).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "1354/h_name/Restaurants/keyword/Restaurants";
                uri = "http://www.teleservices.mu/index.php/front/yellow/searchHeading/heading/"+type+"/where//country/1/localitydd//page/"+pagenum;
                btnClicked = "btRestaurant";
                new getContactDetails().execute();
            }
        });
    }

    // Title AsyncTask
    private class getContacts extends AsyncTask<Void, String, String> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Getting contact details");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Document document = null;
           for(String url :urls) {
               try {
                   document = Jsoup.connect(url).get();
                   for (Element div : document.select(".result_tb")) {
                       //tvOutput.append(String.valueOf(div.text())+"\n");
                       String name = div.select(".pname").text();
                       String tel = div.select(".tel").text();
                       String location = div.select(".locality").text();
                       x += String.valueOf("[ "+name+" " + tel+" ]\n");
                       Contact contact = new Contact(name,tel,location);
                       contacts.add(contact);
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
               x +="\n\n\n end ***************";
           }
            return x;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            tvOutput.setText(x);
            ContactAdapter contactAdapter= new ContactAdapter(getApplicationContext(),0,contacts);
            lvContact.setAdapter(contactAdapter);
        }
    }

    public static ArrayList<String> getPhoneNumbers(String phoneStr) {
        ArrayList<String> phoneNumbers = new ArrayList<>();
        String[] pNums = phoneStr.split("Tel: ");
        for(String phoneNumber : pNums)
        phoneNumbers.add(phoneNumber);

        return phoneNumbers;
    }

    // Title AsyncTask
    private class getUrls extends AsyncTask<Void, String,  ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Fetching from web");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }
        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            x += String.valueOf("retreiving...\n");
            try {
                // Connect to the web site
                int counter = 0;
                String last;
                int n0 = 0;
                Document document = Jsoup.connect(url).get();
                Elements ul = document.select("#ts_pages");
                if(ul.size() != 0) {
                    Elements li = ul.select("li"); // select all li from ul
                    for(Element a : li) {
                        String link = a.select("a[href]").attr("href");
                        x += link+"\n";
                        //urls.add("http://www.teleservices.mu"+link);
                        if(a.select("a[href]").text().equalsIgnoreCase("Last")) {
                            last = a.select("a[href]").attr("href");
                            String[] pp = last.split("/index.php/front/residential/search/keyword/"+searchKey+"/where//yt0/Search/page/");
                            String []pN = pp[1].split("/");
                            n0 = Integer.parseInt(pN[0]);
                        }
                    }
                    for(int i = n0;i>0;i--) {
                        urls.add("http://www.teleservices.mu/index.php/front/residential/search/keyword/"+searchKey+"/where//yt0/Search/page/"+i);
                    }
                }
                else
                    urls.add(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> urls) {
            mProgressDialog.dismiss();
            new getContacts().execute();
        }
    }



    // Get Rautaurant Asynctask
    private class getContactDetails extends AsyncTask<Void, String, String> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pagenum=1;
            mProgressDialog = new ProgressDialog(MainActivity.this);
            if(btnClicked.equals("btRestaurant"))
                mProgressDialog.setTitle("Getting restaurant details");
            else
                mProgressDialog.setTitle("Getting jewellery details");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Document document = null;
            int contactSize=0;
            while(true) {
                try {
                    uri = "http://www.teleservices.mu/index.php/front/yellow/searchHeading/heading/"+type+"/where//country/1/localitydd//page/"+pagenum;
                    document = Jsoup.connect(uri).get();
                    Elements division = document.select("#search_y_result");
                    for (Element table : division.select(".result_tb")) {
                        //tvOutput.append(String.valueOf(div.text())+"\n");
                        Elements tr = table.select(".mid_result");
                        Elements td = tr.select(".mid_left_result");
                        //Elements div = tr.select("div");
                        for(Element divi : td) {
                            String name = divi.select(".resultlistb").text();
                            if(name.equals(""))
                                name = divi.select(".pname").text();
                            String tel = divi.select(".tel").text();
                            String location = divi.select(".locality").text();
                            x += String.valueOf("[ " + name + " " + tel + " ]\n");
                            Contact contact = new Contact(name, tel, location);
                            contacts.add(contact);
                        }

                    }
                    if(contacts.size()==contactSize)
                        break;
                    contactSize = contacts.size();
                    pagenum++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                x +="\n\n\n end ***************";
            }
            return x;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            tvOutput.setText(x);
            ContactAdapter contactAdapter= new ContactAdapter(getApplicationContext(),0,contacts);
            lvContact.setAdapter(contactAdapter);
        }
    }

}














/*
// Logo AsyncTask
private class Logo extends AsyncTask<Void, Void, Void> {
    Bitmap bitmap;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Android Basic JSoup Tutorial");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        try {
            // Connect to the web site
            Document document = Jsoup.connect(url).get();
            // Using Elements to get the class data
            Elements img = document.select("a[class=brand brand-image] img[src]");
            // Locate the src attribute
            String imgSrc = img.attr("src");
            // Download image from URL
            InputStream input = new java.net.URL(imgSrc).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Set downloaded image into ImageView
        ImageView logoimg = (ImageView) findViewById(R.id.logo);
        logoimg.setImageBitmap(bitmap);
        mProgressDialog.dismiss();
    }
}*/
