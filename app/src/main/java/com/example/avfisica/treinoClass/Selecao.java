package com.example.avfisica.treinoClass;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.avfisica.MainActivity;
import com.example.avfisica.R;
import com.example.avfisica.Register;
import com.example.avfisica.models.treino.Ficha;
import com.example.avfisica.models.treino.TreinoModel;
import com.example.avfisica.resources.treino.FichaResource;
import com.example.avfisica.resources.treino.TreinoResource;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.model.CalendarEvent;
import devs.mulham.horizontalcalendar.utils.CalendarEventsPredicate;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class Selecao extends AppCompatActivity {

    Button btn_cadFicha, btn_treino;
    TextView treino_dia, treino_semanal, treino_mes, treino_ano;

    private HorizontalCalendar horizontalCalendar;

    RecyclerView rv; //NEW
    Integer drawableArray[];
    String titleArray[];
    CustomAdapterTreino ad;

    FichaResource helperFicha;
    List<Ficha> lficha;
    List<Ficha> lfichaFlagAtivo;
    Ficha ficha;

    TreinoResource helperTreino;
    List<TreinoModel> ltreino;
    TreinoModel treino;

    private Handler handler;
    int delay = 1000;
    boolean flag_thread = true;
    String name_treino;
    String diaDaSemana;
    Boolean flag = true;

    //constante
    private static final int FLAG_ATIVO = 1;
    private static final int FLAG_DESATIVADO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treino_sel);

        btn_cadFicha = (Button) findViewById(R.id.button_ficha);
        btn_treino = (Button) findViewById(R.id.button_treino);
        //img_plus_treino = (ImageView) findViewById(R.id.imageView_plus_treino);

        treino_dia = (TextView) findViewById(R.id.textView_treino_dia);
        treino_semanal = (TextView) findViewById(R.id.textView_treino_semanal);
        treino_mes = (TextView) findViewById(R.id.textView_treino_mes);
        treino_ano = (TextView) findViewById(R.id.textView_treino_ano);

        helperFicha = new FichaResource(this);
        ficha = new Ficha();
        lficha = new ArrayList<Ficha>();
        lfichaFlagAtivo = new ArrayList<Ficha>();
        lficha = helperFicha.getData(Register.id_login);

        helperTreino = new TreinoResource(this);
        treino = new TreinoModel();
        ltreino = new ArrayList<TreinoModel>();
//######################################################################################
        /* start 2 months ago from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        /* end after 2 months from now */
        final Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        // Default Date set to Today.
        final Calendar defaultSelectedDate = Calendar.getInstance();

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)

                .addEvents(new CalendarEventsPredicate() {
                    Random rnd = new Random();

                    @Override
                    public List events(Calendar date) {
                        List event = new ArrayList<>();
                        String dataSelection = DateFormat.format("yyyyMMdd", date).toString();

                        ltreino.clear();
                        ltreino = helperTreino.getData(Register.id_login);
                        if(ltreino.size()>0)
                        {
                            for (int i = 0; i < ltreino.size(); i++)
                            {
                               if(dataSelection.equals(ltreino.get(i).getData()))
                                {
                                    final String currentTime = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                    if(dataSelection.equals(currentTime)) {
                                        int id_ficha = (int) ltreino.get(i).getId_ficha();
                                        name_treino = lficha.get(id_ficha - 1).getNome();
                                        if ((name_treino != "") && (treino_dia.getText() != "")) {
                                            treino_dia.setText(treino_dia.getText() + "," + name_treino);
                                        }
                                        else
                                            treino_dia.setText(name_treino);
                                    }

                                    event.add(new CalendarEvent(Color.RED));

                                }
                            }
                        }
                        return event;
                    }
                })
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String dataSelection = DateFormat.format("yyyyMMdd", date).toString();

                // Apresenta qual treino foi realizado na data especifica
                ltreino.clear();
                ltreino = helperTreino.getData(Register.id_login);
                name_treino = "";
                treino_dia.setText("");
                if(ltreino.size()>=0)
                {
                    for (int i = 0; i < ltreino.size(); i++)
                    {
                        if(dataSelection.equals(ltreino.get(i).getData()))
                        {
                            name_treino="";
                            int id_ficha = (int) ltreino.get(i).getId_ficha();
                           name_treino = lficha.get(id_ficha-1).getNome();
                           if ((name_treino!="")&&(treino_dia.getText()!=""))
                               treino_dia.setText(treino_dia.getText()+","+name_treino);
                           else
                               treino_dia.setText(name_treino);
                        }
                        findDay(dataSelection);
                    }
                }

            }

        });

        if(flag){ // Tratativa ao entrar na tela!
            String currentTime = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            findDay(currentTime);
            flag =false;
        }

