package com.widgetxpress.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.squareup.picasso.Picasso;
import com.widgetxpress.R;
import com.widgetxpress.Xpress;
import com.widgetxpress.api.Api;
import com.widgetxpress.model.Actividades;
import com.widgetxpress.model.EditarActividad;
import com.widgetxpress.util.CircleTransform;
import com.widgetxpress.util.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailActividad extends AppCompatActivity {

    public static String TAG=DetailActividad.class.getSimpleName();
    private Gson gson;
    @BindView(R.id.edtActividad)
    EditText edit_nombre_actividad;
    @BindView(R.id.edtFechaInicio)
    EditText editText_fecha_inicio;
    @BindView(R.id.edtFechaFin)
    EditText editText_fecha_termino;
    static  LinearLayout linearLayout;
    @BindView(R.id.edtHora)
    EditText recordatorio;
    @BindView(R.id.editCreador)
    EditText responsable;
    @BindView(R.id.edtResponsable)
    EditText edit_responsable;
    @BindView(R.id.ivCreador)
    ImageView imgProfilePic;
    @BindView(R.id.ivResponsable)
    ImageView img_profile_responsable;
    @BindView(R.id.checkFinished)
    CheckBox tarea_terminada;
    private Context context;
    static EditText titulo_actividad;
    static  EditText fecha_inicio_dialogo;
    static EditText hora_inicio_dialogo;
    private static String  actividad;
    private static  String nombre_actividad;
    private  static String titulo_actividad_modificado;
    private  static String fecha_inicio;
    private String fecha_fin;
    private String creador;
    private static String id;
    private String responsable_nombre;
    private String responsable_id;
    private Boolean check;
    private String url_profile_picture;
    private String responsable_profile_picture;
    private static DatePickerDialog datePickerDialog;
    private static TimePickerDialog timePickerDialog;
    private static String id_google;
    static EditText  edit_titulo_actividad;
    static String horas_fecha_inicio;
    static String  fecha_inicio_separada;
    static String fecha_fin_separado;
    static  String tiempo_separado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        responsable.setEnabled(false);

        context=getApplicationContext();

        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            actividad=bundle.getString("titulo");
            fecha_inicio=bundle.getString("fecha_inicio");
            fecha_fin=bundle.getString("fecha_fin");
            creador=bundle.getString("creador");
            check=bundle.getBoolean("check");
            id=bundle.getString("id");
            responsable_nombre=bundle.getString("responsable");
            url_profile_picture=bundle.getString("picture");
            responsable_profile_picture=bundle.getString("responsable_picture");
            responsable_id=bundle.getString("responsable_id");
            edit_nombre_actividad.setText(actividad);
            responsable.setText(creador);
            linearLayout=(LinearLayout)findViewById(R.id.root_editar);
            edit_responsable.setText(responsable_nombre);
            tarea_terminada.setChecked(check);

            edit_titulo_actividad=(EditText)findViewById(R.id.edtActividad);
            fecha_inicio_dialogo=(EditText)findViewById(R.id.edtFechaInicio);
            hora_inicio_dialogo=(EditText)findViewById(R.id.edtHora);

            String fmt = "yyyy-MM-dd HH:mm:ss";
            DateFormat df = new SimpleDateFormat(fmt,Locale.getDefault());

            try {
                Date fecha_inicial = df.parse(fecha_inicio);
                Date fecha_termino=df.parse(fecha_fin);

                DateFormat tdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                DateFormat dfmt  = new SimpleDateFormat("yyyy-MM-dd ",Locale.getDefault());

                horas_fecha_inicio= tdf.format(fecha_inicial);
                fecha_inicio_separada = dfmt.format(fecha_inicial);
                fecha_fin_separado=dfmt.format(fecha_termino);
                tiempo_separado=tdf.format(fecha_termino);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            GsonBuilder gsonBuilder=new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gsonBuilder.disableHtmlEscaping();
            gson=gsonBuilder.create();

            View view= getLayoutInflater().inflate(R.layout.dialogo_editar,null);
            titulo_actividad=(EditText)view.findViewById(R.id.dilogo_tituloactividad);



            Log.e(TAG,actividad+ " " + "FehcaInicio:"+fecha_inicio+ ""+ "FechaFin:"+fecha_fin+" "+"ID:"+ id+" "+String.valueOf(check)+ " "+ responsable_id);
            editText_fecha_inicio.setText(fecha_inicio_separada);
            editText_fecha_termino.setText(fecha_fin_separado);
            recordatorio.setText(horas_fecha_inicio);
            if(!url_profile_picture.isEmpty())
            {
                Picasso.with(getApplicationContext())
                        .load(url_profile_picture)
                        .placeholder(R.drawable.ic_account_circle_black)
                        .error(R.drawable.ic_account_circle_black)
                        .centerInside()
                        .resize(60,60)
                        .transform(new CircleTransform())
                        .into(imgProfilePic);
            }
            if(!responsable_profile_picture.isEmpty())
            {
                Picasso.with(getApplicationContext())
                        .load(url_profile_picture)
                        .placeholder(R.drawable.ic_account_circle_black)
                        .error(R.drawable.ic_account_circle_black)
                        .centerInside()
                        .resize(60,60)
                        .transform(new CircleTransform())
                        .into(img_profile_responsable);
            }
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
                finish();
                notificarLista();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }

    void showDialog()
    {
        DialogFragment newFragment=Dialogotitulo.newInstance(R.string.appwidget_text);
        newFragment.show(getFragmentManager(),"dialog");


    }
    public void doPositiveClick() {
        Log.i("FragmentAlertDialog", "Positive click!");
        notificarLista();


    }
    public void doNegativeClick() {
        Log.i("FragmentAlertDialog", "Negative click!");
        finish();
    }

    /**
     * Dialogo para editar fecha de inicio
     */

    void mostrarDialogoFechaInicio()
    {
        DialogFragment dialogFragment=DialogoFechaInicio.newInstance(R.string.appwidget_text);
        dialogFragment.show(getFragmentManager(),"dialogo");
    }

    public static class  DialogoFechaInicio extends  DialogFragment
    {
        public  static  DialogoFechaInicio newInstance(int titulo)
        {
            DialogoFechaInicio dialogoFechaInicio=new DialogoFechaInicio();
            Bundle bundle=new Bundle();
            bundle.putInt("titulo",titulo);
            bundle.putString("fecha_inicio",fecha_inicio_separada);
            bundle.putString("hora_inicio",horas_fecha_inicio);
            dialogoFechaInicio.setArguments(bundle);
            return dialogoFechaInicio;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String fecha_inicial=getArguments().getString("fecha_inicio");
            String hora_inicial=getArguments().getString("hora_inicio");
            int title = getArguments().getInt("titulo");
            final AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
            myDialog.setIcon(R.mipmap.ic_launcher);
            final View vistaditar= LayoutInflater.from(getActivity()).inflate(R.layout.dialogo_fecha,null);
            final EditText fecha_inicio=(EditText)vistaditar.findViewById(R.id.dilogofecha);
            final EditText hora_inicio=(EditText)vistaditar.findViewById(R.id.dilogohora);
            final TextView text_eror=(TextView)vistaditar.findViewById(R.id.tv_error);
            fecha_inicio.setText(fecha_inicial);
            hora_inicio.setText(hora_inicial);
            myDialog.setTitle(title);
            myDialog.setView(vistaditar);
            myDialog.setMessage("Guardar cambios");

            fecha_inicio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i(TAG,"Click");

                        final Calendar c=Calendar.getInstance();
                        int myear=c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int  monthOfYear, int dayOfMonth) {
                                fecha_inicio.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);
                            }
                        },myear,mMonth,mDay);
                        datePickerDialog.show();
                    }
            });

            hora_inicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    final int second = c.get(Calendar.SECOND);

                    timePickerDialog=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                            hora_inicio.setText(hora+":"+minuto+":"+second);
                        }
                    },hour,minute,false);
                    timePickerDialog.show();
                }
            });

            myDialog.setPositiveButton(R.string.dialogo_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DetailActividad)getActivity()).doPositiveClick();

                    final String id_actividad=id;
                    String codificar = "";
                    final String date_time_inicio=fecha_inicio.getText().toString()+" "+hora_inicio.getText().toString();
                    try {
                       codificar=URLEncoder.encode(date_time_inicio,"UTF8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG,date_time_inicio+"  "+ id_actividad);

                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    String url = "http://xpress.genniux.net/php/xpress2.0/agenda/editActivity.php?=" +"&id="+id_actividad+"&campo="+ "fecha_inicio"+"&valor="+
                            codificar;

                    StringRequest sr = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>()
                            {


                                @Override
                                public void onResponse(String response)
                                {

                                    Log.i("Respuesta:",response);
                                    try
                                    {
                                        JSONObject statusReport=new JSONObject(response);

                                        String status=statusReport.optString("status");
                                        Log.i(TAG,status);

                                        if(status.equalsIgnoreCase("OK"))
                                        {

                                            fecha_inicio_dialogo.setText(fecha_inicio.getText().toString());
                                            hora_inicio_dialogo.setText(hora_inicio.getText().toString());
                                            dismiss();


                                        }
                                        else
                                        {
                                            text_eror.setText("Ocurrio un error al guardar los cambios");
                                        }


                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Toast.makeText(getActivity(),VolleyErrorHelper.getErrorMessage(getActivity(),error),Toast.LENGTH_LONG).show();
                                VolleyLog.e(TAG,error.getLocalizedMessage());
                                text_eror.setText("Ocurrio un error con el servidor");
                                
                           /* Snackbar.make(vistaditar, "Ocurrio un error con el sevidor", Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.primary_text))
                                    .show();*/

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", id_actividad);
                            params.put("campo", "fecha_inicio");
                            params.put("fecha_inicio", date_time_inicio);


                            return params;
                        }
                    };
                    queue.add(sr);






                }
            });


            return myDialog.create();

        }
    }


    /**
     * Dialogo para editar titulo de activividad
     */


    public static class Dialogotitulo extends DialogFragment
    {

        public static Dialogotitulo newInstance(int titulo){
            Dialogotitulo dr=new Dialogotitulo();
            Bundle args=new Bundle();
            args.putInt("titulo",titulo);
            args.putString("actividad",edit_titulo_actividad.getText().toString());
            dr.setArguments(args);
            return  dr;
        }



        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("titulo");
            String nombre=getArguments().getString("actividad");
            final AlertDialog.Builder myDialog =new AlertDialog.Builder(getActivity());
            myDialog.setIcon(R.mipmap.ic_launcher);
            final View vistaeditar= LayoutInflater.from(getActivity()).inflate(R.layout.dialogo_editar,null);
            final EditText titulo=(EditText)vistaeditar.findViewById(R.id.dilogo_tituloactividad);
            titulo.setText(nombre);
            myDialog.setTitle(title);
            myDialog.setView(vistaeditar);
            myDialog.setMessage("Guardar cambios");
            myDialog.setPositiveButton(R.string.dialogo_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    // Toast.makeText(getActivity(),titulo.getText().toString(),Toast.LENGTH_SHORT).show();

                    ((DetailActividad)getActivity()).doPositiveClick();

                    final String id_actividad=id;
                    final String actividad_titulo=titulo.getText().toString();



                    String encode= null;
                    try {
                        encode = URLEncoder.encode(actividad_titulo,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

                    String url = "http://xpress.genniux.net/php/xpress2.0/agenda/editActivity.php?=" +"&id="+id_actividad+"&campo="+ "titulo"+"&valor="+encode;

                    /**
                     * Función que permite actulizar el titulo de una actividad
                     */
                    StringRequest sr = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>()
                            {


                                //@TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onResponse(String response)
                                {

                                    Log.i("Respuesta:",response);
                                    try
                                    {
                                        JSONObject statusReport=new JSONObject(response);

                                        String status=statusReport.optString("status");
                                        Log.i(TAG,status);

                                        if(status.equalsIgnoreCase("OK"))
                                        {
                                            dismiss();
                                            edit_titulo_actividad.setText(titulo.getText().toString());

                                        }
                                        else
                                        {
                                           // Toast.makeText(getActivity(),"Ocrrrio un error al guardar ",Toast.LENGTH_LONG).show();
                                            Snackbar.make(vistaeditar,"Ocurrio un error al guar los cambios",Snackbar.LENGTH_LONG)
                                                    .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                                        }


                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                           // Toast.makeText(getActivity(),VolleyErrorHelper.getErrorMessage(getActivity(),error),Toast.LENGTH_LONG).show();
                            Snackbar.make(vistaeditar,"Ocurrio un erorr con el servidor",Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.colorPrimary));

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("id", id_actividad);
                            params.put("campo", "titulo");
                            params.put("titulo", actividad);


                            return params;
                        }
                    };
                    queue.add(sr);



                }
            });



            return myDialog.create();
        }
    }





    @OnClick(R.id.btn_FechaInicio)
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
    @OnClick(R.id.btnFechaFin)
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
    @OnClick(R.id.edtHora)
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
    @OnClick(R.id.btnhora)
    void selecionaerhora()
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
    @OnClick(R.id.edtActividad)
    void ver_nombre()
    {
        showDialog();
    }
    @OnClick(R.id.edtFechaInicio)
    void mostrarFechaInicio()
    {
        mostrarDialogoFechaInicio();
    }


    /**
     * Funcion que actualiza todos los campos al mismo tiempo(Se cambio por actualizar por separado )
     * @throws JSONException Lanza la exepcion si el Json enviado es incorrecto
     * @throws UnsupportedEncodingException Si el titulo no es enviado en formato url se obtiene la exepcion
     */
    public  void editarActividad() throws JSONException, UnsupportedEncodingException {
        final  String id_actividad=id;
        final String titulo_actividad=edit_nombre_actividad.getText().toString().trim();
        final String fecha_inicio=editText_fecha_inicio.getText().toString().trim();
        final String fecha_fin=editText_fecha_termino.getText().toString().trim();
        final String hora=recordatorio.getText().toString().trim();
        final String date_time_inicio=fecha_inicio+" " +hora;
        final String date_time_fin=fecha_fin+"  "+ hora;
        final  boolean tarea_finalizada;
        final  String responsable=responsable_id;
        tarea_finalizada = tarea_terminada.isChecked();
        id_google=Login.id_google;
        String encodetitulo = URLEncoder.encode(titulo_actividad, "UTF-8");
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("id",id_actividad);
        jsonObject.put("titulo",titulo_actividad);
        jsonObject.put("responsable",responsable);
        jsonObject.put("check",tarea_finalizada);
        jsonObject.put("fecha_inicio",date_time_inicio);
        jsonObject.put("fecha_fin",date_time_fin);

        Log.i(TAG,jsonObject.toString());
        String url_editar="http://xpress.genniux.net/php/xpress2.0/agenda/editFullActivity.php?actividad="+jsonObject;
        //System.out.println(url_editar);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id_actividad);
        params.put("titulo",encodetitulo);
        params.put("responsable",responsable);
        params.put("check",String.valueOf(tarea_finalizada));
        params.put("fecha_inicio",date_time_inicio);
        params.put("fecha_fecha_fin",date_time_fin);


        JsonObjectRequest req=new JsonObjectRequest(Request.Method.POST,url_editar,new JSONObject(params)
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    VolleyLog.e("Response:%n %s",response.toString(4));

                    String status=response.getString("status");

                    if(status.equalsIgnoreCase("OK"))
                    {
                        Snackbar.make(linearLayout, "Se guardaron los cambios exitosamente ", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                notificarLista();
                            }
                        },3000L);
                    }//Termina validación de status al actualizar
                    else
                    {
                        Snackbar.make(linearLayout,"Ocurrio un error al guar los cambios",Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }//Termina JsonResponse
            }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(linearLayout, VolleyErrorHelper.getErrorMessage(getApplicationContext(),error), Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.primary_text))
                        .show();
            }//Termina Response de Eerror
        } )
        {
            @Override
            public String getBodyContentType() {
                return"application/json"+getParamsEncoding() ;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
                return  headers;
            }
        };

        queue.add(req);



    }
    public void editarTitulo() throws UnsupportedEncodingException {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final  String id_actividad=id;
        final String actividad=titulo_actividad.getText().toString().trim();

        String url = "http://xpress.genniux.net/php/xpress2.0/agenda/editActivity.php?=" +"&id="+id_actividad+"&campo="+ "titulo"+"&valor="
                +URLEncoder.encode(actividad,"UTF-8");


        StringRequest sr = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>()
                {


                    @Override
                    public void onResponse(String response)
                    {

                        Log.i("Respuesta:",response);
                        try {
                            JSONObject statusReport=new JSONObject(response);

                            String status=statusReport.optString("status");
                            Log.i(TAG,status);

                            if(status.equalsIgnoreCase("OK"))
                            {
                                Snackbar.make(linearLayout, "Se guardaron los cambios exitosamente ", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                        .show();
                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        notificarLista();
                                    }
                                },2000L);

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

                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(linearLayout, VolleyErrorHelper.getErrorMessage(getApplicationContext(),error), Snackbar.LENGTH_LONG)
                        .setActionTextColor(getResources().getColor(R.color.primary_text))
                        .show();
                //Log.i(TAG,error.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id_actividad);
                params.put("campo", "titulo");
                params.put("titulo", actividad);


                return params;
            }
        };
        queue.add(sr);



    }
    private  void notificarLista()
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), Xpress.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_main_view);
    }





}
