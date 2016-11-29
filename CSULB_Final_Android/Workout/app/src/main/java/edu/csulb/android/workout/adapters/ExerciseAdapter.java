package edu.csulb.android.workout.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.csulb.android.workout.R;
import edu.csulb.android.workout.models.Exercise;
import edu.csulb.android.workout.providers.WorkoutContract;
import edu.csulb.android.workout.utils.UIUtils;

public class ExerciseAdapter extends CursorAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	private List<Exercise> mExercises;

	static class WorkoutViewHolder {
		TextView name;
		LinearLayout series;
	}

	public ExerciseAdapter(Context context, Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mExercises = new ArrayList<>();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View convertView = mInflater.inflate(R.layout.item_exercise, parent, false);
		final WorkoutViewHolder viewHolder = new WorkoutViewHolder();

		viewHolder.name = (TextView) convertView.findViewById(R.id.exercise_name);
		viewHolder.series = (LinearLayout) convertView.findViewById(R.id.exercise_series);

		convertView.setTag(viewHolder);

		return convertView;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final WorkoutViewHolder viewHolder = (WorkoutViewHolder) view.getTag();

		final Exercise exercise = new Exercise();

		exercise.id = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesTable._ID));
		exercise.iconResId = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesTable.ICON));
		exercise.name = cursor.getString(cursor.getColumnIndex(WorkoutContract.ExercisesTable.NAME));
		exercise.count = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExercisesTable.COUNT));

		viewHolder.name.setText(exercise.name);

		final int dip10 = UIUtils.dipToPixel(8);
		final LinearLayout.LayoutParams paramView = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		paramView.gravity = Gravity.CENTER_VERTICAL;
		paramView.setMargins(dip10, 0, dip10, 0);

		viewHolder.series.removeAllViews();
		for (int i = 0; i < exercise.count / 4 + 1; i++) {
			// Layout for line
			final LinearLayout linear = new LinearLayout(mContext);
			final LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
			linear.setLayoutParams(param);
			linear.setOrientation(LinearLayout.HORIZONTAL);

			for (int j = 0; j < 4 && j < exercise.count - (i * 4); j++) {
				final View contentView = mInflater.inflate(R.layout.item_serie, null);
				final TextView serie = (TextView) contentView.findViewById(R.id.serie);
				final int value = (i * 4) + j + 1;

				serie.setText(String.valueOf(value));
				for (Exercise e : mExercises) {
					if (e.id == exercise.id) {
						toggleSerie(serie, value <= e.count);
					}
				}

				contentView.setLayoutParams(paramView);
				contentView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						boolean found = false;
						for (Exercise e : mExercises) {
							if (e.id == exercise.id) {
								if (e.count == value) {
									e.count--;
								} else {
									e.count = value;
								}
								found = true;
							}
						}

						if (!found) {
							final Exercise e = new Exercise();
							e.id = exercise.id;
							e.count = value;
							mExercises.add(e);
						}

						notifyDataSetChanged();
					}
				});
				linear.addView(contentView);
			}

			viewHolder.series.addView(linear);
		}
	}

	private void toggleSerie(TextView v, boolean state) {
		int pL = v.getPaddingLeft();
		int pT = v.getPaddingTop();
		int pR = v.getPaddingRight();
		int pB = v.getPaddingBottom();

		Drawable background;

		if (state) {
			background = mContext.getResources().getDrawable(R.drawable.green_card);
		} else {
			background = mContext.getResources().getDrawable(R.drawable.grey_card);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			v.setBackground(background);
		} else {
			v.setBackgroundDrawable(background);
		}

		v.setPadding(pL, pT, pR, pB);
	}

}
