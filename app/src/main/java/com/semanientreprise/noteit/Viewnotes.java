package com.semanientreprise.noteit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.semanientreprise.noteit.adapter.NotesAdapter;
import com.semanientreprise.noteit.model.Author;
import com.semanientreprise.noteit.model.Notes;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class Viewnotes extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.notes_recView)
    RecyclerView notesRecView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private NotesAdapter notesRecyclerAdapter;
    private String authorsId;
    private Author author;
    private Realm realm;
    private RealmResults<Notes> notes;

    private RealmChangeListener<RealmResults<Notes>> realmChangeListener = new RealmChangeListener<RealmResults<Notes>>() {
        @Override
        public void onChange(RealmResults<Notes> notes) {
            notesRecyclerAdapter.setData(notes);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewnotes);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Realm.setDefaultConfiguration(SyncConfiguration.automatic());
        realm = Realm.getDefaultInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewNoteActivity();
            }
        });

        setUpNotesDisplay();
    }

    private void setUpNotesDisplay() {
        authorsId = getIntent().getStringExtra("author_id");

        RealmResults<Author> results = realm.where(Author.class).findAll();

        author = realm.where(Author.class).equalTo("id", authorsId).findFirst();

        notes = author.getNotes().sort("timestamp", Sort.ASCENDING);

        notes.addChangeListener(realmChangeListener);

        notesRecyclerAdapter = new NotesAdapter(this, notes);

        setTitle(author.getName() + "'s Notes");

        notesRecView.setLayoutManager(new LinearLayoutManager(this));
        notesRecView.setAdapter(notesRecyclerAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int position = viewHolder.getAdapterPosition();
            final String id = notesRecyclerAdapter.getItem(position).getNoteId();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Notes note = realm.where(Notes.class).equalTo("noteId",id).findFirst();
                    if (note != null)
                        note.deleteFromRealm();

                }
            });
        }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notesRecView);
    }

    private void startNewNoteActivity() {
        Intent intent = new Intent(this, NewNote.class);
        intent.putExtra("author_id", authorsId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewnotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
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
}