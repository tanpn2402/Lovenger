package com.tanpn.messenger.login;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.receiver.MessageReceiver;
import com.tanpn.messenger.receiver.NetworkReceiver;
import com.tanpn.messenger.service.AppService;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity implements View.OnClickListener, SignUp.onSignUpResult, SetupAvatar.OnSetupCompletion, NetworkReceiver.OnNetworkReceiver {

    private EditText edtUsername, edtPassword;
    private Button btnLogin, btnSignUp, btnForgot;
    private ImageView imAppIcon, imBackground;

    private Snackbar signinStatus;

    private PrefUtil prefUtil;

    private boolean newbie = false;

    private void init(){
        prefUtil = new PrefUtil(this);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        edtUsername.setText(prefUtil.getString(R.string.pref_key_email, "email"));

        btnLogin = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnCreate);
        btnForgot = (Button) findViewById(R.id.btnForgot);
        btnSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgot.setOnClickListener(this);



        signinStatus = Snackbar.make(
                findViewById(R.id.activity_login),
                "",
                Snackbar.LENGTH_INDEFINITE);

        signinStatus.setAction("Ẩn", new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                signinStatus.dismiss();
            }
        });


        /**
         * checking internet connected
         * */


    }
    /**
     * read at: https://firebase.google.com/docs/auth/android/start/
     * */
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference photoRef;  // reference to storage

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        photoRef = storage.getReferenceFromUrl("gs://messenger-d08e4.appspot.com/avatar/");

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if(signinStatus.isShown())
                        signinStatus.dismiss();
                    signinStatus.setText("đăng nhập thành công").show();

                    // lấy thông tin của user
                    String name = user.getDisplayName();
                    String email = user.getEmail();
                    Uri photoUrl = user.getPhotoUrl();
                    String uid = user.getUid();


                    prefUtil.put(R.string.pref_key_email, email);
                    prefUtil.put(R.string.pref_key_password, edtPassword.getText().toString());
                    prefUtil.put(R.string.pref_key_uid, uid);
                    prefUtil.put(R.string.pref_key_user_photo, photoUrl);
                    prefUtil.put(R.string.pref_key_username, name);

                    prefUtil.apply();




                } else {
                    // User is signed out
                    prefUtil.put(R.string.pref_key_email, "");
                    prefUtil.put(R.string.pref_key_password, "");
                    prefUtil.put(R.string.pref_key_uid, "");
                    prefUtil.put(R.string.pref_key_user_photo, "");
                    prefUtil.put(R.string.pref_key_username, "");

                    prefUtil.apply();
                }
            }
        };*/
    }

    private void initReceiver(){
        NetworkReceiver networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);


        /*MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter f = new IntentFilter();
        f.addAction(MessageReceiver.ACTION_RECEIVE_MESSAGE);
        registerReceiver(messageReceiver, f);*/


    }


    private void startService(){
        if(!AppService.isRunning(this)){
            AppService.startAppService(this);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        initFirebase();

        startService();

        initReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (mAuthListener != null) {
        //    mAuth.removeAuthStateListener(mAuthListener);
        //}
    }

    //Set the radius of the Blur. Supported range 0 < radius <= 25
    private static final float BLUR_RADIUS = 25f;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCreate:
                signup();
                break;
            case R.id.btnSignIn:
                signin();
                break;
            case R.id.btnForgot:
                forgot();
                break;
        }
    }


    private void signin(){
        // dang nhap
        // valadite email
        /**
         * Tai sao k de firebase validate luon?
         * lam nhu the nay se tiet kiem 1 buoc neu user nhap email sai -> do ton dung luong
         * */
        if(!validateEmail(edtUsername) || edtPassword.getText().toString().isEmpty())
            return;

        if(signinStatus.isShown())
            signinStatus.dismiss();
        signinStatus.setText("đang đăng nhập...").setDuration(Snackbar.LENGTH_INDEFINITE).show();

        mAuth.signInWithEmailAndPassword(edtUsername.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // sign in fail
                            if(signinStatus.isShown())
                                signinStatus.dismiss();
                            signinStatus.setText("đăng nhập thất bại, vui lòng thử lại.").setDuration(Snackbar.LENGTH_INDEFINITE).show();
                        }
                        else {
                            // User is signed in
                            if(signinStatus.isShown())
                                signinStatus.dismiss();
                            signinStatus.setText("đăng nhập thành công").setDuration(Snackbar.LENGTH_LONG).show();

                            _password = edtPassword.getText().toString();
                            new setupPreferences().execute(task);
                        }

                    }
                });
    }

    private String _password;

    @Override
    public void onReceiver(boolean isConnected) {
        if(isConnected)
            Log.i("tag123", "connect");
        else
            signinStatus.setText("không thể kết nối đến internet.").setDuration(Snackbar.LENGTH_INDEFINITE).show();
    }

    private class setupPreferences extends AsyncTask<Task<AuthResult>, Void, Void>{


        @Override
        protected Void doInBackground(Task<AuthResult>... tasks) {
            Task<AuthResult> task = tasks[0];

            FirebaseUser user = task.getResult().getUser();
            // lấy thông tin của user
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String uid = user.getUid();


            prefUtil.put(R.string.pref_key_email, email);
            prefUtil.put(R.string.pref_key_password, _password);
            prefUtil.put(R.string.pref_key_uid, uid);
            if(photoUrl != null)
                prefUtil.put(R.string.pref_key_user_photo, photoUrl.getPath());

            if(name != null)
                prefUtil.put(R.string.pref_key_username, name);

            prefUtil.apply();
            SystemClock.sleep(1200);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // chuyen screen

            signinSuccess();
        }
    }

    private void signinSuccess(){
        if(newbie){
            // lan dau su dung thi huong dan chon avatar
            actionSetupAvatar();
        }
        else{
            // chuyen den Main Activity
            gotoMainAvtivity();
        }
    }

    private void signup(){
        // dang ki
        // hien thi dialog sign up
        new SignUp().show(getSupportFragmentManager(), "dialog");
    }

    private void forgot(){
        // quen mat khau
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private boolean validateEmail(EditText input){
        String text = input.getText().toString();
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(text);

        if(!matcher.matches()){
            // validate by using pattern
            // sai qui dinh
            input.setError("sai định dạng");
            return false;
        }
        return true;

    }

    private String fullname;
    private String email;
    private String password;

    private void setNotification(String text, int lenght){
        if(signinStatus.isShown())
            signinStatus.dismiss();
        signinStatus.setText(text).setDuration(lenght).show();
    }


    @Override
    public void onResult(String data) {
        if(signinStatus.isShown())
            signinStatus.dismiss();
        signinStatus.setText("đang tài khoản mới...").show();

        String[] info = data.split("\\|");
        email = info[1];
        password = info[2];
        fullname = info[0];

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            setNotification("tạo tài khoản mới thất bại.", Snackbar.LENGTH_INDEFINITE);

                        }
                        else {
                            FirebaseUser user =  task.getResult().getUser();

                            // quay trở lại màn hình đăng nhập
                            // sẽ điền trước email, và user sẽ nhập mật khẩu và log in
                            setNotification("tạo tài khoản mới thành công.", Snackbar.LENGTH_INDEFINITE);

                            edtUsername.setText(user.getEmail());
                            newbie = true;
                        }


                    }
                });


    }

    private void actionSetupAvatar(){
        /**
         * ln dau tien su dung
         * */
        SetupAvatar setupAvatar = new SetupAvatar();
        setupAvatar.setFullname(fullname);
        setupAvatar.show(getSupportFragmentManager(), "dialog");
    }


    /**
     * chon anh lam avatar: gio upload len firebase, setup lai auth
     * */
    @Override
    public void OnCompleteWithBitmap(Bitmap b) {
        if(b == null)
            return;

        // convert to upload to storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, baos);   // full quality 100
        byte[] bytes = baos.toByteArray();

        final String photoName = utils.generatePhotoId();
        UploadTask uploadTask = photoRef.child(photoName).putBytes(bytes);  // upload len storage

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                setupFirebaseAuth(taskSnapshot.getDownloadUrl().getPath());
            }
        });
    }

    @Override
    public void OnCompleteWithUri(Uri u) {
        if(u == null)
            return;

        final String photoName = utils.generatePhotoId();
        UploadTask uploadTask = photoRef.child(photoName).putFile(u);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // upload to storage success
                setupFirebaseAuth(taskSnapshot.getDownloadUrl().getPath());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {}
        });
    }

    @Override
    public void OnSkip(boolean skip) {
        if(skip)
            gotoMainAvtivity();
    }

    private void gotoMainAvtivity() {
        Intent in = new Intent(this, MainActivity.class);
        startActivity(in);
    }

    private void setupFirebaseAuth(String photoPath){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .setPhotoUri(Uri.parse(photoPath))
                .build();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                // hien thi thong bao snackbar
                                //if(signinStatus.isShown())
                                //    signinStatus.dismiss();
                                signinStatus.setText("cập nhật thông tin thành công").show();

                                // chuyen qua main screen
                                newbie = false;
                                signinSuccess();
                            }
                        }
                    });
        }

    }


    /**
     * RenderScript:
     * see at : http://stacktips.com/tutorials/android/how-to-create-bitmap-blur-effect-in-android-using-renderscript
     * */
    /*public Bitmap blur(Bitmap image) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }*/
}
