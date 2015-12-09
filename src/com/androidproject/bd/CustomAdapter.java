package com.androidproject.bd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.androidproject.GameView;
import com.androidproject.Player;
import com.androidproject.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private List<GameRoundData> data;
	private static LayoutInflater inflater = null;
	// public Resources res;
	GameRoundData tempValues = null;
	int i = 0;

	ArrayList<Integer> shipsandcolors = new ArrayList<Integer>();

	class IDScore implements Comparable<IDScore> {
		public int id;
		public int score;

		public IDScore(int id, int score) {
			this.id = id;
			this.score = score;
		}

		@Override
		public int compareTo(IDScore another) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	/************* CustomAdapter Constructor *****************/
	public CustomAdapter(Activity a, List<GameRoundData> d, Resources resLocal) {

		/********** Take passed values **********/
		activity = a;
		data = d;
		// res = resLocal;
		// List<Integer> scores= new ArrayList<Integer>();
		// List<Integer> ids= new ArrayList<Integer>();
		ArrayList<IDScore> scores = new ArrayList<IDScore>();

		/*********** Layout inflator to call external xml layout () ***********/
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		shipsandcolors.add(R.drawable.playership1_blue);
		shipsandcolors.add(R.drawable.playership1_green);
		shipsandcolors.add(R.drawable.playership1_orange);
		shipsandcolors.add(R.drawable.playership1_red);

		shipsandcolors.add(R.drawable.playership2_blue);
		shipsandcolors.add(R.drawable.playership2_green);
		shipsandcolors.add(R.drawable.playership2_orange);
		shipsandcolors.add(R.drawable.playership2_red);

		shipsandcolors.add(R.drawable.playership3_blue);
		shipsandcolors.add(R.drawable.playership3_green);
		shipsandcolors.add(R.drawable.playership3_orange);
		shipsandcolors.add(R.drawable.playership3_red);
	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public TextView TextView01;
		public TextView TextView02;
		public TextView TextView03;
		public TextView TextView04;
		public TextView text;
		public ImageView image;

	}

	/****** Depends upon data size called for each row , Create each ListView row *****/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		tempValues = (GameRoundData) data.get(position);

		ArrayList<Player> scores = new ArrayList<Player>();
		scores.add(new Player(tempValues.getPlayer1(), tempValues.getPlayer1_ship(), tempValues.getPlayer1_color(),
				tempValues.getPlayer1_distance(), tempValues.getPlayer1_score()));
		scores.add(new Player(tempValues.getPlayer2(), tempValues.getPlayer2_ship(), tempValues.getPlayer2_color(),
				tempValues.getPlayer2_distance(), tempValues.getPlayer2_score()));
		scores.add(new Player(tempValues.getPlayer3(), tempValues.getPlayer3_ship(), tempValues.getPlayer3_color(),
				tempValues.getPlayer3_distance(), tempValues.getPlayer3_score()));
		scores.add(new Player(tempValues.getPlayer4(), tempValues.getPlayer4_ship(), tempValues.getPlayer4_color(),
				tempValues.getPlayer4_distance(), tempValues.getPlayer4_score()));
		Collections.sort(scores);

		ViewHolder holder = new ViewHolder();
		View rowView;
		rowView = inflater.inflate(R.layout.tabitem, null);
		holder.TextView01 = (TextView) rowView.findViewById(R.id.TextView01);
		holder.TextView02 = (TextView) rowView.findViewById(R.id.TextView02);
		holder.TextView03 = (TextView) rowView.findViewById(R.id.TextView03);
		holder.TextView04 = (TextView) rowView.findViewById(R.id.TextView04);
		holder.image = (ImageView) rowView.findViewById(R.id.image);
		holder.text = (TextView) rowView.findViewById(R.id.text);
		holder.TextView01.setText("Winner: " + scores.get(0).name + "  Score: " + scores.get(0).score);
		if (scores.get(1).distance != 0)
			holder.TextView02.setText("2nd: " + scores.get(1).name + "  Score: " + scores.get(1).score);
		else
			holder.TextView02.setVisibility(View.GONE);
		if (scores.get(2).distance != 0)
			holder.TextView03.setText("3rd: " + scores.get(2).name + "  Score: " + scores.get(2).score);
		else
			holder.TextView03.setVisibility(View.GONE);
		if (scores.get(3).distance != 0)
			holder.TextView04.setText("4th: " + scores.get(3).name + "  Score: " + scores.get(3).score);
		else
			holder.TextView04.setVisibility(View.GONE);
		holder.image.setImageResource(shipsandcolors.get(scores.get(0).getShipColor()));
		Collections.sort(scores, new Comparator<Player>() {
			@Override
			public int compare(Player lhs, Player rhs) {
				// TODO Auto-generated method stub
				return ((Integer) rhs.distance).compareTo((Integer) lhs.distance);
			}
		});

		holder.text.setText("Distance: " + scores.get(0).distance);
		rowView.setOnClickListener(new OnItemClickListener(position));
		return rowView;

		// View vi = convertView;
		// ViewHolder holder;
		//
		// if(convertView==null){
		// Log.v("aqui", "aa");
		// /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
		// vi = inflater.inflate(R.layout.tabitem, null);
		//
		// /****** View Holder Object to contain tabitem.xml file elements ******/
		//
		// holder = new ViewHolder();
		// holder.text = (TextView) vi.findViewById(R.id.text);
		// holder.text1=(TextView)vi.findViewById(R.id.text1);
		//// holder.image=(ImageView)vi.findViewById(R.id.image);
		//
		// /************ Set holder with LayoutInflater ************/
		// vi.setTag( holder );
		// }
		// else
		// holder=(ViewHolder)vi.getTag();
		//
		// if(data.size()<=0)
		// {
		// holder.text.setText("No Data");
		//
		// }
		// else
		// {
		// /***** Get each Model object from Arraylist ********/
		// tempValues=null;
		// tempValues = ( GameRoundData ) data.get( position );
		//
		// /************ Set Model values in Holder elements ***********/
		//
		// holder.text.setText( tempValues.getPlayer1() );
		// holder.text1.setText( tempValues.getPlayer1_distance() );
		//// holder.image.setImageResource(
		//// res.getIdentifier(
		//// "com.androidexample.customlistview:drawable/"+tempValues.getImage()
		//// ,null,null));
		//
		// /******** Set Item Click Listner for LayoutInflater for each row *******/
		//
		// vi.setOnClickListener(new OnItemClickListener( position ));
		// }
		// return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked=====");
	}

	/********* Called when Item click in ListView ************/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {

			Toast.makeText(activity, "pos: " + mPosition, Toast.LENGTH_LONG).show();
			// CustomListViewAndroidExample sct = (CustomListViewAndroidExample)activity;

			/**** Call onItemClick Method inside CustomListViewAndroidExample Class ( See Below ) ****/

			// sct.onItemClick(mPosition);
		}
	}
}
