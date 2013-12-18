package cabrerizo.luis.tarea4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cabrerizo.luis.tarea4.App;
import cabrerizo.luis.tarea4.data.Store;
import cabrerizo.luis.tarea4.global.Utiles;

import com.android.volley.toolbox.NetworkImageView;
import com.cabrerizo.luis.tarea4.R;

public class DetalleActivity extends FragmentActivity {
	int id = -1;
	int esFavorito = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalle);

		id = getIntent().getExtras().getInt("id");

		final Store store = Utiles.locateStore(getApplicationContext(), id);

		final TextView nombre = (TextView) findViewById(R.id.Nombre);
		final TextView direccion = (TextView) findViewById(R.id.Direccion);
		final TextView telefono = (TextView) findViewById(R.id.Telefono);
		final TextView horarios = (TextView) findViewById(R.id.Horarios);
		final TextView website = (TextView) findViewById(R.id.Website);
		final TextView eMail = (TextView) findViewById(R.id.EMail);
		final NetworkImageView fotoDetalle = (NetworkImageView) findViewById(R.id.fotoDetalle);
		final TextView favoritos = (TextView) findViewById(R.id.textoFavoritos);

		nombre.setText(store.getNombre());
		direccion.setText(store.getDireccion());
		telefono.setText(store.getTelefono());
		horarios.setText(store.getHorarios());
		website.setText(store.getWebsite());
		eMail.setText(store.getEmail());
		fotoDetalle.setImageUrl(store.getFoto().getUrl(),
				((App) getApplicationContext()).getImageLoader());
		favoritos.setText(getString(R.string.Favoritos)
				+ String.valueOf(store.getNumeroFavoritos()));
		
		esFavorito = store.getEsFavorito();

		Linkify.addLinks(direccion, Linkify.MAP_ADDRESSES);
		Linkify.addLinks(telefono, Linkify.PHONE_NUMBERS);
		Linkify.addLinks(website, Linkify.WEB_URLS);
		Linkify.addLinks(eMail, Linkify.EMAIL_ADDRESSES);

		OnClickListener botonImagen = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						FotografiaActivity.class);

				intent.putExtra("id", id);

				startActivity(intent);
			}
		};

		fotoDetalle.setOnClickListener(botonImagen);

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (esFavorito == 0) {
			menu.getItem(0).setIcon(R.drawable.ic_action_not_important);
		} else {
			menu.getItem(0).setIcon(R.drawable.ic_action_important);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detalle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_share:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			String mensaje = getString(R.string.msg_share_text) + "\r\n";

			mensaje = mensaje
					+ ((TextView) findViewById(R.id.Nombre)).getText()
							.toString() + "\r\n";
			mensaje = mensaje
					+ ((TextView) findViewById(R.id.Direccion)).getText()
							.toString() + "\r\n";
			mensaje = mensaje
					+ ((TextView) findViewById(R.id.Telefono)).getText()
							.toString() + "\r\n";
			mensaje = mensaje
					+ ((TextView) findViewById(R.id.Horarios)).getText()
							.toString() + "\r\n";
			mensaje = mensaje
					+ ((TextView) findViewById(R.id.Website)).getText()
							.toString() + "\r\n";
			mensaje = mensaje
					+ ((TextView) findViewById(R.id.EMail)).getText()
							.toString() + "\r\n";

			intent.putExtra(Intent.EXTRA_TEXT, mensaje);
			intent.setType("text/plain");
			startActivity(Intent.createChooser(intent,
					getString(R.string.action_share)));

			return true;
		case R.id.action_star:
			if (esFavorito == 0) {
				esFavorito = 1;
			} else {
				esFavorito = 0;
			}
			supportInvalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}