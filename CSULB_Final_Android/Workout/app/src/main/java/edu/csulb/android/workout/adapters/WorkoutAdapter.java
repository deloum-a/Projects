package edu.csulb.android.workout.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import edu.csulb.android.workout.R;
import edu.csulb.android.workout.providers.WorkoutContract;

public class WorkoutAdapter extends CursorAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	static class WorkoutViewHolder {
		TextView name;
	}

	public WorkoutAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View convertView = mInflater.inflate(R.layout.item_workout, parent, false);
		final WorkoutViewHolder viewHolder = new WorkoutViewHolder();

		viewHolder.name = (TextView) convertView.findViewById(R.id.workout_name);

		convertView.setTag(viewHolder);

		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final WorkoutViewHolder viewHolder = (WorkoutViewHolder) view.getTag();

		String name = cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutsTable.NAME));
		viewHolder.name.setText(name);
	}

}
