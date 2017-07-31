package com.widgetxpress.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.widgetxpress.R;
import com.widgetxpress.api.Api;
import com.widgetxpress.model.Actividad;
import com.widgetxpress.model.Actividades;
import com.widgetxpress.model.GoogleUser;
import com.widgetxpress.util.SessionPrefs;

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
    @BindView(R.id.tv_reminder_time)
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
        creador=new GoogleUser(Login.id_google);
        gson=new GsonBuilder().setPrettyPrinting().create();
        setFinishOnTouchOutside(false);
       // Log.e(TAG,objectToJson(creador));
        mPrefs =getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
       // nombre_actividad.addTextChangedListener(titulo_actividad);
        ButterKnife.bind(this);
    }
    public  String objectToJson(GoogleUser o) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(o);

        return json;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause");
        id=Login.id_google;
        Log.e(TAG,id);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(pref_id, id);
        editor.apply();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart");
        SharedPreferences.Editor editor = mPrefs.edit();
        id=Login.id_google;
        Log.e(TAG,id);
        editor.putString(pref_id, id);
        editor.apply();

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
    private final TextWatcher titulo_actividad=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()==0)
            {
                finish();
            }
            else
            {
                showDialog();
            }
        }
    };

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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return true;
            case android.R.id.home:
                finish();
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
    @OnClick(R.id.tv_reminder_time)
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
    void showDialog()
    {
        DialogFragment newFragment=DialogoCancelar.newInstance(R.string.appwidget_text);
        newFragment.show(getFragmentManager(),"dialog");

    }
    public void doPositiveClick() {
        Log.i("FragmentAlertDialog", "Positive click!");
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
            myDialog.setMessage("¿Deseas salir sin guardar cambios ");
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


    /* private void agregarActividad(Actividad actividad)
    {

        String url = "http://xpress.genniux.net/php/xpress2.0/agenda/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api servicio = retrofit.create(Api.class);
        Call<Actividad> call=servicio.agregarActividad(actividad);
        call.enqueue(new Callback<Actividad>() {
            @Override
            public void onResponse(Call<Actividad> call, retrofit2.Response<Actividad> response) {
                Toast.makeText(getApplicationContext(),"Se agrego"+response.body().getId(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<Actividad> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }*/


      public void agregarActividad() throws UnsupportedEncodingException {
        actividad=nombre_actividad.getText().toString().trim();
        fecha_inicio=editText_fecha_inicio.getText().toString().trim();
        fecha_fin=editText_fecha_termino.getText().toString().trim();
        hora=recordatorio.getText().toString().trim();
        date_time_inicio=fecha_inicio+" "+hora;
        date_time_termino=fecha_fin+" "+hora;

          if(TextUtils.isEmpty(actividad) || TextUtils.isEmpty(fecha_inicio) || TextUtils.isEmpty(fecha_fin) || TextUtils.isEmpty(hora))
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
                              Log.d(TAG, response);

                              JSONObject statusReport;
                              try
                              {
                                  statusReport = new JSONObject(response);
                                  String status=statusReport.optString("status");

                                  if(status.equalsIgnoreCase("OK"))
                                  {
                                      Snackbar.make(linearLayout, "Se agrego la actividad"+" "+ status, Snackbar.LENGTH_LONG)
                                              .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                              .show();
                                      Handler handler=new Handler();
                                      handler.postDelayed(new Runnable() {
                                          @Override
                                          public void run() {
                                              finish();
                                          }
                                      },5000L);
                                  }
                                  else
                                  {
                                      Snackbar.make(linearLayout, "Ocurrio un error al agregar la actividad"+" ", Snackbar.LENGTH_LONG)
                                              .setActionTextColor(getResources().getColor(R.color.colorPrimary))
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
                              Toast.makeText(AgregarActividad.this, error.toString(), Toast.LENGTH_LONG).show();
                              Log.i(TAG, error.getLocalizedMessage());
                          }
                      }) {
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



}
