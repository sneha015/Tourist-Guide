package com.example.masterproject.touristguide;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.masterproject.touristguide.interfaces.OnRestartRequest;
import com.example.masterproject.touristguide.utils.Logger;
import com.mutualmobile.cardstack.CardStackAdapter;
import com.tramsun.libs.prefcompat.Pref;

public class MyCardStackAdapter extends CardStackAdapter implements CompoundButton.OnCheckedChangeListener {
    private static int[] bgColorIds;
    private final LayoutInflater mInflater;
    private final Context mContext;
    private Logger log = new Logger(MyCardStackAdapter.class.getSimpleName());
    private OnRestartRequest mCallback;
    private Runnable updateSettingsView;
    String text1[] = new String[8];
    int cardNumber1[] = new int[8];
    String cardsTitles[];

    public MyCardStackAdapter(MainActivity activity) {
        super(activity);
        mContext = activity;
        mInflater = LayoutInflater.from(activity);
        mCallback = activity;
        bgColorIds = new int[]{
                R.drawable.ar,
         R.drawable.cafe,
        R.drawable.restaurant,
         R.drawable.bar1,
        R.drawable.hotel,
        R.drawable.atm,
         R.drawable.attractions,
         R.drawable.location
        };
        text1[0] = "Fascinated? Confused? Click to know more ";
        text1[1] = "Hungry? Thirsty? Need a break? Click to find cafes near you";
        text1[2] = "Want to eat like a horse? One click and we got you covered.";
        text1[3] = "Too sober? Click to find the best bars in town";
        text1[4] = "Impromptu trip? Click to find a comfortable place to stay";
        text1[5] = "Need cash? Bank issues? Click to find the nearest ATM/Bank";
        text1[6] = "To find the next attraction before sundown, click here";
        text1[7] = "Lost? Click here and we'll find you";
        for(int i =0; i<8;i++){
            cardNumber1[i] = i+1;
        }
        cardsTitles = new String[]{
                "What AM I Looking At?",
                "Find Cafes Near Me",
                "Find Restaurants Nearby",
                "Suggest Me Some Bars",
                "Where can I Stay?",
                "Find Me An ATM/Bank",
                "Where Do I Go Next?",
                "I Think I'm Lost!"
        };
    }

    @Override
    public int getCount() {
        return bgColorIds.length;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        log.d("onCheckedChanged() called with: " + "buttonView = [" + buttonView + "], isChecked = [" + isChecked + "]");
        switch (buttonView.getId()) {
            case R.id.parallax_enabled:
                Pref.putBoolean(Prefs.PARALLAX_ENABLED, isChecked);
                break;
            case R.id.reverse_click_animation:
                Prefs.setReverseClickAnimationEnabled(isChecked);
                break;
            case R.id.show_init_animation:
                Pref.putBoolean(Prefs.SHOW_INIT_ANIMATION, isChecked);
                break;
        }
        updateSettingsView.run();
    }

