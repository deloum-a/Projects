package edu.csulb.android.workout.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import edu.csulb.android.workout.R;
import edu.csulb.android.workout.adapters.ExerciseAdapter;
import edu.csulb.android.workout.providers.WorkoutContract;


public class WorkoutActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	public static final String EXTRA_WORKOUT_ID = "EXTRA_WORKOUT_ID";

	private static final int WORKOUT_LOADER_ID = 0;
	private static final int EXERCISES_LOADER_ID = 1;

	private long mWorkoutId;

	private ListView mExercisesList;
	private ExerciseAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mExercisesList = (ListView) findViewById(R.id.workout_list);
		mWorkoutId = getIntent().getLongExtra(EXTRA_WORKOUT_ID, -1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.restartLoader(WORKOUT_LOADER_ID, null, this);
		loaderManager.restartLoader(EXERCISES_LOADER_ID, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
			case WORKOUT_LOADER_ID: {
				final String selection = WorkoutContract.ExercisesTable._ID + "=?";
				final String[] selectionArgs = {String.valueOf(mWorkoutId)};
				return new CursorLoader(this, WorkoutContract.WorkoutsTable.CONTENT_URI, null, selection, selectionArgs, null);
			}

			case EXERCISES_LOADER_ID: {
				final String selection = WorkoutContract.ExercisesTable.WORKOUT_ID + "=?";
				final String[] selectionArgs = {String.valueOf(mWorkoutId)};
				return new CursorLoader(this, WorkoutContract.ExercisesTable.CONTENT_URI, null, selection, selectionArgs, null);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader == null || cursor == null) {
			return;
		}

		switch (loader.getId()) {
			case WORKOUT_LOADER_ID:
				if (cursor.moveToFirst()) {
					setTitle(cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutsTable.NAME)));
				}
				break;

			case EXERCISES_LOADER_ID:
				cursor.moveToFirst();
				if (mAdapter == null) {
					mAdapter = new ExerciseAdapter(this, cursor);
					mExercisesList.setAdapter(mAdapter);
				} else {
					mAdapter.swapCursor(cursor);
				}
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_workout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;

			case R.id.action_edit_workout:
				final Intent intent = new Intent(this, NewWorkoutActivity.class);
				intent.putExtra(NewWorkoutActivity.EXTRA_WORKOUT_ID, mWorkoutId);
				startActivity(intent);
				return true;

		}
		return super.onOptionsItemSelected(item);
	}
}
