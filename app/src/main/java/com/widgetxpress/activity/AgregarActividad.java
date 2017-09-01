package com.widgetxpress.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.widgetxpress.R;
import com.widgetxpress.Xpress;
import com.widgetxpress.api.Api;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.Actividades;
import com.widgetxpress.model.GoogleUser;
import com.widgetxpress.util.SessionPrefs;
import com.widgetxpress.util.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Irving on 19/07/2017.
 */

public class AgregarActividad extends AppCompatActivity
{
    private String TAG=AgregarActividad.class.getSimpleName();
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Gson gson;
    @BindView(R.id.edit_actividad)
    EditText nombre_actividad;
    @BindView(R.id.edit_fecha_inicio)
    EditText editText_fecha_inicio;
    @BindView(R.id.edit_fecha_termino)
    EditText editText_fecha_termino;
    @BindView(R.id.root)
    LinearLayout linearLayout;
    @BindView(R.id.contenedor_fecha_inicio)
    LinearLayout linear_fecha;
    @BindView(R.id.contenedor_fecha_termino)
    LinearLayout liner_fecha_termino;
    @BindView(R.id.container_reminder)
    LinearLayout linearLayout_hora;
    @BindView(R.id.edit_hora)
    TextView recordatorio;
    private  GoogleUser creador;
    private GoogleUser responsable;
    private static String id;
    private String actividad;
    private String fecha_inicio;
    private String fecha_fin;
    private String date_time_inicio;
    private String hora;
    private String date_time_termino;
    public static final String PREFS_NAME="XPRESS_PREFS";
    public static final String pref_id="PREF_USER_ID";
    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"OnCreate");
        setContentView(R.layout.agregar_actividad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);
        gson=new GsonBuilder().setPrettyPrinting().create();
        EditText titulo_actividad=(EditText)findViewById(R.id.edit_actividad);
        EditText fecha_inicio=(EditText)findViewById(R.id.edit_fecha_inicio);
        EditText fecha_fin=(EditText)findViewById(R.id.edit_fecha_termino);
        setFinishOnTouchOutside(false);
       // Log.e(TAG,objectToJson(creador));
        id=Login.id_google;
        if(id!=null) {
            Log.i(TAG, id);

        }
        mPrefs =getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
         titulo_actividad.addTextChangedListener(titulo);
        fecha_inicio.addTextChangedListener(fecha_inicial);
        fecha_fin.addTextChangedListener(fecha_termino);

        titulo_actividad.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    showDialog();
                }
            }
        });

        ButterKnife.bind(this);
    }
    private TextWatcher fecha_termino=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            linearLayout_hora.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private  TextWatcher fecha_inicial=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            liner_fecha_termino.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private  TextWatcher titulo=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            linear_fecha.setVisibility(View.VISIBLE);

        }

        @Override
        public void afterTextChanged(Editable s) {
           /* if(s.length()==0)
            {
                finish();
            }
            else
            {
                showDialog();
            }*/

        }
    };

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart");
        if(id!=null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            id = Login.id_google;
            Log.i(TAG, id);
            editor.putString(pref_id, id);
            editor.apply();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"OnDestroy");
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(pref_id,null);
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"OnStop");
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
       if(keyCode==KeyEvent.KEYCODE_BACK)
       {
           Toast.makeText(getApplicationContext(),"Back",Toast.LENGTH_SHORT).show();

       }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_agregar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_agregar:
                try {
                    agregarActividad();
                    notificarLista();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
                if(nombre_actividad.getText().length()==0)
              {
                  finish();
              }
              else
            {
                showDialog();
            }

                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.btnFechaInicio)
    void escogerfecha_inicio()
    {
        final Calendar c=Calendar.getInstance();
        int myear=c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog=new DatePickerDialog(AgregarActividad.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int  monthOfYear, int dayOfMonth) {
                editText_fecha_inicio.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
            }
        },myear,mMonth,mDay);
        datePickerDialog.show();
    }
    @OnClick(R.id.btnfecha_termino)
    void escogerfecha_termino()
    {
        final Calendar c=Calendar.getInstance();
        int myear=c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog=new DatePickerDialog(AgregarActividad.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int  monthOfYear, int dayOfMonth) {
                editText_fecha_termino.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
            }
        },myear,mMonth,mDay);
        datePickerDialog.show();
    }
    @OnClick(R.id.btn_hora)
    void esocgerhora()
    {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        final int second = c.get(Calendar.SECOND);

        timePickerDialog=new TimePickerDialog(AgregarActividad.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                recordatorio.setText(hora+":"+minuto+":"+second);
            }
        },hour,minute,false);
        timePickerDialog.show();

    }
    @OnClick(R.id.edit_fecha_inicio)
    void escogerfecha_inicio_edit()
    {
        final Calendar c=Calendar.getInstance();
        int myear=c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        datePickerDialog=new DatePickerDialog(AgregarActividad.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int  monthOfYear, int dayOfMonth) {
                editText_fecha_inicio.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
            }
        },myear,mMonth,mDay);
        datePickerDialog.show();
    }
    void showDialog()
    {
        DialogFragment newFragment=DialogoCancelar.newInstance(R.string.appwidget_text);
        newFragment.show(getFragmentManager(),"dialog");

    }
    public void doPositiveClick() {
        Log.i("FragmentAlertDialog", "Positive click!");
        finish();
    }
    public void doNegativeClick() {
        Log.i("FragmentAlertDialog", "Negative click!");
    }
    public static class DialogoCancelar extends DialogFragment
    {
        public static  DialogoCancelar newInstance(int titulo){
            DialogoCancelar dialogoCancelar=new DialogoCancelar();
            Bundle args=new Bundle();
            args.putInt("titulo",titulo);
            dialogoCancelar.setArguments(args);
            return  dialogoCancelar;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("titulo");

            AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());

            myDialog.setIcon(R.mipmap.ic_launcher);
            myDialog.setTitle(title);
            myDialog.setMessage("¿Deseas salir sin guardar cambios? ");
            myDialog.setPositiveButton(R.string.dialogo_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            ((AgregarActividad) getActivity()).doPositiveClick();
                        }
                    });
            myDialog.setNegativeButton("No"
                    , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((AgregarActividad) getActivity()).doNegativeClick();

                        }
                    });
            return myDialog.create();

        }
    }



      public void agregarActividad() throws UnsupportedEncodingException {
        actividad=nombre_actividad.getText().toString().trim();
        fecha_inicio=editText_fecha_inicio.getText().toString().trim();
        fecha_fin=editText_fecha_termino.getText().toString().trim();
        hora=recordatorio.getText().toString().trim();
        date_time_inicio=fecha_inicio+" "+hora;
        date_time_termino=fecha_fin+" "+hora;

          if(TextUtils.isEmpty(actividad) || TextUtils.isEmpty(fecha_inicio) || TextUtils.isEmpty(fecha_fin) || TextUtils.isEmpty(hora) || id==null)
          {
              nombre_actividad.setError("Ingresa una actividad");
              editText_fecha_inicio.setError("Seleciona  fecha de inicio");
              editText_fecha_termino.setError("Seleciona  fecha de termino");
              recordatorio.setError("Seleciona la hora");


          }
          else {

              Actividades json_actividad = new Actividades(actividad,
                      id, id, false, date_time_inicio, date_time_termino, "0");
              final String formatoJSON = gson.toJson(json_actividad);
              Log.i(TAG, formatoJSON);
              String encodedString = URLEncoder.encode(formatoJSON, "UTF-8");
              String url = "http://xpress.genniux.net/php/xpress2.0/agenda/addActivity.php?actividad=" + encodedString;
              System.out.println(url);

              StringRequest stringRequest = new StringRequest(Request.Method.POST,
                      url,

                      new Response.Listener<String>() {
                          @Override
                          public void onResponse(String response)
                          {
                              Log.i(TAG, response);

                              JSONObject statusReport;
                              try
                              {
                                  statusReport = new JSONObject(response);
                                  String status=statusReport.optString("status");

                                  if(status.equalsIgnoreCase("OK"))
                                  {
                                      Snackbar.make(linearLayout, "Se agrego la actividad correctamente", Snackbar.LENGTH_LONG)
                                              .setActionTextColor(getResources().getColor(R.color.primary_text))
                                              .show();
                                      Handler handler=new Handler();
                                      handler.postDelayed(new Runnable()
                                      {
                                          @Override
                                          public void run() {
                                              finish();
                                             notificarLista();
                                          }
                                      },2000L);
                                  }
                                  else
                                  {
                                      Snackbar.make(linearLayout, "Ocurrio un error al agregar la actividad", Snackbar.LENGTH_LONG)
                                              .setActionTextColor(getResources().getColor(R.color.primary_text))
                                              .show();
                                  }

                              } catch (JSONException e)
                              {
                                  e.printStackTrace();
                              }

                          }
                      },
                      new Response.ErrorListener() {
                          @Override
                          public void onErrorResponse(VolleyError error) {
                              //VolleyErrorHelper.getErrorMessage(getApplicationContext(),error);
                             // Toast.makeText(getApplicationContext(),VolleyErrorHelper.getErrorMessage(getApplicationContext(),error),Toast.LENGTH_LONG).show();
                              Snackbar.make(linearLayout,VolleyErrorHelper.getErrorMessage(getApplicationContext(),error),Snackbar.LENGTH_LONG)
                                      .setActionTextColor(getResources().getColor(R.color.primary_text));

                              Log.i(TAG, error.getLocalizedMessage());
                          }
                      })
              {
                  @Override
                  protected Map<String, String> getParams() {


                      Map<String, String> params = new HashMap<String, String>();
                      params.put("Json", formatoJSON);
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
          }
    }
    private void notificarLista() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), Xpress.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_main_view);
    }



}
