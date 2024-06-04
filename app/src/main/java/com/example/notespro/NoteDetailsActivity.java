package com.example.notespro;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextView;
    ProgressBar editNoteProgressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.title_editText);
        contentEditText = findViewById(R.id.content_editText);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title_TextView);
        deleteNoteTextView  =findViewById(R.id.delete_note_textView);
        editNoteProgressBar = findViewById(R.id.editNoteProgressBar);

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }


        titleEditText.setText(title);
        contentEditText.setText(content);
        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextView.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v) -> saveNote());

        deleteNoteTextView.setOnClickListener((v) -> deleteNoteFromFirebase());

    }

    private void deleteNoteFromFirebase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!!!");
        builder.setMessage("\nDo you want to delete this note?\nAfter clicking 'Yes' it will delete permanently.");
        builder.setPositiveButton("Yes", (dialog, which) -> {

            DocumentReference documentReference;
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);

            documentReference.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(NoteDetailsActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(NoteDetailsActivity.this, "Failed while deleting note", Toast.LENGTH_SHORT).show();
                }
            });

        });
        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void saveNote() {
        String titleText = titleEditText.getText().toString();
        String contentText = contentEditText.getText().toString();
        if(titleText==null || titleText.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(titleText);
        note.setContent(contentText);
        note.setTimestamp(Timestamp.now());
        
        saveNoteToFirebase(note);
    }

    private void saveNoteToFirebase(Note note) {
        editNoteProgressBar.setVisibility(View.VISIBLE);
        saveNoteBtn.setVisibility(View.GONE);
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(NoteDetailsActivity.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(NoteDetailsActivity.this, "Failed while adding note", Toast.LENGTH_SHORT).show();
                editNoteProgressBar.setVisibility(View.GONE);
                saveNoteBtn.setVisibility(View.VISIBLE);
            }
        });
    }


}