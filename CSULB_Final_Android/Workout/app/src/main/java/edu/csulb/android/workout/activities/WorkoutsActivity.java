package edu.csulb.android.workout.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.csulb.android.workout.Constants;
import edu.csulb.android.workout.R;
import edu.csulb.android.workout.adapters.WorkoutAdapter;
import edu.csulb.android.workout.providers.WorkoutContract;


public class WorkoutsActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

	private static final int WORKOUTS_LOADER_ID = 0;

	private ListView mWorkoutsList;
	private View mTutorial;
	private View mTutorialHint;

	private WorkoutAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workouts);

		mWorkoutsList = (ListView) findViewById(R.id.workout_list);
		mTutorial = findViewById(R.id.workout_tutorial);
		mTutorialHint = findViewById(R.id.workout_hint);

		mWorkoutsList.setOnItemClickListener(this);
		registerForContextMenu(mWorkoutsList);

		final SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
		final boolean isFirstTime = preferences.getBoolean(Constants.Preferences.IS_FIRST_TIME, true);

		if (isFirstTime) {
			createDefaultExercises();
			final SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(Constants.Preferences.IS_FIRST_TIME, false);
			editor.apply();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.restartLoader(WORKOUTS_LOADER_ID, null, this);
	}

	private void createDefaultExercises() {
		ContentResolver contentResolver = getContentResolver();
		for (String exercise : Constants.defaultExercises) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(WorkoutContract.ExerciseTypesColumns.NAME, exercise);
			contentResolver.insert(WorkoutContract.ExerciseTypesTable.CONTENT_URI, contentValues);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.workout_list) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			if (mAdapter != null) {
				Cursor cursor = (Cursor) mAdapter.getItem(info.position);
				if (cursor != null) {
					menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(WorkoutContract.WorkoutsTable.NAME)));
				}
			}
			menu.add(Menu.NONE, 0, 0, "Edit");
			menu.add(Menu.NONE, 1, 1, "Delete");
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case 0: // Edit
				if (mAdapter != null) {
					final Intent intent = new Intent(this, NewWorkoutActivity.class);
					intent.putExtra(NewWorkoutActivity.EXTRA_WORKOUT_ID, mAdapter.getItemId(info.position));
					startActivityForResult(intent, 0);
				}
				break;

			case 1: // Delete
				final ContentResolver contentResolver = getContentResolver();
				String where = WorkoutContract.WorkoutsTable._ID + "=?";
				String[] selectionArgs = {String.valueOf(mAdapter.getItemId(info.position))};
				contentResolver.delete(WorkoutContract.WorkoutsTable.CONTENT_URI, where, selectionArgs);
				final LoaderManager loaderManager = getSupportLoaderManager();
				loaderManager.restartLoader(WORKOUTS_LOADER_ID, null, this);
				break;
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Intent intent = new Intent(this, WorkoutActivity.class);
		intent.putExtra(WorkoutActivity.EXTRA_WORKOUT_ID, id);
		startActivity(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
			case WORKOUTS_LOADER_ID: {
				return new CursorLoader(this, WorkoutContract.WorkoutsTable.CONTENT_URI, null, null, null, null);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (loader == null || cursor == null || !cursor.moveToFirst()) {
			mTutorial.setVisibility(View.VISIBLE);
			mTutorialHint.setVisibility(View.VISIBLE);
			mWorkoutsList.setVisibility(View.GONE);
			return;
		}

		switch (loader.getId()) {
			case WORKOUTS_LOADER_ID:
				mTutorial.setVisibility(View.GONE);
				mTutorialHint.setVisibility(View.GONE);
				mWorkoutsList.setVisibility(View.VISIBLE);
				mAdapter = new WorkoutAdapter(this, cursor);
				mWorkoutsList.setAdapter(mAdapter);
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_workouts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_new_workout) {
			startActivity(new Intent(this, NewWorkoutActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
