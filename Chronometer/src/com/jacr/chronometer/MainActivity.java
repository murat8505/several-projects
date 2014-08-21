/*
 * (c) Jesus Castro
 * 
 * Simple Chronometer DD:HH:MM:SS:SSSS
 * 
 */

package com.jacr.chronometer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Clase Principal.
 * 
 * @author j.castro 26/07/2014
 * 
 */
public class MainActivity extends Activity {

	public static final String AMARILLO = "#E3EA25";
	public static final String ROJO = "#FF0000";
	public static final String VERDE = "#FF0000";
	public static final String AZUL = "#33B5E5";

	private TextView txtPantalla;
	private Timer timer = null;
	private long days = 0;
	private long hours = 0;
	private long minutes = 0;
	private long seconds = 0;
	private long millis = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// NO barra de titulo & Fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Bloqueando orientacion
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		/* FONDO */
		final LinearLayout fondo = (LinearLayout) findViewById(R.id.fondo);
		fondo.setBackgroundColor(Color.parseColor(ROJO));

		/* pANTALLA */
		txtPantalla = (TextView) findViewById(R.id.txtPantalla);

		/* Botones */

		ArrayList<ImageButton> listaB = new ArrayList<ImageButton>();
		listaB.add((ImageButton) findViewById(R.id.btInicio));
		listaB.add((ImageButton) findViewById(R.id.btPausa));
		listaB.add((ImageButton) findViewById(R.id.btReset));
		for (ImageButton btn : listaB) {
			final ImageButton bt = btn;

			bt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v.getId() == R.id.btInicio) {
						fondo.setBackgroundColor(Color.parseColor(AMARILLO));
						iniciarTimer();
					} else if (v.getId() == R.id.btPausa) {
						fondo.setBackgroundColor(Color.parseColor(ROJO));
						pausarTimer();

					} else if (v.getId() == R.id.btReset) {
						fondo.setBackgroundColor(Color.parseColor(ROJO));
						reiniciarTimer();
					}

				}

			});
		}

		mostrarEnPantalla();

	}

	private void mostrarEnPantalla() {
		String mil = "";
		if (millis < 10) {
			mil = "00" + millis;
		} else if (millis < 100) {
			mil = "0" + millis;
		} else if (millis == 1000) {
			mil = "000";
		} else {
			mil = millis + "";
		}
		String second = (seconds < 10) ? "0" + seconds : seconds + "";
		String minute = (minutes < 10) ? "0" + minutes : minutes + "";
		String hour = (hours < 10) ? "0" + hours : hours + "";
		String day = (days < 10) ? "0" + days : days + "";
		txtPantalla.setText(day + ":" + hour + ":" + minute + ":" + second
				+ ":" + mil);
	}

	private void reiniciarTimer() {
		pausarTimer();
		millis = 0;
		seconds = 0;
		minutes = 0;
		hours = 0;
		days = 0;
		mostrarEnPantalla();
	}

	private void pausarTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
		}
	}

	private void iniciarTimer() {
		pausarTimer();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						// Calculo
						millis++;

						if (millis == 1000) {
							millis = 0;
							seconds++;
						}

						if (seconds == 60) {
							seconds = 0;
							minutes++;
						}

						if (minutes == 60) {
							minutes = 0;
							hours++;
						}

						if (hours == 24) {
							hours = 0;
							days++;
						}

						if (days == 100) {
							days = 0;
						}

						// Mostrar en opantalla
						mostrarEnPantalla();

					}

				});
			}
		}, 1, 1);

	}

}
