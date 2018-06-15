package com.semanientreprise.noteit;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.semanientreprise.noteit.model.Author;
import com.semanientreprise.noteit.model.Notes;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.SyncConfiguration;

public class NewNote extends AppCompatActivity {
    @BindView(R.id.notes)
    EditText note_ET;

    private Realm realm;
    private String authors_id;
    private String note_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ButterKnife.bind(this);

//        Realm.setDefaultConfiguration(SyncConfiguration.automatic());
        realm = Realm.getDefaultInstance();

        authors_id = getIntent().getStringExtra("author_id");
        note_id = getIntent().getStringExtra("note_id");

        if (note_id != null)
            setUpNoteDetails(note_id);
    }

    private void setUpNoteDetails(String noteID) {
        Notes noteGotten = realm.where(Notes.class).equalTo("noteId",noteID).findFirst();
        note_ET.setText(noteGotten.getNote().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newnote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                String string_note = note_ET.getText().toString();
                if(note_id != null)
                    updateNote(note_id,string_note);
                else
                    showSaveDialog(string_note);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNote(String noteID, final String SentNote) {
        final Notes noteToBeUpdated = realm.where(Notes.class).equalTo("noteId",noteID).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                noteToBeUpdated.setNote(SentNote);
                showToast("Note Update Successful");
                finish();
            }
        });
    }

    private void showSaveDialog(final String Sentnote) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        final EditText note_title = dialogView.findViewById(R.id.authors_name);
        note_title.setHint(R.string.enter_note_title);
        Button OK_Btn = dialogView.findViewById(R.id.ok_btn);
        Button CANCEL_btn = dialogView.findViewById(R.id.cancel_btn);

        AlertDialog.Builder noteTitleDialog = new AlertDialog.Builder(this);
        noteTitleDialog.setTitle("Notes Details");
        noteTitleDialog.setView(dialogView);
        noteTitleDialog.setCancelable(false);

        final AlertDialog alertDialog = noteTitleDialog.create();

        alertDialog.show();

        OK_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String noteTitle = note_title.getText().toString();
                final Notes notes = new Notes();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        notes.setNoteTitle(noteTitle);
                        notes.setNote(Sentnote);
                        notes.setIsSaved(true);

                        realm.where(Author.class).equalTo("id",authors_id).findFirst().getNotes().add(notes);
                    }
                });
                showToast("Note Saved Successfully!");
                note_ET.setText("");
                alertDialog.dismiss();
                finish();
            }
        });

        CANCEL_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}