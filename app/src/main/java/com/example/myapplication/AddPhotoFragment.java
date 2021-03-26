package com.example.myapplication;


import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


public class AddPhotoFragment extends Fragment {

    VideoView videoView;
    EditText videoText;
    Button uploadVideo;
    ProgressBar progressBar;
    ImageButton addVideoButton;
    private static final int PICK_VIDEO = 1;
    private Uri videoUri;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Member member;
    UploadTask uploadTask;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == PICK_VIDEO || resultCode == Activity.RESULT_OK || data!= null || data.getData() != null){
            videoUri = data.getData();

            videoView.setVideoURI(videoUri);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_photo,container, false);

        videoView = view.findViewById(R.id.videoView);
        videoText = view.findViewById(R.id.videoText);
        uploadVideo = view.findViewById(R.id.uploadButton);
        progressBar = view.findViewById(R.id.uploadProgressBar);
        addVideoButton = view.findViewById(R.id.addVideoButton);
        mediaController = new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        videoView.start();
        member = new Member();
        storageReference = FirebaseStorage.getInstance().getReference("Video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");

        uploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideo();

            }
        });


        addVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_VIDEO);
            }
        });







        return view;


    }
    private String getExt (Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void UploadVideo (){
        String videoName = videoText.getText().toString().trim();
        if (videoUri != null || !videoName.isEmpty() || videoName != ""){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NotNull Task<Uri> task){

                            if(task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "Data saved", Toast.LENGTH_LONG).show();

                                member.setName(videoName);
                                member.setVideoUrl(downloadUrl.toString());
                                String i = databaseReference.push().getKey();
                                databaseReference.child(i).setValue(member);
                            }else{
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }else{
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_LONG).show();
        }
    }



}
