package com.estimote.examples.demos.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.estimote.examples.demos.R;

public  class Events_layout extends RelativeLayout
{
    private Events.Field event_field;
    private ImageView icon;
    private TextView id;
    private TextView description;
    private TextView title;
    private TextView date;
    public Events_layout(Context context){
        super(context);

        init_component();
    }
    private  void init_component()
    {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.events, this);
        icon = (ImageView)findViewById(R.id.event_logo);
        title =(TextView) findViewById(R.id.event_title);
        id = (TextView)findViewById(R.id.event_id);
        description=(TextView)findViewById(R.id.event_description);
        date = (TextView)findViewById(R.id.event_time);
    }
   public void set_icon(Bitmap image)
   {
    icon.setImageBitmap(image);
   }
    public  void set_id(String id)
    {
        this.id.setText(id);
    }
    public  void set_description(String desc)
    {
        this.description.setText(desc);
    }
    public  void set_date(String start_date,String end_date)
    {
        this.date.setText(start_date+" - "+ end_date);
    }

    public  void set_Field(Events.Field field)
    {
        this.event_field =field;
        update_field();
    }
    public void set_Title(String title ){
        this.title.setText(title);
    }
   void update_field()
    {
       // icon=event_field.icon;// тут нужно подгуржать картинку из интернета

        set_id(event_field.id.toString());
        set_description(event_field.description);
        set_date(event_field.start_date, event_field.end_date);
        set_Title(event_field.title);
    }
}
