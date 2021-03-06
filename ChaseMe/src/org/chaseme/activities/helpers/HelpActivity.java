package org.chaseme.activities.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import org.chaseme.R;

public abstract class HelpActivity extends FragmentActivity implements OnClickListener {

	/**
	 * Calls fragment activity constructor 
	 */
	public HelpActivity() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            default:
                return false;
        }
	}

	/**
	 * Get help items to be populated
	 *
	 * @return A matrix with pars of help guides names, with the associated
	 *         video url
	 */
	public abstract CharSequence[][] getHelpItems();

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getHelpItems()[1][which].toString())));
	}

}
