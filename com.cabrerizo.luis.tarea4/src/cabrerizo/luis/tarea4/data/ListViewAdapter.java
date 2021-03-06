package cabrerizo.luis.tarea4.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cabrerizo.luis.tarea4.App;
import cabrerizo.luis.tarea4.data.models.InstagramPicture;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.cabrerizo.luis.tarea4.R;

public class ListViewAdapter extends BaseAdapter {

	private static final int PIC_HEIGHT = 600;
	private static final int PIC_WIDTH = 300;
	private final static String urlHttp = "http://";
	private final static String urlHttps = "https://";

	private ImageLoader imageLoader;

	private ArrayList<InstagramPicture> dataArray = new ArrayList<InstagramPicture>();

	private LayoutInflater inflater;

	static RequestQueue requestQueue;
	static ProgressBar barra;
	static ListView lista;
	static PullToRefreshLayout pull;

	public void addImage(String picturePath) {

		InstagramPicture pic = new InstagramPicture();
		String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm",
				Locale.getDefault()).format(Calendar.getInstance().getTime());

		pic.setUrl(picturePath);
		pic.setDescripcion(fecha);
		pic.setFecha(fecha);

		dataArray.add(0, pic);

		this.notifyDataSetChanged();

	}

	public ListViewAdapter(Activity activity,
			ArrayList<InstagramPicture> dataArray, int idBarraProgreso,
			int idListView, PullToRefreshLayout valorPull, Context context) {

		requestQueue = ((App) context).getRequestQueue();

		this.dataArray = dataArray;
		this.inflater = LayoutInflater.from(activity.getApplicationContext());
		this.imageLoader = ((App) context).getImageLoader();

		barra = (ProgressBar) activity.findViewById(idBarraProgreso);
		lista = (ListView) activity.findViewById(idListView);
		pull = valorPull;

		ApiCall();

	};

	public void ApiCall() {

		if (dataArray.isEmpty()) {
			String url = HelperInstagram.getRecentMediaUrl("shopping");

			Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					JSONArray data;
					try {
						data = response.getJSONArray("data");

						for (int i = 0; i < data.length(); i++) {
							JSONObject element = data.getJSONObject(i);

							String type = element.getString("type");

							if (type.equals("image")) {

								String descripcion = "";

								int createdTime = Integer.parseInt(element
										.getString("created_time"));

								String fecha = new SimpleDateFormat(
										"dd/MM/yyyy HH:mm", Locale.getDefault())
										.format(new java.util.Date(
												(long) createdTime * 1000));

								if (!element.isNull("caption")) {
									JSONObject caption = element
											.getJSONObject("caption");

									descripcion = caption.getString("text");

								} else {
									descripcion = fecha;

								}

								JSONObject images = element
										.getJSONObject("images");
								JSONObject standardResolution = images
										.getJSONObject("standard_resolution");

								String urlPic = standardResolution
										.getString("url");

								InstagramPicture img = new InstagramPicture();

								img.setFecha(fecha);
								img.setDescripcion(descripcion);
								img.setUrl(urlPic);

								dataArray.add(img);

								notifyDataSetChanged();
							}

						}

					} catch (JSONException e) {
						Log.e("ERROR", Log.getStackTraceString(e));
					}

					lista.setVisibility(View.VISIBLE);
					barra.setVisibility(View.GONE);

					if (pull.isRefreshing()) {
						pull.setRefreshComplete();
					}
				}
			};

			JsonObjectRequest request = new JsonObjectRequest(
					Request.Method.GET, url, null, listener, null);

			requestQueue.add(request);
		}

	}

	@Override
	public int getCount() {
		return dataArray.size();
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		InstagramPicture current = dataArray.get(position);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_image, null);

			holder = new ViewHolder();

			holder.txt = (TextView) convertView.findViewById(R.id.listTexto);
			holder.img = (NetworkImageView) convertView
					.findViewById(R.id.listImagen);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		holder.txt.setText(current.getDescripcion());

		if (((current.getUrl()).indexOf(urlHttp) != -1)
				|| ((current.getUrl()).indexOf(urlHttps) != -1)) {
			holder.img.setImageUrl(current.getUrl(), imageLoader);
		} else {
			holder.img.setImageURI(Uri.parse(current.getUrl()));
			holder.img.setMinimumHeight(PIC_HEIGHT);
			holder.img.setMinimumWidth(PIC_WIDTH);
			holder.img.setMaxHeight(PIC_HEIGHT);
			holder.img.setMaxWidth(PIC_WIDTH);
		}

		return convertView;
	}

	static class ViewHolder {
		public NetworkImageView img;
		public TextView txt;
	}
}
