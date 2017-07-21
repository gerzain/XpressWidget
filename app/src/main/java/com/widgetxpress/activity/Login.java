package com.widgetxpress.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.widgetxpress.R;
import com.widgetxpress.Xpress;
import com.widgetxpress.util.CircleTransform;

/**
 * Created by Irving on 19/07/2017.
 */



public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    private static final String TAG = Login.class.getSimpleName();
    private GoogleApiClient googleApiClient;
    private  static  final int SIGN_IN_CODE = 777;
    public static   String  id_google;
    private SignInButton btnSignIn;
    private Button btnSignOut, btnRevokeAccess;
    private LinearLayout llProfileLayout;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private ProgressDialog mProgressDialog;
    private RemoteViews remoteViews;
    private ComponentName watchWidget;
    private int mAppWidgetId;
    public static final String PREFS_NAME="XPRESS_PREFS";
    public static final String PREF_ID_GOOGLE="PREF_USER_ID";
    private static boolean mIsLoggedIn=false;
    private  SharedPreferences mPrefs;

    public  static boolean ismIsLoggedIn()
    {
        return mIsLoggedIn;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setResult(RESULT_CANCELED);
        btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        llProfileLayout = (LinearLayout) findViewById(R.id.llProfile);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        btnSignIn.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        btnSignIn.setColorScheme(SignInButton.COLOR_DARK);

        mPrefs =getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);


        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone())
        {
            GoogleSignInResult  result=opr.get();
            handleSignInResult(result);
        }
        else
        {
             showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog!=null && mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_CODE);
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
               if(status.isSuccess())
               {
                   updateUI(false);
                   setResultAndFinish();
                   broadcastWidgetUpdate();
                   SharedPreferences.Editor editor = mPrefs.edit();
                   editor.putString(PREF_ID_GOOGLE,null);
                   editor.apply();
                   mIsLoggedIn=false;

               }
               else
               {
                   Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
               }
            }
        });
    }
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
                mIsLoggedIn=false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_CODE)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if(result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            final RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.xpress);
            final ComponentName watchWidget = new ComponentName(getApplicationContext(), Xpress.class);
             assert account != null;
             id_google= account.getId();
             Log.i(TAG,id_google);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_ID_GOOGLE,id_google);
            editor.apply();
             txtName.setText(account.getDisplayName());
             txtEmail.setText(account.getEmail());
              Picasso.with(getApplicationContext())
                        .load(account.getPhotoUrl())
                         .centerInside()
                         .resize(100,100)
                         .transform(new CircleTransform())
                        .into(imgProfilePic);

                //Para la imagen de perfil  dentro del widget
            Picasso.with(getApplicationContext())
                    .load(account.getPhotoUrl())
                    .placeholder(R.drawable.ic_account_circle)
                    .error(R.drawable.ic_account_circle)
                    .centerInside()
                    .resize(80,80)
                    .transform(new CircleTransform())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            remoteViews.setImageViewBitmap(R.id.btn_logout,bitmap);
                            appWidgetManager.updateAppWidget(watchWidget,remoteViews);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                updateUI(true);
                mIsLoggedIn=true;


        }
        else
        {
          updateUI(false);
        }

    }
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private void updateUI(boolean isSignedIn)
    {

        if (isSignedIn)
        {
            AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(getApplicationContext());
            remoteViews=new RemoteViews(getApplicationContext().getPackageName(),R.layout.xpress);
            watchWidget=new ComponentName(getApplicationContext(), Xpress.class);
            remoteViews.setViewVisibility(R.id.btn_login,View.GONE);
            remoteViews.setViewVisibility(R.id.btn_logout,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.btn_agregar,View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
            appWidgetManager.updateAppWidget(watchWidget,remoteViews);


        } else
            {
            AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(getApplicationContext());
             remoteViews=new RemoteViews(getApplicationContext().getPackageName(),R.layout.xpress);
                watchWidget=new ComponentName(getApplicationContext(),Xpress.class);
                remoteViews.setViewVisibility(R.id.btn_login,View.VISIBLE);
                remoteViews.setViewVisibility(R.id.btn_logout,View.GONE);
                remoteViews.setViewVisibility(R.id.btn_agregar,View.GONE);
                remoteViews.setEmptyView(R.id.widget_list,R.id.empty_view);
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.GONE);
            btnRevokeAccess.setVisibility(View.GONE);
            llProfileLayout.setVisibility(View.GONE);
                appWidgetManager.updateAppWidget(watchWidget,remoteViews);


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.e(TAG,"Fallo conexión");
    }

    @Override
    public void onClick(View view) {
        int id= view.getId();

        switch (id)
        {
            case R.id.btn_sign_in:
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                break;
            case R.id.btn_revoke_access:
                revokeAccess();
                break;

        }
    }
    private void broadcastWidgetUpdate()
    {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this,Xpress.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
        sendBroadcast(intent);
    }

    private void setResultAndFinish()
    {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(Activity.RESULT_OK, resultValue);
        finish();
    }
}
