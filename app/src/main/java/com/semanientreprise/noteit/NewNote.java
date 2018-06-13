package com.semanientreprise.noteit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.semanientreprise.noteit.model.Notes;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class NewNote extends AppCompatActivity {
    @BindView(R.id.note)
    EditText note_ET;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        ButterKnife.bind(this);
        Realm.init(this);

        Realm.setDefaultConfiguration(SyncConfiguration.automatic());
        realm = Realm.getDefaultInstance();
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
                showSaveDialog(string_note);
                break;
            case R.id.action_logout:
                logoutUser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        SyncUser syncUser = SyncUser.current();
        if (!(syncUser == null)) {
            syncUser.logOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void showSaveDialog(final String Sentnote) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        final EditText note_title = dialogView.findViewById(R.id.authors_name);
        note_title.setHint(R.string.enter_note_title);
        Button OK_Btn = dialogView.findViewById(R.id.ok_btn);
        Button CANCEL_btn = dialogView.findViewById(R.id.cancel_btn);


        AlertDialog.Builder noteTitleDialog = new AlertDialog.Builder(this);
        noteTitleDialog.setTitle("Note Details");
        noteTitleDialog.setView(dialogView);

        final AlertDialog alertDialog = noteTitleDialog.create();

        alertDialog.show();

        OK_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTitle = note_title.getText().toString();
                final Notes note = new Notes();
                note.setNoteTitle(noteTitle);
                note.setNote(Sentnote);
                note.setIsSaved(true);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(note);
                    }
                });
                showToast("Note Saved Successfully!");
                note_ET.setText("");
                alertDialog.dismiss();

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