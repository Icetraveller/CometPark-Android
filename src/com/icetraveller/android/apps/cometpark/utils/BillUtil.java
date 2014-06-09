package com.icetraveller.android.apps.cometpark.utils;


import com.icetraveller.android.apps.cometpark.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

public class BillUtil {
	public static final String SKU_DONATION_ONE = "one_dollar";
	public static final String SKU_DONATION_FIVE = "five_dollar";

	public static String DONATE_FRAGMENT_TAG = "dialog_dondate";

	public static final int RC_REQUEST = 10001;

	public static String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp8wXzhC7OAMMPGkbbRbEMs3Z9a41pAvCsk7SK+ebYmtQnt8gw60ipljrAcWrbEF0tdTBKoknMOQAebwHGNRi2jwvexPZv4F8oYHKcbfbHO7ysQwwuMJnpF5+soEPGyo5Xpptg1lddpSlqiJDi5+Ke0D+hEzoCtaS0w2j8fNRacnC4MO7IgkggTVSAHVuA8eu8yqaKIlNL+5o79noqhLnleX1p3gHyRY+FBxdjPXQZhpEaEpVly4UJcSNRB96fkUIUIH3j1OArIxyTF8GS9TCAdW1xggANbKBrgBAGXMsbTbkYCbHIW8oqqYVCjhv2Z3GyoeS3/RsngYBo/Wro/d5GQIDAQAB";

	public static void complain(Context context, String TAG, String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		alert(context, TAG, "Error: " + message);
	}

	private static void alert(Context context, String TAG, String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	public static void showAbout(FragmentActivity activity) {
		FragmentManager fm = activity.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(DONATE_FRAGMENT_TAG);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		new DonationDialog().show(ft, DONATE_FRAGMENT_TAG);
	}

	public static class DonationDialog extends DialogFragment {
		public DonationDialog() {

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			 LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
	                    Context.LAYOUT_INFLATER_SERVICE);
			TextView donationView = (TextView) layoutInflater.inflate(R.layout.donate_dialog, null);
			return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.donate)
            .setView(donationView)
            .setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }
            )
            .create();
		}

	}
}