//####################################################################################################
        lfichaFlagAtivo = helperFicha.getFichaAtivo(FLAG_ATIVO);
        if (lfichaFlagAtivo.size() > 0)//existe a ficha
        {
            titleArray = new String[lfichaFlagAtivo.size()];
            drawableArray = new Integer[lfichaFlagAtivo.size()];

            for (int i = 0; i < lfichaFlagAtivo.size(); i++) {
                    titleArray[i] = lfichaFlagAtivo.get(i).getNome();
            }

            RecyclerView rv; //NEW
            rv = (RecyclerView) findViewById(R.id.recyclerView_treino);
            LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(Selecao.this, LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(horizontalLayoutManagaer);
            ad = new CustomAdapterTreino(Selecao.this, titleArray);
            rv.setAdapter(ad);
        }


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (flag_thread == true) {
                    thread_treino();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);


        // Button Cadastro Ficha
        btn_cadFicha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityFicha();

                } catch (Exception e) {
                }
            }
        });

        // Button Login
        btn_treino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openActivityTreino();

                } catch (Exception e) {
                }
            }
        });

    }

    public void openActivityFicha() {
        Intent intent = new Intent(this, CadastroFicha.class);
        startActivity(intent);
    }

    public void openActivityTreino() {
        Intent intent = new Intent(this, Treino.class);
        startActivity(intent);
    }

    public void thread_treino() {
        if (CustomAdapterTreino.id_treino_adpter >= 0)
        {
            Intent intent = new Intent(this, Treino.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            flag_thread = false;
            handler.removeCallbacksAndMessages(null);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void findDay(String datatreino) {
        String dataTreinoParse = datatreino;
        String mesTreinoParse = "";

       try {
           int dia = Integer.parseInt((datatreino.substring(6, 8)));
           int mes = Integer.parseInt((datatreino.substring(4, 6)));
           int ano = Integer.parseInt((datatreino.substring(0, 4)));
           LocalDate localDate = null;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               localDate = LocalDate.of(ano, mes, dia);
           }

           DayOfWeek dayOfWeek = null;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               dayOfWeek = localDate.getDayOfWeek();
               diaDaSemana = String.valueOf(dayOfWeek);
           }
           System.out.println(dayOfWeek);

           int day = 0;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               switch (diaDaSemana) {
                   case "SUNDAY":
                       day = 0;
                       break;
                   case "MONDAY":
                       day = 1;
                       break;
                   case "TUESDAY":
                       day = 2;
                       break;
                   case "WEDNESDAY":
                       day = 3;
                       break;
                   case "THURSDAY":
                       day = 4;
                       break;
                   case "FRIDAY":
                       day = 5;
                       break;
                   case "SATURDAY":
                       day = 6;
                       break;

               }
           }
           int ind_times = 0;

           Calendar calendar = Calendar.getInstance();
           calendar.set(Calendar.DAY_OF_MONTH, dia);
           calendar.set(Calendar.MONTH, mes - 1);
           calendar.set(Calendar.YEAR, ano);
           calendar.add(Calendar.DAY_OF_YEAR, -(day));
           SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

           Calendar calendar_ = Calendar.getInstance();
           calendar_.set(Calendar.DAY_OF_MONTH, dia);
           calendar_.set(Calendar.MONTH, mes - 1);
           calendar_.set(Calendar.YEAR, ano);
           calendar_.add(Calendar.DAY_OF_YEAR, +(7-day));
           SimpleDateFormat sdf_ = new SimpleDateFormat("yyyyMMdd");


           helperTreino.getData_(sdf.format(calendar.getTime()).toString(), datatreino);
           if (lficha.size() > 0) {
               ind_times = (int) helperTreino.getData_(sdf.format(calendar.getTime()).toString(), sdf_.format(calendar_.getTime()).toString());
               treino_semanal.setText(ind_times + "");
           }

           // Verificar qnt treinos realizados no MES//
           if (lficha.size() > 0) {
               if (mes < 10)
                   mesTreinoParse = "0" + Integer.toString(mes);
               else
                   mesTreinoParse = Integer.toString(mes);

               ind_times = (int) helperTreino.getData_(Integer.toString(ano) + mesTreinoParse + "01", Integer.toString(ano) + mesTreinoParse + "31");
               treino_mes.setText(ind_times + "");
           }

           // Verificar qnt treinos realizados no ANO//
           if (lficha.size() > 0) {
               ind_times = 0;
               ind_times = (int) helperTreino.getData_(Integer.toString(ano) + "0101", Integer.toString(ano) + "3112");
               treino_ano.setText(ind_times + "");
           }
       }catch (Exception e)
       {
           e.printStackTrace();
       }
   }
}