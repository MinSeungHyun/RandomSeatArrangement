package com.seunghyun.randomseats.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

import com.seunghyun.randomseats.utils.DataViewModel;
import com.seunghyun.randomseats.R;
import com.seunghyun.randomseats.utils.SeatAppearanceDialog;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ListPreference rowSeatNumber, columnSeatNumber;
    private SharedPreferences sharedPreferences;

    private static void tintIcons(Preference preference, int color) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup group = ((PreferenceGroup) preference);
            for (int i = 0; i < group.getPreferenceCount(); i++) {
                tintIcons(group.getPreference(i), color);
            }
        } else {
            Drawable icon = preference.getIcon();
            if (icon != null) {
                icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        DataViewModel model = ViewModelProviders.of(requireActivity()).get(DataViewModel.class);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        rowSeatNumber = (ListPreference) findPreference(getString(R.string.row_seat_number_key));
        columnSeatNumber = (ListPreference) findPreference(getString(R.string.column_seat_number_key));

        rowSeatNumber.setDefaultValue(getString(R.string.alphabet));
        columnSeatNumber.setDefaultValue(getString(R.string.number));

        rowSeatNumber.setSummary(sharedPreferences.getString(getString(R.string.row_seat_number_key), getString(R.string.alphabet)));
        columnSeatNumber.setSummary(sharedPreferences.getString(getString(R.string.column_seat_number_key), getString(R.string.number)));

        //좌석 크기
        findPreference(getString(R.string.seat_appearance_key)).setOnPreferenceClickListener(preference -> {
            int width = requireContext().getResources().getDisplayMetrics().widthPixels;
            SeatAppearanceDialog dialog = new SeatAppearanceDialog(requireContext(), sharedPreferences);

            WindowManager.LayoutParams windowManager = Objects.requireNonNull(dialog.getWindow()).getAttributes();
            windowManager.copyFrom(dialog.getWindow().getAttributes());
            windowManager.width = (int) (width / 1.2);
            dialog.show();
            dialog.setOnDismissListener(dialog_ -> model.getIsSeatAppearanceSettingFinished().setValue(true));
            return false;
        });
        //리뷰 남기기
        Preference review = findPreference(getString(R.string.review_key));
        review.setOnPreferenceClickListener(preference -> {
            DataViewModel.makeReview(requireActivity());
            return false;
        });
        //이메일 보내기
        Preference sendEmail = findPreference(getString(R.string.send_email_key));
        sendEmail.setOnPreferenceClickListener(preference -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.setType("plain/text");
            email.putExtra(Intent.EXTRA_EMAIL, "asdf014563@naver.com");
            email.putExtra(Intent.EXTRA_SUBJECT, R.string.email_title);
            startActivity(email);
            return false;
        });
        //업데이트 확인
        Preference checkUpdate = findPreference(getString(R.string.check_update_key));
        checkUpdate.setOnPreferenceClickListener(preference -> {
            DataViewModel.updateIfRequire((AppCompatActivity) requireActivity(), true);
            return false;
        });

        tintIcons(getPreferenceScreen(), requireActivity().getColor(R.color.colorAccent)); //아이콘 색깔 변경
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.row_seat_number_key)))
            rowSeatNumber.setSummary(sharedPreferences.getString(key, getString(R.string.alphabet)));
        if (key.equals(getString(R.string.column_seat_number_key)))
            columnSeatNumber.setSummary(sharedPreferences.getString(key, getString(R.string.number)));
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
//        return new PreferenceGroupAdapter(preferenceScreen) {
//            @Override
//            public void onBindViewHolder(PreferenceViewHolder holder, int position) {
//                super.onBindViewHolder(holder, position);
//                Preference preference = getItem(position);
//                if (preference instanceof PreferenceCategory)
//                    setZeroPaddingToLayoutChildren(holder.itemView);
//                else {
//                    View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
//                    if (iconFrame != null) {
//                        iconFrame.setVisibility(preference.getIcon() == null ? View.GONE : View.VISIBLE);
//                    }
//                }
//            }
//        };
//    }
//
//    private void setZeroPaddingToLayoutChildren(View view) {
//        if (!(view instanceof ViewGroup))
//            return;
//        ViewGroup viewGroup = (ViewGroup) view;
//        int childCount = viewGroup.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            setZeroPaddingToLayoutChildren(viewGroup.getChildAt(i));
//            viewGroup.setPaddingRelative(CATEGORY_LEFT_PADDING, viewGroup.getPaddingTop(), viewGroup.getPaddingEnd(), viewGroup.getPaddingBottom());
//        }
//    }
}