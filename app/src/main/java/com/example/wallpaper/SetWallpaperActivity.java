package com.example.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetWallpaperActivity extends Activity implements View.OnClickListener{

    LinearLayout linearLayout_left,linearLayout_right;
    private ArrayList<WallpaperItem> itemList;
    private Map<String,Integer> title_to_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int cwidth = (int)Math.round(width*0.2);

        setContentView(R.layout.dynamic_main);
        linearLayout_left = findViewById(R.id.linear_layout_left);
        linearLayout_right = findViewById(R.id.linear_layout_right);
        init_items();

        for(int i=0; i< itemList.size(); i++)
        {
            ImageView imgView = new ImageView(this);
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(cwidth, cwidth);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,cwidth);
            imgView.setLayoutParams(lp);
            textView.setLayoutParams(lp2);
            textView.setPadding(15, 5, 0, 0);

            imgView.setAdjustViewBounds(true);
            int temp = getResources().getIdentifier(itemList.get(i).image , "drawable", getPackageName());
            if(temp == 0)
            {
                temp = getResources().getIdentifier(itemList.get(i).image , "mipmap", getPackageName());
            }
            imgView.setImageResource(temp);
            textView.setText(getString(R.string.item_description, itemList.get(i).title, itemList.get(i).description));
            textView.setClickable(true);
            textView.setOnClickListener(this);
            linearLayout_left.addView(imgView);
            linearLayout_right.addView(textView);
        }
    }

    private class WallpaperItem
    {
        private String title,image,description;
        private boolean sensor_enabled = false;
    }

    private void init_items()
    {
        parseXML();
    }

    private void parseXML()
    {
        XmlPullParserFactory parserFactory;
        try
        {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getAssets().open("descriptions.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            processParsing(parser);
        }
        catch (XmlPullParserException e)
        {
            Log.d("Error","XmlPullParserException");
        }
        catch (IOException e)
        {
            Log.d("Error","IOException");
        }
    }

    private void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        itemList = new ArrayList<>();
        title_to_id = new HashMap<>();
        int id = 0;
        int eventType = parser.getEventType();
        WallpaperItem currentItem = null;
        String eltName;

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                eltName = parser.getName();
                if ("item".equals(eltName))
                {
                    currentItem = new WallpaperItem();
                }
                else if (currentItem != null)
                {
                    if ("title".equals(eltName))
                    {
                        currentItem.title = parser.nextText();
                        Log.d("title:", currentItem.title);
                        title_to_id.put(currentItem.title,id);
                        id++;
                    }
                    else if ("image".equals(eltName))
                    {
                        currentItem.image = parser.nextText();
                    }
                    else if ("description".equals(eltName))
                    {
                        currentItem.description = parser.nextText();
                        Log.d("description:", currentItem.description);
                    }
                    else if ("sensor_enabled".equals(eltName))
                    {
                        if(parser.nextText().toLowerCase().equals("true"))
                        {
                            currentItem.sensor_enabled = true;
                        }
                    }
                }
            }
            else if (eventType == XmlPullParser.END_TAG)
            {
                eltName = parser.getName();
                if ("item".equals(eltName)) {
                    itemList.add(currentItem);
                }
            }
            eventType = parser.next();
        }
    }

    @Override
    public void onClick(View view)
    {
        TextView tview = (TextView) view;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        int start = tview.getLayout().getLineStart(0);
        int end = tview.getLayout().getLineEnd(0);
        String substring = (String)tview.getText().subSequence(start, end-1);
        int iden;
        if (title_to_id.containsKey(substring)) {
            iden = title_to_id.get(substring);
        }else{
            iden = 0;
        }
        editor.putInt("Wallpaper number", iden);
        editor.putBoolean("Sensor enabled", itemList.get(iden).sensor_enabled);
        editor.apply();
        //editor.commit();

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try
        {
            wallpaperManager.clear();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(this, MainService.class));
        startActivity(intent);
    }
}
