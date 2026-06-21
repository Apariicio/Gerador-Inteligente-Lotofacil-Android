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
        // 🌟 MATEMÁTICA DO FILTRO: Verifica o dia da semana atual no celular
        Calendar calendar = Calendar.getInstance();
        int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);

        // Se for Domingo (SUNDAY), encerra o processo sem criar notificação
        if (diaDaSemana == Calendar.SUNDAY) {
            return;
        }

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "lembretes_sorteio_sniper";

        // Cria o canal de notificações obrigatório para Android 8.0 ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Lembretes de Sorteio Lotofácil", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Avisos diários para lembrar o horário de conferência e sorteios");
            if (nm != null) nm.createNotificationChannel(channel);
        }

        // Configura a ação de clique da notificação (Abre a tela inicial do seu app)
        Intent abrirApp = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, abrirApp, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Monta o design visual da Notificação Push nativa
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Ícone clássico de alarme nativo
                .setContentTitle("⏰ Hora do Sorteio Lotofácil!")
                .setContentText("Não se esqueça de gerar e conferir suas sequências Sniper hoje no app!")
                .setAutoCancel(true) // Desaparece da barra ao ser clicada
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (nm != null) {
            // Dispara a notificação na barra de tarefas do usuário
            nm.notify(777, builder.build());
        }
    }
}