package edu.csulb.android.workout.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.csulb.android.workout.R;
import edu.csulb.android.workout.models.Exercise;
import edu.csulb.android.workout.providers.WorkoutContract;

public class NewExerciseAdapter extends CursorAdapter {

	private List<Exercise> mExercises;

	private Context mContext;
	private LayoutInflater mInflater;

	static class ExerciseViewHolder {
		ImageView icon;
		TextView name;
		View minusButton;
		TextView count;
		View plusButton;
	}

	public NewExerciseAdapter(Context context,  Cursor c) {
		super(context, c, 0);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mExercises = new ArrayList<>();
	}

	public void setExercises(List<Exercise> mExercises) {
		this.mExercises = mExercises;
		notifyDataSetInvalidated();
	}

	public List<Exercise> getExercises() {
		return mExercises;
	}

	public int getExercisesCount() {
		int count = 0;

		for (Exercise e : mExercises) {
			if (e.count > 0) {
				count++;
			}
		}

		return count;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View convertView = mInflater.inflate(R.layout.item_new_exercise, parent, false);
		final ExerciseViewHolder viewHolder = new ExerciseViewHolder();

		// viewHolder.icon = (ImageView) convertView.findViewById(R.id.new_exercise_icon);
		viewHolder.name = (TextView) convertView.findViewById(R.id.new_exercise_name);
		viewHolder.minusButton = convertView.findViewById(R.id.new_exercise_minus);
		viewHolder.count = (TextView) convertView.findViewById(R.id.new_exercise_count);
		viewHolder.plusButton = convertView.findViewById(R.id.new_exercise_plus);

		convertView.setTag(viewHolder);

		return convertView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final ExerciseViewHolder viewHolder = (ExerciseViewHolder) view.getTag();
		final Exercise exercise = new Exercise();

		exercise.id = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExerciseTypesTable._ID));
		exercise.iconResId = cursor.getInt(cursor.getColumnIndex(WorkoutContract.ExerciseTypesColumns.ICON));
		exercise.name = cursor.getString(cursor.getColumnIndex(WorkoutContract.ExerciseTypesColumns.NAME));

		for (Exercise e : mExercises) {
			if (exercise.id == e.id) {
				exercise.count = e.count;
			}
		}

		refreshView(viewHolder, exercise);
		viewHolder.name.setText(exercise.name);

		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.new_exercise_minus:
						exercise.count = (exercise.count > 0) ? exercise.count - 1 : 0;
						break;

					case R.id.new_exercise_plus:
						exercise.count = (exercise.count < 99) ? exercise.count + 1 : 99;
						break;
				}

				viewHolder.count.setText(String.valueOf(exercise.count));
				refreshView(viewHolder, exercise);

				for (Exercise e : mExercises) {
					if (exercise.id == e.id) {
						e.count = exercise.count;
						return;
					}
				}

				mExercises.add(exercise);
			}
		};

		viewHolder.minusButton.setOnClickListener(listener);
		viewHolder.plusButton.setOnClickListener(listener);
	}

	private void refreshView(ExerciseViewHolder viewHolder, Exercise exercise) {
		viewHolder.count.setText(String.valueOf(exercise.count));
	}
}