    @Override
    public View createView(final int position, ViewGroup container) {

        CardView root = (CardView) mInflater.inflate(R.layout.card, container, false);
        ImageView backgroundImage = (ImageView) root.findViewById(R.id.backgroundImage);
        backgroundImage.setBackgroundResource(bgColorIds[position]);
        Button cardActionButton = (Button) root.findViewById(R.id.card_action_button);
        cardActionButton.setText(text1[position]);
        TextView cardTitle = (TextView) root.findViewById(R.id.cardTitle);
        cardTitle.setText(cardsTitles[position]);
        cardActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (cardNumber1[position]){
                    case 1:
                        ar();
                        break;
                    case 2:
                        listCafes();
                        break;
                    case 3:
                        listRestaurants();
                        break;
                    case 4:
                        listBars();
                        break;
                    case 5:
                        listHotels();
                        break;
                    case 6:
                        listATM();
                        break;
                    case 7:
                        listAttractionsnearby();
                        break;
                    case 8:
                        showLocation();
                        break;
                }
            }
        });
        return root;
    }

    @Override
    protected Animator getAnimatorForView(View view, int currentCardPosition, int selectedCardPosition) {
        if (Prefs.isReverseClickAnimationEnabled()) {

            int offsetTop = getScrollOffset();

            if (currentCardPosition > selectedCardPosition) {
                return ObjectAnimator.ofFloat(view, View.Y, view.getY(), offsetTop + getCardFinalY(currentCardPosition));
            } else {
                return ObjectAnimator.ofFloat(view, View.Y, view.getY(), offsetTop + getCardOriginalY(0) + (currentCardPosition * getCardGapBottom()));
            }
        } else {
            return super.getAnimatorForView(view, currentCardPosition, selectedCardPosition);
        }
    }

    private View getSettingsView(ViewGroup container) {
        CardView root = (CardView) mInflater.inflate(R.layout.settings_card, container, false);
        root.setCardBackgroundColor(ContextCompat.getColor(mContext, bgColorIds[0]));

        final Switch showInitAnimation = (Switch) root.findViewById(R.id.show_init_animation);
        final Switch parallaxEnabled = (Switch) root.findViewById(R.id.parallax_enabled);
        final Switch reverseClickAnimation = (Switch) root.findViewById(R.id.reverse_click_animation);
        final EditText parallaxScale = (EditText) root.findViewById(R.id.parallax_scale);
        final EditText cardGap = (EditText) root.findViewById(R.id.card_gap);
        final EditText cardGapBottom = (EditText) root.findViewById(R.id.card_gap_bottom);

        updateSettingsView = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                showInitAnimation.setChecked(Prefs.isShowInitAnimationEnabled());
                showInitAnimation.setOnCheckedChangeListener(MyCardStackAdapter.this);

                reverseClickAnimation.setChecked(Prefs.isReverseClickAnimationEnabled());
                reverseClickAnimation.setOnCheckedChangeListener(MyCardStackAdapter.this);

                boolean isParallaxEnabled = Prefs.isParallaxEnabled();
                parallaxEnabled.setChecked(isParallaxEnabled);
                parallaxEnabled.setOnCheckedChangeListener(MyCardStackAdapter.this);

                parallaxScale.setText("" + Prefs.getParallaxScale(mContext));
                parallaxScale.setEnabled(isParallaxEnabled);

                cardGap.setText("" + Prefs.getCardGap(mContext));

                cardGapBottom.setText("" + Prefs.getCardGapBottom(mContext));
            }
        };

        updateSettingsView.run();

        Button restartActivityButton = (Button) root.findViewById(R.id.restart_activity);
        restartActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePrefsIfRequired(parallaxScale);
                updatePrefsIfRequired(cardGap);
                updatePrefsIfRequired(cardGapBottom);
                mCallback.requestRestart();
            }
        });

        Button resetDefaultsButton = (Button) root.findViewById(R.id.reset_defaults);
        resetDefaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.resetDefaults(mContext);
                updateSettingsView.run();
            }
        });

        return root;
    }

    private void updatePrefsIfRequired(EditText view) {
        String text = view.getText().toString();
        int value;

        try {
            value = Integer.parseInt(text);
        } catch (Exception e) {
            value = Integer.MIN_VALUE;
        }
        if (value == Integer.MIN_VALUE) {
            log.e("Invalid value for " + view.getResources().getResourceName(view.getId()));
            return;
        }

        switch (view.getId()) {
            case R.id.parallax_scale:
                log.d("parallax_scale now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.PARALLAX_SCALE, Integer.parseInt(text));
                break;
            case R.id.card_gap:
                log.d("card_gap now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.CARD_GAP, Integer.parseInt(text));
                break;
            case R.id.card_gap_bottom:
                log.d("card_gap_bottom now is " + Integer.parseInt(text));
                Pref.putInt(Prefs.CARD_GAP_BOTTOM, Integer.parseInt(text));
                break;
        }
    }
    private void listCafes()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=cafe");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void listRestaurants()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void listBars()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=bar");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void listHotels()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=hotel");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void listAttractionsnearby()
    {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=tourist");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void showLocation()
    {
        Intent Intent = new Intent(mContext, MyLocationActivity.class);
        mContext.startActivity(Intent);
    }
    private void listATM()
    {
        //handle the click here
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=atm,bank");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
    private void ar()
    {
        Intent arIntent = new Intent(mContext, cameraActivity.class);
        mContext.startActivity(arIntent);
    }
}
