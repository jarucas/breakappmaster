package org.jarucas.breakapp;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jarucas.breakapp.utils.Utils;

//TODO this activity should be called first time the app gets executed
public class TutorialActivity extends AppCompatActivity {

    private static final int MAX_STEP = 4;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private String[] aboutTitleArray = {
            getAppString(R.string.tutorial_title_1),
            getAppString(R.string.tutorial_title_2),
            getAppString(R.string.tutorial_title_3),
            getAppString(R.string.tutorial_title_4)};

    private String aboutDescriptionArray[] = {
            getAppString(R.string.tutorial_desc_1),
            getAppString(R.string.tutorial_desc_2),
            getAppString(R.string.tutorial_desc_3),
            getAppString(R.string.tutorial_desc_4),};
    private int aboutImagesArray[] = {
            R.drawable.img_wizard_2,
            R.drawable.img_wizard_2,
            R.drawable.img_wizard_2,
            R.drawable.img_wizard_2};
    private int bgImagesArray[] = {
            R.drawable.image_11,
            R.drawable.image_10,
            R.drawable.image_7,
            R.drawable.image_24};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        bottomProgressDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        Utils.setSystemBarColor(this, R.color.grey_5);
        Utils.setSystemBarLight(this);
    }

    private void bottomProgressDots(int current_index) {
        LinearLayout dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        ImageView[] dots = new ImageView[MAX_STEP];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int widthHeight = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(widthHeight, widthHeight));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[current_index].setImageResource(R.drawable.shape_circle);
            dots[current_index].setColorFilter(getResources().getColor(R.color.light_blue_600), PorterDuff.Mode.SRC_IN);
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(final int position) {
            bottomProgressDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @NonNull
    private String getAppString(final int id) {
        return App.getContext().getResources().getString(id);
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Button btnNext;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_card_wizard_bg, container, false);
            ((TextView) view.findViewById(R.id.title)).setText(aboutTitleArray[position]);
            ((TextView) view.findViewById(R.id.description)).setText(aboutDescriptionArray[position]);
            ((ImageView) view.findViewById(R.id.image)).setImageResource(aboutImagesArray[position]);
            ((ImageView) view.findViewById(R.id.image_bg)).setImageResource(bgImagesArray[position]);

            btnNext = (Button) view.findViewById(R.id.btn_next);

            if (position == aboutTitleArray.length - 1) {
                btnNext.setText(R.string.tutorial_button_end);
            } else {
                btnNext.setText(R.string.tutorial_button);
            }


            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = viewPager.getCurrentItem() + 1;
                    if (current < MAX_STEP) {
                        // move to next screen
                        viewPager.setCurrentItem(current);
                    } else {
                        finish();
                    }
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return aboutTitleArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
