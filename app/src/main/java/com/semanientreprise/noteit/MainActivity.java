package com.semanientreprise.noteit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.semanientreprise.noteit.model.Author;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    ProgressDialog progressDialog;

    private Realm realm;
    private String authors_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Realm.init(this);

        fab.setOnClickListener(this);

        checkIfUserIsLoggedIn();
    }

    private void checkIfUserIsLoggedIn() {
        if (SyncUser.current() != null){
            startViewNotesActivity();
        }
    }

    @Override
    public void onClick(View v) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        final EditText authors_name = dialogView.findViewById(R.id.authors_name);
        Button OK_Btn = dialogView.findViewById(R.id.ok_btn);
        Button CANCEL_btn = dialogView.findViewById(R.id.cancel_btn);

        AlertDialog.Builder noteTitleDialog = new AlertDialog.Builder(this);
        noteTitleDialog.setTitle("Author Details");
        noteTitleDialog.setView(dialogView);
        noteTitleDialog.setCancelable(false);

        final AlertDialog alertDialog = noteTitleDialog.create();

        alertDialog.show();

        OK_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String author = authors_name.getText().toString();
                if (!author.isEmpty()) {
                    showProgressDialog(true);
                    logUserIn(author);
                }
                else
                    showToast("Author Name cannot be empty");

            }
        });

        CANCEL_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void showProgressDialog(boolean toShow) {
        if (toShow) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Creating \\ Logging In User!\nPlease Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        else
            progressDialog.dismiss();
    }

    private void logUserIn(final String author) {
        String authenticationUrl = "https://note-it-android.us1a.cloud.realm.io/auth";
        SyncCredentials credentials = SyncCredentials.nickname(author, false);
        SyncUser.logInAsync(credentials, authenticationUrl, new SyncUser.Callback<SyncUser>() {
            @Override
            public void onSuccess(SyncUser user) {
                Realm.setDefaultConfiguration(SyncConfiguration.automatic());
                realm = Realm.getDefaultInstance();

                final Author note_author = new Author();

                note_author.setName(author);
                note_author.setTimeStamp(new Date());
                note_author.setId(user.getIdentity());

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.insert(note_author);
                    }
                });

                showProgressDialog(false);
                startViewNotesActivity();
            }

            @Override
            public void onError(ObjectServerError error) {
                showProgressDialog(false);
                showToast("An Error Occurred...\nPlease try again later...");
            }
    });
}

    private void startViewNotesActivity() {
        Intent intent = new Intent(this, Viewnotes.class);
        authors_id = SyncUser.current().getIdentity();
        intent.putExtra("author_id",authors_id);

        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}