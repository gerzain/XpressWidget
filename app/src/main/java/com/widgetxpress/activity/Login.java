package com.widgetxpress.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.widgetxpress.R;
import com.widgetxpress.WidgetFetchService;
import com.widgetxpress.WidgetService;
import com.widgetxpress.Xpress;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.GoogleUser;
import com.widgetxpress.util.CircleTransform;
import com.widgetxpress.util.SessionPrefs;
import com.android.volley.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


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
    private CircleImageView imgProfilePic;
    private TextView txtName, txtEmail;
    private ProgressDialog mProgressDialog;
    private int mAppWidgetId;
    private String nombre;
    public static final String PREFS_NAME="XPRESS_PREFS";
    public static final String pref_id="PREF_USER_ID";
    private static boolean mIsLoggedIn=false;
    private  SharedPreferences mPrefs;
    private Gson gson;
    GoogleUser registrar_usuario;

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
        imgProfilePic = (CircleImageView) findViewById(R.id.imgProfilePic);
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
       // removeActividad();

        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gson=gsonBuilder.create();



        mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_login:
               finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


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
             //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                   // hideProgressDialog();
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
                    finish();
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
               // SessionPrefs.get(getApplicationContext()).logOut();
               // logoutRefresh();
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
             assert account != null;
             id_google= account.getId();
             Log.i(TAG,id_google);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(pref_id, id_google);
            editor.apply();

            SessionPrefs.get(getApplicationContext()).guardar_id(id_google);
             txtName.setText(account.getDisplayName());
             txtEmail.setText(account.getEmail());
             nombre=account.getDisplayName();
              Picasso.with(getApplicationContext())
                        .load(account.getPhotoUrl())
                      .placeholder(R.drawable.ic_account_circle_black)
                      .error(R.drawable.ic_account_circle_black)
                        .into(imgProfilePic);
                updateUI(true);
                mIsLoggedIn=true;
            Uri photoUri=account.getPhotoUrl();
            if(photoUri!=null) {


                String url_imagen = photoUri.toString();
                GoogleUser registrar_usuario = new GoogleUser(
                        id_google,
                        account.getDisplayName(),
                        account.getEmail(),
                        url_imagen,
                        "0",
                        "googleplus");

                final String user = gson.toJson(registrar_usuario);
                Log.i(TAG, user);
                String url_usuario = null;
                try {
                    url_usuario = URLEncoder.encode(user, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                String url = "http://xpress.genniux.net/php/xpress2.0/signin.php?user=" + url_usuario + "&token=0";
                System.out.println(url);

                StringRequest stringRequest = new StringRequest(Request.Method.POST
                        , url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {


                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Json", user);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        return params;
                    }

                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

                sendRefreshBroadcast();
            }






        }
        else
        {
          updateUI(false);
        }

    }

    private void populateWidget() {
        Intent widgetUpdateIntent = new Intent();
        widgetUpdateIntent.setAction(Xpress.DATA_FETCHED);
        widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                mAppWidgetId);
        sendBroadcast(widgetUpdateIntent);

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

    /**
     *
     * @param isSignedIn Permite saber si tiene iniciada la sesión
     */
    private void updateUI(boolean isSignedIn)
    {

        RemoteViews remoteViews;
        ComponentName watchWidget;
        if (isSignedIn)
        {
            AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(getApplicationContext());
            remoteViews =new RemoteViews(getApplicationContext().getPackageName(),R.layout.xpress_widget);
            watchWidget =new ComponentName(getApplicationContext(), Xpress.class);
            remoteViews.setViewVisibility(R.id.btn_login,View.GONE);
            remoteViews.setViewVisibility(R.id.widget_title_star,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.widget_title_add,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.refresh,View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.VISIBLE);
            btnRevokeAccess.setVisibility(View.VISIBLE);
            llProfileLayout.setVisibility(View.VISIBLE);
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);



        } else
            {
            AppWidgetManager appWidgetManager =AppWidgetManager.getInstance(getApplicationContext());
             remoteViews =new RemoteViews(getApplicationContext().getPackageName(),R.layout.xpress_widget);
                watchWidget =new ComponentName(getApplicationContext(),Xpress.class);
                remoteViews.setViewVisibility(R.id.widget_title_star,View.GONE);
                remoteViews.setViewVisibility(R.id.widget_title_add,View.GONE);
                remoteViews.setViewVisibility(R.id.refresh,View.GONE);
                btnSignIn.setVisibility(View.VISIBLE);
                btnSignOut.setVisibility(View.GONE);
                btnRevokeAccess.setVisibility(View.GONE);
                llProfileLayout.setVisibility(View.GONE);
                appWidgetManager.updateAppWidget(watchWidget, remoteViews);

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

    /**
     * Permite obtener las actividaes al mismo tiempo que hace el login
     */
    public  void sendRefreshBroadcast() {
        Intent intent = new Intent(this, Xpress.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplicationContext(), Xpress.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
