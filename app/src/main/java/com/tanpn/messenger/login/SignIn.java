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
import android.support.v4.view.ViewPager;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tanpn.messenger.MainActivity;
import com.tanpn.messenger.R;
import com.tanpn.messenger.receiver.NetworkReceiver;
import com.tanpn.messenger.service.AppService;
import com.tanpn.messenger.utils.PrefUtil;
import com.tanpn.messenger.utils.utils;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity
        implements NetworkReceiver.OnNetworkReceiver, SignInFragment.onSignInCompletion {


    private Button btnFunction;
    private ImageView imAppIcon, imBackground;

    private Snackbar signinStatus;

    private PrefUtil prefUtil;

    private boolean newbie = false;
    private ViewPager mViewPager;

    private void init(){

        prefUtil = new PrefUtil(this);

        /**
         * kiem tra xem co phai newbie k
         *
         * */
        if(!prefUtil.getBoolean(R.string.pref_key_first_use, false)){
            newbie = true;
            prefUtil.put(R.string.pref_key_first_use, true);
            prefUtil.apply();
        }

        /**
         * kiem tra xem account --> k login nua
         *
         * */
        if(!prefUtil.getString(R.string.pref_key_email, "null").equals("null") &&
                !prefUtil.getString(R.string.pref_key_password, "null").equals("null")){
            signinSuccess();

            return ; // khong init nua
        }


        /**
         * init compomments
         * */

        mViewPager = (ViewPager) findViewById(R.id.myViewPager);
        mViewPager.setAdapter(new SignInAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: // signin view:
                        btnFunction.setText("Đăng kí");
                        break;
                    case 1: // sign up view
                        btnFunction.setText("Đăng nhập");
                        break;
                    default: // forgot view
                        btnFunction.setText("Đăng nhập");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        btnFunction = (Button) findViewById(R.id.btnFunction);
        btnFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mViewPager.getCurrentItem()){
                    case 0: // signin view:
                        mViewPager.setCurrentItem(1);
                        btnFunction.setText("Đăng nhập");
                        break;
                    case 1: // sign up view
                        mViewPager.setCurrentItem(0);
                        btnFunction.setText("Đăng kí");
                        break;
                    default: // forgot view
                        mViewPager.setCurrentItem(0);
                        btnFunction.setText("Đăng kí");
                }
            }
        });

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


    }
    /**
     * read at: https://firebase.google.com/docs/auth/android/start/
     * */
    private FirebaseAuth mAuth;

    private DatabaseReference userRef;

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase root = FirebaseDatabase.getInstance();
        userRef = root.getReference("user");

    }

    private void initReceiver(){
        NetworkReceiver networkReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

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


        initFirebase();

        init();


        startService();

        //initReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
                            //signinStatus.setText("đăng nhập thất bại, vui lòng thử lại.").setDuration(Snackbar.LENGTH_INDEFINITE).show();
                            signinStatus.setText(task.getException().toString()).setDuration(Snackbar.LENGTH_INDEFINITE).show();

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


    /**
     * implemments tu interface onSigninCompletion
     *
     * */
    private EditText edtUsername, edtPassword, edtFullname;
    @Override
    public void onSignIn(EditText edt1, EditText edt2) {
        edtUsername = edt1;
        edtPassword = edt2;

        signin();
    }

    @Override
    public void onSignUp(EditText edt1, EditText edt2, EditText edt3) {
        edtFullname = edt1;
        edtUsername = edt2;
        edtPassword = edt3;

        signup();
    }

    @Override
    public void onForgot(EditText edt1) {
        edtUsername = edt1;

        forgot();
    }

    @Override
    public void onForgetTextviewClick() {
        mViewPager.setCurrentItem(2); // chuyen qua forgot view
        btnFunction.setText("Đăng nhập");
    }


    /**
 * khi dang nhap thanh cong thi luu cac thong tin co ban cua user vao share preferences
 * bo vao thread chay cho nhanh :v
 *
 * */
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
            SystemClock.sleep(700);
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
            showIntroScreen();
        }
        else{
            // chuyen den Main Activity
            checkHasGroup(prefUtil.getString(R.string.pref_key_uid));
        }
    }

    /**
     * doi voi nguoi dau tien su dung ung dung thi show intro scrren
     * */
    private void showIntroScreen(){
        Intent in = new Intent(this, IntroActivity.class);
        startActivity(in);
    }

    private void checkHasGroup(String uid){
        /**
         * kiem tra xem user nay co group nao hay chua
         * path:   root/user/uid/groups/
         * */
        DatabaseReference groupRef = userRef.child(uid).child("groups");

        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue().toString();
                if(data.equals("false")){
                    //prefUtil.put(R.string.pref_key_groups, null);
                    //prefUtil.apply();
                    gotoMainAvtivity(false);
                }
                else{
                    prefUtil.put(R.string.pref_key_groups, data);
                    prefUtil.apply();
                    gotoMainAvtivity(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    /**
     * neu dang nhap thanh cong va k phai la lan dau su dung nen skip qua main activity luon
     *
     * */


    private void gotoMainAvtivity(boolean hasGroup) {

        /**
         * Kiem tra xem da co nhom chua
         * neu khong thi hien thi man hinh addfirend
         * */

        if (!hasGroup){
            Intent in = new Intent(this, AddFriendActivity.class);
            startActivity(in);
        }
        else {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        }


    }


    /**
     * sign up: dang ki
     * duoc nhan thogn tin tu interface .... trong class SignInFragment
     *
     * */
    private void signup(){
        // dang ki

        if(!validateEmail(edtUsername))
            return;

        if(!validatePassword(edtPassword))
            return;


        if(signinStatus.isShown())
            signinStatus.dismiss();
        signinStatus.setText("đang tài khoản mới...").show();

        email = edtUsername.getText().toString();
        password = edtPassword.getText().toString();
        fullname = edtFullname.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            setNotification(task.getException().toString(), Snackbar.LENGTH_INDEFINITE);

                        }
                        else {

                            // setup name for new account
                            setupFirebaseAuth(task.getResult().getUser());
                        }


                    }
                });
    }

    private void forgot(){
        // quen mat khau

        if(!validateEmail(edtUsername))
            return;
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

    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";

    private boolean validatePassword(EditText input){
        String text = input.getText().toString();
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
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

    /**
     * cap nhat thong tin cho user
     * */
    private void setupFirebaseAuth(final FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullname)
                    .build();

        if(user != null){
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                setupDatabase(user);

                            }
                            else{
                                // delete account
                                deleteAuth(user);
                            }
                        }
                    });
        }

    }

    /**
     * upload databse for GROUPS node
     * */

    private void setupDatabase(final FirebaseUser user){
        Log.i("aaa", user.toString());
        userRef.child(user.getUid()).child("groups").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // thong bao tao tai khoan thanh cong
                signinStatus.setText("tạo tài khoản thành công").setDuration(Snackbar.LENGTH_SHORT).show();

                // quay tro lại man hinh dang nhap
                mViewPager.setCurrentItem(0);
                btnFunction.setText("Đăng kí");

                edtUsername.setText(user.getEmail());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                deleteAuth(user);

                // thong bao tao tai khoan thanh cong
                signinStatus.setText("tạo tài khoản thất bại").setDuration(Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * o 1 buoc nao do ma that bai thi xoa tai khoan
     * */

    private void deleteAuth(FirebaseUser user){
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }
}
