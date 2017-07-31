package com.widgetxpress.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;
import com.widgetxpress.R;
import com.widgetxpress.util.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActividad extends AppCompatActivity {

    public static String TAG=DetailActividad.class.getSimpleName();
    @BindView(R.id.edit_actividad)
    EditText edit_nombre_actividad;
    @BindView(R.id.edit_fecha_inicio)
    EditText editText_fecha_inicio;
    @BindView(R.id.edit_fecha_termino)
    EditText editText_fecha_termino;
    @BindView(R.id.root)
    LinearLayout linearLayout;
    @BindView(R.id.tv_reminder_time)
    TextView recordatorio;
    @BindView(R.id.edit_responsable)
    EditText responsable;
    @BindView(R.id.iv_creador)
    ImageView imgProfilePic;
    @BindView(R.id.tarea_terminada)
    CheckBox tarea_terminada;
    private String actividad;
    private String fecha_inicio;
    private String fecha_fin;
    private String creador;
    private String id;
    private Boolean check;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private static String id_google;
    String horas_fecha_inicio;
    String  fecha_inicio_separada;
    String fecha_fin_separado;
    String tiempo_separado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_actividad);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFinishOnTouchOutside(false);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            actividad=bundle.getString("titulo");
            fecha_inicio=bundle.getString("fecha_inicio");
            fecha_fin=bundle.getString("fecha_fin");
            creador=bundle.getString("responsable");
            check=bundle.getBoolean("check");
            id=bundle.getString("id");
            edit_nombre_actividad.setText(actividad);
            responsable.setText(creador);
            tarea_terminada.setChecked(check);
            String fmt = "yyyy-MM-dd HH:mm:ss";
            DateFormat df = new SimpleDateFormat(fmt,Locale.getDefault());

            try {
                Date fecha_inicial = df.parse(fecha_inicio);
                Date fecha_termino=df.parse(fecha_fin);

                DateFormat tdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                DateFormat dfmt  = new SimpleDateFormat("d MMMyy",Locale.getDefault());

                horas_fecha_inicio= tdf.format(fecha_inicial);
                fecha_inicio_separada = dfmt.format(fecha_inicial);
                fecha_fin_separado=dfmt.format(fecha_termino);
                tiempo_separado=tdf.format(fecha_termino);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Log.e(TAG,actividad+ " " + "FehcaInicio:"+fecha_inicio+ ""+ "FechaFin:"+fecha_fin+" "+"ID:"+ id+" "+String.valueOf(check));
            editText_fecha_inicio.setText(fecha_fin_separado);
            editText_fecha_termino.setText(fecha_fin_separado);
            recordatorio.setText(horas_fecha_inicio);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_editar:
                editarActividad();
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

        datePickerDialog=new DatePickerDialog(DetailActividad.this, new DatePickerDialog.OnDateSetListener() {
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

        datePickerDialog=new DatePickerDialog(DetailActividad.this, new DatePickerDialog.OnDateSetListener() {
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

        timePickerDialog=new TimePickerDialog(DetailActividad.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                recordatorio.setText(hora+":"+minuto+":"+second);
            }
        },hour,minute,false);
        timePickerDialog.show();
    }


    public  void editarActividad() {
        final  String id_actividad=id;
        final String titulo_actividad=edit_nombre_actividad.getText().toString().trim();
        final String fecha_inicio=editText_fecha_inicio.getText().toString().trim();
        final String fecha_fin=editText_fecha_termino.getText().toString().trim();
        final String hora=recordatorio.getText().toString().trim();
        final String date_time_inicio=fecha_inicio+" " +hora;
        final String date_time_fin=fecha_fin+"  "+ hora;
        id_google=Login.id_google;

        String url = "http://xpress.genniux.net/php/xpress2.0/agenda/editActivity.php?=" +"&id="+id_actividad+"&campo="+ "titulo"+"&valor="+titulo_actividad;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>()
                {


                    @Override
                    public void onResponse(String response)
                    {

                        Log.d("Respuesta:",response);
                        try {
                            JSONObject statusReport=new JSONObject(response);

                                String status=statusReport.optString("status");
                                Log.d(TAG,status);

                            if(status.equalsIgnoreCase("OK"))
                            {
                                Snackbar.make(linearLayout, "Se guardaron los cambios"+status, Snackbar.LENGTH_LONG)
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
                                  Snackbar.make(linearLayout,"Ocurrio un error al guar los cambios",Snackbar.LENGTH_LONG)
                                          .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                            }


                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        //response.equals()
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                Log.d("AQUI",error.toString());
            }
        })
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("id",id_actividad);
                params.put("titulo",titulo_actividad);
                params.put("fecha_inicio",date_time_inicio);


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }





}
