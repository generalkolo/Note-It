package com.semanientreprise.noteit.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semanientreprise.noteit.NewNote;
import com.semanientreprise.noteit.R;
import com.semanientreprise.noteit.model.Notes;

import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    List<Notes> notes;
    Context context;

    public NotesAdapter(Context context,List<Notes> notes) {
        this.context = context;
        this.notes = notes;
    }

    public void setData(List<Notes> setDataNotes) {
        if (setDataNotes == null) {
            setDataNotes = Collections.emptyList();
        }
        this.notes = setDataNotes;
        notifyDataSetChanged();
    }

    public Notes getItem(int position) {
        return notes.get(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_layout_note, parent, false);
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position)  {
        final Notes notes = getItem(position);
        if (notes != null) {
            holder.Title.setText(notes.getNoteTitle());
            holder.Title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, NewNote.class);
                    intent.putExtra("note_id", notes.getNoteId());
                    context.startActivity(intent);
                }
            });
        }
    }

    class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView Title;

        NotesViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.note_title);
        }
    }
}
