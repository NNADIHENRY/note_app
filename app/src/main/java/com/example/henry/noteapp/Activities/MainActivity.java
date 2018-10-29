package com.example.henry.noteapp.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.henry.noteapp.Adapter.NoteAdapter;
import com.example.henry.noteapp.Helper.DatabaseHelper;
import com.example.henry.noteapp.Model.Note;
import com.example.henry.noteapp.R;
import com.example.henry.noteapp.Utils.MyDividerItemDecorator;
import com.example.henry.noteapp.Utils.Recyclertouchlistener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteAdapter mAdapter;
    private List<Note> noteList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_note_view);

        db = new DatabaseHelper(this);
        noteList.addAll(db.getAllNote());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null,
                        -1);


            }
        });
        mAdapter = new NoteAdapter(this, noteList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecorator(this,
                LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        toggleEmptyNote();

        /*
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         */

        recyclerView.addOnItemTouchListener(new Recyclertouchlistener(this, recyclerView, new Recyclertouchlistener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionDialog(position);


            }
        }));
    }

    /*
     * Inserting new note in db
     * and refreshing the list
     */

    private void createNote(String note) {

        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertNote(note);
        // get the newly inserted note from db
        Note n= db.getNote(id);
        if (n != null){
            // adding new note to array list at 0 position
            noteList.add(0, n);
            // refreshing the list
            mAdapter.notifyDataSetChanged();
            toggleEmptyNote();

        }

    }

    /*
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateNote(String note, int position) {
        Note n = noteList.get(position);
        // updating note text
        n.setNote(note);
        // updating the note in the database
        db.updateNote(n);
        // refreshing the list
        noteList.set(position, n);
        mAdapter.notifyItemChanged(position);
        toggleEmptyNote();

    }

    /*
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position){
        // deleting the note from db
        db.deleteNote(noteList.get(position));
        // removing the note from the list
        noteList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNote();
    }

    /*
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */

    private void showActionDialog(final int position) {
        CharSequence colour[] = new CharSequence[]{"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(colour, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (id == 0){
                    showNoteDialog(true, noteList.get(position), position);

                }
                else{
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /*
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */

    private void showNoteDialog( final boolean shouldUpdate,
                                 final Note note, final int position ) {
        // Override active layout
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.note_dialog, null);
        // AlertDialog used for pop-Ups
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);

        // Used to link or get views in the dialogBox
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        final EditText inputNote = view.findViewById(R.id.note);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_note):
                getString(R.string.edit_note)) ;
        if (shouldUpdate && note != null){
            inputNote.setText(note.getNote());
        }
        builder.setCancelable(false)
//                positive button is used to indicate whether to save or update
                .setPositiveButton(shouldUpdate ? "update" : "save",
                        new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                // Used to set Negative button to cancel
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();

                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setOnClickListener
                (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())){

                    Toast.makeText(MainActivity.this, " Enter Note ",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null){
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), position);


                }
                else {
                    // create new note
                    createNote(inputNote.getText().toString());

                }

            }
        });


        }
    /*
     * Toggling list and empty notes view
     */

    private void toggleEmptyNote() {
        if (db.getNoteCount() > 0 ){
            noNotesView.setVisibility(View.GONE);
        }
        else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

}
