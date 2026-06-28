package com.aparicioamaral.quinzenumerosaleatorios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class LembreteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Verifica o dia da semana atual no celular
        Calendar calendar = Calendar.getInstance();
        int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        // 2. SÓ CRIA A NOTIFICAÇÃO SE NÃO FOR DOMINGO
        if (diaDaSemana != Calendar.SUNDAY) {

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "lembretes_sorteio_sniper";

            // Cria o canal de notificações obrigatório para Android 8.0 ou superior
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, "Lembretes de Sorteio Lotofácil", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Avisos diários para lembrar o horário de conferência e sorteios");
                if (nm != null) nm.createNotificationChannel(channel);
            }

            // Configura a ação de clique da notificação
            Intent abrirApp = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(
                    context, 0, abrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Monta o design visual da Notificação Push nativa
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setContentTitle("⏰ Hora do Sorteio Lotofácil!")
                    .setContentText("Não se esqueça de gerar e conferir suas sequências Sniper hoje no app!")
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            if (nm != null) {
                // Dispara a notificação
                nm.notify(777, builder.build());
            }
        }

        // =========================================================================
        // 3. REAGENDAR ALARME EXATO PARA O DIA SEGUINTE (SEMPRE EXECUTA!)
        // =========================================================================
        android.content.SharedPreferences prefs = context.getSharedPreferences("HistoricoJogos", Context.MODE_PRIVATE);
        if (prefs.getBoolean("lembrete_ativo", false)) {
            int hora = prefs.getInt("lembrete_hora", 19);
            int minuto = prefs.getInt("lembrete_minuto", 0);

            java.util.Calendar amanha = java.util.Calendar.getInstance();
            amanha.add(java.util.Calendar.DAY_OF_YEAR, 1);
            amanha.set(java.util.Calendar.HOUR_OF_DAY, hora);
            amanha.set(java.util.Calendar.MINUTE, minuto);
            amanha.set(java.util.Calendar.SECOND, 0);

            android.app.AlarmManager am = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            android.content.Intent novoIntent = new android.content.Intent(context, LembreteReceiver.class);
            android.app.PendingIntent novoPi = android.app.PendingIntent.getBroadcast(
                    context, 777, novoIntent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (am != null) am.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, amanha.getTimeInMillis(), novoPi);
            } else {
                if (am != null) am.setExact(android.app.AlarmManager.RTC_WAKEUP, amanha.getTimeInMillis(), novoPi);
            }
        }
    }
}