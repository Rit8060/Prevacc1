package com.ctc.accihelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Progress extends AppCompatActivity {
    FirebaseFirestore db;
    private static final String PHONE = "phone";
    private static final int GALLERY_INTENT=2;
    private ImageView mImage;
    private Button mButton;
    private static final int CAMERA_REQUEST_CODE = 0;
    private StorageReference mStorage;
    private Uri mainImageURI=null;
    private String user_id;
    private boolean isChnaged=false;
    private EditText setupPhone;
    private CircleImageView setupImage;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseAuth fireabseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setupPhone=(EditText) findViewById( R.id.setup_phone);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> newData = new HashMap<>();
        newData.put(PHONE, String.valueOf(setupPhone));
        db.collection("User").document("Phones").set(newData).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        mStorage= FirebaseStorage.getInstance().getReference();
        mButton=(Button) findViewById(R.id.setup_btn);
        mImage=(ImageView) findViewById(R.id.imageView);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
         Intent intent=new Intent();
         intent.setType("image/*");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent,"Select Image"),GALLERY_INTENT);
            }
        });


        }
        protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_INTENT&&resultCode==RESULT_OK){
            Uri uri=data.getData();
            StorageReference filepath=mStorage.child("Photos").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Progress.this,"License and Contact Uploaded",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Progress.this, MainActivity.class);
                    startActivity(intent );
                }
            });
        }

        }
       // mButton.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
              //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //    if (ContextCompat.checkSelfPermission(Progress.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                  //      Toast.makeText(Progress.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    //    ActivityCompat.requestPermissions(Progress.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    //} else {
                      //  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    //}

                //}
            //}
        //});


//        setupBtn.setEnabled( false );
//        firebaseFirestore.collection(   "Users" ).document(user_id).get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    if(task.getResult().exists()){
//
//                        String phone=task.getResult().getString( "phone" );
//                        String image=task.getResult().getString( "image" );
//                        mainImageURI= Uri.parse(image);
//                        setupPhone.setText(phone);
//                        RequestOptions placeholderRequest=new RequestOptions();
//                        placeholderRequest.placeholder(R.mipmap.default_image);
//                        Glide.with(Progress.this).setDefaultRequestOptions(  placeholderRequest).load(image).into(setupImage);
//                    }
//                }    else{
//                    String error=task.getException().getMessage();
//                    Toast.makeText( Progress.this,"FIRESTORE Retrieval Error:"+error,Toast.LENGTH_LONG ).show();
//                }
//            }
//        } );
//        setupBtn=(Button) findViewById( R.id.setup_btn );
//        fireabseAuth= FirebaseAuth.getInstance();
//        user_id=fireabseAuth.getCurrentUser().getUid();
//        firebaseFirestore=FirebaseFirestore.getInstance();
//        storageReference= FirebaseStorage.getInstance().getReference();
//        setupBtn.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String user_name = setupPhone.getText().toString();
//                if (isChnaged) {
//
//                    if (!TextUtils.isEmpty( user_name ) && mainImageURI != null) {
//                        user_id = fireabseAuth.getCurrentUser().getUid();
//                        StorageReference image_path = storageReference.child( "Profile Images" ).child( user_id + ".jpg" );
//                        image_path.putFile( mainImageURI ).addOnCompleteListener( new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    storeFirestore( task, user_name );
//                                } else {
//                                    String error = task.getException().getMessage();
//                                    Toast.makeText( Progress.this, "Image Error:" + error, Toast.LENGTH_LONG ).show();
//                                }
//
//
//                            }
//                        } );
//
//
//                    }
//                }else{
//                    storeFirestore( null,user_name );
//                }
//            }
//        } );
//
//    }
//    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {
//        Task<Uri> download_uri = task.getResult().getStorage().getDownloadUrl();
//
//        Map<String,String> userMap=new HashMap<>(  );
//        userMap.put("phone",user_name);
//        firebaseFirestore.collection( "Users" ).document(user_id).set(userMap).addOnCompleteListener( new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(Progress.this,"The user Settings are updated",Toast.LENGTH_LONG).show();
//                    Intent mainIntent=new Intent(Progress.this,MainActivity.class);
//                    startActivity(mainIntent );
//                    finish();
//
//                }   else{
//                    String error=task.getException().getMessage();
//                    Toast.makeText( Progress.this,"FIRESTORE Error:"+error,Toast.LENGTH_LONG ).show();
//                }
//                setupBtn.setEnabled( true );
//            }
//        } );
//
//}

  //  @Override
  //  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
      //  if(requestCode==CAMERA_REQUEST_CODE&& resultCode==RESULT_OK){

        //    Uri uri=null;
          //  uri=data.getData();


            //StorageReference filepath =mStorage.child("Photos").child(uri.getLastPathSegment());

            //filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              //  @Override
                //public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                  //  Toast.makeText(Progress.this,"upload finished",Toast.LENGTH_LONG).show();
                }
            //}).addOnFailureListener(new OnFailureListener() {
              //  @Override
                //public void onFailure(@NonNull Exception e) {
                  //  Toast.makeText(Progress.this, "Sending failed", Toast.LENGTH_SHORT).show();

                //}
            //});
        //}
    //}
//}
