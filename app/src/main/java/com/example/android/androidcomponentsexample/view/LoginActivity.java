package com.example.android.androidcomponentsexample.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.androidcomponentsexample.R;
import com.example.android.androidcomponentsexample.model.User;
import com.example.android.androidcomponentsexample.utils.ComandosHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textAccount, textForm, textPhone, textSocial;
    private TextView googleName, googleMail;
    private ImageView googleProfilePic;
    private Button btnRegisterForm;
    //Button phone
    private Button btnCode, btnResend, btnLogin;
    private Button btn_loginPhone, btn_logOut;
    private ImageView logOutTest;
    private ProgressBar formProgress;
    //EditText form
    private EditText mName, mEmail, mPhone, mPassword, mPasswordConfirm;
    //EditText phone
    private EditText mPhoneNumber, mPhoneCode;
    //EditText signInAccount
    private EditText signInMail, signInPassword;
    //Input form
    private TextInputLayout inputName, inputEmail, inputPhone, inputPassword, input_confirmPass;
    //Layouts
    private ConstraintLayout consOptions;
    private ConstraintLayout consLogin;
    private ConstraintLayout consForm;
    private ConstraintLayout consPhone;
    private ConstraintLayout consSocial;
    private ConstraintLayout consAlreadyLogin;
    //Layouts back
    private ConstraintLayout backLogin;
    private ConstraintLayout backForm;
    private ConstraintLayout backPhone;
    private ConstraintLayout backSocial;

    private RadioButton chkLogin;
    private RadioButton chkForm;
    private RadioButton chkPhone;
    private RadioButton chkSocial;

    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private PhoneAuthProvider fAuth;
    private String userID;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private SignInButton googleBtn;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleData;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComandosHelper.makeFullScreen(this);
        setContentView(R.layout.activity_login);
        init();
        setButtons();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fAuth = PhoneAuthProvider.getInstance();
        initFireBasePhoneCallbacks();
        callbackManager = CallbackManager.Factory.create();

        if (mAuth.getUid() != null) {
            consOptions.setVisibility(View.GONE);
            consAlreadyLogin.setVisibility(View.VISIBLE);
        }
        setGooglePhoneLogin();
        setLoginOptions();
        setFacebookLogin();

        mEmail.addTextChangedListener(new LoginActivity.MyTextWatcher(mEmail));
        mPasswordConfirm.addTextChangedListener(new LoginActivity.MyTextWatcher(mPasswordConfirm));
    }
    private void init() {

        //Facebook
        loginButton = findViewById(R.id.login_buttonFacebook);
        //Constraint login
        consOptions = findViewById(R.id.login_loginOptions);
        consLogin = findViewById(R.id.actLogin_constrLogin);
        consForm = findViewById(R.id.actLogin_constrRegister);
        consPhone = findViewById(R.id.actLogin_constrPhone);
        consSocial = findViewById(R.id.actLogin_constrSocial);
        consAlreadyLogin = findViewById(R.id.actLogin_successLogin);
        //Back constraint
        backLogin = findViewById(R.id.actLogin_backOptionsAccount);
        backForm = findViewById(R.id.actLogin_backOptionsForm);
        backPhone = findViewById(R.id.actLogin_backOptionsPhone);
        backSocial = findViewById(R.id.actLogin_backOptionsSocial);
        //Check Login + Text
        chkLogin = findViewById(R.id.login_chkAlreadyRegister);
        textAccount = findViewById(R.id.act_loginWithAccount);
        chkForm = findViewById(R.id.login_chkRegisterFull);
        textForm = findViewById(R.id.act_loginRegisterFull);
        chkPhone = findViewById(R.id.login_chkRegisterPhone);
        textPhone = findViewById(R.id.act_loginRegisterPhone);
        chkSocial = findViewById(R.id.login_chkRegisterSocial);
        textSocial = findViewById(R.id.act_loginRegisterSocial);
        //input Form
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPhone = findViewById(R.id.input_telephone);
        inputPassword = findViewById(R.id.input_pass);
        input_confirmPass = findViewById(R.id.input_confirmPass);
        //editText form
        mName = findViewById(R.id.register_Lname);
        mEmail = findViewById(R.id.register_email);
        mPhone = findViewById(R.id.register_telephone);
        mPassword = findViewById(R.id.register_password);
        mPasswordConfirm = findViewById(R.id.register_confirmPassword);
        //editText phone
        mPhoneNumber = findViewById(R.id.login_phoneRegister);
        mPhoneCode = findViewById(R.id.login_phoneRegisterCode);
        //editText SignInAccount
        signInMail = findViewById(R.id.login_email);
        signInPassword = findViewById(R.id.login_password);
        //progressBar
        formProgress = findViewById(R.id.register_progressBar);
        //Buttons
        btnRegisterForm = findViewById(R.id.btn_register);
        btnCode = findViewById(R.id.btn_SendCode);
        btnResend = findViewById(R.id.btn_reSendCode);
        btnLogin = findViewById(R.id.btn_login);
        btn_logOut = findViewById(R.id.btn_logOut);
        logOutTest = findViewById(R.id.logOutTest);
        btn_loginPhone = findViewById(R.id.btn_loginPhone);
        googleBtn = findViewById(R.id.sign_in_buttonGoogle);
        //googleName = findViewById(R.id.login_socialName);
        //googleMail = findViewById(R.id.login_socialEmail);
        //googleProfilePic = findViewById(R.id.googleProfileImage);
    }

    private void setButtons() {
        btnCode.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegisterForm.setOnClickListener(this);
        btn_loginPhone.setOnClickListener(this);
        btn_logOut.setOnClickListener(this);
        logOutTest.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        googleBtn.setOnClickListener(this);
    }

    private void setLoginOptions() {
        setCheckBox(chkLogin, consLogin, textAccount);
        backToOptions(backLogin, consLogin);
        setCheckBox(chkForm, consForm, textForm);
        backToOptions(backForm, consForm);
        setCheckBox(chkPhone, consPhone, textPhone);
        backToOptions(backPhone, consPhone);
        setCheckBox(chkSocial, consSocial, textSocial);
        backToOptions(backSocial, consSocial);
    }

    private void backToOptions(ConstraintLayout btnConstraint, ConstraintLayout layout) {
        btnConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility(View.GONE);
                consOptions.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registerOk();
                break;
            case R.id.btn_SendCode:
                if (!mPhoneNumber.getText().toString().isEmpty()) {
                    fAuth.verifyPhoneNumber(
                            mPhoneNumber.getText().toString().trim(), 60, TimeUnit.SECONDS,
                            this, mCallbacks);
                } else {
                    Toast.makeText(this, "Agregue un numero telefonico", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_reSendCode:
                fAuth.verifyPhoneNumber(
                        mPhoneNumber.getText().toString(), 1, TimeUnit.MINUTES,
                        this, mCallbacks, mResendToken);
                break;
            case R.id.btn_loginPhone:
                verifyPhoneCodeAndLogin();
                break;
            case R.id.btn_logOut:
                //LogOut Firebase
                mAuth.signOut();
                //LogOut Google
                googleSignInClient.signOut();
                //LogOut Facebook
                LoginManager.getInstance().logOut();

                Toast.makeText(this, "Deslogueado exitoso", Toast.LENGTH_SHORT).show();
                consOptions.setVisibility(View.VISIBLE);
                consAlreadyLogin.setVisibility(View.GONE);
                break;
            case R.id.btn_login:
                signInWithAccount();
                break;
            case R.id.sign_in_buttonGoogle:
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
                break;
        }
    }

    private void registerOk() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        if (!validatePhone()) {
            return;
        }
        formProgress.setVisibility(View.VISIBLE);

        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String fullName = mName.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario creado exitosamente!", Toast.LENGTH_LONG).show();
                    userID = mAuth.getCurrentUser().getUid();
                    User formUser = new User();
                    formUser.setFullName(fullName);
                    formUser.setEmail(email);
                    formUser.setTelephone(phone);
                    formUser.setPassword(password);
                    formUser.setRegisterType("Formulario");
                    addUserTodatabase(formUser, "Form: " + userID);
                    setVisibility(consForm);
                }
                formProgress.setVisibility(View.GONE);
            }
        });

    }

    private void setGooglePhoneLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initFireBasePhoneCallbacks() {
        ///Add this method below auth initialization in the onCreate method.

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(LoginActivity.this, "Verification Complete", Toast.LENGTH_LONG).show();
                setVisibility(consPhone);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verification Failed:  " + e, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void verifyPhoneCodeAndLogin() {
        //Verifica codigo enviado al telefono
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mPhoneCode.getText().toString());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();

                            User phoneUser = new User();
                            String phone = mPhoneNumber.getText().toString().trim();
                            phoneUser.setTelephone(phone);
                            phoneUser.setRegisterType("Phone");
                            addUserTodatabase(phoneUser, "Phone: " + phone);
                            setVisibility(consPhone);

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void setFacebookLogin(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginButton.setPermissions(Arrays.asList("email", "public_profile"));
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Glide.with(LoginActivity.this).load(user.getPhotoUrl()).into(profileImage);
                            //user.getDisplayName();
                            User facebookUser = new User();
                            facebookUser.setEmail(user.getEmail());
                            facebookUser.setFullName(user.getDisplayName());
                            addUserTodatabase(facebookUser,"Facebook: "+user.getUid());
                            setVisibility(consSocial);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), getString(R.string.default_web_client_id));
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            User googleUser = new User();
                            googleUser.setFullName(googleData.getDisplayName());
                            googleUser.setEmail(googleData.getEmail());
                            googleUser.setRegisterType("Google");
                            addUserTodatabase(googleUser, "Google: " + googleData.getId());
                        } else {
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make((findViewById(R.id.constraint_loginContainer)), "Authentication Failed.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addUserTodatabase(User user, String documentPath) {
        DocumentReference documentReference = fStore.collection("usuarios").document(documentPath);
        documentReference.set(user)
                .addOnSuccessListener(aVoid -> Log.d("Creado", "usuario creado: " + userID))
                .addOnFailureListener(e -> Log.d("ERROR", "onFailure: " + e.toString()));

    }

    private void signInWithAccount() {
        String email = signInMail.getText().toString().trim();
        String password = signInPassword.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, user.getEmail() + " Logueado exitosamente", Toast.LENGTH_SHORT).show();
                            setVisibility(consLogin);
                        } else {
                            Log.w("TAG", "ERROR EN LOGUEO", task.getException());
                            Toast.makeText(LoginActivity.this, "Error al ingresar, revise los datos e intente nuevamente",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateName() {
        if (mName.getText().toString().trim().isEmpty()) {
            inputName.setError(getString(R.string.emptyField));
            requestFocus(inputName);
            return false;
        } else {
            inputName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = mEmail.getText().toString().trim();

        if (!isValidEmail(email)) {
            inputEmail.setError(getString(R.string.mailFormat));
            requestFocus(inputEmail);
            return false;
        } else if (email.isEmpty()) {
            inputEmail.setError(getString(R.string.emptyField));
            requestFocus(inputEmail);
            return false;
        } else {
            inputEmail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        String phone = mPhone.getText().toString().trim();

        if (!isValidPhone(phone)) {
            inputPhone.setError(getString(R.string.phoneFormat));
            requestFocus(inputPhone);
            return false;
        } else if (phone.isEmpty()) {
            inputPhone.setError(getString(R.string.emptyField));
            requestFocus(inputPhone);
            return false;
        } else {
            inputPhone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        if (mPassword.getText().toString().trim().isEmpty()) {
            inputPassword.setError(getString(R.string.emptyField));
            requestFocus(inputPassword);
            return false;
        } else if (!mPassword.getText().toString().trim().equals(mPasswordConfirm.getText().toString().trim())) {
            inputPassword.setError(getString(R.string.passwordWrong));
            requestFocus(input_confirmPass);
            return false;
        } else if (mPassword.getText().toString().length() < 6) {
            inputPassword.setError("Minimo 6 caracteres");
        } else {
            inputPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setVisibility(ConstraintLayout layout) {
        layout.setVisibility(View.GONE);
        consAlreadyLogin.setVisibility(View.VISIBLE);
    }

    private void setCheckBox(RadioButton radioButton, ConstraintLayout constraintLayout, TextView text) {
        text.setOnClickListener(v -> {
            consOptions.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.VISIBLE);
        });
        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                radioButton.setChecked(false);
                consOptions.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.register_email:
                    validateEmail();
                    break;
                case R.id.register_password:
                case R.id.register_confirmPassword:
                    validatePassword();
                    break;
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ComandosHelper.makeFullScreen(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ComandosHelper.makeFullScreen(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        googleData = account;

/*                        googleName.setText(account.getDisplayName());
                        googleMail.setText(account.getEmail());
                        Glide.with(this).load(googleData.getPhotoUrl()).centerInside().into(googleProfilePic);
                       */
                        task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                setVisibility(consSocial);
                                firebaseAuthWithGoogle(account);
                            }
                        });
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

}
