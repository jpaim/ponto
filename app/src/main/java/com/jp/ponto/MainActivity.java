package com.jp.ponto;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends Activity implements AbsListView.MultiChoiceModeListener {

    private MarcacaoDbAdapter db;
    private ListView listView;
    MarcacoesAdapter adapter;
    SharedPreferences prefs;
    View viewFooter;

    //    ALARMES
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static MainActivity inst;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        inst = this;
        init();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
        View t = (View) findViewById(R.id.textClock);
        t.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                novaMarcacao(null);
                displayListView();
                return true;
            }
        });
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null)
            db.close();
    }

    private void init() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = new MarcacaoDbAdapter(this);
        db.open();
        listView = (ListView) findViewById(R.id.lvHorario);
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        displayListView();
    }


    private void displayListView() {
        ArrayList<Marcacoes> lm = db.listaMarcacoes(Funcoes.getCurDate());
        adapter = new MarcacoesAdapter(this, lm);

        long soma = somarHoras(lm);

        listView.setAdapter(adapter);

        viewFooter = getLayoutInflater().inflate(R.layout.list_footer, null);
        if (listView.getFooterViewsCount() == 0)
            listView.addFooterView(viewFooter, null, false);

        TextView textTrab = (TextView) findViewById(R.id.TextView21);
        TextView textDif = (TextView) findViewById(R.id.TextView22);
        TextView textSaida = (TextView) findViewById(R.id.TextView23);
        TextView textTermo = (TextView) findViewById(R.id.TextView02);

        long jornada = Long.parseLong(prefs.getString("jornada_list", "8")) * 3600000;

        long saida = jornada - soma;
        String termo = "Exato";
        Calendar hora_saida = null;
        String text_saida = " --- ";
        String br = System.getProperty("line.separator");

        if (saida < 0) {
            termo = "Extra";
        } else if (saida > 0) {
            termo = "Faltam";

            hora_saida = horarioSaida(lm, saida);
            if (hora_saida != null) {
                text_saida = new SimpleDateFormat("HH:mm").format(hora_saida.getTime());
                if (hora_saida.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
                    setAlarm(hora_saida);
            }
        }

        textTermo.setText(termo);
        textTrab.setText(converteHMS(soma));
        textDif.setText(converteHMS(Math.abs(saida)));
        textSaida.setText(text_saida);


        listView.setSelection(listView.getAdapter().getCount());
    }

    private void setAlarm(Calendar hora_saida) {
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        //alarmManager.cancel(pendingIntent);

        if (prefs.getBoolean("alarme_checkbox", false))
            alarmManager.set(AlarmManager.RTC_WAKEUP, hora_saida.getTimeInMillis(), pendingIntent);
        else
            alarmManager.cancel(pendingIntent);

    }



    private void cancelAlarm() {
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        //alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntent);
    }

    private Calendar horarioSaida(ArrayList<Marcacoes> lm, long saida) {
        int i = lm.size();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        long novoHorario = -1;
        Calendar retorno = null;
        if (i > 0 && i % 2 != 0) {

            Calendar c1 = GregorianCalendar.getInstance();
            try {
                c1.setTime(df.parse(lm.get(i - 1).getData() + " " + lm.get(i - 1).getHora()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            novoHorario = saida + c1.getTimeInMillis();
            c1.setTimeInMillis(novoHorario);
            retorno = c1;
        }
        return retorno;
    }

    private String converteHMS(long soma) {
        soma /= 60000;
        return String.format("%01dh%02d", (soma / 60), (soma % 60));
    }


    private long somarHoras(ArrayList<Marcacoes> lm) {
        long dif = 0;
        for (int i = 1; i < lm.size(); i++) {
            if (i % 2 != 0) {
                try {
                    dif += Funcoes.difDateTime(lm.get(i).getData() + " " + lm.get(i).getHora(),
                            lm.get(i - 1).getData() + " " + lm.get(i - 1).getHora());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return dif;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.sair);
        builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void novaMarcacao(String dthr) {
        if (dthr == null) {
            dthr = Funcoes.getCurDateTime();
        }
        db.createMarcacao(dthr);
        displayListView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        cancelAlarm();

        if (id == R.id.action_add) {
            novaMarcacao(null);
        }

        if (id == R.id.action_limpar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirma_exclusao);

            builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    db.deleteAllMarcacoes();
                    displayListView();
                }
            });

            builder.setNegativeButton(R.string.CANCEL, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (id == R.id.action_manual) {

            final TimePicker timePicker = new TimePicker(this);
            timePicker.setIs24HourView(true);

            new AlertDialog.Builder(this)
                    .setMessage(R.string.pick_time)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String hora = String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                            db.createMarcacao(Funcoes.getCurDate() + " " + hora);
                            displayListView();
                            Toast.makeText(getBaseContext(), getString(R.string.manual_entry) + hora, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setView(timePicker).show();

        }

        if (id == R.id.action_apagar) {
            CheckBox cb;
            TextView idn;
            ArrayList<String> lista = new ArrayList<String>();
            int c = listView.getAdapter().getCount();
            for (int i = 0; i < c; i++) {
                cb = (CheckBox) listView.getChildAt(i).findViewById(R.id.checkBox1);
                idn = (TextView) listView.getChildAt(i).findViewById(R.id.textID);
                if (cb.isChecked()) {
                    lista.add(idn.getText().toString());
                }
            }

            for (int i = 0; i < lista.size() - 1; i++) {
                db.deleteMarcacao(lista.get(i));
            }

            displayListView();
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        final int checkedCount = listView.getCheckedItemCount();
        final long[] checkedItemIds = listView.getCheckedItemIds();
        mode.setTitle(checkedCount + " Selecionado" + (checkedCount > 1 ? "s" : ""));
        adapter.toggleSelection(position);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_context, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_apagar) {

            SparseBooleanArray selected = adapter.getSelectedIds();

            for (int i = (selected.size() - 1); i >= 0; i--) {
                if (selected.valueAt(i)) {
                    Marcacoes mar = adapter.getItem(selected.keyAt(i));
                    db.deleteMarcacao(String.valueOf(mar.getId()));
                }
            }

            mode.finish();
            displayListView();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.removeSelection();
    }
}

