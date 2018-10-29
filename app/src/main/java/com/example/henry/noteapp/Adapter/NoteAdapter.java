package com.example.henry.noteapp.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.henry.noteapp.Model.Note;
import com.example.henry.noteapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by henry on 10/24/18.
 */

// Extends the RecyclerView
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewholder>{
    private Context context;
    private List<Note> noteList;


    public class MyViewholder extends RecyclerView.ViewHolder{

        public TextView note, dot, timestamp;

        public MyViewholder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }


    }

    public NoteAdapter(Context context, List<Note> noteList){
        this.context = context;
        this.noteList = noteList;

    }


    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list, parent, false);
        return new MyViewholder(itemview);
    }

    @Override
    public void onBindViewHolder(MyViewholder holder, int position) {

        Note note = noteList.get(position);
        holder.note.setText(note.getNote());
        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));
        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));


    }
    /*
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */

    private String formatDate(String date){
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = fmt.parse(date);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd");
            return fmtOut.format(date1);

        } catch (ParseException e){
            Log.e("dateFormat", "" + e.getMessage() );


        }
        return "";


    }



    @Override
    public int getItemCount() {
        return 0;
    }
}
